package OOP.Provided;

import java.lang.reflect.Method;

public class OOPInherentAmbiguity extends OOPMultipleException{

    private Class<?> faultyClass;
    private Method faultyMethod;
    private Class<?> interfaceClass;

    public OOPInherentAmbiguity(Class<?> interfaceClass, Class<?> faultyClass, Method faultyMethod) {
        this.interfaceClass = interfaceClass;
        this.faultyClass = faultyClass;
        this.faultyMethod = faultyMethod;
    }
    private String changeInterfaceNameToClassName(String interfaceName){
        int indexOfI = interfaceName.lastIndexOf('I');
        return interfaceName.substring(0,indexOfI) + "C" + interfaceName.substring(indexOfI+1);
    }

    @Override
    public String getMessage() {
        return changeInterfaceNameToClassName(interfaceClass.getName()) + " Could not be generated \n" +
        "because of Inherent Ambiguity, caused by inheriting method: " + faultyMethod.getName() + "\n" +
                "which is first defined in : " + changeInterfaceNameToClassName(faultyClass.getName());
    }
}
