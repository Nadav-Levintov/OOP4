package OOP.Solution;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by Nadav on 01-Jun-16.
 */
@Target(ElementType.METHOD)
public @interface OOPMethod {
    OOPModifier modifier() default OOPModifier.DEFAULT;
}
