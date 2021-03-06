package integersets;

import java.util.Comparator;
import java.util.Random;
import java.util.TreeSet;

/**
 * Utility class, containing the helper functions described in Section 3.1 of the report.
 */

public abstract class Util {

  /**
   * This abstract class cannot be instantiated.
   */
  public Util(){}

  /* BIT OPERATIONS */

  /**
   * Given a {@code 32}-bit word, {@code A}, returns the value ({@code 0} or {@code 1}) of the
   * {@code d}-th bit.
   * 
   * @param d The bit index.
   * @param A The int value to have the digit extracted from.
   * @return {@code 0} or {@code 1} depending on if it is {@code 0} or {@code 1} at the specified
   *      index {@code d}.
   */
  public static int bit(final int d, final int A) {
    if (d < 0 || d >= Integer.SIZE) {
      throw new IndexOutOfBoundsException("Query out of bounds.");
    }
    return (A >>> d) & 1;
  }

  /**
   * Given a {@code 64}-bit word, {@code A}, returns the value ({@code 0} or {@code 1}) of the
   * {@code d}-th bit.
   *
   * @param d The bit index.
   * @param A The int value to have the digit extracted from.
   * @return {@code 0} or {@code 1} depending on if it is {@code 0} or {@code 1} at the specified
   *      index {@code d}.
   */
  public static int bit(final int d, final long A) {
    if (d < 0 || d >= Long.SIZE) {
      throw new IndexOutOfBoundsException("Query out of bounds.");
    }
    return (int) ((A >>> d) & 1);

  }

  /**
   * Sets bit at position {@code d} to {@code 1} and returns the key, {@code A}.
   * 
   * @param A The key to have the bit altered.
   * @param d The index of the bit to be set to {@code 1}.
   * @return The key with the bit altered.
   */
  public static int setBit(final int d, int A) {
    if (d >= 0 && d < Integer.SIZE) {
      A |=  1 << d;
    }
    return A;
  }

  /**
   * Sets bit at position {@code d} to {@code 1} and returns the key, {@code A}.
   *
   * @param A The key to have the bit altered.
   * @param d The index of the bit to be set to {@code 1}.
   * @return The key with the bit altered.
   */
  public static long setBit(final int d, long A) {
    if (d >= 0 && d < Long.SIZE) {
      A |= 1L << d;
    }
    return A;
  }

  /**
   * Sets bit at position {@code d} to {@code 0} and returns the key, {@code A}.
   *
   * @param A The key to have the bit altered.
   * @param d The index of the bit to be set to {@code 0}.
   * @return The key with the bit altered.
   */
  public static int deleteBit(final int d, int A) {
    if (d >= 0 && d < Integer.SIZE) {
      A &= ~(1 << d);
    }
    return A;
  }

  /**
   * Sets bit at position {@code d} to {@code 0} and returns the key, {@code A}.
   *
   * @param A The key to have the bit altered.
   * @param d The index of the bit to be set to {@code 0}.
   * @return The key with the bit altered.
   */
  public static long deleteBit(final int d, long A) {
    if (d >= 0 && d < Long.SIZE) {
      A &= ~(1L << d);
    }
    return A;
  }

  /* FIELDS OF WORDS */

  /**
   * Field retrieval function. This function returns field {@code i} in the word {@code A},
   * whose fields have length {@code f}.
   * @param A The word containing fields.
   * @param i The position of the field.
   * @param f The length of the fields in {@code A}.
   * @return The field at the specified position in the {@code A} word.
   */
  public static int getField(final int i, final int f, final int A) {
    if (f < 0 || f >= Integer.SIZE || i < 0 || i * f > Integer.SIZE) {
      throw new IndexOutOfBoundsException("Query out of bounds.");
    }
    return (A >>> (i * f)) & ((1 << f) - 1);
  }

  /**
   * Field retrieval function. This function returns field {@code i} in the word {@code A},
   * whose fields have length {@code f}.
   * @param A The word containing fields.
   * @param i The position of the field.
   * @param f The length of the fields in {@code A}.
   * @return The field at the specified position in the {@code A} word.
   */
  public static long getField(final int i, final int f, final long A) {
    if (f < 0 || f >= Long.SIZE || i < 0 || i * f > Long.SIZE) {
      throw new IndexOutOfBoundsException("Query out of bounds.");
    }
    return (A >>> (i * f)) & ((1L << f) - 1);
  }

  /**
   * Two-dimensional field retrieval. When viewing words as matrices, the authors use the notation
   * {@code x}&#9001;{@code i,j}&#9002;<sub>{@code g}&times;{@code f}</sub>, meaning, field
   * {@code j} of field {@code i}, with lengths {@code g} and {@code f}, respectively. This
   * corresponds to {@code x}&#9001;{@code i}&times;{@code j}+{@code f}&#9002;<sub>{@code f}</sub>.
   *
   * @param A The word containing fields.
   * @param i The position of the field in {@code A}.
   * @param j The position of the subfield in {@code i}.
   * @param g The length of the subfield {@code j}.
   * @param f The length of fields in {@code A}.
   * @return The specified subfield.
   */
  public static int getField2d(final int i, final int j, final int g, final int f, final int A) {
    if (f < 0 || f >= Integer.SIZE || i < 0 || i * g + j > Integer.SIZE || g > f) {
      throw new IndexOutOfBoundsException("Query out of bounds.");
    }
    return getField(i * g + j, f, A);
  }

