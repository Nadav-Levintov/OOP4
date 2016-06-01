package OOP.Tests.Example;

import OOP.Provided.OOPMultipleException;


public interface I1 {

    @OOPMethod(modifier = OOPModifier.PUBLIC)
    String f() throws OOPMultipleException;
}
