package OOP.Provided;

import java.lang.reflect.Method;

public class OOPBadClass extends OOPMultipleException {

    private Method corrupted;

    /***
     * Builds an exception with the bad method.
     * @param corrupted the method object of the method that isn't written appropriately.
     */
    public OOPBadClass(Method corrupted){this.corrupted = corrupted;}

    @Override
    public String getMessage(){
        Class<?> declaringClass = corrupted.getDeclaringClass();
        return super.getMessage() +
                declaringClass.getName() +
                " : " + corrupted.getName() + " is Corrupted!";
    }
}