  /**
   * Two-dimensional field retrieval. When viewing words as matrices, the authors use the notation
   * {@code x}&#9001;{@code i,j}&#9002;<sub>{@code g}&times;{@code f}</sub>, meaning, field
   * {@code j} of field {@code i}, with lengths {@code g} and {@code f}, respectively. This
   * corresponds to {@code x}&#9001;{@code i}&times;{@code j}+{@code f}&#9002;<sub>{@code f}</sub>.
   *
   * @param A The word containing fields.
   * @param i The position of the field in {@code A}.
   * @param j The position of the subfield in {@code i}.
   * @param g The length of the subfield {@code j}.
   * @param f The length of fields in {@code A}.
   * @return The specified subfield.
   */
  public static long getField2d(final int i, final int j, final int g, final int f, final long A) {
    if (f < 0 || f > Long.SIZE || i < 0 || i * g + j > Long.SIZE || g > f) {
      throw new IndexOutOfBoundsException("Query out of bounds.");
    }
    return getField(i * g + j, f, A);
  }

  /**
   * Field retrieval function. This function returns fields from {@code i} to {@code j} in the word
   * {@code A}, whose fields have length {@code f}.
   * @param A The word containing fields.
   * @param i The smallest field (inclusive), the right most field to be included.
   * @param j The largest field (exclusive), the left most field.
   * @param f The length of the fields in {@code A}.
   * @return A word containing the specified field range shifted to the least significant positions.
   */
  public static int getFields(final int i, final int j, final int f, final int A) {
    if (f < 0 || f > Integer.SIZE || i < 0 || i * f > Integer.SIZE || j < 0
        || j * f > Integer.SIZE || j < i || (j - i) * f > Integer.SIZE)  {
      throw new IndexOutOfBoundsException("Query out of bounds.");
    }
    if (i == 0 && j * f == Integer.SIZE) {
      return A;
    }
    return (A >>> (i * f)) & ((1 << ((j - i) * f)) - 1);
  }

  /**
   * Field retrieval function. This function returns fields from {@code i} to {@code j} in the word
   * {@code A}, whose fields have length {@code f}.
   * @param A The word containing fields.
   * @param i The smallest field (inclusive), the right most field to be included.
   * @param j The largest field (exclusive), the left most field.
   * @param f The length of the fields in {@code A}.
   * @return A word containing the specified field range shifted to the least significant positions.
   */
  public static long getFields(final int i, final int j, final int f, final long A) {
    if (f < 0 || f > Long.SIZE || i < 0 || i * f > Long.SIZE || j < 0
        || j * f > Long.SIZE || j < i || (j - i) * f > Long.SIZE) {
      throw new IndexOutOfBoundsException("Query out of bounds.");
    }
    if (i == 0 && j * f == Long.SIZE) {
      return A;
    }
    return (A >>> (i * f)) & ((1L << ((j - i) * f)) - 1);
  }

  /**
   * Field retrieval function. This function returns all fields larger than {@code i} (inclusive)
   * in the word {@code A}, whose fields have length {@code f}.
   * @param A The word containing fields.
   * @param i The position of the first field to return.
   * @param f The length of the fields in {@code A}.
   * @return A word containing the remaining fields after the operation.
   */
  public static int getFields(final int i, final int f, final int A) {
    if (f < 0 || f > Integer.SIZE || i < 0 || i * f > Integer.SIZE) {
      throw new IndexOutOfBoundsException("Query out of bounds.");
    }
    return (A >>> (i * f));
  }

  /**
   * Field retrieval function. This function returns all fields larger than {@code i} (inclusive)
   * in the word {@code A}, whose fields have length {@code f}.
   * @param A The word containing fields.
   * @param i The position of the first field to return.
   * @param f The length of the fields in {@code A}.
   * @return A word containing the remaining fields after the operation.
   */
  public static long getFields(final int i, final int f, final long A) {
    if (f < 0 || f > Long.SIZE || i < 0 || i * f > Long.SIZE) {
      throw new IndexOutOfBoundsException("Query out of bounds.");
    }
    return (A >>> (i * f));
  }

  /**
   * Field assignment function. This function returns the {@code A} word after overwriting the
   * field at position {@code i} with {@code y}. Only the {@code f} lower bits of {@code y} are
   * considered when setting the field in {@code A}. In other words, the bits between {@code f} and
   * w in {@code y} are disregarded and do not influence the result of this operation.
   *
   * @param A The word containing fields.
   * @param i The position of the first field to return.
   * @param y The field to be assigned in {@code A}.
   * @param f The length of the fields in {@code A}.
   * @return returns the word {@code A} after the operation.
   */
  public static int setField(final int i, final int y, final int f, final int A) {
    if (f < 0 || f > Integer.SIZE || i < 0 || i * f > Integer.SIZE) {
      throw new IndexOutOfBoundsException("Query out of bounds.");
    }
    final int m = ((1 << f) - 1) << (i * f);
    return (A & ~m) | (y << (i * f) & m);
  }

