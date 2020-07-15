package DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch;

import java.util.Arrays;
/**
 * Utility class, containing many helful functions.
 */
public class Util {

  /** Finding the most and least significant bits in constant time.
   * We have an operation msb(x) that for an integer x computes the index of its most significant
   * set bit. Fredman and Willard [FW93] showed how to implement this in constant time using
   * multiplication, but msb can also be implemented very efficiently by assigning x to a floating
   * point number and extract the exponent. A theoretical advantage to using the conversion to
   * floating point numbers is that we avoid the universal constants depending on w used in [FW93].
   */

  // https://graphics.stanford.edu/~seander/bithacks.html#IntegerLogObvious
  public static int msb32Obvious(int x) {
    if (x == 0) {
      return -1; // convention
    }

    int r = 32; // r will be lg(v)

    while (x != 0) {
      x >>>= 1;
      r--;
    }
    return r;
  }

  public static int msb64Obvious(long x) {
    if (x == 0) {
      return -1;
    }

    int r = 64; // r will be lg(v)

    while (x != 0) {
      x >>>= 1;
      r--;
    }
    return r;
  }

  /** Apparently the same 64 bits are interpreted by t.d as a double and by t.u
   * as two 32-bit unsigned integers.
   * Anyway "union" is just a short-hand, it can also be done by 
   * - first defining u as unsigned int[2],
   * - then interpreting (not converting!) it as a 64-bit double,
   * - subtracting a number from it,
   * - and then reinterpreting it as unsigned int[2].
   * I suspect Java cannot do that, because there is no operation "interpret
   * this memory cell as int[2] now, please". This is also related to the fact
   * that Java does not have pointers. Unless you disagree with this, I would
   * say simply ignore the float/double trick for now.
   * 
   * @param x
   * @return
   */
  public static int msb32double(final int x) {
    final int v = x;
    int r;

    final int u[] = { 0x43300000, v };
    final long aux1 = mergeInts(u);
    final double aux2 = Double.longBitsToDouble(aux1);
    final double d = aux2 - 4503599627370496.0d;
    final long aux3 = Double.doubleToLongBits(d);
    r = (int) (aux3 >> 20) - 0x3FF;
    return r;
  }

  static int[] LogTable256;

  // https://graphics.stanford.edu/~seander/bithacks.html#IntegerLogLookup
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

  public static int msb64LookupDistributedOutput(final long x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }

    final int[] aux = splitLong(x);
    final int high = msb32LookupDistributedOutput(aux[0]);

    if (high == -1) {
      return msb32LookupDistributedOutput(aux[1]) + 32;
    }

    return high;
  }

  public static int msb64LookupDistributedInput(final long x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }

    final int[] aux = splitLong(x);
    final int high = msb32LookupDistributedInput(aux[0]);

    if (high == -1) {
      return msb32LookupDistributedInput(aux[1]) + 32;
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
    res[0] = (int) (x >>> 32);
    res[1] = (int) (x << 32 >>> 32);
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
    return (res << 32) | Integer.toUnsignedLong(x[1]);
  }

  public static int msb32LookupDistributedOutput(final int x) {
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

  public static int msb32LookupDistributedInput(final int x) {
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

  public static int msbNaive(final int x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    final String bin = bin(x);
    for (int i = 0; i < 32; i++) {
      // System.out.println(bin.charAt(i));
      if (bin.charAt(i) == '1')
        return i;
    }
    return -1;
  }

  public static int msb(final int x) {
    if (x == 0)
      return -1; // because 0 has no 1 bits
    return Integer.numberOfLeadingZeros(x);
  }

  public static int msbDouble(final long x) {
    // https://en.wikipedia.org/wiki/Double-precision_floating-point_format
    // 1 - Convert to double,
    // 2 - Get the binary representation of the double
    // 3 - Mask and Shift
    // 4 - Get the integer value (a number between 0 and 63)
    return (int) (Double.doubleToRawLongBits(x) << 1 >>> 53) - 0x3FF;
  }

  /**
   * Using msb, we can also easily find the least significant bit of x as lsb(x) =
   * msb((x − 1) ⊕ x).
   * 
   * @param x
   * @return
   */
  public static int lsb(final int x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    return msb((x - 1) ^ x);
  }

  public static int lsbNaive(final int x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    return msbNaive((x - 1) ^ x);
  }

  public static int lsbNaiver(final int x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    final String bin = bin(x);
    for (int i = 31; i > -1; i--) {
      // System.out.println(bin.charAt(i));
      if (bin.charAt(i) == '1') {
        return i;
      }
    }
    return -1;
  }

  /**
   * Returns a binary representation of integer x in a String containing leading
   * zeroes.
   * 
   * @param x
   * @return
   */
  public static String bin(final int x) {
    final String aux = Integer.toBinaryString(x);
    if (aux.length() < 32) {
      final StringBuilder res = new StringBuilder(32);
      for (int i = 32 - aux.length(); i > 0; i--) {
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
   * @param x
   * @return
   */
  public static String bin64(final long x) {
    final String aux = Long.toBinaryString(x);
    if (aux.length() < 64) {
      final StringBuilder res = new StringBuilder(64);
      for (int i = 64 - aux.length(); i > 0; i--) {
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
  public static int bit(long val, int d) {
    return (int) (val >> (63 - d)) & 1;
  }

  /**
   * Given a 32-bit word, val, return the value (0 or 1) of the d-th digit.
   * Digits are indexed 0..63.
   * 
   * @param val the long value to have the digit extracted from
   * @param d the digit index
   * @return 0 or 1 depending on if it's 0 or 1 at the specified index d.
   */
  public static int bit(int val, int d) {
    return (val >> (31 - d)) & 1;
  }

  /**
   * Sets bit at position {@code bit} to 1 and returns the key {@code key}
   * @param key the key to have the bit altered
   * @param bit the bit to be set to 1
   * @return the key with the bit altered
   */
  public static long setBit(final long key, final int bit) {
    assert ((key >>> (63 - bit)) & 1) == 0; // checks if the bit at position bit is 0!
    return key | (1L << (63 - bit));
  }

  public static void print(Object s) {
    System.out.println(s);
  }

  public static void main(final String[] args) {
    final int i = Integer.MAX_VALUE;
    System.out.println("Binary representation: " + bin(i));
    System.out.println("Indices go from 0 .. 31. If it returns -1 then there is no 1-bit");
    System.out.println("MSB Naive: " + msbNaive(i));
    System.out.println("MSB standard library: " + msb(i));
    System.out.println("LSB using the MSB naive: " + lsbNaive(i));
    System.out.println("LSB using the same loop as in MSB naive: " + lsbNaiver(i));
    System.out.println("LSB using MSB standard library: " + lsb(i));
    // System.out.println(new BitsKey(Double.doubleToRawLongBits(1)).bin());

    final long j = -6442450944L;
    System.out.println("whole 64 bits:\n" + bin64(j));
    System.out.println(bin(splitLong(j)[0]));
    System.out.println(bin(splitLong(j)[1]));
    System.out.println(bin64(mergeInts(splitLong(j))));
    System.out.println(Arrays.toString(new String[]{bin(splitLong(j)[0]),bin(splitLong(j)[1])}));
    System.out.println(msb64Obvious(j));
    System.out.println(msb64LookupDistributedInput(j));

    System.out.println(bin64(mergeInts(splitLong(j))));
    System.out.println(mergeInts(splitLong(j)) == j);

    System.out.println(bin64(-6442450944L));

  }

}