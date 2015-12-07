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

    @Test
    public void testRuleReferenceCounting() {
        Rule r = new Rule(-1);
        Assert.assertEquals("rule initialization failed",0,r.getRefCount());
        r.addReference();
        Assert.assertEquals("first increment failed",1,r.getRefCount());
        r.addReference();
        Assert.assertEquals("second increment failed",2,r.getRefCount());
        r.removeReference();
        Assert.assertEquals("first decrement failed",1,r.getRefCount());
        r.removeReference();
        Assert.assertEquals("second decrement failed",0,r.getRefCount());
    }

    @Test
    public void testRuleReduce() {
        Symbol a = new Symbol(1);
        Symbol b = new Symbol(2);
        Symbol c = new Symbol(3);
        Rule r1 = new Rule(-1);
        r1.append(a);
        r1.append(b);
        r1.append(c);
        Assert.assertEquals("append failed","1,2,3",r1.toString());
        Rule r2 = new Rule(-2);
        r1.reduce(b,r2);
        Symbol A = a.getNext();
        Assert.assertEquals("symbol created with incorrect value",-2,A.getValue());
        Assert.assertEquals("first reduce failed","1,-2",r1.toString());
        Rule r3 = new Rule(-3);
        r1.reduce(a,r3);
        Assert.assertEquals("second reduce failed","-3",r1.toString());
    }

    @Test
    public void testRuleExpand() {
        Symbol A = new Symbol(-2);
        Symbol B = new Symbol(-3);
        Symbol C = new Symbol(-4);
        Symbol a1 = new Symbol(1);
        Symbol a2 = new Symbol(1);
        Symbol b2 = new Symbol(2);
        Symbol c2 = new Symbol(3);
        Rule r1 = new Rule(-1);
        r1.append(A);
        Assert.assertEquals("append failed","-2",r1.toString());
        Rule r2 = new Rule(-2);
        r2.append(B);
        r1.expand(A, r2);
        Assert.assertEquals("first expand failed","-3",r1.toString());
        Rule r3 = new Rule(-3);
        r3.append(a1);
        r3.append(C);
        r1.expand(B, r3);
        Assert.assertEquals("second expand failed","1,-4",r1.toString());
        Rule r4 = new Rule(-4);
        r4.append(a2);
        r4.append(b2);
        r4.append(c2);
        r1.expand(C, r4);
        Assert.assertEquals("third expand failed","1,1,2,3",r1.toString());
    }

    @After
    public void cleanup() {
    }
}
