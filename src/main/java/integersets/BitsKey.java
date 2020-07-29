package integersets;

public class BitsKey implements Comparable<BitsKey> {
  /** BitsKey class created to conveniently store keys at the nodes in the Binary Search Trie.
   */

  public final long val;

  public BitsKey(final long val) {
    this.val = val;
  }

  /** Helper function.
   * Given a 64-bit word, val, return the value (0 or 1) of the d-th digit.
   * Digits are indexed 0..63.
   * 
   * @param d the digit index
   * @return 0 or 1 depending on if it's 0 or 1 at the specified index d.
   */
  public int bit(final int d) {
    return (int) (val >>> (Long.SIZE - 1 - d)) & 1;
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
    
    int i = 0;
    while (this.bit(i) == that.bit(i)) {
      i++;
    }
    
    if (this.bit(i) > that.bit(i)) {
      return 1;
    }
    return -1;
  }

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

  /** Returns true iff this key and that key hold the same val.
   * 
   * @param that the other key
   * @return boolean value, true when they are equal. False otherwise.
   */
  public boolean equals(final BitsKey that) {
    if (that == null) {
      return false;
    }
    return ((val ^ that.val) == 0);
  }

  /**
   * Helper function. Returns a binary representation of the long x in a String
   * containing leading zeroes. It uses the helper function bit to do so and the
   * StringBuilder for fast concatenation.
   * 
   * @return
   */
  public String bin() {
    final StringBuilder res = new StringBuilder(Long.SIZE);
    for (int i = 0; i < Long.SIZE; i++) {
      res.append(bit(i));
    }
    return res.toString();
  }

  public String toString() {
    return "[Bin = " + bin() + ", Val = " + val + "]";
  }
}