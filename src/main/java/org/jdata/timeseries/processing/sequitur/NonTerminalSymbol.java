package org.jdata.timeseries.processing.sequitur;

/**
 * Created by David on 12/20/2015.
 */
class NonTerminalSymbol extends Symbol {
    private Rule associatedRule;

    public NonTerminalSymbol(Rule rule) {
        super(rule.getId());
        this.associatedRule = rule;
    }

    @Override
    public boolean isNonTerminal() {
        return true;
    }

    public Rule getAssociatedRule() {
        return associatedRule;
    }

    public String toString() {
        return "{" + (-getValue()) + "}";
    }
}
