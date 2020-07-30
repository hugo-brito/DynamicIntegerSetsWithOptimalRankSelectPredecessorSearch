package integersets;

/**
 * Utility class, containing many helful functions.
 */

public class Util {

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
    return bin(x, 0);
  }

  /**
   * Returns a binary representation of integer x in a String containing leading
   * zeroes.
   * 
   * @param x the target
   * @return a String representation of {@code x}
   */
  public static String bin(final int x, final int blockSize) {
    final StringBuilder res = new StringBuilder("0b");
    if (blockSize > 0) {
      final int r = Integer.SIZE % blockSize == 0 ? blockSize : Integer.SIZE % blockSize;
      for (int i = 0; i < r; i++) {
        res.append(bit(x, i));
      }
      for (int i = r; i < Integer.SIZE; i += blockSize) {
        res.append("_");
        for (int j = i; j < i + blockSize; j++) {
          res.append(bit(x, j));
        }
      }
    } else {
      for (int i = 0; i < Integer.SIZE; i++) {
        res.append(bit(x, i));
      }
    }
    return res.toString();
  }

  /**
   * Returns a binary representation of integer x in a String containing leading
   * zeroes.
   * 
   * @param x the target
   * @return a String representation of {@code x}
   */
  public static String bin(final long x) {
    return bin(x, 0);
  }

  /**
   * Returns a binary representation of integer x in a String containing leading
   * zeroes.
   * 
   * @param x the target
   * @return a String representation of {@code x}
   */
  public static String bin(final long x, final int blockSize) {
    final StringBuilder res = new StringBuilder("0b");
    if (blockSize > 0) {
      final int r = Long.SIZE % blockSize == 0 ? blockSize : Long.SIZE % blockSize;
      for (int i = 0; i < r; i++) {
        res.append(bit(x, i));
      }
      for (int i = r; i < Long.SIZE; i += blockSize) {
        res.append("_");
        for (int j = i; j < i + blockSize; j++) {
          res.append(bit(x, j));
        }
      }
    } else {
      for (int i = 0; i < Long.SIZE; i++) {
        res.append(bit(x, i));
      }
    }
    return res.toString();
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

  public static void print(final Object o) {
    System.out.print(o);
  }

  public static void println(final Object o) {
    System.out.println(o);
  }

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
    return msbConstant(x);
  }

  /** Returns the index of the most significant bit of the target {@code x}.
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msb(final long x) {
    return msbConstant(x);
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

  private static int parallelComparison(final long cluster) {

    final int blockSize = 8;

    // Copying mask:
    final long m = 0b0_00000001_0_00000001_0_00000001_0_00000001L; // copy x and pad it with zeroes

    // Summarizing mask:
    final long d = 0b000100000_001000000_010000000_100000000L;

    // Clearing mask:
    final long mask = 0b1_00000000_1_00000000_1_00000000_1_00000000L;

    // Fetching mask:
    final long fetch = 0b1111L << (4 * blockSize);

    final long hiPowers = 0b1_01111111_1_00111111_1_00011111_1_00001111L;

    long hi = ((((((hiPowers - (m * cluster)) & mask) ^ mask) >>> blockSize) * d) & fetch)
        >>> 4 * blockSize;

    if (hi > 0) {
      return parallelLookup((int) hi);
    }

    final long loPowers = 0b1_00000111_1_00000011_1_00000001_1_00000000L;
    long lo = ((((((loPowers - (m * cluster)) & mask) ^ mask) >>> blockSize) * d) & fetch)
        >>> 4 * blockSize;

    return 4 + parallelLookup((int) lo);
  }

  /**
   * Lookup table for the parallel comparison.
   */
  private static int parallelLookup(int pow) {
    switch (pow) {
      case 0b1111:
        return 0;
      case 0b0111:
        return 1;
      case 0b0011:
        return 2;
      case 0b0001:
        return 3;
      default:
        return -1;
    }
  }

  /**
   * Most significant bit in constant time. This implementation follows the Jelani Nelson and
   * Erik Demaine's lecture notes.
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msbConstant(int x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    if (x < 0) {
      return 0;
    }

    return msbConstant(Integer.toUnsignedLong(x)) - Integer.SIZE;
  }

  /**
   * Most significant bit in constant time. This implementation follows the Jelani Nelson and
   * Erik Demaine's lecture notes.
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msbConstant(long x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    if (x < 0) {
      return 0;
    }

    final int w = Long.SIZE;
    final int blockSize = 8;
    final long F = 0b10000000_10000000_10000000_10000000_10000000_10000000_10000000_10000000L;
    final long C = 0b00000001_00000010_00000100_00001000_00010000_00100000_01000000_10000000L;

    final long summary = ((((x & F) | ((~(F - (x ^ (x & F)))) & F)) >>> (blockSize - 1)) * C)
        >>> (w - blockSize);

    final int cluster = parallelComparison(summary);

    x = (x >>> (blockSize - 1 - cluster) * blockSize) & 0b11111111L;

    final int d = parallelComparison(x);

    return cluster * blockSize + d;
  }

  /**
   * Most significant bit in constant time. Commented version of {@code msbConstant(x)}.
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msbConstantCommented(long x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    if (x < 0) {
      return 0;
    }
    // MSB (x)
    final int w = Long.SIZE; // 64
    final int blockSize = (int) Math.sqrt(w); // 8
    final int numBlocks = blockSize; // 8
    // There will be an example in the comments.
    // It this particular example, let's assume w = 16. Let
    // x = 0b0101_0000_1000_1101

    println("Query");
    println(bin(x, blockSize));

    // 1. Find F
    // F is a w-bit word (same size as x) with 1 at the most significant positions
    // of each sqrt(w)  cluster
    // In my example,
    // F = 0b1000_1000_1000_1000
    final long F = 0b10000000_10000000_10000000_10000000_10000000_10000000_10000000_10000000L;

    // 2. Store information about the leading bits of each cluster in a local
    // variable.
    // We do that by ANDing F with x: x & F
    // In my example
    // x = 0b0101_0000_1000_1101
    // F = 0b1000_1000_1000_1000
    // x&F = 0b0000_0000_1000_1000
    final long leadingBits = x & F;
    // now I know that the last and the second to last clusters are non-zero. In particular because
    // of their first bit.

    // 3. We set the leading bits of each block in x to 0, effectively clearing the
    // msb of each
    // cluster This is easily done by x ^ (x & F)
    // In my example,
    // x = 0b0101_0000_1000_1101
    // F = 0b0101_0000_1000_1101
    // x&F = 0b0000_0000_1000_1000
    // x^(x&F) = 0b0101_0000_0000_0101
    final long noLeadingBits = x ^ (x & F);

    // 4. We subtract F to the previous result. Since F has 1 set at every msb of  every cluster, by
    // subtracting the previous result to F, eg subtracting x after having all the msb of every
    // cluster set to 0 to F, we will have the information about if that cluster was empty (all
    // zeros) or not. This information is given by the bit that remains at the msb.
    // If a 1 remains, then it means that we have subtracted by 0, which means that such cluster was
    // empty. Otherwise, it wasn't empty.
    // In my example:
    // F = 0b1000_1000_1000_1000
    // x^(x&F) = 0b0101_0000_0000_0101
    // F-(x^(x&F)) = 0b0abc_1000_1000_0xyz --> if the cluster was non zero, then
    // there will be a 0
    // after subtracting because msb of each cluster
    // "borrowed". Otherwise, there will be a 1.
    final long difference = F - (x ^ (x & F));

    // 5. Note that in the non empty clusters, there will be the remainder of the difference, which
    // is some noise we do not care about. In fact, the result we are looking for at this stage is
    // a word with 1 at the msb of the clusters that we non empty in x after we cleared the msb
    // of each cluster of x. So we negate that result and we AND it with F.
    // In my example:
    // F-(x^(x&F)) = 0b0abc_1000_1000_0xyz
    // ~(F-(x^(x&F))) = 0b1abc_0111_0111_1xyz
    // F = 0b1000_1000_1000_1000
    // ~(F-(x^(x&F))) & F = 0b1000_0000_0000_1000
    final long noLeadingBitsNonEmptyClusters = (~difference) & F;

    // 6. In order to get all the information about the whole x word, we need only
    // to OR with the
    // first word we saved which contained information about the msb of each cluster
    // In my example:
    // x&F = 0b0000_0000_1000_1000
    // ~(F-(x^(x&F))) & F = 0b1000_0000_0000_1000
    // (x&F) | (~(F-(x^(x&F))) & F) = 0b1000_0000_1000_1000
    // The result is a word which holds the information of which cluster of x the
    // msb of x is.
    long res = leadingBits | noLeadingBitsNonEmptyClusters;
    // Or in a single operation:
    // final long res = (x & F) | (~(F - (x ^ (x & F))) & F);
    println("Clusters empty/non-empty");
    println(bin(res, blockSize));

    // 7. Perfect sketch: I want to have all these leading bits consecutive
    // Lemma: When the bi are i*sqrt(w) + sqrt(w) - 1, there is an m such that
    // multiplying by m makes all the important bits consecutive with no gaps.
    // to do that, we shift to the right result by (block size - 1);

    // In my example:
    // (x&F) | (~(F-(x^(x&F))) & F) = 0b1000_0000_1000_1000
    // >>> (sqrt(w) - 1 = 4 - 1 = 3)= 0b0001_0000_0001_0001

    res >>>= (blockSize - 1);
    println("Clusters shifted (res >>> (sqrt(w) - 1)");
    println(bin(res, blockSize));

    // 8. Sketch compression.
    // We find C, which is a word that will put all the important bits in the same cluster.
    // Then we multiply by C.
    // Let the clusters be indexed from most significant position 0 to least significant position i.
    // Let the cluster also be indexed from 0 to i where cluster 0 is the most significant cluster.
    // Them C is word where every cluster i has bit i set, and all the other bits are 0.

    // In my example:
    // C = 0b0001_0010_0100_1000

    final long C = 0b00000001_00000010_00000100_00001000_00010000_00100000_01000000_10000000L;
    res *= C;
    println("Clusters summarized in the first cluster (res * 00001_00010_...)");
    println(bin(res, blockSize));

    // 9. The sketch is now compressed, but it is stored in the first cluster. So we
    // shift all the
    // clusters to the right minus one.

    // In my example:
    // (((x&F) | (~(F-(x^(x&F))) & F)) >>> (sqrt(w) - 1)) * C) >>> (w - sqrt(w))
    res >>>= w - blockSize;
    println("Summary of the important bits on the right-most cluster (res >>> (w - sqrt(w))");
    println(bin(res, blockSize));

    // So do parallel comparison between 1011 and:
    // {1000, 0100, 0010, 0001}
    // The idea is to repeat the vector 1011 a bunch of times and pad it with 0's.
    // To find how it compares to the other 4 vectors, we pad them with 1's
    // to know the index of the first cluster with this first parallel comparison.
    // In order to repeat the vector, we do:
    // res = 0b1011;
    // M = 0b0001_0001_0001_0001; // integer that repeats the vector
    // res = 0b1011;
    // res * m = 0b1011_1011_1011_1011;
    
    println("Cluster (0..7):");
    final int cluster = parallelComparison(res);
    println(cluster);
    // 0..7

    println("Block shifts to make (0..8..64)= " + (7 - cluster));

    print("The query before the shift: ");
    println(bin(x, blockSize));

    x = (x >>> (blockSize - 1 - cluster) * blockSize) & 0b11111111L;

    print("The query AFTER the shift: ");
    println(bin(x, blockSize));

    println("the cluster: " + bin(x, blockSize));

    final int d = parallelComparison(x);

    println("d (0..7): " + d);
    // 0..7

    return cluster * blockSize + d;
  }
  
  /**
   * Produces a {@code long} word with at {@code 1}-bit set every {@code b} position, up to
   * {@code w-positions}. The first set position is the least significant position of the word.
   * @param b the block size
   * @param w the word size
   * @return
   */
  public static long M(final int b, final int w) {
    long M = 1L;
    for (int i = 1; i < (w / b); i++) {
      M |= 1L << (i * b);
    }
    return M;
  }

  public static void rank_lemma_1() {
    final int A = 0b1110_1101_1010_1001; // compressed keys in descending sorted order!
    final int blockSize = 4;
    System.out.print("           A = ");
    println(bin(A, 4));
    // int x = 0b0001; // a key which rank I'm looking for
    final int x = 0b0100;

    System.out.print("           x = ");
    println(bin(x, 4));

    final int w = 16;
    final int b = 4; // block size
    // with this block size and we can index 16 different keys (0..15)

    final int m = Integer.SIZE / b; // #keys in Integer.SIZE with b size
    // it's 8

    // !!!!!

    final int M = (int) M(b, w);

    System.out.print("           M = ");
    println(bin(M, 4));

    System.out.print("       M * x = ");
    println(bin(M * x, 4));

    final int d = A - (M * x);

    System.out.print(" A - (M * x) = ");
    println(bin(d, 4));

    // int mask = (M << (Integer.SIZE + b - 1 - w)) >>> (Integer.SIZE - w);
    final int mask = 0b1000 * M;

    System.out.print("        mask = ");
    println(bin(mask, 4));

    System.out.print("(d&mask)^mask= ");
    println(bin((d & mask) ^ mask, 4));

    println("     msb / b = " + (Integer.SIZE - msb((d & mask) ^ mask)) / b);

    // The rank of x is equal to the number of blocks whose left-most bit is 1
  }

  /** Lemma 1:
   * Let mb <= w. If we are given a b-bit number x and a word A with m b-bit numbers stored in
   * sorted order, then in constant time we can find the rank of x in A, denoted rank(x, A).
   * In order for this method to give plausible results, the following requirements *must* be
   * fulfilled: Each b-bit word in A must be prefixed (padded) with a 1, and the words in A must
   * also be sorted.
   * @param x the query of b size
   * @param A the A word
   * @param m the number of keys in A
   * @param blockSize the lenght of each key in A excluding padding bits
   */
  public static void rank_lemma_1(long x, long A, int m, int blockSize) {
    // final int A = 0b1110_1101_1010_1001; // compressed keys in descending sorted order!
    // final int blockSize = 4;
    print("           A = ");
    println(bin(A, blockSize + 1));
    // int x = 0b0001; // a key which rank I'm looking for
    // final int x = 0b0100;

    print("           x = ");
    println(bin(x, blockSize + 1));

    final long M = M(blockSize + 1, m * blockSize + m);

    System.out.print("           M = ");
    println(bin(M, blockSize + 1));

    System.out.print("       M * x = ");
    println(bin(M * x, blockSize + 1));

    final long d = A - (M * x);

    print(" A - (M * x) = ");
    println(bin(d, blockSize + 1));

    final long mask = (1L << blockSize) * M;

    print("        mask = ");
    println(bin(mask, blockSize + 1));

    print("(d&mask)^mask= ");
    println(bin((d & mask) ^ mask, blockSize + 1));

    println("     msb / b = " + (Long.SIZE - msb((d & mask) ^ mask)) / (blockSize + 1));

    // The rank of x is equal to the number of blocks whose left-most bit is 1
  }

  /** No padding version of rank_lemma_1 
   * Lemma 1:
   * Let mb <= w. If we are given a b-bit number x and a word A with m b-bit numbers stored in
   * sorted order, then in constant time we can find the rank of x in A, denoted rank(x, A).
   * In order for this method to give plausible results, the following requirements *must* be
   * fulfilled: Each b-bit word in A must be prefixed (padded) with a 1, and the words in A must
   * also be sorted.
   * @param x the query of b size
   * @param A the A word
   * @param m the number of keys in A
   * @param blockSize the lenght of each key in A excluding padding bits
   */
  public static int rank_lemma_1_2(long x, long A, int m, int blockSize) {
    // final int A = 0b1110_1101_1010_1001; // compressed keys in descending sorted order!
    // final int blockSize = 4;
    print("           A = ");
    println(bin(A, blockSize));
    // int x = 0b0001; // a key which rank I'm looking for
    // final int x = 0b0100;

    print("           x = ");
    println(bin(x, blockSize));

    long M = M(blockSize, m * blockSize); // copying integer
    print("           M = ");
    println(bin(M, blockSize));

    // First figure out how many clusters of A have leading bit zero.
    // One way to do so is to compute the LSB of A & leading_bits.
    // Alternatively, this number can also be stored and maintained alongside the data structure in
    // an extra variable.

    print("    A & 1000 = ");
    println(bin(A & (M << (blockSize - 1)), blockSize));

    int numClustersW0 = m;
    if ((A & (M << (blockSize - 1))) != 0) {
      numClustersW0 = (Long.SIZE - lsb(A & (M << (blockSize - 1)))) / blockSize - 1; // works
    } 

    print("#lead zeroes = ");
    println(numClustersW0);

    
    int leadingBitOfX = bit(x, Long.SIZE - blockSize);
    print("x leading bit= ");
    println(leadingBitOfX);
    
    // If the leading bit of x is one:
    if (leadingBitOfX == 1) {
      // 1) remove the clusters of A which have a non-leading bit
      
      A >>>= blockSize * numClustersW0;
      print("A w/o 0-leadC= ");
      println(bin(A, blockSize));

      // we need to do the same on M
      M >>>= blockSize * numClustersW0;
      print("       new M = ");
      println(bin(M, blockSize));
      
      x &= ~(1 << (blockSize - 1));
      print("x w/o leadBit= ");
      println(bin(x, blockSize));

      x *= M;
      print("    x copied = ");
      println(bin(x, blockSize));

      
      long d = A - x;
      print("   d = A - x = ");
      println(bin(d, blockSize));

      long mask = (1L << (blockSize - 1)) * M;
      print("        mask = ");
      println(bin(mask, blockSize));
      
      print("    d & mask = ");
      println(bin((d & mask), blockSize));

      int res = m;

      if ((d & mask) != 0) {
        print("#clusters <x = ");
        int numClustersSmallerThanX = ((Long.SIZE - lsb(d & mask)) / blockSize) - 1;
        println(numClustersSmallerThanX);
        res = numClustersW0 + numClustersSmallerThanX;
      }
      println("   rank(x,A) = " + res);

      return res;

    } else {
      // 1) remove the clusters of A which leading bit is one

      A = (A << Long.SIZE - (blockSize * numClustersW0))
          >>> (Long.SIZE - (blockSize * numClustersW0));
      print("A w/o 1-leadC= ");
      println(bin(A, blockSize));

      // we need to do the same on M
      M >>>= blockSize * (m -  numClustersW0);
      print("       new M = ");
      println(bin(M, blockSize));

      x *= M;
      print("    x copied = ");
      println(bin(x, blockSize));

      M *= 1L << (blockSize - 1);
      print("        mask = ");
      println(bin(M, blockSize));

      A |= M;
      print("    A | mask = ");
      println(bin(A, blockSize));

      
      long d = A - x;
      print("   d = A - x = ");
      println(bin(d, blockSize));

      print("    d & mask = ");
      println(bin((d & M), blockSize));

      int res = numClustersW0; // the total number of blocks minus the ones that are larger
      // than x
      print("  canditates = ");
      println(res);

      if ((d & M) != 0) {


        print("#clusters >x = ");
        int numClustersLargerThanX = numClustersW0 - ((Long.SIZE - lsb(d & M)) / blockSize);
        println(numClustersLargerThanX);
        res -= numClustersLargerThanX + 1;
      }

      println("   rank(x,A) = " + res);

      return res;
    }

  }

  /**
   * Debugging.
   * 
   * @param args --
   */
  public static void main(final String[] args) {
    // Works:
    // long A = 0b1_1110010_1_1101101_1_1100111_1_1001010_1_0100101_1_0100011_1_0011110_1_0001100L;
    // // [114, 109, 103, 74, 37, 35, 30, 12] // 8 keys of 7 bits each
    // long x = 0b0_1010101L; // 85, rank 5
    // println(x);
    // rank_lemma_1(x, A, 8, 7);

    // // Works:
    // long A = 0b1_1110_1_1101_1_1010_1_1001; // compressed keys in descending sorted order!
    // long x = 0b0_1100;
    // rank_lemma_1(x, A, 4, 4);

    long A = 0b1110_0101_0010_0001; // compressed keys in descending sorted order!
    long x = 0b0_0111;
    rank_lemma_1_2(x, A, 4, 4);

    for (int b = 1; b < 34; b++) {
      int m = (int) Math.min(Math.pow(2, b), Long.SIZE / b);
      println("b = " + b + ", m = " + m);
    }
  }
}