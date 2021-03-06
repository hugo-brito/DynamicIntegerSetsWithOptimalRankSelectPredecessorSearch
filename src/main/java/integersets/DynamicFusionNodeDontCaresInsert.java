package integersets;

/**
 * Implementation of the {@code DynamicFusionNodeDontCaresInsert} data structure, as described in
 * Section 3.6 of the report.
 */

public class DynamicFusionNodeDontCaresInsert implements RankSelectPredecessorUpdate {
  
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
   * Constructs an empty {@code DynamicFusionNodeDontCaresInsert} with capacity for 8 elements.
   */
  public DynamicFusionNodeDontCaresInsert() {
    reset();
  }

  @Override
  public void insert(final long x) {
    int rank = 0;
    if (!isEmpty()) {
      // Run rank(x). If x is already a member, do nothing. Otherwise, continue.
      int i = match(x);
      final long y = select(i);
      final int comp = Long.compareUnsigned(x, y);
      if (comp == 0) { // already in the set
        return;
      }
      if (size() == k) {
        throw new RuntimeException("Cannot insert. Node is full.");
      }
      final int j = Util.msb(x ^ y);
      // rank of j among the significant positions // works but no guarantees about the running time
      final int h = Long.bitCount(compressingKey & ((1L << j) - 1));
      final int i_0 = match(x & ~((1L << j) - 1));
      final int i_1 = match(x | ((1L << j) - 1));
      rank = (comp < 0 ? i_0 : i_1 + 1); // rank of x
      final long matrixM_h = matrixM(h); // matrix where only column h is set

      if (Util.bit(j, compressingKey) != 1) {
        // If j is not yet a significant position, mark it as a significant position.
        compressingKey = Util.setBit(j, compressingKey);
        // Since the compressing key has been updated, we need to add a new column of 0s in branch
        // and a column of 1s in free.
        insertAndInitializeColumn(h);
      }

      // fixing the range of keys i_0 ... i_1 in the j column
      // column h for the rows i_0... i_1 now is a "we care", so we update them
      // we need to fix column h for the range of keys i_0, i_1.
      final long matrixMi0_Mi1_h = matrixMRowRange(i_0, i_1) & matrixM_h;
      free &= ~matrixMi0_Mi1_h;
      branch |= (matrixMi0_Mi1_h * Util.bit(j, y));

      // Making room for hat(x^?) with rank r in branch and free = adding a row
      insertRow(rank);

      // if j was already a significant bit, then i is just match(x), that is, i is
      // the position of y
      // if j was not already a significant bit, then i is the new position of y (so
      // either it is the same, or it was shifted by 1).
      if (comp < 0) { // then x < y. So y is the succ of x. we increment i
        i++;
      }

      // Fixing row rank (the new key):
      branch = Util.setField(rank, ~((1L << h) - 1), k, branch);
      free = Util.setField(rank, (1L << h) - 1, k, free);

      // set the bit h by reading bit j in x:
      branch = Util.setField2d(rank, h, (long) Util.bit(j, x), 1, k, branch);
      free = Util.setField2d(rank, h, 0L, 1, k, free);

      // Get h+1 ... k-1 bits of row r // Deleting h+1...k-1 bits of r:
      long rowR = Util.getField(rank, k, branch) & ((1L << (h + 1)) - 1);
      // copy the high (k - h - 1) bits of row i:
      long rowI = Util.getField(i, k, branch) & ~((1L << (h + 1)) - 1);
      // we merge both results and write it to branch
      branch = Util.setField(rank, rowI | rowR, k, branch);

      // Get h+1 ... k-1 bits of row r // Deleting h+1...k-1 bits of r:
      rowR = Util.getField(rank, k, free) & ((1L << (h + 1)) - 1);
      // we get the field we want to copy from; we keep only the h+1...k-1 bits of i:
      rowI = Util.getField(i, k, free) & ~((1L << (h + 1)) - 1);
      // we merge both results and write it to free
      free = Util.setField(rank, rowI | rowR, k, free);
    }

    final int indexInKey = firstEmptySlot();
    key[indexInKey] = x;
    fillSlot(indexInKey);
    updateIndex(rank, indexInKey);
    n++;

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
    final long compressedKeys = compressedKeys();
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
   * Returns a word which when interpreted as a {@code k * k} matrix has only column {@code h} set.
   * @param h the index of the column with the bits set
   * @return the resulting word
   */
  private long matrixM(final int h) {
    return M << h;
  }

  /**
   * Returns a word which when interpreted as a {@code k * k} matrix will have the bits in the
   * range of columns between {@code lo} (inclusive) and {@code hi} (inclusive) set.
   * @param lo the low boundary (inclusive)
   * @param hi the high boundary (inclusive)
   * @return the resulting word
   */
  private long matrixMColumnRange(final int lo, final int hi) {
    if (lo == 0 && hi == k - 1) {
      return -1;
    } else {
      return matrixM(hi + 1) - matrixM(lo);
    }
  }

  /**
   * Updates {@code branch} and {@code free} to include a new column {@code h},
   * setting the default values in both words.
   *
   * @param h the index of the column to be added
   */
  private void insertAndInitializeColumn(final int h) {

    final long Mlo = matrixMColumnRange(0, h - 1);
    final long Mhi = matrixMColumnRange(h, k - 1);
    final long matrixM_h = matrixM(h);

    // shift all columns >= h one to the left
    branch = (branch & Mlo) | (((branch & Mhi) << 1) & ~Mlo);
    free = (free & Mlo) | (((free & Mhi) << 1) & ~Mlo);

    // adding the default value in both matrices, initializing the column.
    // In branch that value is 0.
    branch &= ~matrixM_h;
    // in free this is 1 (we don't care).
    free |= matrixM_h;
  }

  /**
   * Returns a word which when interpreted as a {@code k * k} matrix will have the bits in the
   * range of rows between {@code lo} (inclusive) and {@code hi} (inclusive) set. 
   * @param lo the low boundary (inclusive)
   * @param hi the high boundary (inclusive)
   * @return the resulting word
   */
  private long matrixMRowRange(final int lo, final int hi) {
    if (hi < lo) {
      return 0;
    }
    return ((-1L) << (lo * k)) & (-1L >>> ((k - hi - 1) * k));
  }

  /**
   * Updates {@code branch} and {@code free} to include a new row with rank
   * {@code rank}, setting the default values in both words.
   *
   * @param rank the index of the column to be added
   */
  private void insertRow(final int rank) {
    final long Mlo = matrixMRowRange(0, rank - 1);
    final long Mhi = matrixMRowRange(rank, k - 1);

    branch = (branch & Mlo) | ((branch & Mhi) << k);
    free = (free & Mlo) | ((free & Mhi) << k);
  }

  /**
   * Verbose version of the {@code insert} method. Prints to the terminal the changes made
   * internally in the data structure while executing the method. Useful for debugging.
   * @param x The query.
   */
  public void insertVerbose(final long x) {
    Util.println("Inserting key x = " + Util.bin(x, k));

    int rank = 0;
    Util.println("Initializing rank(x) = " + rank);

    if (!isEmpty()) {
      // Run rank(x). If x is already a member, do nothing. Otherwise, continue.
      Util.println("The set is not empty. Running rank.");

      int i = match(x);
      Util.println("i = match(x) = " + i);

      final long y = select(i);
      Util.println("y = select(i) = " + Util.bin(y, k));


      final int comp = Long.compareUnsigned(x, y);
      Util.println("comp = Long.compareUnsigned(x, y) = " + comp);

      if (comp == 0) { // already in the set
        Util.println("comp = 0. x is already in the set. Returning.");
        return;
      }

      if (size() == k) {
        Util.println("size() = " + size() + " | the set is full. Throwing exception.");
        throw new RuntimeException("Cannot insert. Node is full.");
      }

      final int j = Util.msb(x ^ y);
      Util.println("j = Util.msb(x ^ y) = " + j);

      // rank of j among the significant positions // works but no guarantees about the running time
      final int h = Long.bitCount(compressingKey & ((1L << j) - 1));
      Util.println("h = rank(j) in the compressing key = " + h);

      final int i_0 = match(x & ~((1L << j) - 1));
      Util.println("i_0 = " + i_0);

      final int i_1 = match(x | ((1L << j) - 1));
      Util.println("i_1 = " + i_1);

      rank = (comp < 0 ? i_0 : i_1 + 1); // rank of x
      Util.println("rank = (comp < 0 ? i_0 : i_1 + 1) = " + rank);

      final long matrixM_h = matrixM(h); // matrix where only column h is set
      // Util.println("matrixM(h):\n" + Util.matrixToString(k, k, matrixM_h));
      Util.println("matrixM(h):\n" + Util.matrixToCSVString(k, k, matrixM_h));

      if (Util.bit(j, compressingKey) != 1) {
        // If j is not yet a significant position, then we have to do some work:
        // mark it as a significant position.
        // (This is so that the compression function will compute correct sketches in
        // the future)
        compressingKey = Util.setBit(j, compressingKey);
        Util.println("j was not a significant position in the compressing key.");
        Util.println("Updating compressing key:\n" + Util.bin(compressingKey, k));

        /*
        Since the compressing key has been updated, we need to add a new column of 0s in branch and
        a column of 1s in free.
        */
        insertAndInitializeColumn(h);
        Util.println("Inserting column h = " + h + " in branch and free:");
        // Util.println("branch:\n" + Util.matrixToString(k, k, branch));
        Util.println("branch:\n" + Util.matrixToCSVString(k, k, branch));
        // Util.println("free:\n" + Util.matrixToString(k, k, free));
        Util.println("free:\n" + Util.matrixToCSVString(k, k, free));
      }

      // fixing the range of keys i_0 ... i_1 in the j column
      // column h for the rows i_0... i_1 now is a "we care", so we update them
      // we need to fix column h for the range of keys i_0, i_1.
      final long matrixMi0_Mi1_h = matrixMRowRange(i_0, i_1) & matrixM_h;
      Util.println("Updating the column h in i_0 and i_1 with the mask:");
      // Util.println("matrixMi0_Mi1(h):\n" + Util.matrixToString(k, k, matrixMi0_Mi1_h));
      Util.println("matrixMi0_Mi1(h):\n" + Util.matrixToCSVString(k, k, matrixMi0_Mi1_h));

      free &= ~matrixMi0_Mi1_h;
      branch |= (matrixMi0_Mi1_h * Util.bit(j, y));
      Util.println("Branch and free after this update:");
      // Util.println("branch:\n" + Util.matrixToString(k, k, branch));
      Util.println("branch:\n" + Util.matrixToCSVString(k, k, branch));
      // Util.println("free:\n" + Util.matrixToString(k, k, free));
      Util.println("free:\n" + Util.matrixToCSVString(k, k, free));


      // Making room for hat(x^?) with rank r in branch and free = adding a row
      insertRow(rank);
      Util.println("Inserting a new row for the new key x in branch and free:");
      // Util.println("branch:\n" + Util.matrixToString(k, k, branch));
      Util.println("branch:\n" + Util.matrixToCSVString(k, k, branch));
      // Util.println("free:\n" + Util.matrixToString(k, k, free));
      Util.println("free:\n" + Util.matrixToCSVString(k, k, free));

      // if j was already a significant bit, then i is just match(x), that is, i is
      // the position of y
      // if j was not already a significant bit, then i is the new position of y (so
      // either it is the same, or it was shifted by 1).
      if (comp < 0) { // then x < y. So y is the succ of x. we increment i
        i++;
        Util.println("x < y, so rank(y)++ = " + i);
      }

      // Fixing row rank (the new key):
      // Branch:
      // 1. The values of the bits in positions between 0 and h-1 are set to zero:
      branch = Util.setField(rank, ~((1L << h) - 1), k, branch);

      // 2. set the bit h by reading bit j in x:
      // We read x<j>_1 and store it at position h:
      branch = Util.setField2d(rank, h, (long) Util.bit(j, x), 1, k, branch);

      // 3. We copy the bit values in positions between h+1 and k-1 from the \hat y^? to the
      // same positions:
      // Get h+1 ... k-1 bits of row r // Deleting h+1...k-1 bits of r:
      long rowR = Util.getField(rank, k, branch) & ((1L << (h + 1)) - 1);
      // copy the high (k - h - 1) bits of row i:
      long rowI = Util.getField(i, k, branch) & ~((1L << (h + 1)) - 1);
      // we merge both results
      // and write it to branch
      branch = Util.setField(rank, rowI | rowR, k, branch);

      Util.println("branch after updating row rank = " + rank);
      // Util.println(Util.matrixToString(k, k, branch));
      Util.println(Util.matrixToCSVString(k, k, branch));


      // Free:
      // 1. The values of the bits in positions between 0 and h-1 are set to one:
      free = Util.setField(rank, (1L << h) - 1, k, free);

      // 2. We set position h to zero:
      free = Util.setField2d(rank, h, 0L, 1, k, free);

      // 3. We copy the bit values in positions between $h+1$ and $k-1$ from the $\hat y^?$ to the
      // same positions:
      // Get h+1 ... k-1 bits of row r // Deleting h+1...k-1 bits of r:
      rowR = Util.getField(rank, k, free) & ((1L << (h + 1)) - 1);
      // we get the field we want to copy from; we keep only the h+1...k-1 bits of i:
      rowI = Util.getField(i, k, free) & ~((1L << (h + 1)) - 1);
      // we merge both results
      // and write it to free
      free = Util.setField(rank, rowI | rowR, k, free);

      Util.println("free after updating row rank = " + rank);
      // Util.println(Util.matrixToString(k, k, free));
      Util.println(Util.matrixToCSVString(k, k, free));
    }

    final int indexInKey = firstEmptySlot();
    key[indexInKey] = x;
    fillSlot(indexInKey);
    updateIndex(rank, indexInKey);
    n++;

  }

  /* HELPER METHODS THAT ARE KEPT FROM THE {@code DynamicFusionNodeDontCaresRank} IMPLEMENTATION. */

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
   * @param x The key to be compressed.
   * @return {@code x} after the compression.
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
  private void updateBranch(final long compressedKeys) {
    branch = compressedKeys & ~free;
  }

  /**
   * Updates the field {@code free} according to the {@code compressedKeys} word
   * provided.
   * 
   * @param compressedKeys A word containing all the compressed keys in the set.
   */
  private void updateFree(final long compressedKeys) {
    free = dontCares(compressedKeys, 0, k - 1, 0, n);
    if (n < k) { // making the unused rows all 1
      free = free | ~((1L << (k * n)) - 1);
    }
  }

  /**
   * Recursive method to naively compute the "don't cares" position of the
   * compressed keys in the set. The result is stored in {@code free}.
   *
   * @param free The word where the results are to be stored
   * @param bit  The column in {@code compressedKeys} to look at
   * @param lo   The lower bound (row in {@code compressedKeys}) considered in the
   *             range
   * @param hi   The upper bound (row in {@code compressedKeys}) considered in the
   *             range
   * @return {@code free} after the given iteration
   */
  private long dontCares(final long compressedKeys, long free, final int bit, final int lo,
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

  /*
   * HELPER METHODS THAT ARE KEPT FROM THE {@code DynamicFusionNodeBinaryRank} IMPLEMENTATION.
   */

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