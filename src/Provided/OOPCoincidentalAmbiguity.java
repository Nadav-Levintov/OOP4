package OOP.Provided;

import javafx.util.Pair;
import java.lang.reflect.Method;
import java.util.Collection;

public class OOPCoincidentalAmbiguity extends OOPMultipleException {

    private Collection<Pair<Class<?>,Method>> candidates;

    /***
     * Builds an exception with the invocation candidates.
     * @param candidates A collection of pairs of (class, Method).
     *                   method - one candidate of invocation
     *                   class  - the class file of the interface that the method was declared in.
     * */
    public OOPCoincidentalAmbiguity(Collection<Pair<Class<?>,Method>> candidates) {
        this.candidates = candidates;
    }

    @Override
    public String getMessage() {
        String message = "Coincidental Ambiguous Method Call. candidates are : \n";
        for (Pair<Class<?>,Method> pair: candidates) {
            message += pair.getKey().getName() + " : " + pair.getValue().getName() + "\n";
        }
        return message;
    }
}
