package integersets;

/**
 * Implementation of the {@code NaiveDynamicFusionNode} data structure, as described in Section 3.4
 * of the report.
 */
public class DynamicFusionNodeDontCaresRank implements RankSelectPredecessorUpdate {
  
  private static final int k = 8;
  private static final int ceilLgK = (int) Math.ceil(Math.log10(k) / Math.log10(2));
  private static final long M = Util.M(k, k * k); // multiplying constant
  private final long[] key = new long[k];
  private long index;
  private int bKey;
  private int n;

  /**
   * Variables for maintaining the rank with don't cares algorithm. 
   */
  private long compressingKey;
  private long branch;
  private long free;

  /**
   * Constructs an empty {@code DynamicFusionNodeDontCaresRank} with capacity for 8 elements.
   */
  public DynamicFusionNodeDontCaresRank() {
    reset();
  }

  @Override
  public void insert(final long x) {
    if (member(x)) {
      return;
    }

    if (size() == k) {
      throw new RuntimeException("Cannot insert. Node is full.");
    }

    final int i = (int) rank(x);
    
    final int j = firstEmptySlot();
    key[j] = x;
    fillSlot(j);
    updateIndex(i, j);
    n++;

    if (size() > 1) {
      // Update:
      // Compressing key
      updateCompressingKey();
      // compressed Keys
      long compressedKeys = compressedKeys();
      // free
      updateFree(compressedKeys);
      // branch
      updateBranch(compressedKeys);
    }

  }

  @Override
  public void delete(final long x) {
    if (!member(x)) {
      return;
    }
    
    if (size() == 1) {
      reset();
      return;
    }

    final int i = (int) rank(x);
    vacantSlot(getIndex(i));
    updateIndex(i);
    n--;
    
    // Update:
    // Compressing key
    updateCompressingKey();
    // compressed Keys
    long compressedKeys = compressedKeys();
    // free
    updateFree(compressedKeys);
    // branch
    updateBranch(compressedKeys);
  }

  @Override
  public long rank(final long x) {
    return dontCaresRank(x);
  }

  @Override
  public Long select(final long rank) {
    if (rank < 0 || rank >= size()) {
      return null;
    }

    return key[getIndex(rank)];
  }

  @Override
  public long size() {
    return n;
  }

  @Override
  public void reset() {
    index = 0L;
    n = 0;
    bKey = -1;

    compressingKey = 0L;
    
    branch = 0L;
    free = -1L;
  }

  /* HELPER METHODS INTRODUCED IN THIS IMPLEMENTATION */

  /**
   * Match subroutine.
   * @param x the key to be match
   * @return the rank of the key it {@code x} has matched
   */
  private int match(final long x) {
    final long xCompressed = compress(x);
    return Util.rankLemma1(xCompressed,
        branch | ((xCompressed * Util.getFields(0, n, k, M)) & free), n, k);
  }

  /**
   * Rank via matching with "don't cares".
   * @param x the query key
   * @return the rank of {@code x} in the set
   */
  private int dontCaresRank(final long x) {
    if (isEmpty()) {
      return 0;
    }

    final int i = match(x);
    final long y = select(i);
    final int comp = Long.compareUnsigned(x, y);

    if (comp == 0) {
      return i;
    }

    final int j = Util.msb(x ^ y);

    if (comp < 0) { // i_0
      return match(x & ~((1L << j) - 1));
    }

    return 1 + match(x | ((1L << j) - 1)); // i_1 + 1
  }

  /**
   * Updates the compressing key after updating the set.
   */
  private void updateCompressingKey() {
    if (n > 1) {
      long res = 0L;
      for (int i = 0; i < n - 1; i++) {
        for (int j = i + 1; j < n; j++) {
          res = Util.setBit(Util.msb(select(i) ^ select(j)), res);
        }
      }
      compressingKey = res;
    } else {
      compressingKey = 0L;
    }
  }

  /**
   * Compresses {@code x}, keeping only the bits at the positions specified in the
   * {@code compressingKey}.
   * 
   * @param x the key to be compressed
   * @return {@code x} after the compression
   */
  private long compress(final long x) {
    long res = 0L;
    long compressingKeyCopy = compressingKey;
    while (compressingKeyCopy != 0) {
      res <<= 1;
      final int bit = Util.msb(compressingKeyCopy);
      res |= Util.bit(bit, x);
      compressingKeyCopy = Util.deleteBit(bit, compressingKeyCopy);
    }
    return res;
  }

  /**
   * Computes all the compressed keys in the set.
   * @return A word containing all the compressed keys in the set.
   */
  private long compressedKeys() {
    long compressedKeys = 0L;
    for (int i = 0; i < n; i++) {
      compressedKeys = Util.setField(i, compress(select(i)), k, compressedKeys);
    }
    return compressedKeys;
  }

  /**
   * Updates branch, using a word, {@code compressedKeys} as parameter.
   * @param compressedKeys A word containing all the compressed keys in the set.
   */
  private void updateBranch(long compressedKeys) {
    branch = compressedKeys & ~free;
  }