  /**
   * Field assignment function. This function returns the {@code A} word after overwriting the
   * field at position {@code i} with {@code y}. Only the {@code f} lower bits of {@code y} are
   * considered when setting the field in {@code A}. In other words, the bits between {@code f} and
   * w in {@code y} are disregarded and do not influence the result of this operation.
   *
   * @param A The word containing fields.
   * @param i The position of the first field to return.
   * @param y The field to be assigned in {@code A}.
   * @param f The length of the fields in {@code A}.
   * @return returns the word {@code A} after the operation.
   */
  public static long setField(final int i, final long y, final int f, final long A) {
    if (f < 0 || f > Long.SIZE || i < 0 || i * f > Long.SIZE) {
      throw new IndexOutOfBoundsException("Query out of bounds.");
    }
    final long m = ((1L << f) - 1) << (i * f);
    return (A & ~m) | (y << (i * f) & m);
  }

  /**
   * Two-dimensional field assignment. When viewing words as matrices, the authors use the notation
   * {@code x}&#9001;{@code i,j}&#9002;<sub>{@code g}&times;{@code f}</sub>, meaning, field
   * {@code j} of field {@code i}, with lengths {@code g} and {@code f}, respectively. This
   * corresponds to {@code x}&#9001;{@code i}&times;{@code j}+{@code f}&#9002;<sub>{@code f}</sub>.
   * <br>
   * This method sets {@code y} to field {@code j}, which is a field in {@code i}, which is a field
   * in {@code A}; and returns {@code A} after the operation.
   * @param A The word containing fields.
   * @param i The position of the field in {@code A}.
   * @param y The field to be assigned in {@code j}.
   * @param j The position of the subfield in {@code i}.
   * @param g The length of the subfield {@code j}.
   * @param f The length of fields in {@code A}.
   * @return returns the word {@code A} after the operation.
   */
  public static int setField2d(final int i, final int j, final int y, final int g, final int f,
        final int A){
    if (g > f) {
      throw new IndexOutOfBoundsException(
        "The length of the subfield must not be larger than the field it belongs to.");
    }
    return setField(i * g + j, y, f, A);
  }

  /**
   * Two-dimensional field assignment. When viewing words as matrices, the authors use the notation
   * {@code x}&#9001;{@code i,j}&#9002;<sub>{@code g}&times;{@code f}</sub>, meaning, field
   * {@code j} of field {@code i}, with lengths {@code g} and {@code f}, respectively. This
   * corresponds to {@code x}&#9001;{@code i}&times;{@code j}+{@code f}&#9002;<sub>{@code f}</sub>.
   * <br>
   * This method sets {@code y} to field {@code j}, which is a field in {@code i}, which is a field
   * in {@code A}; and returns {@code A} after the operation.
   * @param A The word containing fields.
   * @param i The position of the field in {@code A}.
   * @param y The field to be assigned in {@code j}.
   * @param j The position of the subfield in {@code i}.
   * @param g The length of the subfield {@code j}.
   * @param f The length of fields in {@code A}.
   * @return returns the word {@code A} after the operation.
   */
  public static long setField2d(final int i, final int j, final long y, final int g, final int f,
        final long A){
    if (g > f) {
      throw new IndexOutOfBoundsException(
        "The length of the subfield must not be larger than the field it belongs to.");
    }
    return setField(i * f + j, y, g, A);
  }

  /* MSB OPERATIONS */

  /**
   * Returns the index of the most significant bit of the target {@code x}.
   * 
   * @param x The key to be evaluated.
   * @return The index of the most significant set bit of {@code x}.
   */
  public static int msb(final int x) {
    return msbConstant(x);
  }

  /**
   * Returns the index of the most significant bit of the target {@code x}.
   * 
   * @param x The key to be evaluated.
   * @return The index of the most significant set bit of {@code x}.
   */
  public static int msb(final long x) {
    return msbConstant(x);
  }

  /**
   * Least significant set bit of {@code x}.
   * Using msb, we can also easily find the least significant bit of {@code x} as {@code lsb(x) =
   * msb((x - 1) }&oplus;{@code  x)}.
   * 
   * @param x The key to be evaluated.
   * @return The index of the least significant set bit of {@code x}.
   */
  public static int lsb(final int x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    return msb((x - 1) ^ x);
  }

  /**
   * Least significant set bit of {@code x}.
   * Using msb, we can also easily find the least significant bit of {@code x} as {@code lsb(x) =
   * msb((x - 1) }&oplus;{@code  x)}.
   * 
   * @param x The key to be evaluated.
   * @return The index of the least significant set bit of {@code x}.
   */
  public static int lsb(final long x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    return msb((x - 1) ^ x);
  }

  /**
   * Uses the standard library function to calculate {@code msb(x)}.
   * 
   * @param x The key to be evaluated.
   * @return The index of the most significant set bit of {@code x}.
   */
  public static int msbLibrary(final int x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    return Integer.SIZE - 1 - Integer.numberOfLeadingZeros(x);
  }

  /**
   * Uses the standard library function to calculate {@code msb(x)}.
   * 
   * @param x The key to be evaluated.
   * @return The index of the most significant set bit of {@code x}.
   */
  public static int msbLibrary(final long x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    return Long.SIZE - 1 - Long.numberOfLeadingZeros(x);
  }

