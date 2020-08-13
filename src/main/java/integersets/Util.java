package integersets;

/**
 * Utility class, containing many helful functions.
 */

public abstract class Util {

  /* BIT OPERATIONS */

  /**
   * Given a 32-bit word, val, return the value (0 or 1) of the d-th digit. Digits
   * are indexed w-1..0.
   * 
   * @param d   the digit index
   * @param A the long value to have the digit extracted from
   * @return 0 or 1 depending on if it's 0 or 1 at the specified index d.
   */
  public static int bit(final int d, final int A) {
    return (A >>> d) & 1;
  }

  /**
   * Given a 64-bit word, val, return the value (0 or 1) of the d-th digit. Digits
   * are indexed w-1..0.
   * 
   * @param d   the digit index
   * @param A the long value to have the digit extracted from
   * @return 0 or 1 depending on if it's 0 or 1 at the specified index d.
   */
  public static int bit(final int d, final long A) {
    return (int) ((A >>> d) & 1);

  }

  /**
   * Sets bit at position {@code d} to 1 and returns the key {@code A}.
   * 
   * @param A the key to have the bit altered
   * @param d the index of the bit to be set to 1
   * @return the key with the bit altered
   */
  public static int setBit(final int d, int A) {
    if (d >= 0 || d < Integer.SIZE) {
      A |=  1 << d;
    }
    return A;
  }

  /**
   * Sets bit at position {@code d} to 1 and returns the key {@code A}.
   * 
   * @param A the key to have the bit altered
   * @param d the index of the bit to be set to 1
   * @return the key with the bit altered
   */
  public static long setBit(final int d, long A) {
    if (d >= 0 || d < Long.SIZE) {
      A |= 1L << d;
    }
    return A;
  }

  /**
   * Sets bit at position {@code d} to 0 and returns the key {@code A}.
   * 
   * @param A the key to have the bit altered
   * @param d the index of the bit to be set to 1
   * @return the key with the bit altered
   */
  public static int deleteBit(final int d, int A) {
    if (d >= 0 || d < Integer.SIZE) {
      A &= ~(1 << d);
    }
    return A;
  }

  /**
   * Sets bit at position {@code d} to 0 and returns the key {@code A}.
   * 
   * @param A the key to have the bit altered
   * @param d the index of the bit to be set to 1
   * @return the key with the bit altered
   */
  public static long deleteBit(final int d, long A) {
    if (d >= 0 || d < Long.SIZE) {
      A &= ~(1L << d);
    }
    return A;
  }

  /* FIELDS OF WORDS */
  
  /* Often we view words as divided into fields of some length f. We then use x(i)_f to denote the
   * ith field, starting from the right with x(0)_f the right most field. Thus x represents the
   * integer E^(w−1)_(i=0) 2^i x(i)1.
   * Note that fields can easily be masked out using regular instructions, e.g
   */

  /**
   * Field retrieval function. This function returns field {@code i} in the word {@code A},
   * whose fields have length {@code f}.
   * @param A The word containing fields
   * @param i The position of the field
   * @param f The length of the fields in {@code A}
   * @return The field at the specified position in the {@code A} word
   */
  public static int getField(final int i, final int f, final int A) {
    return (A >>> (i * f)) & ((1 << f) - 1);
  }

  /**
   * Field retrieval function. This function returns field {@code i} in the word {@code A},
   * whose fields have length {@code f}.
   * @param A The word containing fields
   * @param i The position of the field
   * @param f The length of the fields in {@code A}
   * @return The field at the specified position in the {@code A} word
   */
  public static long getField(final int i, final int f, final long A) {
    return (A >>> (i * f)) & ((1L << f) - 1);
  }

  /**
   * Two-dimensional field retrieval. When viewing words as matrices, the authors use the notation
   * x&lt;i,j&gt_{g*f}, meaning, field {@code j} of field {@code i}, with lengths {@code g} and
   * {@code f} respectively. This corresponds to x&lt;i*g + j&gt_f. For obvious reasons, {@code g}
   * should never be larger than {@code f}.
   * @param A The word containing fields
   * @param i The position of the field in {@code A}
   * @param j The position of the subfield in {@code i}
   * @param g The lenght of the subfield {@code j}
   * @param f The length of fields in {@code A}
   * @return The specified subfield
   */
  public static int getField2d(final int i, final int j, final int g, final int f, final int A) {
    if (g > f) {
      throw new IndexOutOfBoundsException(
        "The length of the subfield must not be larger than the field it belongs to.");
    }
    return getField(i * g + j, f, A);
  }

