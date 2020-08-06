package integersets;

/** This version of DynamicFusionNode iterates from the DynamicFusionNodeBinaryRank by introducing
 * key sketching and "don't cares", as described in pages 7--9 of the paper.
 * The sketches and the "don't cares" are stored in 2 words of k^2 size. For this reason, we limit
 * k to 8, because sqrt(64) = 8, and k must also be a power of 2. This way we maximize the number
 * of keys in the node but keep the 
  */

public class DynamicFusionNodeDontCares implements RankSelectPredecessorUpdate {
  
  private final int k = 8;
  private final int ceilLgK = (int) Math.ceil(Math.log10(k) / Math.log10(2));
  private final long[] key = new long[k];
  private long index;
  private int bKey;
  private int n;

  /**
   * Builds an empty DynamicFusionNodeDontCares.
   */
  public DynamicFusionNodeDontCares() {
    index = 0;
    n = 0;
    bKey = -1;
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
  }

  @Override
  public long rank(final long x) {
    return binaryRank(x);
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
    n = 0;
    bKey = -1;
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
      throw new IndexOutOfBoundsException("Invalid rank or slot");
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
 
  /*
  Overall Rank Algorithm
        
  Ingredients:
  - sketching in the same style as fusion trees ---  store this result in a word BRANCH
  - don't cares (introduced by patrascu and thorup) --- store this result in a word FREE
  - rankLemma1 (already implemented)
  
  How to:
    1 .Sketching:
    For a given node, compute the "significant" bit indices. A "significant" bit index is the
    most significant bit of the XOR of two keys. A naive algorithm is to XOR all the
    combinations of keys and compute the their msb, have a local variable where we set the bits
    we find to be significant, which we can then read from.
    2 .Don't cares:
        We assume keys to be sorted and compressed, for instance in an array (done in the previous
        step)
        The algorithm consists of the following recursion:
        - Starting from the most significant bit of the sketch, if all bits are the same in all
        keys, then that position is a don't care for all keys
        - If one or more bits differ at that position, we care for that position.
        - Recurse, grouping the sketches that have the same bit at the position we looked that.
        3 .We now have the sketches and we also know which positions are the don't cares. We need
        now to store this information in BRANCH and FREE:
        - Given a key y, and we know its rank i in S (the node), at every "significant" position
        h:
        - If h is not a don't care, we store the bit of that position of y at position
        <i,h>_k*1 of BRANCH, and we set <i,h>_k*1 of FREE to zero.
        - Otherwise, h is a don't care, so we set <i,h>_k*1 of BRANCH to zero and <i,h>_k*1 of
        FREE to 1.
        Note how subtle this is: we store exactly as many positions per key as we have keys in the
        set, hence the k*k, and hence why here a field in both BRANCH and FREE is of length k*1.
        Now some math, k^2 must fit in 64 bits, and k must also be a power of 2: sqrt(64) = 8 which
        is also a power of 2. So here we have another constraint in regards to k, which until now
        could be up to 16, now it can only be 8.
        4 .We define a subroutine match(x), which uses rank lemma 1 (expression is on page 9)
        5 .Having match defined, we can compute the overall rank algorithm, which is also given the
        steps in the paper.
  */
  
  /**
   * Given key {@code x}, this function compresses {@code x}, keeping only the bits at the
   * positions specified in {@code significantBits}. It assumes that {@code significantBits} is
   * sorted.
   * @param x the key to be compressed
   * @param significantBits the indices of the bits to keep in the compressed key
   * @return the compressed key
   */
  public static long compress(final long x, long significantBits) {
    long res = 0L;
    while (significantBits != 0) {
      res <<= 1;
      int bit = Util.msb(significantBits);
      res |= Util.bit(bit, x);
      significantBits = Util.deleteBit(bit, significantBits);
    }
    return res;
  }

  /**
   * Returns the branching positions of all keys in the provided array. The result is computed and
   * stored in a bit vector. If a given bit is
   * @param keys the keys to have their branching bits checked for
   * @return all the significant bits for the given set of keys
   */
  public static long significantBits(final long[] keys) {
    long res = 0L;
    for (int i = 0; i < keys.length - 1; i++) {
      for (int j = i + 1; j < keys.length; j++) {
        res = Util.setBit(Util.msb(keys[i] ^ keys[j]), res);
      }
    }
    return res;
  }

  public static void dontCares(final long[] compressedKeys) {


  }
        
  /**
   * Debugging.
   * 
   * @param args --
   */
  public static void main(final String[] args) {
    long[] keys = new long[]{
      0b0000,
      0b0010,
      // 0b0101,
      0b1100,
      0b1111
    };

    long significantBits = significantBits(keys);
    
    Util.println(Util.bin(significantBits, 4));

    long[] compressedKeys = new long[4];
    for (int i = 0; i < keys.length; i++){
      compressedKeys[i] = compress(keys[i], significantBits);
    }
    // - Starting from the most significant bit of the sketch, if all bits are the same in all
    // keys, then that position is a don't care for all keys

    long significantBitsBackup = significantBits;
    int bitCount = Long.bitCount(significantBits);
    // for (int i = Util.msb(significantBits); i <= bitCount; ) { // iterate the 
    //   // - If one or more bits differ at that position, we care for that position.
    //   boolean isADontCare = true;

    //   for (int i = )

    // }
    // - Recurse, grouping the sketches that have the same bit at the position we looked that.


    Util.println(Util.bin(compress(0b0101, significantBits), 2));
  }
}