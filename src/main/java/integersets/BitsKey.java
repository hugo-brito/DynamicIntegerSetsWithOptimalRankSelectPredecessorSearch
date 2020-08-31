package integersets;

/**
 * Implementation of the helper {@code BitsKey} data structure, as described in Section A.1 of the
 * report. It conveniently stores keys at the nodes in the Tries included in the package.
 */
public class BitsKey implements Comparable<BitsKey> {

  /**
   * Word size.
   */
  public static final int w = Long.SIZE;

  /**
   * The value held by the instance.
   */
  public final long val;

  /**
   * Constructs a new {@code BitsKey}, holding the provided {@code val}.
   * @param val The value to be held by the instance.
   */
  public BitsKey(final long val) {
    this.val = val;
  }

  /** Helper function.
   * Given a 64-bit word, val, return the value (0 or 1) of the d-th digit.
   * Digits are indexed 63..0.
   * 
   * @param d the digit index
   * @return 0 or 1 depending on if it's 0 or 1 at the specified index d.
   */
  public int bit(final int d) {
    return Util.bit(d, val);
  }

  /** Naive way of calculating compareTo.
   * 
   * @param that the other key
   * @return 0 if they are the same, -1 if {@code that} is larger, 1 if {@code this} is larger
   */
  public int compareToNaive(final BitsKey that) {
    if (this.equals(that)) {
      return 0;
    }
    
    int i = w - 1;
    while (this.bit(i) == that.bit(i)) {
      i--;
    }
    
    if (this.bit(i) > that.bit(i)) {
      return 1;
    }
    return -1;
  }

  /** Returns an integer value resulting from the comparison of two {@code BitsKey} instances.
   * <br>If {@code this} is smaller than {@code that}, it will return {@code -1}.
   * <br>If {@code this} is equal to {@code that}, it will return {@code 0}.
   * <br>If {@code this} is larger than {@code that}, it will return {@code 1}.
   *
   * @param that The other key.
   * @return The result of the comparison.
   */
  @Override
  public int compareTo(final BitsKey that) {
    // To find if an unsigned long is larger than another:
    // - XOR the keys,
    // - msb of that result
    // - check which key is one at that position.
    long aux = val ^ that.val;

    if (aux == 0) { // same value
      return 0;
    }

    if (this.bit(Util.msb(aux)) == 0) {
      return -1;
    }

    return 1;

  }

  /** Returns {@code true} iff {@code this} key and {@code that} key hold the same {@code val}.
   * 
   * @param that The other key.
   * @return {@code true} when they are equal; {@code false} otherwise.
   */
  public boolean equals(final BitsKey that) {
    if (that == null) {
      return false;
    }
    return ((val ^ that.val) == 0);
  }

  /**
   * Returns a binary representation of the long {@code val} in a string containing leading zeroes.
   * 
   * @return The string representation of the value held by the instance in binary.
   */
  public String bin() {
    return Util.bin(val);
  }

  /**
   * Returns a string representation of the instance.
   *
   * @return The string representation of the instance.
   */
  public String toString() {
    return "[Bin = " + bin() + ", Val = " + val + "]";
  }
}