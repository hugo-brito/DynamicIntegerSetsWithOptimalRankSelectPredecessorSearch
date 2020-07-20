package DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch;

import java.util.Arrays;
/**
 * Utility class, containing many helful functions.
 */

public class Util {

  /* Finding the most and least significant bits in constant time.
   * We have an operation msb(x) that for an integer x computes the index of its most significant
   * set bit. Fredman and Willard [FW93] showed how to implement this in constant time using
   * multiplication, but msb can also be implemented very efficiently by assigning x to a floating
   * point number and extract the exponent. A theoretical advantage to using the conversion to
   * floating point numbers is that we avoid the universal constants depending on w used in [FW93].
   */

  /** Returns the index of the most significant bit of the target {@code x}.
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msb(final int x) {
    return msbObvious(x);
  }

  /** Returns the index of the most significant bit of the target {@code x}.
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msb(final long x) {
    return msbObvious(x);
  }

  /**
   * Using msb, we can also easily find the least significant bit of x as lsb(x) =
   * msb((x − 1) ⊕ x).
   * 
   * @param x the key to be evaluated
   * @return the index of the least significant bit of {@code x}
   */
  public static int lsb(final int x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    return msb((x - 1) ^ x);
  }

  /**
   * Using msb, we can also easily find the least significant bit of x as lsb(x) =
   * msb((x − 1) ⊕ x).
   * 
   * @param x the key to be evaluated
   * @return the index of the least significant bit of {@code x}
   */
  public static int lsb(final long x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    return msb((x - 1) ^ x);
  }

  /** Uses the standard library function to calculate msb(x).
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msbLibrary(int x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    return Integer.numberOfLeadingZeros(x);
  }

  /** Uses the standard library function to calculate msb(x).
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msbLibrary(long x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    return Long.numberOfLeadingZeros(x);
  }

  /** Naive way to calculate msb(x) as described in:
   * https://graphics.stanford.edu/~seander/bithacks.html#IntegerLogObvious
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msbObvious(int x) {
    if (x == 0) {
      return -1; // convention
    }

    int r = Integer.SIZE; // r will be lg(v)

    while (x != 0) {
      x >>>= 1;
      r--;
    }
    return r;
  }

  /** Naive way to calculate msb(x) as described in:
   * https://graphics.stanford.edu/~seander/bithacks.html#IntegerLogObvious
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msbObvious(long x) {
    if (x == 0) {
      return -1;
    }

    int r = Long.SIZE; // r will be lg(v)

    while (x != 0) {
      x >>>= 1;
      r--;
    }
    return r;
  }

  static int[] LogTable256;

  /** Populates a lookup table for fast queries of msb.
   * https://graphics.stanford.edu/~seander/bithacks.html#IntegerLogLookup
   */
  public static void generateLookupTable() {
    if (LogTable256 == null) {
      LogTable256 = new int[256];
      LogTable256[0] = -1; // if you want log(0) to return -1
      LogTable256[1] = 0;
      for (int i = 2; i < 256; i++) {
        LogTable256[i] = 1 + LogTable256[i / 2];
      }
    }
  }

  /** Uses the lookup table to return the most significant bit in {@code x} as described in
   * https://graphics.stanford.edu/~seander/bithacks.html#IntegerLogLookup
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msbLookupDistributedOutput(final int x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }

    generateLookupTable();
    int r;
    final int tt = x >>> 16;
    int t;
    if (tt != 0) {
      t = tt >>> 8;
      r = (t != 0) ? 24 + LogTable256[t] : 16 + LogTable256[tt];
    } else {
      t = x >>> 8;
      r = (t != 0) ? 8 + LogTable256[t] : LogTable256[x];
    }
    return 31 - r;
  }

  /** Splits the long {@code x} and then uses msbLookupDistributedOutput to return the most
   * significant bit.
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msbLookupDistributedOutput(final long x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }

    final int[] aux = splitLong(x);
    final int high = msbLookupDistributedOutput(aux[0]);

    if (high == -1) {
      return msbLookupDistributedOutput(aux[1]) + 32;
    }

    return high;
  }

  /** Uses the lookup table to return the most significant bit in {@code x} as described in
   * https://graphics.stanford.edu/~seander/bithacks.html#IntegerLogLookup
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msbLookupDistributedInput(final int x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }

    generateLookupTable();
    int r;

    if ((x >>> 24) != 0) {
      r = 24 + LogTable256[x >>> 24];

    } else if ((x >>> 16) != 0) {
      r = 16 + LogTable256[x >>> 16];

    } else if ((x >>> 8) != 0) {
      r = 8 + LogTable256[x >>> 8];

    } else {
      r = LogTable256[x];
    }
    return 31 - r;
  }

  /** Splits the long {@code x} and then uses msbLookupDistributedInput to return the most
   * significant bit.
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msbLookupDistributedInput(final long x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }

    final int[] aux = splitLong(x);
    final int high = msbLookupDistributedInput(aux[0]);

    if (high == -1) {
      return msbLookupDistributedInput(aux[1]) + 32;
    }

    return high;
  }

  /**
   * Given one 64-bit integer x, returns 2 32-bit integers in a 2-entry array. The
   * most significant bits of x will be at position 0 of the array, whereas the
   * least significant bits will be at position 1.
   * 
   * @param x the long to be split
   * @return the array containing 2 32-bit integers.
   */
  public static int[] splitLong(final long x) {
    final int[] res = new int[2];
    res[0] = (int) (x >>> Integer.SIZE);
    res[1] = (int) (x << Integer.SIZE >>> Integer.SIZE);
    return res;
  }

