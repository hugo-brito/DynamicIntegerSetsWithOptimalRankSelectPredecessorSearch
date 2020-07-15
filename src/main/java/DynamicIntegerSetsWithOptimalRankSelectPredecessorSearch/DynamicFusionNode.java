package DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch;

public class DynamicFusionNode implements RankSelectPredecessorUpdate {
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
  * of k words plus a single word INDEX such that KEY[INDEX_i ceilLogK] is the
  * key of rank i in S.". So what happens is that we max out the capacity for
  * storing keys in INDEX given the premise that INDEX shall not exceed 64 bits
  * in size. We calculate k such that k*ceil(log k) = 64, and we know that
  * k = 16.</p>
  * 
  * <p>To help us storing the integers in the set, we use:
  * - 1 array of words KEYS, which in this particular case is a long[]
  * - An additional "array" which we called KEY with k bits, where each "cell"
  * takes ceil(log k) bits. The paper tells us that all of this information needs
  * to fit in one word, e. g. 64 bits. So, doing the math we have 64 = k * ceil(log k)
  * k = 16 (https://www.wolframalpha.com/input/?i=k+ceil%28log_2%28k%29%29+%3D+64)
  * This means that our set will have capaticity for 16 elements.</p>
  * 
  * <p>An important note is that each "slot" in INDEX will have ceil(log k) size and
  * k slots in total. This means that with this specific word size, we will have
  * 16 slots of 4 bits each.</p>
  */


  // this will potentially be a node

  private static final int BITSWORD = 63; // how many bits are stored in a word


  private final int k;
  // capacity

  /* We will store our key set S in an unsorted array KEY with room for k w-bit integers */
  private final long[] key;

  /* We will also maintain an array INDEX of ceil(lg k)-bit indices */
  private long index;
  private final int ceilLgK;

  private int bKey;
  // a bit map containing the empty spots in KEY
  // we only use the first k bits

  private int n;
  // the current number of elements in S

  public DynamicFusionNode() {
    /*
     * We will ourselves use k = w^(1/4) The exact value makes no theoretical
     * difference: We choose k = 16, such that we can fill a whole word to index all
     * the
     */
    k = 16;
    key = new long[k];

    // We use the logarithm rules to change the base from 10 to 2. We make
    // ceil(log2_k) to depend on k
    ceilLgK = (int) Math.ceil(Math.log10(k) / Math.log10(2));

    // We start with no items.
    index = 0;
    n = 0;

    // bitmap containing the empty spots. 1 if it is empty, 0 if it is taken.
    bKey = -1; // because -1 in java binary is 1111..11

    System.out.println("ceil(lg k) = " + ceilLgK); // we get 4 when w = 64
    System.out.println(ceilLgK * k);

    System.out.println("(" + Long.toBinaryString(bKey).length() + ") " + Long.toBinaryString(bKey));

  }

  /** Debugging.
   * 
   * @param args --
   */
  public static void main(final String[] args) {
    // int k = 16;
    // int ceilLgK = (int) Math.ceil(Math.log10(k) / Math.log10(2));
    // // Util.print(k);
    // // Util.print(Util.bin64((k - 1) << ceilLgK));

    // // Util.print(Util.bin64((k - 1) << 2 * ceilLgK));

    // // Util.print(Util.bin64(((k - 1) * (k - 1)) << ceilLgK));

    // Util.print(Util.bin64(-1));
    // Util.print(Util.bin64((-1L >>> ceilLgK)));

    // Util.print(Util.bin64(clearAfterPos(-1, ceilLgK, 2)));

    // Util.print(Util.bin64(clearBeforePos(-1, ceilLgK, 2)));

    // int emptySpotAt = 2;
    // long res = clearAfterPos(-1, ceilLgK, emptySpotAt) | clearBeforePos(-1, ceilLgK, emptySpotAt);
    
    // Util.print(Util.bin64(-2));

    long bKey = -1;
    int j = 15;
    long res = bKey & ~(1L << (BITSWORD - j));
    Util.print(res);
    Util.print(Util.bin64(res));

    DynamicFusionNode n = new DynamicFusionNode();
    n.insert(10);
    n.rank(12);
    Util.print("rank(12) = " + n.rank(12));
    n.insert(12);
    n.insert(42);
    n.insert(-1337);
    n.insert(-42);

  }

  public static long clearAfterPos(long target, int ceilLgK, int pos) {

    return (target >>> ((BITSWORD + 1) - (pos * ceilLgK))) << ((BITSWORD + 1) - (pos * ceilLgK));
  }

  public static long clearBeforePos(long target, int ceilLgK, int pos) {
    return (target << (pos * ceilLgK)) >>> ((pos + 1) * ceilLgK);
  }

  /**
   * Returns the index of the first empty slot in KEY.
   * 
   * @return the index in KEY of the first empty slot.
   */
  private int firstEmptySlot() {
    if (n == k) { // no empty spot
      return -1;
    }
    return Util.msb32Obvious(bKey);
  }

  private void fillEmptySlot(final int j) {
    if (j >= 0 && j < k) {
      bKey &= ~(1L << (31 - j));
    } else {
      throw new IndexOutOfBoundsException("j must be between 0 and k (" + k + ")!");
    }
  }

  /**
   * Returns the index of the key x in KEY such that its rank is {@code i}.
   * 
   * @param i The rank of the key in the S
   * @return the index in KEY of rank {@code rank}
   */
  private int indexOfithLargestKey(final long i) {
    return (int) (index << (ceilLgK * i)) >>> (ceilLgK * (k - 1));
  }

  @Override
  public Long select(final long rank) {
    if (rank < 0 || rank > n) {
      return null;
    }

    return key[indexOfithLargestKey(rank)];
  }

  public long rank(final long x) {
    return binaryRank(x);
  }

  private int binaryRank(final long x) {
    if (n == 0) {
      return 0;
    }

    int lowerBound = 0; // indices of the KEY array.
    int upperBound = n - 1;
    int middle = -1;

    while (lowerBound <= upperBound) {
      middle = lowerBound + ((upperBound - lowerBound) / 2);

      final int compare = Long.compareUnsigned(select(middle), x);

      if (compare == 0) {
        break;

      } else if (compare > 0) {
        upperBound = middle - 1;

      } else {
        lowerBound = middle + 1;
      }

    }

    if (Long.compareUnsigned(select(middle), x) > 0) {
      return middle;
    } else {
      return middle + 1;
    }
  }

  @Override
  public boolean member(final long x) {
    final Long res = successor(x);
    return res != null && res == x;
  }

  public void insert(final long x) {

    if (n > 0 && member(x)) {
      return;
    }

    if (n == k) {
      throw new RuntimeException("Cannot insert. Node is full.");
    }

    // 1. Find the rank of the key
    final long i = rank(x);

    // 2. Find a free slot
    final int j = firstEmptySlot();

    // 3. Set KEY[j] = key
    key[j] = x;

    // 4. Set bKEY[j] = 0
    fillEmptySlot(j);

    // 5. update INDEX according to the new key
    // this operation consists of moving all the indices of the keys that have rank smaller than
    // the new key one position to the left to make room for the index of the new key
    // After this is done, store the index in KEY in INDEX of the new key, respecting the rank of
    // the keys.

    long hi = (index >>> ((BITSWORD + 1) - (i * ceilLgK))) << ((BITSWORD + 1) - (i * ceilLgK));
    long mid = j << (i * ceilLgK);
    long lo = (index << (i * ceilLgK)) >>> ((i + 1) * ceilLgK);

    index = hi | mid | lo;

    // Need to implement method to move all indices one position to the front
    n++;

  }

  @Override
  public void delete(final long x) {
    if (!member(x)) {
      return;
    }
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
}