package org.jdata.timeseries.processing;

import java.util.*;

public class Sequitur {
    private int numRules;
    private DigramIndex digramIndex;
    private Rule startRule;
    private HashMap<Integer, Rule> ruleIndex;

    public Sequitur() {
        numRules = 1;
        startRule = new Rule(-1);
        digramIndex = new DigramIndex();
        ruleIndex = new HashMap<>();
    }

    public Collection<Rule> generateRuleSet(String input) {
        digramIndex = new DigramIndex();
        ruleIndex = new HashMap<>();
        startRule = new Rule(-1);
        ruleIndex.put(-1, startRule);
        numRules = 1;

        for (int i = 0; i < input.length(); i++) {
            Symbol newChar = new Symbol(input.charAt(i));

            // Append new input symbol to S
            startRule.append(newChar);
            linkToPrevious(newChar);
        }

        // Get the results, and sort them descending by ID (actually increasing
        // by creation order)
        List<Rule> result = new ArrayList<>(ruleIndex.values());
        Collections.sort(result, (Rule r1, Rule r2) -> r1.getId() - r2.getId());
        return result;
    }

    /**
     * Performs all the operations that need to occur when we make a link
     * between two symbols Assumes the new symbol has already been appropriately
     * added to a rule
     * 
     * @param newSymbol
     *            the new symbol we've added
     */
    private void linkToPrevious(Symbol newSymbol) {
        Symbol newDigram = newSymbol.getPrev();

        // Check if we made a digram at all
        if (!newDigram.isGuard() && !newSymbol.isGuard()) {
            // If the new digram is repeated elsewhere
            if (digramIndex.existsInRules(newDigram)) {
                // Get the existing digram's first char
                Symbol existingDigram = digramIndex.findInRules(newDigram);

                // Only do something if no overlap occurs
                if (overlap(existingDigram, newSymbol)) {
                    // If the other occurrence is already a rule, replace with
                    // the symbol for that rule
                    if (isRule(existingDigram)) {
                        reduce(newDigram, existingDigram.getContainingRule());
                    }
                    // Otherwise, create a new rule and replace both occurrences
                    else {
                        Rule newRule = createNewRule(newDigram);

                        reduce(newDigram, newRule);
                        reduce(existingDigram, newRule);
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

        if (firstRule != null && firstRule.getRefCount() == 1) {

            expand(rule.getFirst(), secondRule);

        }
        if (secondRule != null && secondRule.getRefCount() == 1) {
            expand(rule.getLast(), secondRule);
        }

        // Add the newly created digrams, and perform the associated operations
        linkToPrevious(newSymbol);
        linkToPrevious(newSymbol.getNext());
    }

    private void expand(Symbol replacedSymbol, Rule replacementRule) {

        Symbol firstOfNewRule = replacementRule.getFirst();
        Symbol lastOfNewRule = replacementRule.getLast();

        replacedSymbol.getContainingRule().expand(replacedSymbol,
                replacementRule);
        ruleIndex.remove(replacementRule.getId());

        linkToPrevious(firstOfNewRule);
        linkToPrevious(lastOfNewRule.getNext());

    }

    /**
     * Creates a new rule from a digram
     * 
     * @param digram
     *            the digram to extract a rule from
     * @return a rule whose right side is the provided digram
     */
    private Rule createNewRule(Symbol digram) {
        numRules += 1;
        Rule newRule = new Rule(-numRules);
        int firstValue = digram.getValue();
        int secondValue = digram.getNext().getValue();
        newRule.append(new Symbol(firstValue));
        newRule.append(new Symbol(secondValue));
        ruleIndex.put(-numRules, newRule);

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
     * @param digram
     *            the first symbol in the digram
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
     * @param a
     *            the start of the first digram
     * @param b
     *            the end of the second digram
     * @return true if the symbol after a is the exact symbol before b
     */
    private boolean overlap(Symbol a, Symbol b) {
        return a.getNext() == b.getPrev();
    }
}