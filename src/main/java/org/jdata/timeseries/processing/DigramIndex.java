package org.jdata.timeseries.processing;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by David on 12/6/2015.
 */
public class DigramIndex {
    private HashMap<Digram, Symbol> theIndex = new HashMap<>();

    public boolean existsInRules(Symbol first) {
        return theIndex.containsKey(new Digram(first));
    }

    public Symbol findInRules(Symbol first) {
        return theIndex.get(new Digram(first));
    }

    public boolean addToIndex(Symbol first) {
        if (existsInRules(first)) {
            return false;
        }
        if (first.isGuard() || first.getNext().isGuard()) {
            return false;
        }
        theIndex.put(new Digram(first), first);
        return true;
    }

    public boolean removeFromIndex(Symbol first) {
        if (!existsInRules(first)) {
            return false;
        }
        theIndex.remove(new Digram(first));
        return true;
    }

    private class Digram {
        private Symbol first;

        public Digram(Symbol first) {
            this.first = first;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Digram)) {
                return false;
            }
            Digram that = (Digram) other;

            return Objects.equals(this.first, that.first) &&
                    Objects.equals(this.first.getNext(), that.first.getNext());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.first, this.first.getNext());
        }
    }
}