  /**
   * Two-dimensional field retrieval. When viewing words as matrices, the authors use the notation
   * x&lt;i,j&gt_{g*f}, meaning, field {@code j} of field {@code i}, with lengths {@code g} and
   * {@code f} respectively. This corresponds to x&lt;i*g + j&gt_f. For obvious reasons, {@code g}
   * should never be larger than {@code f}.
   * @param A The word containing fields
   * @param i The position of the field in {@code A}
   * @param j The position of the subfield in {@code i}
   * @param g The lenght of the subfield {@code j}
   * @param f The length of fields in {@code A}
   * @return The specified subfield
   */
  public static long getField2d(final int i, final int j, final int g, final int f, final long A) {
    if (g > f) {
      throw new IndexOutOfBoundsException(
        "The length of the subfield must not be larger than the field it belongs to.");
    }
    return getField(i * g + j, f, A);
  }

  /**
   * Field retrieval function. This function returns fields from {@code i} to {@code j} in the word
   * {@code A}, whose fields have length {@code f}.
   * @param A The word containing fields
   * @param i The smallest field (inclusive), the right most field to be included
   * @param j The largest field (exclusive), the left most field
   * @param f The length of the fields in {@code A}
   * @return A word containing the specified field range shifted to the least significant positions
   */
  public static int getFields(final int i, final int j, final int f, final int A) {
    if (i == 0 && j * f == Integer.SIZE) {
      return A;
    }
    return (A >>> (i * f)) & ((1 << ((j - i) * f)) - 1);
  }

  /**
   * Field retrieval function. This function returns fields from {@code i} to {@code j} in the word
   * {@code A}, whose fields have length {@code f}.
   * @param A The word containing fields
   * @param i The smallest field (inclusive), the right most field to be included
   * @param j The largest field (exclusive), the left most fieldd
   * @param f The length of the fields in {@code A}
   * @return A word containing the specified field range shifted to the least significant positions
   */
  public static long getFields(final int i, final int j, final int f, final long A) {
    if (i == 0 && j * f == Long.SIZE) {
      return A;
    }
    return (A >>> (i * f)) & ((1L << ((j - i) * f)) - 1);
  }

  /**
   * Field retrieval function. This function returns all fields larger than {@code i} (inclusive)
   * in the word {@code A}, whose fields have length {@code f}.
   * @param A The word containing fields
   * @param i The position of the first field to return
   * @param f The length of the fields in {@code A}
   * @return A word containing the remaining fields after the operation
   */
  public static int getFields(final int i, final int f, final int A) {
    return (A >>> (i * f));
  }

  /**
   * Field retrieval function. This function returns all fields larger than {@code i} (inclusive)
   * in the word {@code A}, whose fields have length {@code f}.
   * @param A The word containing fields
   * @param i The position of the first field to return
   * @param f The length of the fields in {@code A}
   * @return A word containing the remaining fields after the operation
   */
  public static long getFields(final int i, final int f, final long A) {
    return (A >>> (i * f));
  }

  /**
   * Field assignment function. This function returns the {@code A} word after overwriting the
   * field at position {@code i} with {@code y}. Only the {@code f} lower bits of {@code y} are
   * considered when setting the field in {@code A}. In other words, the bits between {@code f} and
   * w in {@code y} are disregarded and do not influence the result of this operation.
   * @param A The word containing fields
   * @param i The position of the first field to return
   * @param y The field to be assigned in {@code A}
   * @param f The length of the fields in {@code A}
   * @return returns the word {@code A} after the operation
   */
  public static int setField(final int i, final int y, final int f, final int A) {
    final int m = ((1 << f) - 1) << (i * f);
    return (A & ~m) | (y << (i * f) & m);
  }

  /**
   * Field assignment function. This function returns the {@code A} word after overwriting the
   * field at position {@code i} with {@code y}. Only the {@code f} lower bits of {@code y} are
   * considered when setting the field in {@code A}. In other words, the bits between {@code f} and
   * w in {@code y} are disregarded and do not influence the result of this operation.
   * @param A The word containing fields
   * @param i The position of the first field to return
   * @param y The field to be assigned in {@code A}
   * @param f The length of the fields in {@code A}
   * @return returns the word {@code A} after the operation
   */
  public static long setField(final int i, final long y, final int f, final long A) {
    final long m = ((1L << f) - 1) << (i * f);
    return (A & ~m) | (y << (i * f) & m);
  }

