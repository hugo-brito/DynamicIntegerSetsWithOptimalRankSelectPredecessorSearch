package integersets;

import java.util.Random;

/**
 * This version of DynamicFusionNode iterates from the
 * DynamicFusionNodeBinaryRank by introducing key sketching and "don't cares",
 * as described in pages 7--9 of the paper. The sketches and the "don't cares"
 * are stored in 2 words of k^2 size. For this reason, we limit k to 8, because
 * sqrt(64) = 8, and k must also be a power of 2. This way we maximize the
 * number of keys in the node but keep the
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
   * Builds an empty DynamicFusionNodeDontCares.
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

  private int match(final long x) {
    final long xCompressed = compress(x);
    return Util.rankLemma1(xCompressed,
        branch | ((xCompressed * Util.getFields(0, n, k, M)) & free), n, k);
  }

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
   * @return the compressed key
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

  private long compressedKeys() {
    long compressedKeys = 0L;
    for (int i = 0; i < n; i++) {
      compressedKeys = Util.setField(i, compress(select(i)), k, compressedKeys);
    }
    return compressedKeys;
  }

  private void updateBranch(long compressedKeys) {
    branch = compressedKeys & ~free;
  }

  private void updateFree(long compressedKeys) {
    free = dontCares(compressedKeys, 0, k - 1, 0, n);
    if (n < k) { // making the unused rows all 1
      free = free | ~((1L << (k * n)) - 1);
    }
  }

  /**
   * Finds the how many compressed keys have {@code bit} set to zero. Since bits can either be zero
   * or one, we can easily compute the which keys start with one.
   * 
   * @param free The word where the results are to be stored
   * @param bit The column in {@code compressedKeys} to look at
   * @param lo The lower bound (row in {@code compressedKeys}) considered in the range
   * @param hi The upper bound (row in {@code compressedKeys}) considered in the range
   * @return {@code free} after the given iteration
   */
  private long dontCares(long compressedKeys, long free, final int bit, final int lo, final int hi) {
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
      return dontCares(compressedKeys, free, bit - 1, lo, mid) | dontCares(compressedKeys, free, bit - 1, mid, hi);
    }
  }

  /**
   * Given key {@code x}, this function compresses {@code x}, keeping only the
   * bits at the positions specified in the {@code compressingKey}. It assumes
   * that {@code compressingKey} is sorted.
   * 
   * @param x              the key to be compressed
   * @param compressingKey the indices of the bits to keep in the compressed key
   * @return the compressed key
   */
  public static long compress(final long x, long compressingKey) {
    long res = 0L;
    String bin = "";
    while (compressingKey != 0) {
      res <<= 1;
      final int bit = Util.msb(compressingKey);
      bin += Util.bit(bit, x);
      res |= Util.bit(bit, x);
      compressingKey = Util.deleteBit(bit, compressingKey);
    }
    return res;
  }

  /**
   * Returns the branching positions of all keys in the provided array. The result
   * is computed and stored in a bit vector. If a given bit is
   * 
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

  /**
   * Finds the how many compressed keys have {@code bit} set to zero. Since bits
   * can either be zero or one, we can easily compute the which keys start with
   * one.
   * 
   * @param compressedKeys The array containing the compressed keys
   * @param bit            The bit to look at
   * @param lo             The minimum index to look at in compressed keys
   * @param hi             The maximum index to look at in compressed keys
   */
  public static long staticDontCares(final long compressedKeys, long dontCares, final int bit, final int lo,
      final int hi) {
    if (bit == -1) {
      return dontCares;
    }
    // if all bits are the same in all keys, then that position is a don't care for
    // all keys

    Util.println("-------------");
    int transition = lo; // we start by assuming that everything is 1 at position bit
    Util.println("Looking at bit = " + bit + "\nlo = " + lo + " | hi = " + hi);
    long field = Util.getField(transition, k, compressedKeys);
    String bin = Util.bin(field, k);
    while (transition < hi && Util.bit(bit, field) == 0) {
      transition++;
      field = Util.getField(transition, k, compressedKeys);
      bin = Util.bin(field, k);
    }

    if (transition == lo || transition == hi) { // we're before a don't care
      transition = -1;
    }
    Util.println("There are " + (transition == -1 ? 0 : transition - lo) + " compressed keys that start with zero");

    // If all bits are the same in all keys, then that position is a don't care for
    // all keys
    if (transition == -1) { // then everything is in the same group, all bits are don't care
      Util.println("Everything is 1/0. Writing don't cares.");
      for (int i = lo; i < hi; i++) {
        dontCares = Util.setField(i, Util.setBit(bit, Util.getField(i, k, dontCares)), k, dontCares);
        // dontCares = Util.setBit(bit + lo, dontCares);
        // dontCares[i] = Util.setBit(bit, dontCares[i]);
      }
      // recursive call with the same range, next least significant bit
      return staticDontCares(compressedKeys, dontCares, bit - 1, lo, hi);
    } else {
      // don't need to delete anything in dontCares as it is zero.
      Util.println("There is a transition. Ignoring don't cares.");

      // 2 x recursive calls
      return staticDontCares(compressedKeys, dontCares, bit - 1, lo, transition)
          | staticDontCares(compressedKeys, dontCares, bit - 1, transition, hi);
    }
    // Util.println("-------------");

  }

  public static long compressKeys(final long[] keys) {
    final long significantBits = significantBits(keys);

    // -- DEBUGGING -- //
    Util.println("-------------");
    Util.println("Compressing word:");
    Util.println(Util.bin(significantBits, Util.msb(significantBits) + 1));
    Util.println("-------------");

    Util.println("Compressed keys:");
    long compressedKeys = 0L;
    for (int i = 0; i < keys.length; i++) {
      compressedKeys = Util.setField(i, compress(keys[i], significantBits), k, compressedKeys);
    }
    Util.println(Util.bin(compressedKeys, k));
    Util.println("-------------");

    return compressedKeys;
  }

  public static long dontCaresR(final long compressedKeys, final int n) {

    long dontCares = 0L;
    // - Starting from the most significant bit of the sketch,

    // int bit = compressedLength - 1;

    dontCares = staticDontCares(compressedKeys, dontCares, k - 1, 0, n);

    Util.println("-------------");
    Util.println("Don't cares keys:");
    for (int i = 0; i < n; i++) {
      Util.println(Util.bin(Util.getField(i, k, dontCares), k));
    }
    Util.println("-------------");

    return dontCares;
  }

  public static long[] generateKeys() {
    // long[] keys = new long[]{
    // 0b0110011100,
    // 0b0110100100,
    // 0b0110111100,
    // 0b0111010100,
    // 0b1111110010,
    // 0b1111111000,
    // 0b1111111010,
    // 0b1111111100,
    // 0b1111111110
    // };

    // int k = 8;
    // Random rand = new Random(42);
    // TreeSet<Long> keySet = new TreeSet<Long>(new Comparator<Long>() {
    // @Override
    // public int compare(Long o1, Long o2) {
    // return Long.compareUnsigned(o1, o2);
    // }
    // });

    // while (keySet.size() < k) {
    // keySet.add(rand.nextLong());
    // }

    // long[] keys = new long[k];

    // int i = 0;
    // for (long key : keySet) {
    // keys[i] = key;
    // i++;
    // }

    final long[] keys = new long[] { 10, 12, 42, -1337, -42 };

    Util.println("-------------");
    Util.println("Uncompressed keys:");
    for (int i = 0; i < keys.length; i++) {
      Util.println(Util.bin(keys[i], 8));
    }

    return keys;
  }

  public static long[] BRANCHandFREE(final long compressedKeys, final long dontCares, final int numSignificantBits,
      final int n) {
    long BRANCH = 0L;
    long FREE = 0L;

    for (int i = 0; i < n; i++) {
      final long compKey = Util.getField(i, k, compressedKeys);
      // long compKey = compressedKeys[i];
      final long dontCare = Util.getField(i, k, dontCares);
      // long dontCare = dontCares[i];
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

      BRANCH = Util.setField(i, keyBranch, k, BRANCH);
      FREE = Util.setField(i, keyFree, k, FREE);
    }

    return new long[] { BRANCH, FREE };
  }

  public static int match(final long x, final long compressingKey, final long BRANCH, final long FREE) {
    Util.println("-------------");
    Util.println("Match:");
    Util.println("x: " + x + " | binary: ");
    Util.println(Util.bin(x, k));

    final long xCompressed = compress(x, compressingKey);
    Util.println("x compressed:");
    Util.println(Util.bin(xCompressed, k));
    long xCompressedCopied = Util.M(k, k * k);

    Util.println("Copying constant:");
    Util.println(Util.bin(xCompressedCopied, k));

    Util.println("x compressed & copied:");
    xCompressedCopied *= xCompressed;
    Util.println(Util.bin(xCompressedCopied, k));

    Util.println("xCompressedCopied & FREE:");
    Util.println(Util.bin(xCompressedCopied & FREE, k));

    Util.println("A = BRANCH | (xCompressedCopied & FREE):");
    final long A = BRANCH | (xCompressedCopied & FREE);
    Util.println(Util.bin(A, k));

    final int match = Util.rankLemma1(xCompressed, A, k, k);

    Util.println("Match of x: " + match);

    return match;
  }

  public static int rankDontCares(final long x, final long compressingKey, final long BRANCH, final long FREE,
      final long[] keys) {

    // match(long x, long compressingKey, long BRANCH, long FREE)
    final int i = match(x, compressingKey, BRANCH, FREE);
    // long y = select(i);
    final long y = keys[i];
    // long y = key[getIndex(i)];
    final int comp = Long.compareUnsigned(x, y);

    if (comp == 0) {
      return i;
    }

    final int j = Util.msb(x ^ y);

    final int i_0 = match(x & ~((1L << j) - 1), compressingKey, BRANCH, FREE);

    final int i_1 = match(x | ((1L << j) - 1), compressingKey, BRANCH, FREE);

    if (comp < 0) {
      return i_0;
    }

    return i_1 + 1;
  }

  public long compressingKey() {
    return compressingKey;
  }

  public long branch() {
    return branch;
  }

  public long free() {
    return free;
  }

  /**
   * Debugging.
   * 
   * @param args --
   */
  public static void main(final String[] args) {

    DynamicFusionNodeDontCaresRank node = new DynamicFusionNodeDontCaresRank();
    Random rand = new Random();
    long[] keys = null;

    while (Long.bitCount(node.compressingKey) < 7) {
      node.reset();
      keys = Util.distinctBoundedSortedLongs(8, (long) Math.pow(2, 12), rand.nextLong());
      for (int i = 0; i < keys.length; i++) {
        node.insert(keys[i]);
      }
    }


    for (int i = 0; i < keys.length; i++) {
      Util.println(Util.bin(keys[i], 4));
    }

    Util.println("CompressingKey:");
    Util.println(Util.bin(node.compressingKey, k));

    Util.println("CompressedKeys:");
    // Util.println(Util.matrixToString(k, k, node.compressedKeys));

    Util.println("BRANCH:");
    Util.println(Util.matrixToString(k, k, node.branch));
    Util.println("FREE:");
    Util.println(Util.matrixToString(k, k, node.free));

  }
}