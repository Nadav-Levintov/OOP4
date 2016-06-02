package OOP.Solution;

import OOP.Provided.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
        // Class[] parentInteface = interfaceClass.getInterfaces();


    }

    //TODO: fill in here :
    public Object invoke(String methodName, Object[] args)
            throws OOPMultipleException {

        return null;
    }

    //TODO: add more of your code :
    public Set<Method> validateAux(Class<?> current) throws OOPMultipleException {
        if (!current.isInterface()) {
            return new TreeSet<Method>();
        }
        Class[] hirarcyTree = current.getInterfaces();
        Set<Method> methodSet = validateAux(hirarcyTree[0]);

        for (int i = 1; i < hirarcyTree.length; i++) {
            Set<Method> currentMethods = validateAux(hirarcyTree[i]);
            for (Method currentMethod : currentMethods) {
                for (Method exsistingMethod : methodSet) {
                    if (currentMethod.getName().equals(exsistingMethod.getName()) &&
                            currentMethod.getDeclaringClass().equals(exsistingMethod.getDeclaringClass()))
                    {
                        throw new OOPInherentAmbiguity(interfaceClass,currentMethod.getDeclaringClass(),currentMethod);
                    }

                }

            }
            for (Method aMethod: currentMethods
                 ) {
                methodSet.add(aMethod);
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
