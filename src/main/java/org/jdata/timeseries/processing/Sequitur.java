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

        // Get the results, and sort them descending by ID (actually increasing by creation order)
        List<Rule> result = new ArrayList<>(ruleIndex.values());
        Collections.sort(result, (Rule r1, Rule r2) -> r1.getId() - r2.getId());
        return result;
    }

    /**
     * Performs all the operations that need to occur when we make a link between two symbols
     * Assumes the new symbol has already been appropriately added to a rule
     * @param newSymbol the new symbol we've added
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
                    // If the other occurrence is already a rule, replace with the symbol for that rule
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
     * Replaces a digram with a rule
     * Handles reference counting, and digram linking/unlinking
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

        // Perform any rule expansions before linking newly formed digrams
        // If these replaced rules only have one reference, that reference will be in the new rule
        // These replaced rules cannot contain the symbol for the new rule so this is SAFE
        /*
         *  TODO #1
         *  There's something missing from these if statements:
         *  I never formed new digram links!
         *  When you expand a rule, you are replacing a non-terminal Symbol (firstReplaced/secondReplaced)
         *  with the right side of a Rule. This creates two new links that I didn't account for:
         *
         *  1) Between the Symbol BEFORE the replaced Symbol and the first Symbol in the replacing Rule
         *  2) Between the last Symbol in the replacing Rule and the Symbol AFTER the replaced Symbol
         *
         *  Since I've already provided a function called linkToPrevious, you should call this TWICE in EACH if statement.
         *  The first linkToPrevious can be called on the FIRST Symbol of firstRule/secondRule.
         *  The second linkToPrevious can be called on the Symbol after firstReplaced/secondReplaced.
         *  However, you should make these calls AFTER rule.expand has been called.
         *  I added getFirst and getLast methods to Rule, so you can get the first symbol with first/secondRule.getFirst()
         *  and the symbol after the replacement with first/secondRule.getLast().getNext().
         *
         *  After you finish writing this code, create a new branch on our github repo called bugfix/rule_expansion,
         *  commit the code, and submit a pull request. I will comment on your code and you should make appropriate
         *  edits and sync the code again when you think you are done.
         *  DO NOT merge or close the pull request.
         */
        /*
         *  TODO #2
         *  When you have successfully finished TODO #1
         *  Notice that the code in these if statements is almost exactly the same.
         *  The only things that are different is that firstRule is swapped with secondRule, and firstReplaced is
         *  swapped with secondRule.
         *
         *  Try to make a function (in this class) called expand that takes the following arguments:
         *  1) Rule toExpand            (rule)
         *  2) Symbol replacedSymbol    (firstReplaced/secondReplaced)
         *  3) Rule removedRule         (firstRule/secondRule)
         *
         *  and replace the code inside each of the ifs with a single call to this function.
         *  Feel free to rename the arguments with more fitting names.
         *
         *  When you are done, sync the code. I will comment on your changes, and you should make appropriate edits like
         *  before.
         *
         *  When everything looks good, I will let you know, and you should merge the pull request.
         */
        if (firstRule != null && firstRule.getRefCount() == 1) {
            Symbol firstReplaced = rule.getFirst();
            rule.expand(firstReplaced, firstRule);
            ruleIndex.remove(firstRule.getId());
        }
        if (secondRule != null && secondRule.getRefCount() == 1) {
            Symbol secondReplaced = rule.getLast();
            rule.expand(secondReplaced, firstRule);
            ruleIndex.remove(secondRule.getId());
        }

        // Add the newly created digrams, and perform the associated operations
        linkToPrevious(newSymbol);
        linkToPrevious(newSymbol.getNext());
    }

    /**
     * Creates a new rule from a digram
     * @param digram the digram to extract a rule from
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
     * @param digram the first symbol in the digram
     * @return true if a rule's right side is entirely the digram
     */
    private boolean isRule(Symbol digram) {
        return digram.getPrev().isGuard() &&
                digram.getNext().getNext().isGuard();
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