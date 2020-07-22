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
  public static int msbLibrary(final int x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    return Integer.numberOfLeadingZeros(x);
  }

  /**
   * Uses the standard library function to calculate msb(x).
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msbLibrary(final long x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    return Long.numberOfLeadingZeros(x);
  }

  /**
   * Naive way to calculate msb(x) as described in:
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

  /**
   * Naive way to calculate msb(x) as described in:
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

  /**
   * Populates a lookup table for fast queries of msb.
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

  /**
   * Uses the lookup table to return the most significant bit in {@code x} as
   * described in
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

  /**
   * Splits the long {@code x} and then uses msbLookupDistributedOutput to return
   * the most significant bit.
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

  /**
   * Uses the lookup table to return the most significant bit in {@code x} as
   * described in
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

  /**
   * Splits the long {@code x} and then uses msbLookupDistributedInput to return
   * the most significant bit.
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
   * Given a 64-bit word, val, return the value (0 or 1) of the d-th digit. Digits
   * are indexed 0..63.
   * 
   * @param val the long value to have the digit extracted from
   * @param d   the digit index
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

  /**
   * Sets bit at position {@code bit} to 1 and returns the key {@code key}.
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

  public static void printBin(final int x, final int blockSize) {
    final StringBuilder res = new StringBuilder("0b");
    final String bin = bin(x);
    res.append(bin.substring(0, blockSize));
    for (int i = blockSize; i < Integer.SIZE; i += blockSize) {
      res.append("_").append(bin.substring(i,
          (i + blockSize > Integer.SIZE) ? Integer.SIZE : i + blockSize));
    }
    Util.print(res.toString());
  }

  public static void printBin(final long x, final int blockSize) {
    final StringBuilder res = new StringBuilder("0b");
    final String bin = bin(x);
    res.append(bin.substring(0, blockSize));
    for (int i = blockSize; i < Long.SIZE; i += blockSize) {
      res.append("_").append(bin.substring(i,
          (i + blockSize > Long.SIZE) ? Long.SIZE : i + blockSize));
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
    final int A = 0b1110_1101_1010_1001; // compressed keys in descending sorted order!
    final int blockSize = 4;
    System.out.print("           A = ");
    printBin(A, 4);
    // int x = 0b0001; // a key which rank I'm looking for
    final int x = 0b0100;

    System.out.print("           x = ");
    printBin(x, 4);

    final int w = 16;
    final int b = 4; // block size
    // with this block size and we can index 16 different keys (0..15)

    final int m = Integer.SIZE / b; // #keys in Integer.SIZE with b size
    // it's 8

    final int M = M(b, w);

    System.out.print("           M = ");
    printBin(M, 4);

    System.out.print("       M * x = ");
    printBin(M * x, 4);

    final int d = A - (M * x);

    System.out.print(" A - (M * x) = ");
    printBin(d, 4);

    // int mask = (M << (Integer.SIZE + b - 1 - w)) >>> (Integer.SIZE - w);
    final int mask = 0b1000 * M;

    System.out.print("        mask = ");
    printBin(mask, 4);

    System.out.print("(d&mask)^mask= ");
    printBin((d & mask) ^ mask, 4);

    print("     msb / b = " + (Integer.SIZE - msb((d & mask) ^ mask)) / b);

    // The rank of x is equal to the number of blocks whose left-most bit is 1
  }

  public static void msbNelsonExhaustive(final long x) {
    // MSB (x)
    final int w = Long.SIZE; // 64
    final int blockSize = (int) Math.sqrt(w); // 8
    final int numBlocks = blockSize; // 8
    // There will be an example in the comments.
    // It this particular example, let's assume w = 16. Let
    // x = 0b0101_0000_1000_1101

    // 1. Find F
    // F is a w-bit word (same size as x) with 1 at the most significant positions of each sqrt(w)
    // cluster
    // In my example,
    // F = 0b1000_1000_1000_1000
    final long F = 0b10000000_10000000_10000000_10000000_10000000_10000000_10000000_10000000L;

    // 2. Store information about the leading bits of each cluster in a local variable.
    // We do that by ANDing F with x: x & F
    // In my example
    //   x = 0b0101_0000_1000_1101
    //   F = 0b1000_1000_1000_1000
    // x&F = 0b0000_0000_1000_1000
    final long leadingBits = x & F;
    // now I know that the last and the second to last clusters are non-zero. In
    // particular because
    // of their first bit.
    // WE DEAL WITH THE FIRST BITS OF A CLUSTER AND THE REMAINING BITS IN A
    // DIFFERENT WAY!

    // 3. We set the leading bits of each block in x to 0, effectively clearing the msb of each
    // cluster This is easily done by x ^ (x & F)
    // In my example,
    //       x = 0b0101_0000_1000_1101
    //       F = 0b0101_0000_1000_1101
    //     x&F = 0b0000_0000_1000_1000
    // x^(x&F) = 0b0101_0000_0000_0101
    // final long noLeadingBits = x ^ (x & F);

    // 4. We subtract F to the previous result. Since F has 1 set at every msb of every cluster, by
    // subtracting the previous result to F, eg subtracting x after having all the msb of every
    // cluster set to 0 to F, we will have the information about if that cluster was empty (all
    // zeros) or not. This information is given by the bit that remains at the msb. If a 1 remains,
    // them it means that we have subtracted by 0, which means that such cluster was empty.
    // Otherwise, it wasn't empty.
    // In my example:
    //           F = 0b1000_1000_1000_1000
    //     x^(x&F) = 0b0101_0000_0000_0101
    // F-(x^(x&F)) = 0b0abc_1000_1000_0xyz --> if the cluster was non zero, then there will be a 0
    //                                         after subtracting because msb of each cluster
    //                                         "borrowed". Otherwise, there will be a 1.
    // final long difference = F - (x ^ (x & F));

    // 5. Note that in the non empty clusters, there will be the remainder of the difference, which
    // is some noise we do not care about. In fact, the result we are looking for at the is stage
    // is a word with 1 at the msb of the clusters that we non empty in x after we cleared the msb
    // of each cluster of x. So we negate that result and we AND it with F.
    // In my example:
    //        F-(x^(x&F)) = 0b0abc_1000_1000_0xyz
    //     ~(F-(x^(x&F))) = 0b1abc_0111_0111_1xyz
    //                  F = 0b1000_1000_1000_1000
    // ~(F-(x^(x&F))) & F = 0b1000_0000_0000_1000
    final long noLeadingBitsNonEmptyClusters = (~(F - (x ^ (x & F)))) & F;

    // 6. In order to get all the information about the whole x word, we need only to OR with the
    // first word we saved which contained information about the msb of each cluster
    // In my example:
    //                          x&F = 0b0000_0000_1000_1000
    //           ~(F-(x^(x&F))) & F = 0b1000_0000_0000_1000
    // (x&F) | (~(F-(x^(x&F))) & F) = 0b1000_0000_1000_1000
    // The result is a word which holds the information of which cluster of x the msb of x is.
    long res = leadingBits | noLeadingBitsNonEmptyClusters;
    // Or in a single operation:
    // final long res = (x & F) | (~(F - (x ^ (x & F))) & F);

    print("Clusters empty/non-empty");
    printBin(res, blockSize);

    // 7. Perfect sketch: I want to have all these leading bits consecutive
    // Lemma: When the bi are i*sqrt(w) + sqrt(w) - 1, there is an m such that multiplying by m
    // makes all the important bits consecutive with no gaps.
    // to do that, we shift to the right result by (block size - 1);

    // In my example:
    // (x&F) | (~(F-(x^(x&F))) & F) = 0b1000_0000_1000_1000
    // >>> (sqrt(w) - 1 = 4 - 1 = 3)= 0b0001_0000_0001_0001

    res >>>= (blockSize - 1);
    print("Clusters shifted");
    printBin(res, blockSize);

    // 8. Sketch compression.
    // We find m, which is a word that will put all the important bits in the same cluster.
    // Then we multiply by m.
    // Let the clusters be indexed from most significant position 0 to least significant position i.
    // Let the cluster also be indexed from 0 to i where cluster 0 is the most significant cluster.
    // Them m is word where every cluster i has bit i set, and all the other bits are 0.

    // In my example:
    // m = 0b0001_0010_0100_1000
    
    final long m = 0b00000001_00000010_00000100_00001000_00010000_00100000_01000000_10000000L;
    res *= m;
    print("Clusters summarized in the first cluster");
    printBin(res, blockSize);

    // 9. The sketch is now compressed, but it is stored in the first cluster. So we shift all the
    // clusters to the right minus one.

    // In my example:
    // (((x&F) | (~(F-(x^(x&F))) & F)) >>> (sqrt(w) - 1)) * 0b0001_0010_0100_1000) >>> (w - sqrt(w))
    res >>>= w - blockSize;
    print("Summary of the important bits on the right-most cluster");
    printBin(res, blockSize);

    // So do parallel comparison between 1011 and:
    // {1000, 0100, 0010, 0001}
    // The idea is to repeat the vector 1011 a bunch of times and find how it compares to the other
    // 4 vectors, to know the index of the first cluster with this first parallel comparison.
    // In order to repeat the vector, we do:
    // res = 0b1011;
    //       M = 0b0001_0001_0001_0001; // integer that repeats the vector
    //     res = 0b1011;    
    // res * m = 0b1011_1011_1011_1011;



    // Then, once we get the index of the cluster, we get the cluster from x, by shifting/masking
    // the remaining clusters. Perform another parallel comparison between that cluster and
    // {0001, 0010, 0100, 1000}.

    


  }

  /** Debugging.
   * 
   * @param args --
   */
  public static void main(final String[] args) {
    // msbNelsonExhaustive(0b0000001_00010000_00000000);
    // rank_lemma_1();

    printBin(0b1011_1011_1011_1011, 4);
    printBin(0b1000_0100_0010_0001, 4);
    printBin((0b1000_0100_0010_0001 - 0b1011_1011_1011_1011), 4);
  }

}