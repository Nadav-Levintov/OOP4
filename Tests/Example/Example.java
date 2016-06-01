package OOP.Tests.Example;

import OOP.Provided.OOPMultipleClassGenerator;
import OOP.Provided.OOPMultipleException;

public class Example {

    public static void main(String[] args) {

        OOPMultipleClassGenerator generator = new OOPMultipleClassGenerator();

        try {
            I3 obj = (I3) generator.generateMultipleClass(I3.class);
            System.out.println(obj.f());
            obj.g();

        } catch (OOPMultipleException e) {
            e.printStackTrace();
        } finally {
            generator.removeSourceFile();
        }
    }
}
