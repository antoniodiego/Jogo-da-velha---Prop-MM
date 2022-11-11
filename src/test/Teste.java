/*
 * Teste.java
 * JMUnit based test
 *
 * Created on 27/11/2018, 18:30:03
 */
package test;

import jmunit.framework.cldc10.*;

/**
 * @author Ant?nio Diego <antonio.diego at antoniodiego.org>
 */
public class Teste extends TestCase {
    
    public Teste() {
        //The first parameter of inherited constructor is the number of test cases
        super(0, "Teste");
    }    
    
    public void test(int testNumber) throws Throwable {
    }    
}
