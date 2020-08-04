package integersets;

/** For this version of indexing, which comes described in page 5 of the paper,
  * we have selected w = 64, e. g. with word is made up of 64 bits. The reason
  * for us to pick this size of word length is that we can conveniently use
  * Java's \>\> \<\< | & ^ operations in constant time.
  * 
  * <p>The problem with this size for the word length is that they can only
  * guarantee the fast running times when the capacity of the set is of w^(1/4)
  * which is very small. Let w = be 64, then w^(1/4) = 2,8..... This is a set
  * of very small size. So we drift away from this limit to a focus on the part
  * where they say "We assume that our set S is stored in an unordered array KEY
  * of k words plus a single word INDEX such that KEY[INDEX_i ceilLgK] is the
  * key of rank i in S.". So what happens is that we max out the capacity for
  * storing keys in INDEX given the premise that INDEX shall not exceed 64 bits
  * in size. We calculate k such that k*ceil(log k) = 64, and we know that
  * k = 16.</p>
  * 
  * <p>To help us storing the integers in the set, we use:
  * - 1 array of words KEYS, which in this particular case is a long[]
  * - An additional "array" which we called KEY with k bits, where each "cell"
  * takes ceil(lg k) bits. The paper tells us that all of this information needs
  * to fit in one word, e. g. 64 bits. So, doing the math we have 64 = k * ceil(log k)
  * k = 16 (https://www.wolframalpha.com/input/?i=k+ceil%28log_2%28k%29%29+%3D+64)
  * This means that our set will have capaticity for 16 elements.</p>
  * 
  * <p>An important note is that each "slot" in INDEX will have ceil(lg k) size and
  * k slots in total. This means that with this specific word size, we will have
  * 16 slots of 4 bits each.</p>
  */

public class DynamicFusionNode implements RankSelectPredecessorUpdate {
  
  /**
  * We will ourselves use k = w^(1/4) The exact value makes no theoretical
  * difference: We choose k = 16, such that we can fill a whole word to index all
  * the keys in KEY.
  */
  private final int k = 16; // capacity

  /* We will store our key set S in an unsorted array KEY with room for k w-bit integers */
  private final long[] key = new long[k];

  /* We will also maintain an array INDEX of ceil(lg k)-bit indices */
  private long index;
  private final int ceilLgK = (int) Math.ceil(Math.log10(k) / Math.log10(2));

  private int bKey;
  // a bit map containing the empty spots in KEY
  // we only consider the first k bits

  private int n;
  // the current number of elements in S

  /**
   * Constructs an empty DynamicFusionNode.
   */
  public DynamicFusionNode() {
    // We start with no items.
    index = 0;
    n = 0;
    // bitmap containing the empty spots. 1 if it is empty, 0 if it is taken.
    bKey = -1; // because -1 in java binary is 1111..11
  }

  /** Sets S = S union {x}.
   * @param x the integer to insert
   */
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
    vacantSlot(i);
    updateIndex(i);
    n--;
  }

  @Override
  public boolean member(final long x) {
    if (isEmpty()) {
      return false;
    }
    final Long res = successor(x);
    return res != null && res == x;
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
    // return (Integer.bitCount(~bKey >>> (Integer.SIZE - k)));
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
      j += Util.lsb(~(bKey >>> j));
      bKey = Util.setBit(j, bKey);
    } else {
      throw new IndexOutOfBoundsException("j must be between 0 and k (" + k + ")!");
    }
  }

  /**
   * Helper method to retrieve the position in KEY of a key, given its rank {@code i}.
   * 
   * @param i The rank of the key in the S
   * @return the index in KEY of the key with rank {@code i}
   */
  private int getIndex(final long i) {
    return (int) Util.getField((int) i, ceilLgK, index);
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
   * their real position in KEY. The methods receives the rank {@code i} of a key
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

  /**
   * Debugging.
   * 
   * @param args --
   */
  public static void main(final String[] args) {
    int k = 16;
    final int ceilLgK = (int) Math.ceil(Math.log10(k) / Math.log10(2));
    // Util.print(ceilLgK);
    DynamicFusionNode node = new DynamicFusionNode();
    node.insert(10);
    node.insert(12);
    node.insert(42);
    node.insert(54);
    node.delete(42);
  }
}