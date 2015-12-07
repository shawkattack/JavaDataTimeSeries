package org.jdata.timeseries.test;

import org.jdata.timeseries.processing.Rule;
import org.jdata.timeseries.processing.Symbol;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SequiturTest {
    @Before
    public void setup() {
    }

    @Test
    public void testSymbolEquals() {
        Symbol s1 = new Symbol(67);
        Symbol s2 = new Symbol(67);
        Symbol s3 = new Symbol(-1);
        Assert.assertEquals(s1, s2);
        Assert.assertNotEquals(s1, s3);
        Assert.assertNotEquals(s2, s3);
    }

    @Test
    public void testRuleAppend() {
        Rule r = new Rule(-1);
        Assert.assertEquals("toString failed","",r.toString());
        r.append(new Symbol(1));
        Assert.assertEquals("first append failed","1",r.toString());
        r.append(new Symbol(2));
        Assert.assertEquals("second append failed","1,2",r.toString());
        r.append(new Symbol(3));
        Assert.assertEquals("final append failed","1,2,3",r.toString());
    }

    @After
    public void cleanup() {
    }
}