  /**
   * Two-dimensional field assignment. When viewing words as matrices, the authors use the notation
   * x&lt;i,j&gt_{g*f}, meaning, field {@code j} of field {@code i}, with lengths {@code g} and
   * {@code f} respectively. This corresponds to x&lt;i*g + j&gt_f. For obvious reasons, {@code g}
   * should never be larger than {@code f}.
   * This method sets {@code y} to field {@code j}, which is a field in {@code i}, which is a field
   * in {@code A}; and returns {@code A} after the operation.
   * @param A The word containing fields
   * @param i The position of the field in {@code A}
   * @param y The field to be assigned in {@code j}
   * @param j The position of the subfield in {@code i}
   * @param g The lenght of the subfield {@code j}
   * @param f The length of fields in {@code A}
   * @return returns the word {@code A} after the operation
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
   * x&lt;i,j&gt_{g*f}, meaning, field {@code j} of field {@code i}, with lengths {@code g} and
   * {@code f} respectively. This corresponds to x&lt;i*g + j&gt_f. For obvious reasons, {@code g}
   * should never be larger than {@code f}.
   * This method sets {@code y} to field {@code j}, which is a field in {@code i}, which is a field
   * in {@code A}; and returns {@code A} after the operation.
   * @param A The word containing fields
   * @param i The position of the field in {@code A}
   * @param y The field to be assigned in {@code j}
   * @param j The position of the subfield in {@code i}
   * @param g The lenght of the subfield {@code j}
   * @param f The length of fields in {@code A}
   * @return returns the word {@code A} after the operation
   */
  public static long setField2d(final int i, final int j, final long y, final int g, final int f,
        final long A){
    if (g > f) {
      throw new IndexOutOfBoundsException(
        "The length of the subfield must not be larger than the field it belongs to.");
    }
    return setField(i * f + j, y, g, A);
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
    if (blockSize <= 0 || blockSize >= Integer.SIZE) {
      for (int i = Integer.SIZE - 1; i > -1; i--) {
        res.append(bit(i, x));
      }
    } else {
      final int r = Integer.SIZE % blockSize == 0 ? blockSize : Integer.SIZE % blockSize;
      for (int i = Integer.SIZE - 1; i > Integer.SIZE - 1 - r; i--) {
        res.append(bit(i, x));
      }
      for (int i = Integer.SIZE - 1 - r; i > -1; i -= blockSize) {
        res.append("_");
        for (int j = i; j > i - blockSize; j--) {
          res.append(bit(j, x));
        }
      }
    }
    return res.toString();
  }

