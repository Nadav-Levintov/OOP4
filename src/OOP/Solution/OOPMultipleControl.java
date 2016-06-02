package OOP.Solution;

import OOP.Provided.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

public class OOPMultipleControl {

    //TODO: DO NOT CHANGE !!!!!!
    private Class<?> interfaceClass;
    private File sourceFile;

    //TODO: DO NOT CHANGE !!!!!!
    public OOPMultipleControl(Class<?> interfaceClass, File sourceFile) {
        this.interfaceClass = interfaceClass;
        this.sourceFile = sourceFile;
    }

    //TODO: fill in here :
    public void validateInheritanceTree() throws OOPMultipleException {
        List<Method> methods = validateAux(interfaceClass);


    }

    //TODO: fill in here :
    public Object invoke(String methodName, Object[] args)
            throws OOPMultipleException {

        return null;
    }

    //TODO: add more of your code :
    public List<Method> validateAux(Class<?> current) throws OOPMultipleException {
        Class[] hierarchyTree = current.getInterfaces();
        if (hierarchyTree.length == 0) {
            List<Method> methods = new LinkedList<Method>();
            Method[] methodArr = current.getMethods();
            for (Method aMethod : methodArr) {
                if (!aMethod.isAnnotationPresent(OOPMethod.class)) {
                    throw new OOPBadClass(aMethod);
                }
                methods.add(aMethod);
            }
            return methods;
        }

        List<Method> methodSet = validateAux(hierarchyTree[0]);

        for (int i = 1; i < hierarchyTree.length; i++) {
            List<Method> currentMethods = validateAux(hierarchyTree[i]);
            for (Method currentMethod : currentMethods) {
                for (Method existingMethod : methodSet) {
                    if (currentMethod.getName().equals(existingMethod.getName())) {
                        if (currentMethod.getDeclaringClass().equals(existingMethod.getDeclaringClass())) {
                            throw new OOPInherentAmbiguity(interfaceClass, currentMethod.getDeclaringClass(), currentMethod);
                        } else if (currentMethod.getDeclaringClass().isAssignableFrom(existingMethod.getDeclaringClass())) {
                            OOPMethod currentA = currentMethod.getAnnotation(OOPMethod.class);
                            OOPMethod existingA = currentMethod.getAnnotation(OOPMethod.class);
                            if (currentA.modifier().ordinal() < existingA.modifier().ordinal()) {
                                throw new OOPBadClass(currentMethod);
                            }


                        }

                    }

                }
                for (Method aMethod : currentMethods
                        ) {
                    methodSet.add(aMethod);
                }

            }
        }
        return methodSet;
    }


    //TODO: DO NOT CHANGE !!!!!!

    public void removeSourceFile() {
        if (sourceFile.exists()) {
            sourceFile.delete();
        }
    }
}
