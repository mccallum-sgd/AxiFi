package model;

import java.util.Arrays;
import java.util.Comparator;

public class BinarySearchDeluxe {

    /**
     * Searches the specified array for the specified object using the binary search algorithm.
     * The array must be sorted into ascending order according to the specified comparator of 
     * its elements prior to making this call. If it is not sorted, the results are undefined.
     * If the array contains multiple elements equal to the specified object, this method 
     * guarantees that the first occurence will be found. This method runs in log(n) time for
     * a "random access" array (which provides near-constant-time positional access).
     * 
     * @param a - the array to search
     * @param key - the key to search for
     * @param comparator - the comparator used to sort the array
     * @return - the index of the first matching element
     */
    public static <Key> int firstIndexOf(Key[] a, Key key, Comparator<Key> comparator) {
        if (a == null || key == null || comparator == null)
            throw new NullPointerException();
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int x = comparator.compare(key, a[mid]);
            if ((mid == 0 || comparator.compare(key, a[mid - 1]) > 0) && x == 0)
                return mid;
            else if (x > 0)
                lo = mid + 1;
            else
                hi = mid - 1;
        }
        assert !Arrays.asList(a).contains(key) : Arrays.asList(a).indexOf(key);
        return -1;
    }

    /**
     * Searches the specified array for the specified object using the binary search algorithm.
     * The array must be sorted into ascending order according to the specified comparator of 
     * its elements prior to making this call. If it is not sorted, the results are undefined.
     * If the array contains multiple elements equal to the specified object, this method 
     * guarantees that the last occurence will be found. This method runs in log(n) time for
     * a "random access" array (which provides near-constant-time positional access).
     * 
     * @param a - the array to search
     * @param key - the key to search for
     * @param comparator - the comparator used to sort the array
     * @return - the index of the last matching element
     */
    public static <Key> int lastIndexOf(Key[] a, Key key, Comparator<Key> comparator) {
        if (a == null || key == null || comparator == null)
            throw new NullPointerException();
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int x = comparator.compare(key, a[mid]);
            if ((mid == a.length - 1 || comparator.compare(key, a[mid + 1]) < 0) && x == 0)
                return mid;
            else if (x < 0)
                hi = mid - 1;
            else
                lo = mid + 1;
        }
        assert !Arrays.asList(a).contains(key) : Arrays.asList(a).indexOf(key);
        return -1;
    }

    // Unit testing
    public static void main(String[] args) {
        String[] a = {"1", "2", "3", "3", "3", "3", "3", "4", "4", "4", "5", "6", "6", "6"};
        String key = "4";
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String arg0, String arg1) {
                return arg0.compareTo(arg1);
            }
        };
        System.out.println("Array: " + Arrays.toString(a) + "\n" + "Search for: " + key + "\n" + "First index: "
                + firstIndexOf(a, key, comparator) + "\n" + "Last index: " + lastIndexOf(a, key, comparator));
    }

}
