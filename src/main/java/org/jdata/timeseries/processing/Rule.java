package org.jdata.timeseries.processing;

/**
 * @author Hunter
 *
 */
public class Rule {

    private int id;
    private int refCount;
    private Symbol head;

    public Rule(int aId) {

        this.id = aId;
        refCount = 0;
        head = new Symbol(0);
        head.setNext(head);                 
        head.setPrev(head);

    }

    public void addReference() {

        this.refCount = refCount + 1;

    }

    public void removeReference() {

        this.refCount = refCount - 1;

    }

    public void append(Symbol last) {

        last = head.getPrev();

    }

    public void reduce(Symbol firstInPair, Rule replacement) {

        Symbol sip = firstInPair.getNext();

        Symbol prev = firstInPair.getPrev();

        Symbol next = sip.getNext();

        Symbol newSymbol = new Symbol(replacement.getId());

        prev.setNext(newSymbol);

        next.setPrev(newSymbol);

        newSymbol.setNext(next);

        newSymbol.setPrev(prev);

        firstInPair.setNext(null);

        firstInPair.setPrev(null);

        sip.setNext(null);

        sip.setPrev(null);

    }

    public void expand(Symbol ruleSymbol, Rule replacement) {

        Symbol prev = ruleSymbol.getPrev();

        Symbol next = ruleSymbol.getNext();

        Symbol left = replacement.head.getNext();

        Symbol right = replacement.head.getPrev();

        prev.setNext(left);

        left.setNext(prev);

        next.setPrev(right);

        right.setPrev(next);

        ruleSymbol.setNext(null);

        ruleSymbol.setPrev(null);

        replacement.head.setNext(null);

        replacement.head.setPrev(null);

    }

    public int getRefCount() {
        return refCount;
    }

    public int getId() {
        return id;
    }

}
