package org.jdata.timeseries.processing;

import java.util.*;

public class Sequitur {
    private int numRules;
    private DigramIndex digramIndex;
    private Rule startRule;
    private HashMap<Integer, Rule> ruleIndex;

    public Sequitur() {
        reset();
    }

    public Collection<Rule> generateRuleSet(String input) {
        reset();

        append(input);

        return getCurrentRules();
    }

    public void reset() {
        numRules = 1;
        startRule = new Rule(-1);
        digramIndex = new DigramIndex();
        ruleIndex = new HashMap<>();
        ruleIndex.put(-1, startRule);
    }

    public void append(String input) {
        for (int i = 0; i < input.length(); i++) {
            append(input.charAt(i));
        }
    }

    public void append(char input) {
        Symbol newChar = new Symbol(input);

        // Append new input symbol to S
        startRule.append(newChar);
        linkToPrevious(newChar);
    }

    public Collection<Rule> getCurrentRules() {
        // Get the results, and sort them descending by ID (actually increasing
        // by creation order)
        List<Rule> result = new ArrayList<>(ruleIndex.values());
        Collections.sort(result, (Rule r1, Rule r2) -> r2.getId() - r1.getId());

        return result;
    }

    /**
     * Performs all the operations that need to occur when we make a link
     * between two symbols. Assumes the new symbol has already been appropriately
     * added to a rule. If symbol or its previous is null/guard, this does nothing.
     *
     * @param symbol the new symbol we've added
     */
    private void linkToPrevious(Symbol symbol) {
        // If a symbol gets disconnected, a null
        if (symbol == null) {
            return;
        }
        Symbol newDigram = symbol.getPrev();

        // Check if we made a digram at all
        if (newDigram != null && !newDigram.isGuard() && !symbol.isGuard()) {
            // If the new digram is repeated elsewhere
            if (digramIndex.existsInRules(newDigram)) {
                // Get the existing digram's first char
                Symbol existingDigram = digramIndex.findInRules(newDigram);

                // Only do something if no overlap occurs
                if (!overlap(existingDigram, symbol)) {
                    // If the other occurrence is already a rule, replace with
                    // the symbol for that rule
                    if (isRule(existingDigram)) {
                        reduce(newDigram, existingDigram.getContainingRule());
                    }
                    // Otherwise, create a new rule and replace both occurrences
                    else {
                        Rule newRule = createNewRule(newDigram);

                        digramIndex.removeFromIndex(newDigram);

                        reduce(newDigram, newRule);
                        reduce(existingDigram, newRule);

                        digramIndex.addToIndex(newRule.getFirst());
                    }
                }
            }
            // If the digram does NOT appear elsewhere
            else {
                // Add the digram to the index
                digramIndex.addToIndex(newDigram);
            }
        }
    }

    /**
     * Replaces a digram with a rule Handles reference counting, and digram
     * linking/unlinking
     *
     * @param digram
     * @param rule
     */
    private void reduce(Symbol digram, Rule rule) {
        // Remove broken digrams from the digram index
        digramIndex.removeFromIndex(digram.getPrev());
        digramIndex.removeFromIndex(digram.getNext());

        // Decrement rule references for any non-terminal symbols being replaced
        Rule firstRule = ruleIndex.get(digram.getValue());
        Rule secondRule = ruleIndex.get(digram.getNext().getValue());
        if (firstRule != null) {
            firstRule.removeReference();
        }
        if (secondRule != null) {
            secondRule.removeReference();
        }

        // Perform the reduce operation and increment the reference count
        Symbol newSymbol = digram.getContainingRule().reduce(digram, rule);
        rule.addReference();

        // If we eliminate all references to a rule EXCEPT the one in the newly formed rule,
        // we should remove the rule and expand its symbol
        if (firstRule != null && firstRule.getRefCount() == 1) {
            expand(rule.getFirst(), firstRule);
        }
        if (secondRule != null && secondRule.getRefCount() == 1) {
            expand(rule.getLast(), secondRule);
        }

        // Add the newly created digrams, and perform the associated operations
        linkToPrevious(newSymbol);
        linkToPrevious(newSymbol.getNext());
    }

    /**
     * Expands a non-terminal symbol into the right side of its rule
     *
     * @param replacedSymbol  the non-terminal symbol to be replaced
     * @param replacementRule the rule to remove/replace the symbol
     */
    private void expand(Symbol replacedSymbol, Rule replacementRule) {
        // Get these before they're disconnected by expand
        Symbol firstOfNewRule = replacementRule.getFirst();
        Symbol lastOfNewRule = replacementRule.getLast();

        // Perform the list operation and reference counting
        replacedSymbol.getContainingRule().expand(replacedSymbol,
                replacementRule);
        ruleIndex.remove(replacementRule.getId());

        // Link the newly formed digrams
        linkToPrevious(firstOfNewRule);
        linkToPrevious(lastOfNewRule.getNext());

    }

    /**
     * Creates a new rule from a digram
     *
     * @param digram the digram to extract a rule from
     * @return a rule whose right side is the provided digram
     */
    private Rule createNewRule(Symbol digram) {
        // Book keeping and rule creation
        numRules += 1;
        Rule newRule = new Rule(-numRules);
        int firstValue = digram.getValue();
        int secondValue = digram.getNext().getValue();
        newRule.append(new Symbol(firstValue));
        newRule.append(new Symbol(secondValue));
        ruleIndex.put(-numRules, newRule);

        // Book keeping on non-terminal symbols
        if (ruleIndex.containsKey(firstValue)) {
            ruleIndex.get(firstValue).addReference();
        }
        if (ruleIndex.containsKey(secondValue)) {
            ruleIndex.get(secondValue).addReference();
        }

        return newRule;
    }

    /**
     * Determines if a digram is a complete rule
     *
     * @param digram the first symbol in the digram
     * @return true if a rule's right side is entirely the digram
     */
    private boolean isRule(Symbol digram) {
        return digram.getPrev().isGuard()
                && digram.getNext().getNext().isGuard();
    }

    /**
     * Determines if the digram STARTING with symbol a overlaps with the diagram
     * ENDING with symbol b
     *
     * @param a the start of the first digram
     * @param b the end of the second digram
     * @return true if the symbol after a is the exact symbol before b
     */
    private boolean overlap(Symbol a, Symbol b) {
        return a.getNext() == b.getPrev();
    }
}