  /**
   * Given 2 32-bit integers in an array, returns 1 64-bit integer (long) using
   * the bits from those integers. Entry 0 of the 32-bit int array will be the
   * most significant bits of resulting long.
   * 
   * @param x The 32-bit integers
   * @return The resulting long
   */
  public static long mergeInts(final int[] x) {
    final long res = x[0];
    return (res << Integer.SIZE) | Integer.toUnsignedLong(x[1]);
  }

  /**
   * Returns a binary representation of integer x in a String containing leading
   * zeroes.
   * 
   * @param x the target
   * @return a String representation of {@code x}
   */
  public static String bin(final int x) {
    final String aux = Integer.toBinaryString(x);
    if (aux.length() < Integer.SIZE) {
      final StringBuilder res = new StringBuilder(32);
      for (int i = Integer.SIZE - aux.length(); i > 0; i--) {
        res.append(0);
      }
      res.append(aux);
      return res.toString();
    } else {
      return aux;
    }
  }

  /**
   * Returns a binary representation of integer x in a String containing leading
   * zeroes.
   * 
   * @param x the target
   * @return a String representation of {@code x}
   */
  public static String bin(final long x) {
    final String aux = Long.toBinaryString(x);
    if (aux.length() < Long.SIZE) {
      final StringBuilder res = new StringBuilder(64);
      for (int i = Long.SIZE - aux.length(); i > 0; i--) {
        res.append(0);
      }
      res.append(aux);
      return res.toString();
    } else {
      return aux;
    }
  }

  /**
   * Given a 64-bit word, val, return the value (0 or 1) of the d-th digit.
   * Digits are indexed 0..63.
   * 
   * @param val the long value to have the digit extracted from
   * @param d the digit index
   * @return 0 or 1 depending on if it's 0 or 1 at the specified index d.
   */
  public static int bit(final long val, final int d) {
    return (int) (val >> (63 - d)) & 1;
  }

  /**
   * Given a 32-bit word, val, return the value (0 or 1) of the d-th digit. Digits
   * are indexed 0..63.
   * 
   * @param val the long value to have the digit extracted from
   * @param d   the digit index
   * @return 0 or 1 depending on if it's 0 or 1 at the specified index d.
   */
  public static int bit(final int val, final int d) {
    return (val >> (31 - d)) & 1;
  }



  /** Sets bit at position {@code bit} to 1 and returns the key {@code key}.
   * 
   * @param target the key to have the bit altered
   * @param bit    the bit to be set to 1
   * @return the key with the bit altered
   */
  public static long setBit(final long target, final int bit) {
    assert ((target >>> (63 - bit)) & 1) == 0; // checks if the bit at position bit is 0!
    return target | (1L << (63 - bit));
  }

  public static void print(final Object s) {
    System.out.println(s);
  }

    
  public static void printBin(final int x) {
    StringBuilder res = new StringBuilder("0b");
    String bin = bin(x);
    res.append(bin.substring(0, 4));
    for (int i = 4; i < Integer.SIZE; i += 4) {
      res.append("_").append(bin.substring(i, i + 4));
    }
    Util.print(res.toString());
  }

  public static void printBin(final long x) {
    StringBuilder res = new StringBuilder("0b");
    String bin = bin(x);
    res.append(bin.substring(0, 4));
    for (int i = 4; i < Long.SIZE; i += 4) {
      res.append("_").append(bin.substring(i, i + 4));
    }
    Util.print(res.toString());
  }

  public static int M(final int b, final int w) {
    int M = 1;
    for (int i = 1; i < (w / b); i++) {
      M |= 1 << (i * b);
    }
    return M;
  }

  public static void rank_lemma_1() {
    final int A = 0b1011_0111_0100_0011; // compressed keys in descending sorted order!
    System.out.print(" A     = ");
    printBin(A);
    // int x = 0b0001; // a key which rank I'm looking for
    int x = 0b0000;

    System.out.print(" x     = ");
    printBin(x);

    int w = 16;
    int b = 4; // block size
    // with this block size and we can index 16 different keys (0..15)

    int m = Integer.SIZE / b; // #keys in Integer.SIZE with b size
    // it's 8

    int M = M(b, w);

    System.out.print(" M     = ");
    printBin(M);

    System.out.print(" M * x = ");
    printBin(M * x);

    int d = A - (M * x);

    System.out.print("A-(M*x)= ");
    printBin(d);

    // int mask = (M << (Integer.SIZE + b - 1 - w)) >>> (Integer.SIZE - w);
    int mask = 0b1000 * M;

    System.out.print(" mask  = ");
    printBin(mask);

    System.out.print("d &mask= ");
    printBin(d & mask);

    print(" msb/b = " + (Integer.SIZE - msb(d & mask)) / b);

    // The rank of x is equal to the number of blocks whose left-most bit is 1
  }

  /** Debugging.
   * 
   * @param args --
   */
  public static void main(final String[] args) {
    rank_lemma_1();
  }

}