  /**
   * Returns a binary representation of integer x in a String containing leading
   * zeroes.
   * 
   * @param x the target
   * @return a String representation of {@code A}
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
    if (blockSize <= 0 || blockSize >= Integer.SIZE) {
      for (int i = Long.SIZE - 1; i > -1; i--) {
        res.append(bit(i, x));
      }
    } else {
      final int r = Long.SIZE % blockSize == 0 ? blockSize : Long.SIZE % blockSize;
      for (int i = Long.SIZE - 1; i > Long.SIZE - 1 - r; i--) {
        res.append(bit(i, x));
      }
      for (int i = Long.SIZE - 1 - r; i > -1; i -= blockSize) {
        res.append("_");
        for (int j = i; j > i - blockSize; j--) {
          res.append(bit(j, x));
        }
      }
    }
    return res.append("l").toString();
  }
  
  /**
   * Returns a String representation of the interpretation of the matrix stored in the word
   * {@code A}, with #{@code rows} rows and #{@code columns} columns.
   * @param rows The number of rows
   * @param columns The number of columns
   * @param A The word containing the matrix
   * @return The String representation of the matrix
   */
  public static String matrixToString(int rows, int columns, long A) {
    StringBuilder sb = new StringBuilder("  ");
    for (int c = columns - 1; c >= 0; c--) {
      sb.append(" ").append(c);
    }
    sb.append("\n");
    for (int r = rows - 1; r >= 0; r--) {
      sb.append(r).append(" ");
      long row = Util.getField(r, columns, A);
      for (int c = columns - 1; c >= 0; c--) {
        sb.append(" ").append(Util.bit(c, row));
      }
      sb.append("\n");
    }
    return sb.toString();
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

  /** Returns the index of the most significant bit of the target {@code A}.
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code A}
   */
  public static int msb(final int x) {
    return msbConstant(x);
  }

  /** Returns the index of the most significant bit of the target {@code A}.
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code A}
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
    return Integer.SIZE - 1 - Integer.numberOfLeadingZeros(x);
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
    return Long.SIZE - 1 - Long.numberOfLeadingZeros(x);
  }

  /**
   * Naive way to calculate msb(x) as described in:
   * https://graphics.stanford.edu/~seander/bithacks.html#IntegerLogObvious
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
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
   * Naive way to calculate msb(x) as described in:
   * https://graphics.stanford.edu/~seander/bithacks.html#IntegerLogObvious
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
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
    return r;
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
      return msbLookupDistributedOutput(aux[1]);
    }

    return Integer.SIZE + high;
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
    return r;
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
      return msbLookupDistributedInput(aux[1]);
    }

    return Integer.SIZE + high;
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

    final long hi = ((((((hiPowers - (m * cluster)) & mask) ^ mask) >>> blockSize) * d) & fetch)
        >>> 4 * blockSize;

    if (hi > 0) {
      return 4 + parallelLookup((int) hi);
    }

    final long loPowers = 0b1_00000111_1_00000011_1_00000001_1_00000000L;
    final long lo = ((((((loPowers - (m * cluster)) & mask) ^ mask) >>> blockSize) * d) & fetch)
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
   * Most significant bit in constant time. This implementation follows the Jelani
   * Nelson and Erik Demaine's lecture notes.
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
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
   * Most significant bit in constant time. This implementation follows the Jelani
   * Nelson and Erik Demaine's lecture notes.
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
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

  /**
   * Most significant bit in constant time. Commented version of
   * {@code msbConstant(x)}.
   * 
   * @param x the key to be evaluated
   * @return the index of the most significant bit of {@code x}
   */
  public static int msbConstantCommented(long x) {
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
    // println("Summary of the important bits on the right-most cluster (res >>> (w - sqrt(w))");
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

  /**
   * Produces a {@code long} word with at {@code 1}-bit set every {@code b}
   * position, up to {@code w-positions}. The first set position is the least
   * significant position of the word.
   * 
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

  /**
   * No padding version of rank_lemma_1 Lemma 1: Let mb <= w. If we are given a
   * b-bit number x and a word A with m b-bit numbers stored in sorted order, then
   * in constant time we can find the rank of x in A, denoted rank(x, A). In order
   * for this method to give plausible results, the following requirements *must*
   * be fulfilled: Each b-bit word in A must be prefixed (padded) with a 1, and
   * the words in A must also be sorted.
   * 
   * @param x the query of b size
   * @param A the A word
   * @param m the number of keys in A
   * @param b the length of each key in A
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
   * No padding version of rank_lemma_1 with terminal comments.
   * Lemma 1: Let mb <= w. If we are given a
   * b-bit number x and a word A with m b-bit numbers stored in sorted order, then
   * in constant time we can find the rank of x in A, denoted rank(x, A). In order
   * for this method to give plausible results, the following requirements *must*
   * be fulfilled: Each b-bit word in A must be prefixed (padded) with a 1, and
   * the words in A must also be sorted.
   * 
   * @param x the query of b size
   * @param A the A word
   * @param m the number of keys in A
   * @param b the length of each key in A
   */
  public static int rankLemma1Commented(long x, long A, final int m, final int b) {
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
        final int numClustersLargerThanX = numClustersLeadingBitIs0
            - (lsb(d & M)) / b;
        print("#clusters >x = ");
        println(numClustersLargerThanX);
        println("   rank(x,A) = " + (lsb(d) / b));
        return lsb(d) / b;
      }

      println("   rank(x,A) = " + numClustersLeadingBitIs0);
      return numClustersLeadingBitIs0;
    }
  }

  /**
   * Debugging.
   * 
   * @param args --
   */
  public static void main(final String[] args) {
    final long A = 0b01111111_01111001_01101000_01010100_01001001_01000110_00101010_00000100l;
    final int x = 0b00000000_00000000_00000000_00011110;

    
    rankLemma1Commented(x, A, 8, 8);
  }
}