  /**
   * Updates the field {@code free} according to the {@code compressedKeys} word provided.
   * @param compressedKeys A word containing all the compressed keys in the set.
   */
  private void updateFree(long compressedKeys) {
    free = dontCares(compressedKeys, 0, k - 1, 0, n);
    if (n < k) { // making the unused rows all 1
      free = free | ~((1L << (k * n)) - 1);
    }
  }

  /**
   * Recursive method to naively compute the "don't cares" position of the compressed keys in the
   * set. The result is stored in {@code free}.
   * 
   * @param free The word where the results are to be stored
   * @param bit The column in {@code compressedKeys} to look at
   * @param lo The lower bound (row in {@code compressedKeys}) considered in the range
   * @param hi The upper bound (row in {@code compressedKeys}) considered in the range
   * @return {@code free} after the given iteration
   */
  private long dontCares(long compressedKeys, long free, final int bit, final int lo,
      final int hi) {
    if (bit == -1) {
      return free;
    }
    // if all bits are the same in all keys, then that position is a don't care for
    // all keys

    int mid = lo; // we start by assuming that everything is 1 at position lo
    while (mid < hi && Util.bit(bit, Util.getField(mid, k, compressedKeys)) == 0) {
      mid++;
    }

    // If all bits are the same in all keys, then that position is a don't care for
    // all keys
    if (mid == lo || mid == hi) {
      for (int i = lo; i < hi; i++) {
        free = Util.setField(i, Util.setBit(bit, Util.getField(i, k, free)), k, free);
        // free = Util.setField(i, Util.getField(i, k, free) & ~(1L << bit), k, free);
      }
      // recursive call with the same range, next least significant bit
      return dontCares(compressedKeys, free, bit - 1, lo, hi);
    } else {
      // don't need to delete anything in dontCares as it is zero.
      // 2 x recursive calls
      return dontCares(compressedKeys, free, bit - 1, lo, mid)
        | dontCares(compressedKeys, free, bit - 1, mid, hi);
    }
  }

  /* HELPER METHODS THAT ARE KEPT FROM THE {@code DynamicFusionNodeBinaryRank} IMPLEMENTATION. */

  /** Returns the index of the first empty slot in KEY.
   *
   * @return the index in KEY of the first empty slot.
   */
  private int firstEmptySlot() {
    final int res = Util.lsb(bKey);
    if (res < k) {
      return res;
    }
    return -1;
  }

  /**
   * Sets position {@code j} in KEY to not empty.
   *
   * @param j the position to be made unavailable
   */
  private void fillSlot(final int j) {
    if (j >= 0 && j < k) {
      bKey = Util.deleteBit(j, bKey);
    } else {
      throw new IndexOutOfBoundsException("j must be between 0 and k (" + k + ")!");
    }
  }

  /**
   * Sets position {@code j}th taken position in KEY to empty.
   *
   * @param j the position to be made available
   */
  private void vacantSlot(final int j) {
    if (j >= 0 && j < k) {
      // j += Util.lsb(~(bKey >>> j));
      bKey = Util.setBit(j, bKey);
    } else {
      throw new IndexOutOfBoundsException("j must be between 0 and k (" + k + ")!");
    }
  }

  /**
   * Helper method to retrieve the position in KEY of a key, given its rank
   * {@code rank}.
   *
   * @param rank The rank of the key in the S
   * @return the index in KEY of the key with rank {@code i}
   */
  private int getIndex(final long rank) {
    return (int) Util.getField((int) rank, ceilLgK, index);
  }

  /**
   * Helper method to maintain the correspondence between the rank of the keys and
   * their real position in KEY. The methods receives the rank {@code rank} of a
   * key and removes such position in Index, keeping all other indices ordered.
   *
   * @param rank the rank of the key that has been put in KEY
   */
  private void updateIndex(final int rank) {
    if (rank >= 0 && rank < k) {
      final long hi = Util.getFields(rank + 1, ceilLgK, index) << (rank * ceilLgK);
      if (rank > 0) {
        final long lo = Util.getFields(0, rank, ceilLgK, index);
        index = hi | lo;
      } else {
        index = hi;
      }
    } else {
      throw new IndexOutOfBoundsException("Invalid rank");
    }
  }

  /**
   * Helper method to maintain the correspondence between the rank of the keys and
   * their real position in KEY. The methods receives the rank {@code rank} of a
   * key and the position where such key is stored in KEY {@code slot} and saves
   * that information in Index.
   *
   * @param rank the rank of the key that has been put in KEY
   * @param slot the real position of the key in KEY
   */
  private void updateIndex(final int rank, final int slot) {
    if (rank >= 0 && rank < k && slot >= 0 && slot < k) {
      final long hi = Util.getFields(rank, ceilLgK, index) << ((rank + 1) * ceilLgK);
      final long mid = Integer.toUnsignedLong(slot) << (rank * ceilLgK);
      if (rank > 0) {
        final long lo = Util.getFields(0, rank, ceilLgK, index);
        index = hi | mid | lo;
      } else {
        index = mid | hi;
      }
    } else {
      throw new IndexOutOfBoundsException("Invalid rank or slot: " + rank + ", " + slot);
    }
  }
}