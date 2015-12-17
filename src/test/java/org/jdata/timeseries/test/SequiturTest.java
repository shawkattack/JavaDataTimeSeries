package org.jdata.timeseries.test;

import org.jdata.timeseries.processing.Rule;
import org.jdata.timeseries.processing.Sequitur;
import org.jdata.timeseries.processing.Symbol;
import org.junit.*;

import java.util.Collection;

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
        Assert.assertEquals("toString failed", "1 -> ", r.toString());
        r.append(new Symbol('a'));
        Assert.assertEquals("first append failed", "1 -> a", r.toString());
        r.append(new Symbol('b'));
        Assert.assertEquals("second append failed", "1 -> ab", r.toString());
        r.append(new Symbol('c'));
        Assert.assertEquals("final append failed", "1 -> abc", r.toString());
    }

    @Test
    public void testRuleReferenceCounting() {
        Rule r = new Rule(-1);
        Assert.assertEquals("rule initialization failed", 0, r.getRefCount());
        r.addReference();
        Assert.assertEquals("first increment failed", 1, r.getRefCount());
        r.addReference();
        Assert.assertEquals("second increment failed", 2, r.getRefCount());
        r.removeReference();
        Assert.assertEquals("first decrement failed", 1, r.getRefCount());
        r.removeReference();
        Assert.assertEquals("second decrement failed", 0, r.getRefCount());
    }

    @Test
    public void testRuleReduce() {
        Symbol a = new Symbol('a');
        Symbol b = new Symbol('b');
        Symbol c = new Symbol('c');
        Rule r1 = new Rule(-1);
        r1.append(a);
        r1.append(b);
        r1.append(c);
        Assume.assumeTrue("append failed", "1 -> abc".equals(r1.toString()));
        Rule r2 = new Rule(-2);
        r1.reduce(b, r2);
        Symbol A = a.getNext();
        Assert.assertEquals("symbol created with incorrect value", -2, A.getValue());
        Assert.assertEquals("first reduce failed", "1 -> a{2}", r1.toString());
        Rule r3 = new Rule(-3);
        r1.reduce(a, r3);
        Assert.assertEquals("second reduce failed", "1 -> {3}", r1.toString());
    }

    @Test
    public void testRuleExpand() {
        Symbol A = new Symbol(-2);
        Symbol B = new Symbol(-3);
        Symbol C = new Symbol(-4);
        Symbol a1 = new Symbol('a');
        Symbol a2 = new Symbol('a');
        Symbol b2 = new Symbol('b');
        Symbol c2 = new Symbol('c');
        Rule r1 = new Rule(-1);
        r1.append(A);
        Assume.assumeTrue("append failed", "1 -> {2}".equals(r1.toString()));
        Rule r2 = new Rule(-2);
        r2.append(B);
        r1.expand(A, r2);
        Assert.assertEquals("first expand failed", "1 -> {3}", r1.toString());
        Rule r3 = new Rule(-3);
        r3.append(a1);
        r3.append(C);
        r1.expand(B, r3);
        Assert.assertEquals("second expand failed", "1 -> a{4}", r1.toString());
        Rule r4 = new Rule(-4);
        r4.append(a2);
        r4.append(b2);
        r4.append(c2);
        r1.expand(C, r4);
        Assert.assertEquals("third expand failed", "1 -> aabc", r1.toString());
    }

    @Test
    public void testSequiturAlgorithm() {
        Sequitur sequitur = new Sequitur();
        String nullResult = ruleSetToString(sequitur.generateRuleSet(""));
        Assert.assertEquals("Null argument failed", "1 -> ,", nullResult);

        String charResult = ruleSetToString(sequitur.generateRuleSet("a"));
        Assert.assertEquals("Single char failed", "1 -> a,", charResult);

        String digramResult = ruleSetToString(sequitur.generateRuleSet("ab"));
        Assert.assertEquals("Digram failed", "1 -> ab,", digramResult);

        String trigramResult = ruleSetToString(sequitur.generateRuleSet("abc"));
        Assert.assertEquals("Normal trigram failed", "1 -> abc,", trigramResult);

        String doubleTrigramResult = ruleSetToString(sequitur.generateRuleSet("abb"));
        Assert.assertEquals("Trigram with duplicate failed", "1 -> abb,", doubleTrigramResult);

        String doubleDigramResult = ruleSetToString(sequitur.generateRuleSet("abab"));
        Assert.assertEquals("Duplicate digram failed", "1 -> {2}{2},2 -> ab,", doubleDigramResult);

        String exampleTest1Result = ruleSetToString(sequitur.generateRuleSet("abcdbc"));
        Assert.assertEquals("\"abcdbc\" failed", "1 -> a{2}d{2},2 -> bc,", exampleTest1Result);

        String exampleTest2Result = ruleSetToString(sequitur.generateRuleSet("abcdbcabc"));
        Assert.assertEquals("\"abcdbcabc\" failed", "1 -> {3}d{2}{3},2 -> bc,3 -> a{2},", exampleTest2Result);

        String exampleTest3Result = ruleSetToString(sequitur.generateRuleSet("abcdbcabcd"));
        Assert.assertEquals("\"abcdbcabc\" failed", "1 -> {4}{2}{4},2 -> bc,4 -> a{2}d,", exampleTest3Result);
    }

    public String ruleSetToString(Collection<Rule> rules) {
        StringBuilder sb = new StringBuilder();
        for (Rule r : rules) {
            sb.append(r.toString());
            sb.append(',');
        }

        return sb.toString();
    }

    @After
    public void cleanup() {
    }
}
