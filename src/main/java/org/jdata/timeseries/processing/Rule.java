package org.jdata.timeseries.processing;

/**
 * @author Hunter
 *
 */
public class Rule {

    private int id;
    private int refCount;
    private Symbol head;

    public Rule(int id) {
        this.id = id;
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
        // Grab references we might need and create new symbol
        Symbol secondInPair = firstInPair.getNext();
        Symbol prev = firstInPair.getPrev();
        Symbol next = secondInPair.getNext();
        Symbol newSymbol = new Symbol(replacement.getId());

        // Patch in new symbol
        prev.setNext(newSymbol);
        next.setPrev(newSymbol);
        newSymbol.setNext(next);
        newSymbol.setPrev(prev);

        // Cleanup
        firstInPair.setNext(null);
        firstInPair.setPrev(null);
        secondInPair.setNext(null);
        secondInPair.setPrev(null);
    }

    public void expand(Symbol ruleSymbol, Rule replacement) {

        // Grab references we might need
        Symbol prev = ruleSymbol.getPrev();
        Symbol next = ruleSymbol.getNext();
        Symbol left = replacement.head.getNext();
        Symbol right = replacement.head.getPrev();

        // Patch in rule
        prev.setNext(left);
        left.setNext(prev);
        next.setPrev(right);
        right.setPrev(next);

        // Cleanup
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

    @Override
    public String toString() {
        if (head.getNext() == head) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        Symbol cursor = head.getNext();
        while (cursor != head) {
            b.append(cursor.getValue());
            if (cursor.getNext() != head) {
                b.append(",");
            }
        }
        return b.toString();
    }
}
