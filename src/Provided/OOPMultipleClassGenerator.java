package OOP.Provided;

import OOP.Solution.OOPMultipleControl;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class OOPMultipleClassGenerator {

    private static final String className = "OOPMultiple";
    private static final String packageName = "OOP.Solution";
    private static final String DispatchClassName = "OOPMultipleControl";
    private static final String sourcePath = System.getProperty("user.dir") + "/src/OOP/Solution"+ "/";
    private static final String classPath = System.getProperty("user.dir");
    private OOPMultipleControl controller;

    /***
     * Generates OOPMultiple for a Multiple Inheritance Tree.
     * @param interfaceClass The class file of the lowest interface in the tree.
     *                       This interface is implemented by OOPMultiple.
     * @return An Object of the OOP Multiple Class.
     * @throws OOPMultipleException
     */
    public Object generateMultipleClass(Class<?> interfaceClass)
    throws OOPMultipleException {

        File sourceFile = new File(sourcePath + className + ".java");
        controller = new OOPMultipleControl(interfaceClass, sourceFile);

        /**
         * A call to your Multiple Inheritance Tree validation.
         */
        controller.validateInheritanceTree();

        Object obj = null;

        String className = OOPMultipleClassGenerator.className;
        String packageName = OOPMultipleClassGenerator.packageName;
        try {
            /**
             * The creation of the source file
             */
            FileWriter writer = new FileWriter(sourceFile);
            writer.write(OOPMultipleClassGenerator.getClassString(interfaceClass));
            writer.close();

            /**
             * A call to Java's compiler, for the compilation of OOPMultiple
             */
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File(classPath)));
            StringWriter out = new StringWriter();
            JavaCompiler.CompilationTask task = compiler.getTask(out, fileManager, null, null, null,
                    fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile)));

            task.call();

            fileManager.close();
            File objectFile = new File(classPath);
            URL url = objectFile.toURI().toURL();
            URL[] urls = new URL[]{url};

            /**
             * Loading the new class to the memory
             */
            ClassLoader loader = new URLClassLoader(urls);
            Class<?> targetClass = loader.loadClass(packageName + "." + className);

            /**
             * Using reflection to create an object of OOPMultiple
             */
            obj = targetClass.getConstructors()[0].newInstance(controller);


        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }


        return obj;
    }

    /***
     * Removes the source file of the auto-generated OOPMultiple Class.
     */
    public void removeSourceFile() {
        controller.removeSourceFile();
    }

    /***
     * Builds the class's source file, as a string.
     * @param interfaceClass The class file of the lowest interface in the tree.
     *                       This interface is implemented by OOPMultiple.
     * @return source file's string.
     */
    private static String getClassString(Class<?> interfaceClass) {
        return packageStatementString() + importStatementString() + classHeaderString(interfaceClass) + "\n" +
                dispatcherFieldString() + "\n" + constructorFieldString() +
                interfaceMethodsString(interfaceClass) + "\n }";
    }

    // These Methods return the strings representing the different class parts.

    /***
     * Import
     * @return import statement string
     */
    private static String importStatementString() { return "import OOP.Provided.OOPMultipleException;\n";}

    /***
     * Package
     * @return package string
     */
    private static String packageStatementString() {
        return "package " + packageName + ";\n";
    }

    /***
     * Class Header
     * @param interfaceClass The class file of the lowest interface in the tree.
     *                       This interface is implemented by OOPMultiple.
     * @return class header string
     */
    private static String classHeaderString(Class<?> interfaceClass) {
        return "public class " + className + " implements " + interfaceClass.getName() + " { \n";
    }

    /***
     * Dispatcher field
     * @return dispatcher field string
     */
    private static String dispatcherFieldString() {
        return "private " + DispatchClassName + " dispatcher;\n\n";
    }

    /***
     * Constructor Field
     * @return constructor field string
     */
    private static String constructorFieldString() {
        return "public " + className + "(" + DispatchClassName + " dispatcher){\n"
                + "this.dispatcher = dispatcher;\n" + "}\n";
    }

    /***
     * Methods from Interface
     * @param interfaceClass The class file of the lowest interface in the tree.
     *                       This interface is implemented by OOPMultiple.
     * @return interface methods string.
     */
    private static String interfaceMethodsString(Class<?> interfaceClass) {

        String interfaceMethods = "";
        Set<String> writtenMethods = new HashSet<>();
        Set<Method> writtenMethodsSet = new HashSet<>();
        for (Method method : interfaceClass.getMethods()) {

            interfaceMethods += "\n \n";
            boolean shouldContinue = false;
            for(Method m : writtenMethodsSet){
                if(method.getName().equals(m.getName()) &&
                        Arrays.equals(method.getParameterTypes(),m.getParameterTypes())&&
                        method.getReturnType().equals(m.getReturnType())){
                    shouldContinue = true;
                    break;
                }
            }

            if(shouldContinue){
                continue;
            }

            writtenMethodsSet.add(method);
            writtenMethods.add(method.getName());

            interfaceMethods += "public " + method.getReturnType().getName();
            interfaceMethods += " " + method.getName() + " " + "(";

            boolean parametersAdded = false;
            int counter = 1;
            List<String> params = new ArrayList<>();
            for (Class<?> param : method.getParameterTypes()) {
                parametersAdded = true;
                String paramName = "param" + counter++;
                params.add(paramName);
                interfaceMethods += param.getName() + " " + paramName + " ,";
            }

            interfaceMethods = parametersAdded ? interfaceMethods.substring(0, interfaceMethods.length() - 2)
                    : interfaceMethods;
            interfaceMethods += ") " + throwsExceptionString() +" {";
            String paramsArray = "";
            
            if (params.size() > 0){
                paramsArray = "new Object[]{";
                for (String param : params){
                    paramsArray += param + ",";
                }
                paramsArray = paramsArray.substring(0,paramsArray.length()-1);
            }
            
            if(params.size() == 0){
                paramsArray = "null";
            }
            if (method.getReturnType().equals(Void.TYPE)) {
                interfaceMethods += "dispatcher.invoke(\"" + method.getName() + "\", " + paramsArray + ");\n}";

            } else {
                interfaceMethods += "return " + "(" + method.getReturnType().getName() +")" +
                        "dispatcher.invoke(\"" + method.getName() + "\", " + paramsArray + ");\n}";
            }
        }

        return interfaceMethods;
    }

    /***
     * Exception
     * @return exception String
     */
    private static String throwsExceptionString(){
        return "throws OOPMultipleException";
    }

    /***
     * Generator Finalizer. removes the source file, when the generator is freed.
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        removeSourceFile();
        super.finalize();
    }
}
