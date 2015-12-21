package org.jdata.timeseries.processing.sequitur;

/**
 * Created by David on 12/20/2015.
 */
class GuardSymbol extends Symbol {
    public GuardSymbol() {
       super(0);
    }

    @Override
    public boolean isGuard() {
        return true;
    }

    public String toString() {
        return "\u00FE";
    }
}
