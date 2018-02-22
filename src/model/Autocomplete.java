package model;
import java.util.Arrays;

public class Autocomplete {

    private Term[] terms;

    /**
     * Constructs an Autocomplete data structure containing the specified terms.
     * @param terms - the terms to store
     * @throws NullPointerException - if terms is null or any element of terms is null
     */
    public Autocomplete(Term[] terms) {
        if (terms == null)
            throw new NullPointerException();
        for (Term t : terms)
            if (t == null)
                throw new NullPointerException();
        this.terms = terms;
        Arrays.sort(this.terms);
    }

    /**
     * Returns an array of all terms matching the given prefix. 
     * This method runs in log N + M log M worst-case time, where
     * N is the number of terms and M is the number of matching 
     * terms.
     * 
     * @param prefix - the prefix to match
     * @throws NullPointerException - if prefix is null
     * @return the array of matching terms
     */
    public Term[] allMatches(String prefix) {
        if (prefix == null)
            throw new NullPointerException();
        Term[] matches;
        int first = BinarySearchDeluxe.firstIndexOf(terms, new Term(prefix, 0), Term.byPrefixOrder(prefix.length()));
        int numMatches = numberOfMatches(prefix);
        if (numMatches <= 0 || first == -1)
            return matches = new Term[] {};
        matches = new Term[numMatches];
        matches[0] = terms[first];
        for (int i = 1; i < matches.length; i++)
            matches[i] = terms[++first];
        Arrays.sort(matches, Term.byReverseWeightOrder());
        return matches;
    }
    
    /**
     * Returns the number of terms matching the given prefix.
     * 
     * @param prefix - the prefix to match
     * @throws NullPointerException - if prefix is null
     * @return the number of matching terms
     */
    public int numberOfMatches(String prefix) {
        if (prefix == null)
            throw new NullPointerException();
        Term key = new Term(prefix, 0);
        int numMatches = BinarySearchDeluxe.lastIndexOf(terms, key, Term.byPrefixOrder(prefix.length())) + 1
                - BinarySearchDeluxe.firstIndexOf(terms, key, Term.byPrefixOrder(prefix.length()));
        return numMatches;
    }

}
