/**
 *
 */
package org.jdata.timeseries.processing;

import java.util.Objects;

/**
 * @author Hunter
 */
public class Symbol {

    private int value;

    private Symbol prev;
    private Symbol next;

    private Rule containingRule;

    public Symbol(int value) {

        this.value = value;

        this.prev = null;
        this.next = null;

        this.containingRule = null;
    }

    public int getValue() {
        return value;
    }

    public void setPrev(Symbol prev) {
        this.prev = prev;
    }

    public void setNext(Symbol next) {
        this.next = next;
    }

    public void setContainingRule(Rule containingRule) {
        this.containingRule = containingRule;
    }

    public Symbol getPrev() {
        return prev;
    }

    public Symbol getNext() {
        return next;
    }

    public Rule getContainingRule() {
        return containingRule;
    }

    boolean isGuard() {
        return value == 0;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Symbol)) {
            return false;
        }
        Symbol that = (Symbol) other;

        return this.value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    @Override
    public String toString() {
        if (value > 0 && value <= Character.MAX_VALUE) {
            return String.valueOf((char) value);
        } else {
            return "{" + (-value) + "}";
        }
    }
}
