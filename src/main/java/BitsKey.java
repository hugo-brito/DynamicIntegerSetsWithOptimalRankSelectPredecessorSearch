import java.util.Random;

class BitsKey {
  /** BitsKey class created to conveniently store keys at the nodes in the Binary Search Trie.
   */

  public final long val;
  
  public static final int bitsword = 64;

  public BitsKey(final long val) {
    this.val = val;
  }

  /**
   * Helper function. Given a 64-bit word, val, return the value (0 or 1) of the
   * d-th digit. Digits are indexed 0..63.
   * 
   * @param d the digit index
   * @return 0 or 1 depending on if it's 0 or 1 at the specified index d.
   */
  public int bit(final int d) {
    return (int) (val >> (bitsword - d - 1)) & 1;
  }

  /**
   * Helper function. Returns a binary representation of the long x in a String
   * containing leading zeroes. It uses the helper function bit to do so and the
   * StringBuilder for fast concatenation.
   * 
   * @return
   */
  public String bin() {
    final StringBuilder res = new StringBuilder(bitsword);
    for (int i = 0; i < bitsword; i++) {
      res.append(bit(i));
    }
    return res.toString();
  }

  public String toString() {
    return "[Bin = " + bin() + ", Val = " + val + "]";
  }

  public static void main(final String[] args) {
    boolean failed = false;
    System.out.println("Testing helper functions of BitsKey class:");
    for (long i = Long.MIN_VALUE; i == Long.MAX_VALUE; i++) {
      final BitsKey key = new BitsKey(i);
      if (!Long.toBinaryString(i).equals(key.bin())) {
        failed = true;
        System.err
            .println("ERROR!\nFor " + i + " expected:\n	" + Long.toBinaryString(i) + "\nbut got:\n	" + key.bin());
      }
    }

    if (!failed) {
      System.out.println("	- \"bin\" and \"bit\" helper funtions work for the full range of long!");
    }

    System.out.println("	- Calling toString on 10 random keys:");
    final Random rand = new Random();
    for (int i = 0; i < 10; i++) {
      final BitsKey key = new BitsKey(rand.nextLong());
      System.out.println("		" + key.toString());
    }
    System.out.println("Finished!");
    
  }
}