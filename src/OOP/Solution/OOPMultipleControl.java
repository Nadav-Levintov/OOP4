package OOP.Solution;

import OOP.Provided.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.util.Pair;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
            throws OOPMultipleException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException, ClassNotFoundException {
        Method[] methods = interfaceClass.getMethods();
        List<Method> compatiableMethods = new LinkedList<Method>();
        int argsLength = 0;
        if (args != null) {
            argsLength = args.length;
        }
        Class[] paramTypes = new Class<?>[argsLength];
        for (int i = 0; i < argsLength; i++) {
            paramTypes[i] = args[i].getClass();
        }

        for (Method aMethod : methods
                ) {
            if (aMethod.getName().equals(methodName) &&
                    Arrays.equals(paramTypes, aMethod.getParameterTypes())) {
                compatiableMethods.add(aMethod);
            }
        }
        if (compatiableMethods.size() == 1) {
            Method theMethod = compatiableMethods.get(0);
            OOPModifier methodMod = theMethod.getAnnotation(OOPMethod.class).modifier();
            if (methodMod.equals(OOPModifier.PRIVATE)) {
                throw new OOPInaccessibleMethod();
            }
            if (methodMod.equals(OOPModifier.DEFAULT)) {
                Package pkg = theMethod.getDeclaringClass().getPackage();
                if (checkPackage(interfaceClass, pkg, theMethod.getDeclaringClass())) {
                    throw new OOPInaccessibleMethod();
                }

            }

            String interfaceName = theMethod.getDeclaringClass().getName();
            int lastDot = interfaceName.lastIndexOf('.');
            String clsName = interfaceName.substring(0, lastDot + 1) + "C" + interfaceName.substring(lastDot + 2, interfaceName.length());
            Class<?> cls = Class.forName(clsName);
            Constructor<?> constr = cls.getConstructor();
            Object obj = constr.newInstance(new Object[]{});
            return theMethod.invoke(obj, args);
        } else {
            if (compatiableMethods.size() == 0) {
                throw new OOPInaccessibleMethod();
            }

            List<Pair<Class<?>, Method>> clashingSet = new LinkedList<Pair<Class<?>, Method>>();
            for (Method aMethod : compatiableMethods) {
                Pair<Class<?>, Method> pair = new Pair<Class<?>, Method>(aMethod.getDeclaringClass(), aMethod);
                clashingSet.add(pair);
            }
            throw new OOPCoincidentalAmbiguity(clashingSet);
        }

    }

    //TODO: add more of your code :
    public Boolean checkPackage(Class<?> cls, Package pkg, Class<?> parent) {
        if (cls.getPackage().equals(pkg)) {
            List<Class<?>> methodsClass = new LinkedList<Class<?>>();
            Class<?>[] parents = cls.getInterfaces();
            for (Class cl : parents) {
                if (parent.isAssignableFrom(cl)) {
                    return checkPackage(cl, pkg, parent);
                }
            }
        }
        return false;
    }

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
                    if (methodCompare(existingMethod, currentMethod)) {
                        checkInharent(existingMethod, currentMethod, current);
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
                if (methodCompare(existingMethod, currentMethod)) {
                    checkInharent(existingMethod, currentMethod,current);
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
            if (/*Modifier.isFinal(existingMethod.getModifiers()) ||*/ currentA.modifier().ordinal() < existingA.modifier().ordinal()) {
                throw new OOPBadClass(currentMethod);
            }
        }
    }

    public void checkInharent(Method existingMethod, Method currentMethod, Class<?> current) throws OOPInherentAmbiguity {
        try {
            Method curLevelMethod = current.getDeclaredMethod(existingMethod.getName(), existingMethod.getParameterTypes());
        } catch (NoSuchMethodException e) {
            if (currentMethod.getDeclaringClass().equals(existingMethod.getDeclaringClass())) {
                throw new OOPInherentAmbiguity(interfaceClass, currentMethod.getDeclaringClass(), currentMethod);
            }
        }
    }

    public Boolean methodCompare(Method m1, Method m2) {
        return m1.getName().equals(m2.getName()) &&
                Arrays.equals(m1.getParameterTypes(), m2.getParameterTypes())
               /*&& m1.getReturnType().equals(m2.getReturnType())*/;
    }
    //TODO: DO NOT CHANGE !!!!!!

    public void removeSourceFile() {
        if (sourceFile.exists()) {
            sourceFile.delete();
        }
    }
}