  /**
   * Naive way to calculate {@code msb(x)} as described in <a href="https://graphics.stanford.edu/~seander/bithacks.html#IntegerLogObvious">https://graphics.stanford.edu/~seander/bithacks</a>.
   * 
   * @param x The key to be evaluated.
   * @return The index of the most significant set bit of {@code x}.
   */
  public static int msbObvious(int x) {
    int r = -1; // r will be lg(v)

    while (x != 0) {
      x >>>= 1;
      r++;
    }
    return r;
  }

  /**
   * Naive way to calculate {@code msb(x)} as described in <a href="https://graphics.stanford.edu/~seander/bithacks.html#IntegerLogObvious">https://graphics.stanford.edu/~seander/bithacks</a>.
   * 
   * @param x The key to be evaluated.
   * @return The index of the most significant set bit of {@code x}.
   */
  public static int msbObvious(long x) {
    int r = -1; // r will be lg(v)

    while (x != 0) {
      x >>>= 1;
      r++;
    }
    return r;
  }

  static int[] LogTable256;

  /**
   * Populates a lookup table for fast queries of msb. Inspired by the implementation from <a href="https://graphics.stanford.edu/~seander/bithacks.html#IntegerLogLookup">https://graphics.stanford.edu/~seander/bithacks</a>.
   */
  private static void generateLookupTable() {
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
   * described in <a href="https://graphics.stanford.edu/~seander/bithacks.html#IntegerLogLookup">https://graphics.stanford.edu/~seander/bithacks.html</a>.
   * 
   * @param x The key to be evaluated.
   * @return The index of the most significant set bit of {@code x}.
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
    return r;
  }

  /**
   * Splits the long {@code x} and then uses the {@code msbLookupDistributedOutput} function to
   * return the most significant set bit of the query.
   * 
   * @param x The key to be evaluated.
   * @return The index of the most significant set bit of {@code x}.
   */
  public static int msbLookupDistributedOutput(final long x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }

    final int[] aux = splitLong(x);
    final int high = msbLookupDistributedOutput(aux[1]);

    if (high == -1) {
      return msbLookupDistributedOutput(aux[0]);
    }

