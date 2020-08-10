package integersets;

/** This version of DynamicFusionNode iterates from the DynamicFusionNodeBinaryRank by introducing
 * key sketching and "don't cares", as described in pages 7--9 of the paper.
 * The sketches and the "don't cares" are stored in 2 words of k^2 size. For this reason, we limit
 * k to 8, because sqrt(64) = 8, and k must also be a power of 2. This way we maximize the number
 * of keys in the node but keep the 
 */

public class DynamicFusionNodeDontCaresInsert implements RankSelectPredecessorUpdate {
  
  private static final int k = 8;
  private static final int ceilLgK = (int) Math.ceil(Math.log10(k) / Math.log10(2));
  private final long[] key = new long[k];
  private long index;
  private int bKey;
  private int n;

  /**
   * Variables for maintaining the rank with don't cares algorithm. 
   */
  private long compressingKey;
  private long compressedKeys;
  private long dontCares;
  private long branch;
  private long free;

  /**
   * Builds an empty DynamicFusionNodeDontCares.
   */
  public DynamicFusionNodeDontCaresInsert() {
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

    // Update:
    // Compressing key
    updateCompressingKey();
    // compressed Keys
    updateKeyCompression();
    // dontcares
    updadeDontCares();
    // branch
    // free
    updateBranchAndFree();

  }

  @Override
  public void delete(final long x) {
    if (!member(x)) {
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
    updateKeyCompression();
    // dontcares
    updadeDontCares();
    // branch
    // free
    updateBranchAndFree();
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
    index = 0;
    n = 0;
    bKey = -1;

    compressingKey = 0;
    compressedKeys = 0;
    dontCares = 0;

    branch = 0;
    free = 0;
  }

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
  private void vacantSlot(int j) {
    if (j >= 0 && j < k) {
      // j += Util.lsb(~(bKey >>> j));
      bKey = Util.setBit(j, bKey);
    } else {
      throw new IndexOutOfBoundsException("j must be between 0 and k (" + k + ")!");
    }
  }

  /**
   * Helper method to retrieve the position in KEY of a key, given its rank {@code rank}.
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
   * their real position in KEY. The methods receives the rank {@code rank} of a key
   * and the position where such key is stored in KEY {@code slot} and saves that
   * information in Index.
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

  /**
   * Helper method than provides the rank of a key {@code x} resorting to binary
   * search. For this reason, it takes O(lg N) time.
   * 
   * @param x the key to be used to compute the rank
   * @return the rank of {@code x} in S
   */
  private int binaryRank(final long x) {
    if (isEmpty()) {
      return 0;
    }

    int lo = 0; // indices of the KEY array.
    int hi = n - 1;

    while (lo <= hi) {
      final int mid = lo + ((hi - lo) / 2);

      final int compare = Long.compareUnsigned(x, select(mid));

      if (compare < 0) {
        hi = mid - 1;

      } else if (compare == 0) {
        return mid;

      } else {
        lo = mid + 1;
      }
    }
    return lo;
  }

  private int match(long x) {
    long xCompressed = compress(x);
    return Util.rankLemma1(xCompressed, branch | ((xCompressed * Util.M(k, n * k)) & free), n, k);
  }

  private int dontCaresRank(final long x) {
    if (isEmpty()) {
      return 0;
    }

    int i = match(x);
    long y = select(i);
    int comp = Long.compareUnsigned(x, y);
        
    if (comp == 0) {
      return i;
    }

    int j = Util.msb(x ^ y);

    if (comp < 0) {
      return match(x & ~((1L << j) - 1));
    }

    return 1 + match(x | ((1L << j) - 1));
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
    }
  }

  /**
   * Compresses {@code x}, keeping only the bits at the positions specified in the
   * {@code compressingKey}.
   * @param x the key to be compressed
   * @return the compressed key
   */
  private long compress(final long x) {
    long res = 0L;
    long compressingKeyCopy = compressingKey;
    while (compressingKeyCopy != 0) {
      res <<= 1;
      int bit = Util.msb(compressingKeyCopy);
      res |= Util.bit(bit, x);
      compressingKeyCopy = Util.deleteBit(bit, compressingKeyCopy);
    }
    return res;
  }

  private void updateKeyCompression() {
    for (int i = 0; i < n; i++) {
      compressedKeys = Util.setField(i, compress(select(i)), k, compressedKeys);
    }
  }

  private void updateBranchAndFree() {
    int numSignificantBits = Long.bitCount(compressingKey); // how many bits are set in the
    // compressing key

    for (int i = 0; i < n; i++) {
      long compKey = Util.getField(i, k, compressedKeys);
      long dontCare = Util.getField(i, k, dontCares);
      long keyBranch = 0L;
      long keyFree = 0L;

      for (int bit = 0; bit < numSignificantBits; bit++) {
        if (Util.bit(bit, dontCare) == 1) { // it's a don't care
          // we set the bit in the free field
          keyFree = Util.setBit(bit, keyFree);
        } else { // we care
          if (Util.bit(bit, compKey) == 1) {
            keyBranch = Util.setBit(bit, keyBranch);
          }
        }
      }

      branch = Util.setField(i, keyBranch, k, branch);
      free = Util.setField(i, keyFree, k, free);
    }
  }

  private void updadeDontCares() {
    dontCares = dontCares(0, k - 1, 0, n);
  }

  /**
   * Finds the how many compressed keys have {@code bit} set to zero. Since bits can either be zero
   * or one, we can easily compute the which keys start with one.
   * @param compressedKeys The array containing the compressed keys
   * @param bit The bit to look at
   * @param lo The minimum index to look at in compressed keys
   * @param hi The maximum index to look at in compressed keys
   */
  private long dontCares(long dontCares, int bit, int lo, int hi) {
    if (bit == -1) {
      return dontCares;
    }
    // if all bits are the same in all keys, then that position is a don't care for all keys

    int mid = lo; // we start by assuming that everything is 1 at position lo
    while (mid < hi && Util.bit(bit, Util.getField(mid, k, compressedKeys)) == 0) {
      mid++;
    }
    
    // If all bits are the same in all keys, then that position is a don't care for all keys
    if (mid == lo || mid == hi) {
      for (int i = lo; i < hi; i++) {
        dontCares = Util.setField(i, Util.setBit(bit, Util.getField(i, k, dontCares)), k,
        dontCares);
      }
      // recursive call with the same range, next least significant bit
      return dontCares(dontCares, bit - 1, lo, hi);
    } else {
      // don't need to delete anything in dontCares as it is zero.

      // 2 x recursive calls
      return dontCares(dontCares, bit - 1, lo, mid)
          | dontCares(dontCares, bit - 1, mid, hi);
    }
  }
      
  /**
   * Debugging.
   * 
   * @param args --
   */
  public static void main(final String[] args) {
  }
}