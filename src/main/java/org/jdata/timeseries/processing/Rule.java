package org.jdata.timeseries.processing;

/**
 * @author Hunter
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
        head.setContainingRule(this);
    }

    public void addReference() {
        this.refCount = refCount + 1;
    }

    public void removeReference() {
        this.refCount = refCount - 1;
    }

    public void append(Symbol newSymbol) {
        // Grab a reference to the tail
        Symbol tail = head.getPrev();

        tail.setNext(newSymbol);
        head.setPrev(newSymbol);
        newSymbol.setPrev(tail);
        newSymbol.setNext(head);

        // Assign containing rule
        newSymbol.setContainingRule(this);
    }

    public Symbol reduce(Symbol firstInPair, Rule replacement) {
        // Grab references we might need and create new symbol
        Symbol secondInPair = firstInPair.getNext();
        Symbol prev = firstInPair.getPrev();
        Symbol next = secondInPair.getNext();
        Symbol newSymbol = new Symbol(replacement.getId());

        // Assign containing rule
        newSymbol.setContainingRule(this);

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

        // Return the new symbol
        return newSymbol;
    }

    public void expand(Symbol ruleSymbol, Rule replacement) {
        // Grab references we might need
        Symbol prev = ruleSymbol.getPrev();
        Symbol next = ruleSymbol.getNext();
        Symbol left = replacement.head.getNext();
        Symbol right = replacement.head.getPrev();

        // Re-assign containing rule
        for (Symbol temp = left; temp != replacement.head; temp = temp.getNext()) {
            temp.setContainingRule(this);
        }

        // Patch in rule
        prev.setNext(left);
        left.setPrev(prev);
        next.setPrev(right);
        right.setNext(next);

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

    public Symbol getFirst() {
        return head.getNext();
    }

    public Symbol getLast() {
        return head.getPrev();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder().append(-id).append(" -> ");
        Symbol cursor = head.getNext();
        while (cursor != head) {
            b.append(cursor.toString());
            cursor = cursor.getNext();
        }
        return b.toString();
    }
}