    return Integer.SIZE + high;
  }

  /**
   * Uses the lookup table to return the most significant bit in {@code x} as
   * described in <a href="https://graphics.stanford.edu/~seander/bithacks.html#IntegerLogLookup">https://graphics.stanford.edu/~seander/bithacks</a>.
   * 
   * @param x The key to be evaluated.
   * @return The index of the most significant set bit of {@code x}.
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
    return r;
  }

  /**
   * Splits the long {@code x} and then uses {@code msbLookupDistributedInput} to return
   * the most significant bit.
   * 
   * @param x The key to be evaluated.
   * @return The index of the most significant set bit of {@code x}.
   */
  public static int msbLookupDistributedInput(final long x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }

    final int[] aux = splitLong(x);
    final int high = msbLookupDistributedInput(aux[1]);

    if (high == -1) {
      return msbLookupDistributedInput(aux[0]);
    }

    return Integer.SIZE + high;
  }

  /* MSB LOOKUP HELPER FUNCTIONS */

  /**
   * Given one 64-bit integer x, returns {@code 2} {@code 32}-bit integers in a 2-entry array. The
   * least significant bits of x will be at position 0 of the array, whereas the
   * least significant bits will be at position 1.
   *
   * @param x the long to be split
   * @return the array containing {@code 2} {@code 32}-bit integers.
   */
  public static int[] splitLong(final long x) {
    final int[] res = new int[2];
    res[0] = (int) (x << Integer.SIZE >>> Integer.SIZE);
    res[1] = (int) (x >>> Integer.SIZE);
    return res;
  }

  /**
   * Given {@code 2} {@code 32}-bit integers in an array, returns {@code 1} {@code 64}-bit integer
   * (long) using the bits from those integers. Entry {@code 0} of the {@code 32}-bit int array
   * will be the least significant bits of resulting long.
   *
   * @param x The {@code 32}-bit integers
   * @return The resulting long
   */
  public static long mergeInts(final int[] x) {
    final long res = x[1];
    return (res << Integer.SIZE) | Integer.toUnsignedLong(x[0]);
  }


  /**
   * Most significant bit in constant time. This implementation follows the Jelani Nelson and Erik
   * Demaine's lecture notes.
   * 
   * @param x The key to be evaluated.
   * @return The index of the most significant set bit of {@code x}.
   */
  public static int msbConstant(final int x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    if (x < 0) {
      return Integer.SIZE - 1;
    }

    return msbConstant(Integer.toUnsignedLong(x));
  }

  /**
   * Most significant bit in constant time. This implementation follows the Jelani Nelson and Erik
   * Demaine's lecture notes.
   * 
   * @param x The key to be evaluated.
   * @return The index of the most significant set bit of {@code x}.
   */
  public static int msbConstant(long x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    if (x < 0) {
      return Long.SIZE - 1;
    }

    final int w = Long.SIZE;
    final int blockSize = 8;
    final long F = 0b10000000_10000000_10000000_10000000_10000000_10000000_10000000_10000000L;
    final long C = 0b00000001_00000010_00000100_00001000_00010000_00100000_01000000_10000000L;

    final long summary = ((((x & F) | ((~(F - (x ^ (x & F)))) & F)) >>> (blockSize - 1)) * C)
        >>> (w - blockSize);

    final int cluster = parallelComparison(summary);

    x = (x >>> (cluster * blockSize)) & 0b11111111L;

    final int d = parallelComparison(x);

    return cluster * blockSize + d;
  }

  /* MSB O(1) HELPER FUNCTIONS */

  /**
   * Helper function for the {@code rankLemma1} method. Returns the index of the transition from
   * 0 to 1 in {@code field}, e.g., which powers of two are smaller than the input {@code field}.
   *
   * @param field the field to evaluate
   * @return the index of the transition from 0 to 1 in {@code field}
   */
  private static int parallelComparison(final long field) {

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

    final long hi = ((((((hiPowers - (m * field)) & mask) ^ mask) >>> blockSize) * d) & fetch)
        >>> 4 * blockSize;

    if (hi > 0) {
      return 4 + parallelLookup((int) hi);
    }

    final long loPowers = 0b1_00000111_1_00000011_1_00000001_1_00000000L;
    final long lo = ((((((loPowers - (m * field)) & mask) ^ mask) >>> blockSize) * d) & fetch)
        >>> 4 * blockSize;

    return parallelLookup((int) lo);
  }

  /**
   * Lookup table for the parallel comparison.
   */
  private static int parallelLookup(final int pow) {
    switch (pow) {
      case 0b1111:
        return 3;
      case 0b0111:
        return 2;
      case 0b0011:
        return 1;
      case 0b0001:
        return 0;
      default:
        return -1;
    }
  }

  /**
   * Verbose version of the most significant bit in constant time. Commented version of
   * {@code msbConstant(x)}.
   * 
   * @param x The key to be evaluated.
   * @return The index of the most significant set bit of {@code x}.
   */
  public static int msbConstantVerbose(long x) {
    if (x == 0) {
      return -1; // because 0 has no 1 bits
    }
    if (x < 0) {
      return Long.SIZE - 1;
    }
    // MSB (x)
    final int w = Long.SIZE; // 64
    final int blockSize = (int) Math.sqrt(w); // 8
    final int numBlocks = blockSize; // 8
    // There will be an example in the comments.
    // It this particular example, let's assume w = 16. Let
    // x = 0b0101_0000_1000_1101

    // println("Query");
    // println(bin(x, blockSize));

    // 1. Find F
    // F is a w-bit word (same size as x) with 1 at the most significant positions
    // of each sqrt(w) cluster
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
    // now I know that the last and the second to last clusters are non-zero. In
    // particular because
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

    // 4. We subtract F to the previous result. Since F has 1 set at every msb of
    // every cluster, by
    // subtracting the previous result to F, eg subtracting x after having all the
    // msb of every
    // cluster set to 0 to F, we will have the information about if that cluster was
    // empty (all
    // zeros) or not. This information is given by the bit that remains at the msb.
    // If a 1 remains, then it means that we have subtracted by 0, which means that
    // such cluster was
    // empty. Otherwise, it wasn't empty.
    // In my example:
    // F = 0b1000_1000_1000_1000
    // x^(x&F) = 0b0101_0000_0000_0101
    // F-(x^(x&F)) = 0b0abc_1000_1000_0xyz --> if the cluster was non zero, then
    // there will be a 0
    // after subtracting because msb of each cluster
    // "borrowed". Otherwise, there will be a 1.
    final long difference = F - (x ^ (x & F));

    // 5. Note that in the non empty clusters, there will be the remainder of the
    // difference, which
    // is some noise we do not care about. In fact, the result we are looking for at
    // this stage is
    // a word with 1 at the msb of the clusters that we non empty in x after we
    // cleared the msb
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
    // println("Clusters empty/non-empty");
    // println(bin(res, blockSize));

    // 7. Perfect sketch: I want to have all these leading bits consecutive
    // Lemma: When the bi are i*sqrt(w) + sqrt(w) - 1, there is an m such that
    // multiplying by m makes all the important bits consecutive with no gaps.
    // to do that, we shift to the right result by (block size - 1);

    // In my example:
    // (x&F) | (~(F-(x^(x&F))) & F) = 0b1000_0000_1000_1000
    // >>> (sqrt(w) - 1 = 4 - 1 = 3)= 0b0001_0000_0001_0001

    res >>>= (blockSize - 1);
    // println("Clusters shifted (res >>> (sqrt(w) - 1)");
    // println(bin(res, blockSize));

    // 8. Sketch compression.
    // We find C, which is a word that will put all the important bits in the same
    // cluster.
    // Then we multiply by C.
    // Let the clusters be indexed from most significant position 0 to least
    // significant position i.
    // Let the cluster also be indexed from 0 to i where cluster 0 is the most
    // significant cluster.
    // Them C is word where every cluster i has bit i set, and all the other bits
    // are 0.

    // In my example:
    // C = 0b0001_0010_0100_1000

    final long C = 0b00000001_00000010_00000100_00001000_00010000_00100000_01000000_10000000L;
    res *= C;
    // println("Clusters summarized in the first cluster (res * 00001_00010_...)");
    // println(bin(res, blockSize));

    // 9. The sketch is now compressed, but it is stored in the first cluster. So we
    // shift all the
    // clusters to the right minus one.

    // In my example:
    // (((x&F) | (~(F-(x^(x&F))) & F)) >>> (sqrt(w) - 1)) * C) >>> (w - sqrt(w))
    res >>>= w - blockSize;
    // println("Summary of the important bits on the right-most cluster (res >>> (w
    // - sqrt(w))");
    // println(bin(res, blockSize));

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

    // println("Cluster (7..0):");
    final int cluster = parallelComparison(res);
    // println(cluster);
    // 7..0

    // println("Block shifts>>> to make (64..8..0)= " + (cluster * blockSize));

    // print("The query before the shift: ");
    // println(bin(x, blockSize));

    x = (x >>> (cluster * blockSize)) & 0b11111111L;

    // print("The query AFTER the shift: ");
    // println(bin(x, blockSize));

    // println("the cluster: " + bin(x, blockSize));

    final int d = parallelComparison(x);

    // println("d (7..0): " + d);
    // 7..0

    return cluster * blockSize + d;
  }

  /* RANK LEMMA 1 */

  /**
   * Implementation of Rank Lemma 1. Lemma 1: Let mb &le; w. If we are given a {@code b}-bit number
   * {@code x} and a word {@code A} with {@code m} {@code b}-bit numbers stored in sorted order,
   * then in constant time we can find the rank of {@code x} in {@code A}, denoted
   * {@code rank(x, A)}. In order for this method to return sound results the fields in {@code A}
   * must be sorted.
   *
   * @param x The query of {@code b} length.
   * @param A The word containing the keys to be used in the comparison.
   * @param m The number of keys in {@code A}.
   * @param b The length of each key in {@code A} in bits.
   * @return The rank of {@code x} among the keys in {@code A}.
   */
  public static int rankLemma1(long x, long A, final int m, final int b) {
    long M = M(b, m * b);
    // copying integer

    int numClustersLeadingBitIs0 = m;
    final long leadingBitOfEachCluster = A & (M << (b - 1));
    // Checks the leading bit of each cluster

    if (leadingBitOfEachCluster != 0) {
      numClustersLeadingBitIs0 = lsb(leadingBitOfEachCluster) / b; // works
    }

    if (bit(b - 1, x) == 1) { // leading bit of x is 1
      if (numClustersLeadingBitIs0 == m) {
        // case where all clusters start w/ 0 and the query starts with 1
        return m;
      }

      A = getFields(numClustersLeadingBitIs0, b, A);
      M = getFields(numClustersLeadingBitIs0, b, M);
      x = deleteBit(b - 1, x) * M;
      M <<= b - 1;

      final long d = (A - x) & M;

      if (d != 0) {
        return numClustersLeadingBitIs0 + lsb(d) / b;
      }
      return m;

    } else {
      if (numClustersLeadingBitIs0 == 0) {
        // case where all clusters start w/ 1 and the query starts with 0
        return 0;
      }

      M = getFields(0, numClustersLeadingBitIs0, b, M);
      x *= M;
      M <<= b - 1;
      A = getFields(0, numClustersLeadingBitIs0, b, A) | M;

      final long d = (A - x) & M;

      if (d != 0) {
        return lsb(d) / b;
      }
      return numClustersLeadingBitIs0;
    }
  }

  /**
   * Implementation of Rank Lemma 1, verbose version. Lemma 1: Let mb &le; w. If we are given a
   * {@code b}-bit number {@code x} and a word {@code A} with {@code m} {@code b}-bit numbers
   * stored in sorted order, then in constant time we can find the rank of {@code x} in {@code A},
   * denoted {@code rank(x, A)}. In order for this method to return sound results the fields in
   * {@code A} must be sorted.
   * 
   * @param x The query of {@code b} length.
   * @param A The word containing the keys to be used in the comparison.
   * @param m The number of keys in {@code A}.
   * @param b The length of each key in {@code A} in bits.
   * @return The rank of {@code x} among the keys in {@code A}.
   */
  public static int rankLemma1Verbose(long x, long A, final int m, final int b) {
    // final int A = 0b1110_1101_1010_1001; // compressed keys in descending sorted
    // order!
    // final int blockSize = 4;
    print("           A = ");
    println(bin(A, b));
    // int x = 0b0001; // a key which rank I'm looking for
    // final int x = 0b0100;

    print("           x = ");
    println(bin(x, b));

    long M = M(b, m * b); // copying integer
    print("           M = ");
    println(bin(M, b));

    // First figure out how many clusters of A have leading bit zero.
    // One way to do so is to compute the LSB of A & leading_bits.
    // Alternatively, this number can also be stored and maintained alongside the
    // data structure in
    // an extra variable.

    int numClustersLeadingBitIs0 = m;

    print("    A & 1000 = ");
    final long leadingBitOfEachCluster = A & (M << (b - 1));
    println(bin(leadingBitOfEachCluster, b));

    if (leadingBitOfEachCluster != 0) {
      numClustersLeadingBitIs0 = lsb(leadingBitOfEachCluster) / b;
    }

    print("#lead zeroes = ");
    println(numClustersLeadingBitIs0);

    final int leadingBitOfX = bit(b - 1, x);
    print("x leading bit= ");
    println(leadingBitOfX);

    // If the leading bit of x is one:
    if (leadingBitOfX == 1) {

      if (numClustersLeadingBitIs0 == m) {
        // case where all clusters start w/ 0 and the query starts with 1
        println("   rank(x,A) = " + m);
        return m;
      }

      // 1) remove the clusters of A which have a non-leading bit

      A = getFields(numClustersLeadingBitIs0, b, A);
      print("A w/o 0-leadC= ");
      println(bin(A, b));

      // we need to do the same on M
      M = getFields(numClustersLeadingBitIs0, b, M);
      print("       new M = ");
      println(bin(M, b));

      x = deleteBit(b - 1, x);
      print("x w/o leadBit= ");
      println(bin(x, b));

      x *= M;
      print("    x copied = ");
      println(bin(x, b));

      M <<= b - 1;
      print("        mask = ");
      println(bin(M, b));

      long d = A - x;
      print("   d = A - x = ");
      println(bin(d, b));

      d &= M;
      print("    d & mask = ");
      println(bin(d, b));

      if (d != 0) {
        print("#clusters <x = ");
        final int numClustersSmallerThanX = lsb(d) / b;
        println(numClustersSmallerThanX);
        println("   rank(x,A) = " + (numClustersLeadingBitIs0 + numClustersSmallerThanX));
        return numClustersLeadingBitIs0 + numClustersSmallerThanX;
      }

      println("   rank(x,A) = " + m);
      return m;

    } else {

      if (numClustersLeadingBitIs0 == 0) {
        // case where all clusters start w/ 1 and the query starts with 0
        println("   rank(x,A) = 0");
        return 0;
      }

      // 1) remove the clusters of A which leading bit is one

      A = getFields(0, numClustersLeadingBitIs0, b, A);
      print("A w/o 1-leadC= ");
      println(bin(A, b));

      // we need to do the same on M
      M = getFields(0, numClustersLeadingBitIs0, b, M);
      print("       new M = ");
      println(bin(M, b));

      x *= M;
      print("    x copied = ");
      println(bin(x, b));

      M <<= b - 1;
      print("        mask = ");
      println(bin(M, b));

      A |= M;
      print("    A | mask = ");
      println(bin(A, b));

      long d = A - x;
      print("   d = A - x = ");
      println(bin(d, b));

      d &= M;
      print("    d & mask = ");
      println(bin(d, b));

      if (d != 0) {
        final int numClustersLargerThanX = numClustersLeadingBitIs0 - (lsb(d & M)) / b;
        print("#clusters >x = ");
        println(numClustersLargerThanX);
        println("   rank(x,A) = " + (lsb(d) / b));
        return lsb(d) / b;
      }

      println("   rank(x,A) = " + numClustersLeadingBitIs0);
      return numClustersLeadingBitIs0;
    }
  }

  /* ADDITIONAL HELPER FUNCTIONS */

  /**
   * The helper method below produces a word comprised of {@code w/b} fields of {@code b} bits in
   * length, having each field its least significant bit set to {@code 1}. E.g., the resulting word
   * will have the bits at index zero and every {@code b}-th index set to {@code 1}.
   *
   * @param b The field size in bits.
   * @param w The word size in bits.
   * @return The resulting word.
   */
  public static long M(final int b, final int w) {
    long M = 1L;
    for (int i = 1; i < (w / b); i++) {
      M |= 1L << (i * b);
    }
    return M;
  }

  /**
   * Returns a String representation of {@code x} in binary prefixed by {@code 0b}, including
   * leading zeros and suffixed by {@code l}.
   *
   * @param x The target.
   * @return The string representation of {@code x} in binary.
   */
  public static String bin(final int x) {
    return bin(x, 0);
  }

  /**
   * Returns a String representation of {@code x} in binary prefixed by {@code 0b}, including
   * leading zeros, spaced by {@code _} every {@code f} bits counting from the least significant
   * bit.
   *
   * @param x The target.
   * @param f The field length in bits.
   * @return The string representation of {@code x} in binary.
   */
  public static String bin(final int x, final int f) {
    final StringBuilder res = new StringBuilder("0b");
    if (f <= 0 || f >= Integer.SIZE) {
      for (int i = Integer.SIZE - 1; i > -1; i--) {
        res.append(bit(i, x));
      }
    } else {
      final int r = Integer.SIZE % f == 0 ? f : Integer.SIZE % f;
      for (int i = Integer.SIZE - 1; i > Integer.SIZE - 1 - r; i--) {
        res.append(bit(i, x));
      }
      for (int i = Integer.SIZE - 1 - r; i > -1; i -= f) {
        res.append("_");
        for (int j = i; j > i - f; j--) {
          res.append(bit(j, x));
        }
      }
    }
    return res.toString();
  }

  /**
   * Returns a String representation of {@code x} in binary prefixed by {@code 0b}, including
   * leading zeros and suffixed by {@code l}.
   *
   * @param x The target.
   * @return The string representation of {@code x} in binary.
   */
  public static String bin(final long x) {
    return bin(x, 0);
  }

  /**
   * Returns a String representation of {@code x} in binary prefixed by {@code 0b}, including
   * leading zeros, spaced by {@code _} every {@code f} bits counting from the least significant
   * bit and suffixed by {@code l}.
   *
   * @param x The target.
   * @param f The field length in bits.
   * @return The string representation of {@code x} in binary.
   */
  public static String bin(final long x, final int f) {
    final StringBuilder res = new StringBuilder("0b");
    if (f <= 0 || f >= Integer.SIZE) {
      for (int i = Long.SIZE - 1; i > -1; i--) {
        res.append(bit(i, x));
      }
    } else {
      final int r = Long.SIZE % f == 0 ? f : Long.SIZE % f;
      for (int i = Long.SIZE - 1; i > Long.SIZE - 1 - r; i--) {
        res.append(bit(i, x));
      }
      for (int i = Long.SIZE - 1 - r; i > -1; i -= f) {
        res.append("_");
        for (int j = i; j > i - f; j--) {
          res.append(bit(j, x));
        }
      }
    }
    return res.append("l").toString();
  }

  /**
   * Returns a String representation of the interpretation of the matrix stored in the word
   * {@code A}, with #{@code rows} rows and #{@code columns} columns.
   * @param rows The number of rows.
   * @param columns The number of columns.
   * @param A The word containing the matrix.
   * @return The string representation of the matrix.
   */
  public static String matrixToString(final int rows, final int columns, final long A) {
    final StringBuilder sb = new StringBuilder("  ");
    for (int c = columns - 1; c >= 0; c--) {
      sb.append(" ").append(c);
    }
    sb.append("\n");
    for (int r = rows - 1; r >= 0; r--) {
      sb.append(r).append(" ");
      final long row = Util.getField(r, columns, A);
      for (int c = columns - 1; c >= 0; c--) {
        sb.append(" ").append(Util.bit(c, row));
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * Returns a String representation of the interpretation of the matrix stored in the word
   * {@code A}, with #{@code rows} rows and #{@code columns} columns. The rows and columns
   * will be separated by commas, making this useful to be parsed as CSV values.
   * @param rows The number of rows.
   * @param columns The number of columns.
   * @param A The word containing the matrix.
   * @return The String representation of the matrix.
   */
  public static String matrixToCSVString(final int rows, final int columns, final long A) {
    final StringBuilder sb = new StringBuilder(" ");
    for (int c = columns - 1; c >= 0; c--) {
      sb.append(",").append(c);
    }
    sb.append("\n");
    for (int r = rows - 1; r >= 0; r--) {
      sb.append(r);
      final long row = Util.getField(r, columns, A);
      for (int c = columns - 1; c >= 0; c--) {
        sb.append(",").append(Util.bit(c, row));
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * Returns an array containing {@code n} distinct {@code long}s produced with seed {@code seed}
   * and unsignedly sorted and between {@code 0} and {@code bound}.
   * 
   * @param n The number of {@code long} keys to produce.
   * @param bound The upper bound of the keys (exclusive).
   * @param seed The seed to be used in the pseudo-random generator.
   * @return The array containing the keys generated with the specified parameters.
   */
  public static long[] distinctBoundedSortedLongs(final int n, final long bound, final long seed) {
    if (Long.compareUnsigned(bound, n) < 0) {
      throw new IndexOutOfBoundsException("bound must be larger than n");
    }
    final Random rand = new Random(seed);
    final TreeSet<Long> keys = new TreeSet<>(new Comparator<Long>() {
      @Override
      public int compare(final Long x, final Long y) {
        return Long.compareUnsigned(x, y);
      }
    });

    while (keys.size() < n) {
      keys.add((Long.remainderUnsigned(rand.nextLong(), bound)));
    }
    final long[] res = new long[n];
    int i = 0;
    for (final long key : keys) {
      res[i] = key;
      i++;
    }

    return res;
  }

  /**
   * Returns an array containing {@code n} distinct {@code long}s produced with seed {@code 42}
   * and unsignedly sorted and between {@code 0} and {@code bound}.
   * 
   * @param n The number of {@code long} keys to produce.
   * @param bound The upper bound of the keys (exclusive).
   * @return The array containing the keys generated with the specified parameters.
   */
  public static long[] distinctBoundedSortedLongs(final int n, final long bound) {
    return distinctBoundedSortedLongs(n, bound, 42);
  }

  /**
   * Returns an array containing {@code n} distinct {@code long}s produced with seed {@code seed}
   * and unsignedly sorted.
   * 
   * @param n The number of {@code long} keys to produce.
   * @param seed the seed to be used in the pseudo-random generator.
   * @return The array containing the keys generated with the specified parameters.
   */
  public static long[] distinctSortedLongs(final int n, final long seed) {
    return distinctBoundedSortedLongs(n, -1, seed);
  }

  /**
   * Returns an array containing {@code n} distinct {@code long}s produced with seed {@code 42}
   * and unsignedly sorted.
   * 
   * @param n The number of {@code long} keys to produce.
   * @return The array containing the keys generated with the specified parameters.
   */
  public static long[] distinctSortedLongs(final int n) {
    return distinctSortedLongs(n, 42);
  }

  /**
   * Shortcut function to print to the terminal.
   * @param o The object to be printed.
   */
  public static void print(final Object o) {
    System.out.print(o);
  }

  /**
   * Shortcut function to print to the terminal.
   * @param o The object to be printed.
   */
  public static void println(final Object o) {
    System.out.println(o);
  }

}