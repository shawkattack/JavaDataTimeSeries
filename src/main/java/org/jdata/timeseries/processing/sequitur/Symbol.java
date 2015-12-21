/**
 *
 */
package org.jdata.timeseries.processing.sequitur;

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

    void setPrev(Symbol prev) {
        this.prev = prev;
    }

    void setNext(Symbol next) {
        this.next = next;
    }

    void setContainingRule(Rule containingRule) {
        this.containingRule = containingRule;
    }

    public Symbol getPrev() {
        return prev;
    }

    public Symbol getNext() {
        return next;
    }

    public boolean isGuard() {
        return false;
    }

    public boolean isTerminal() {
        return false;
    }

    public boolean isNonTerminal() {
        return false;
    }

    public Rule getContainingRule() {
        return containingRule;
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
}
