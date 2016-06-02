package OOP.Solution;

import OOP.Provided.*;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
        /*check if this is the highest level in the current branch*/
        if (hierarchyTree.length == 0) {
            List<Method> methods = new LinkedList<Method>();
            Method[] methodArr = current.getMethods();
            for (Method aMethod : methodArr) {
                checkAnnotations(aMethod);
                methods.add(aMethod);
            }
            return methods;
        }

        List<Method> methodList = validateAux(hierarchyTree[0]);
    /*check for functions declared in all of the parent levels*/
        for (int i = 1; i < hierarchyTree.length; i++) {
            List<Method> currentMethods = validateAux(hierarchyTree[i]);
            for (Method currentMethod : currentMethods) {
                for (Method existingMethod : methodList) {
                    if (methodCompare(existingMethod,currentMethod)) {
                        checkInharent(existingMethod, currentMethod);
                        checkAnnotationsPermission(existingMethod, currentMethod);
                    }

                }
            }
            methodList.addAll(currentMethods);
        }

        /*check for functions declared in current level*/
        Method[] currentMethods = current.getDeclaredMethods();
        for (Method currentMethod : currentMethods) {
            for (Method existingMethod : methodList) {
                if (methodCompare(existingMethod,currentMethod)) {
                    checkInharent(existingMethod, currentMethod);
                    checkAnnotationsPermission(existingMethod, currentMethod);
                }
            }
        }
        methodList.addAll(Arrays.asList(currentMethods));


        return methodList;
    }

    public void checkAnnotations(Method aMethod) throws OOPBadClass {
        if (!aMethod.isAnnotationPresent(OOPMethod.class)) {

            throw new OOPBadClass(aMethod);
        }
    }

    public void checkAnnotationsPermission(Method existingMethod, Method currentMethod) throws OOPBadClass {
        if (existingMethod.getDeclaringClass().isAssignableFrom(currentMethod.getDeclaringClass())) {
            OOPMethod currentA = currentMethod.getAnnotation(OOPMethod.class);
            OOPMethod existingA = existingMethod.getAnnotation(OOPMethod.class);
            if (Modifier.isFinal(existingMethod.getModifiers()) || currentA.modifier().ordinal() < existingA.modifier().ordinal()) {
                throw new OOPBadClass(currentMethod);
            }
        }
    }

    public void checkInharent(Method existingMethod, Method currentMethod) throws OOPInherentAmbiguity {
        if (currentMethod.getDeclaringClass().equals(existingMethod.getDeclaringClass())) {
            throw new OOPInherentAmbiguity(interfaceClass, currentMethod.getDeclaringClass(), currentMethod);
        }
    }

    public Boolean methodCompare(Method m1,Method m2)
    {
       return m1.getName().equals(m2.getName()) &&
                Arrays.equals(m1.getParameterTypes(),m2.getParameterTypes())&&
                m1.getReturnType().equals(m2.getReturnType());
    }
    //TODO: DO NOT CHANGE !!!!!!

    public void removeSourceFile() {
        if (sourceFile.exists()) {
            sourceFile.delete();
        }
    }
}
