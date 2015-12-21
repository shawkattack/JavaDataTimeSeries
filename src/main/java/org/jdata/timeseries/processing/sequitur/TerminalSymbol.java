package org.jdata.timeseries.processing.sequitur;

/**
 * Created by David on 12/20/2015.
 */
class TerminalSymbol extends Symbol {
    public TerminalSymbol(char value) {
        super(value);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    public String toString() {
        return String.valueOf((char) getValue());
    }
}
