package model;

import java.util.Arrays;
import java.util.Comparator;

import edu.princeton.cs.algs4.StdOut;

public class Term implements Comparable<Term> {
    private String query;
    private long weight;

    /**
     * Contstructs a term with the specified query and weight.
     */
    public Term(String query, long weight) {
        this.query = query;
        this.weight = weight;
    }

    /**
     * Returns a comparator that compares the two terms' 
     * weights in descending order.
     * 
     * @return the reverse weight order comparator
     */
    public static Comparator<Term> byReverseWeightOrder() {
        return new Comparator<Term>() {
            @Override
            public int compare(Term o1, Term o2) {
                return Long.compare(o2.weight, o1.weight);
            }
        };
    }

    /**
     * Returns a comparator that compares the two terms' 
     * queries alphabetically with only their prefix of 
     * the specified length.
     * 
     * @param r - the length of prefix to compare by
     * @return the prefix order comparator
     */
    public static Comparator<Term> byPrefixOrder(int r) {
        int rr = r;
        return new Comparator<Term>() {

            @Override
            public int compare(Term o1, Term o2) {
                if (rr < 0)
                    throw new IllegalArgumentException();
                else if (rr > o1.query.length())
                    return -1;
                else if (rr > o2.query.length())
                    return 1;
                return o1.query.substring(0, rr).compareTo(o2.query.substring(0, rr));
            }
        };
    }

    /**
     * Compares the queries alphabetically.
     */
    public int compareTo(Term that) {
        return query.compareTo(that.query);
    }
    
    public String getQuery() {return query;}

    public String toString() {
        return weight + "\t" + query;
    }

    public static void main(String[] args) {
        Term[] a = {new Term("hi", 40), new Term("yo", 50), new Term("yes", 20), new Term("no", 200)};
        StdOut.println("Unsorted terms:    " + Arrays.toString(a));
        Arrays.sort(a, Term.byReverseWeightOrder());
        StdOut.println("By reverse weight: " + Arrays.toString(a));
        Arrays.sort(a, Term.byPrefixOrder(2));
        StdOut.println("By prefix (2):     " + Arrays.toString(a));
    }
}
