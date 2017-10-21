package de.jbamberger.vplan.util;

import junit.framework.TestCase;

public class JSONParserTest extends TestCase {

    public void testPatternGenerator() {
        assertTrue("#00FF00".matches("#00[Ff][Ff]00"));
        assertTrue("#00Ff00".matches("#00[Ff][Ff]00"));
        assertTrue("#00ff00".matches("#00[Ff][Ff]00"));
        assertTrue("#00fF00".matches("#00[Ff][Ff]00"));
        assertFalse("#00F00".matches("#00[Ff][Ff]00"));
        assertFalse("#0000ff".matches("#00[Ff][Ff]00"));
    }

}