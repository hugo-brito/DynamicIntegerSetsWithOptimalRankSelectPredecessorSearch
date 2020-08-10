package integersets;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/** This version of DynamicFusionNode iterates from the DynamicFusionNodeBinaryRank by introducing
 * key sketching and "don't cares", as described in pages 7--9 of the paper.
 * The sketches and the "don't cares" are stored in 2 words of k^2 size. For this reason, we limit
 * k to 8, because sqrt(64) = 8, and k must also be a power of 2. This way we maximize the number
 * of keys in the node but keep the 
 */

public class DynamicFusionNodeDontCares implements RankSelectPredecessorUpdate {
  
  private static final int k = 8;
  private final int ceilLgK = (int) Math.ceil(Math.log10(k) / Math.log10(2));
  private final long[] key = new long[k];
  private long index;
  private int bKey;
  private int n;

  /**
   * Variables for maintaining the rank with don't cares algorithm. 
   */
  private long compressingKey;
  private String binCompressingKey;
  private long compressedKeys;
  private String binCompressedKeys;
  private long dontCares;
  private String binDontCares;

  private long branch;
  private String binBranch;
  private long free;
  private String binFree;

  /**
   * Builds an empty DynamicFusionNodeDontCares.
   */
  public DynamicFusionNodeDontCares() {
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
    binCompressingKey = Util.bin(compressingKey, k);
    // compressed Keys
    updateKeyCompression();
    binCompressedKeys = Util.bin(compressedKeys, k);
    // dontcares
    dontCares = dontCares();
    binDontCares = Util.bin(dontCares, k);

    // branch
    // free
    updateBranchAndFree();
    binBranch = Util.bin(branch, k);
    binFree = Util.bin(free, k);
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
    dontCares = dontCares();

    // branch
    // free
    updateBranchAndFree();
  }

  @Override
  public long rank(final long x) {
    // if (n > 2) {
      return dontCaresRank(x);
    // } else {
      // return binaryRank(x);
    // }
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

  private int dontCaresRank(final long x) {
    if (isEmpty()) {
      return 0;
    }

    // if (n == 1) {
    //   if (Long.compareUnsigned(x, select(0)) < 0) {
    //     return 0;
    //   } else {
    //     return 1;
    //   }
    // }

    int i = match(x);
    // long y = select(i);
    long y = key[getIndex(i)];
    int comp = Long.compareUnsigned(x, y);
        
    if (comp == 0) {
      return i;
    }

    int j = Util.msb(x ^ y);

    int i_0 = match(x & ~((1L << j) - 1)); // special case j = 64.

    int i_1 = match(x | ((1L << j) - 1));

    if (comp < 0) {
      return i_0;
    }

    return i_1 + 1;
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
          // not sure about the select thing
        }
      }
      compressingKey = res;
    // } else {
    //   compressingKey = Util.setBit(Util.msb(select(0)), 0);
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
    // -- DEBUGGING -- //
    Util.println("-------------");
    Util.println("Compressing word:");
    Util.println(Util.bin(compressingKey, k));
    Util.println("-------------");

    Util.println("Compressed keys:");

    for (int i = 0; i < n; i++) {
      compressedKeys = Util.setField(i, compress(select(i)), k, compressedKeys);
      Util.println(Util.bin(Util.getField(i, k, compressedKeys), k));
    }
    Util.println("-------------");
  }

  private int match(long x) {
    Util.println("-------------");
    Util.println("Match:");
    Util.println("x:");
    Util.println(Util.bin(x, k));

    long xCompressed = compress(x);
    Util.println("x compressed:");
    Util.println(Util.bin(xCompressed, k));

    long xCompressedCopied = Util.M(k, n * k);
    // each block has k bits, and the word has n * k bits, so k bits per key.

    Util.println("Copying constant:");
    Util.println(Util.bin(xCompressedCopied, k));

    Util.println("x compressed & copied:");
    xCompressedCopied *= xCompressed;
    Util.println(Util.bin(xCompressedCopied, k));

    Util.println("xCompressedCopied & FREE:");
    Util.println(Util.bin(xCompressedCopied & free, k));

    Util.println("A = BRANCH | (xCompressedCopied & FREE):");
    long A = branch | (xCompressedCopied & free);
    Util.println(Util.bin(A, k));

    int match = Util.rankLemma1(xCompressed, A, n, k);

    Util.println("Match of x: " + match);
    
    return match;

  }

  private void updateBranchAndFree() {
    int numSignificantBits = Long.bitCount(compressingKey); // how many bits are set in the
    // compressing key

    long BRANCH = 0L;
    long FREE = 0L;

    for (int i = 0; n > 1 && i < n; i++) {
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

      BRANCH = Util.setField(i, keyBranch, n, BRANCH);
      FREE = Util.setField(i, keyFree, n, FREE);
    }

    // if (n == 1) {
    //   BRANCH = (1L << k) - 1;
    // }

    branch = BRANCH;
    free = FREE;
  }

  private long dontCares() {

    long dontCares = 0L;
    // - Starting from the most significant bit of the sketch,

    // int bit = compressedLength - 1;

    dontCares = dontCares(dontCares, k - 1, 0, n);

    Util.println("-------------");
    Util.println("Don't cares keys:");
    for (int i = 0; i < n; i++) {
      Util.println(Util.bin(Util.getField(i, k, dontCares), k));
    }
    Util.println("-------------");

    return dontCares;    
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
    Util.println("There are " + (transition == -1 ? 0 : transition - lo)
        + " compressed keys that start with zero");

    // If all bits are the same in all keys, then that position is a don't care for all keys
    if (transition == -1) { // then everything is in the same group, all bits are don't care
      Util.println("Everything is 1/0. Writing don't cares.");
      for (int i = lo; i < hi; i++) {
        dontCares = Util.setField(i, Util.setBit(bit, Util.getField(i, k, dontCares)), k, dontCares);
        // dontCares = Util.setBit(bit + lo, dontCares);
        // dontCares[i] = Util.setBit(bit, dontCares[i]);
      }
      // recursive call with the same range, next least significant bit
      return dontCares(compressedKeys, dontCares, bit - 1, lo, hi);
    } else {
      // don't need to delete anything in dontCares as it is zero.
      Util.println("There is a transition. Ignoring don't cares.");

      // 2 x recursive calls
      return dontCares(compressedKeys, dontCares, bit - 1, lo, transition)
        | dontCares(compressedKeys, dontCares, bit - 1, transition, hi);
    }
    // Util.println("-------------");

  }
 
  /*
  Overall Rank Algorithm
        
  Ingredients:
  - sketching in the same style as fusion trees --- store this result in a word BRANCH
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
    4 .We now have the sketches and we also know which positions are the don't cares. We need
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
    3 .We define a subroutine match(x), which uses rank lemma 1 (expression is on page 9)
    4 .Having match defined, we can compute the overall rank algorithm, which is also given the
      steps in the paper.
  */
  
  /**
   * Given key {@code x}, this function compresses {@code x}, keeping only the bits at the
   * positions specified in the {@code compressingKey}. It assumes that {@code compressingKey} is
   * sorted.
   * @param x the key to be compressed
   * @param compressingKey the indices of the bits to keep in the compressed key
   * @return the compressed key
   */
  public static long compress(final long x, long compressingKey) {
    long res = 0L;
    String bin = "";
    while (compressingKey != 0) {
      res <<= 1;
      int bit = Util.msb(compressingKey);
      bin += Util.bit(bit, x);
      res |= Util.bit(bit, x);
      compressingKey = Util.deleteBit(bit, compressingKey);
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

  /**
   * Finds the how many compressed keys have {@code bit} set to zero. Since bits can either be zero
   * or one, we can easily compute the which keys start with one.
   * @param compressedKeys The array containing the compressed keys
   * @param bit The bit to look at
   * @param lo The minimum index to look at in compressed keys
   * @param hi The maximum index to look at in compressed keys
   */
  public static long dontCares(final long compressedKeys, long dontCares, int bit, int lo, int hi) {
    if (bit == -1) {
      return dontCares;
    }
    // if all bits are the same in all keys, then that position is a don't care for all keys

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
    Util.println("There are " + (transition == -1 ? 0 : transition - lo)
        + " compressed keys that start with zero");

    // If all bits are the same in all keys, then that position is a don't care for all keys
    if (transition == -1) { // then everything is in the same group, all bits are don't care
      Util.println("Everything is 1/0. Writing don't cares.");
      for (int i = lo; i < hi; i++) {
        dontCares = Util.setField(i, Util.setBit(bit, Util.getField(i, k, dontCares)), k, dontCares);
        // dontCares = Util.setBit(bit + lo, dontCares);
        // dontCares[i] = Util.setBit(bit, dontCares[i]);
      }
      // recursive call with the same range, next least significant bit
      return dontCares(compressedKeys, dontCares, bit - 1, lo, hi);
    } else {
      // don't need to delete anything in dontCares as it is zero.
      Util.println("There is a transition. Ignoring don't cares.");

      // 2 x recursive calls
      return dontCares(compressedKeys, dontCares, bit - 1, lo, transition)
        | dontCares(compressedKeys, dontCares, bit - 1, transition, hi);
    }
    // Util.println("-------------");

  }

  public static long compressKeys(long[] keys) {
    long significantBits = significantBits(keys);

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

  public static long dontCaresR(long compressedKeys, int n) {

    long dontCares = 0L;
    // - Starting from the most significant bit of the sketch,

    // int bit = compressedLength - 1;

    dontCares = dontCares(compressedKeys, dontCares, k - 1, 0, n);

    Util.println("-------------");
    Util.println("Don't cares keys:");
    for (int i = 0; i < k; i++) {
      Util.println(Util.bin(Util.getField(i, k, dontCares), k));
    }
    Util.println("-------------");

    return dontCares;    
  }

  public static long[] generateKeys() {
    // long[] keys = new long[]{
    //   0b0110011100, 
    //   0b0110100100,
    //   0b0110111100,
    //   0b0111010100,
    //   0b1111110010,
    //   0b1111111000,
    //   0b1111111010,
    //   0b1111111100,
    //   0b1111111110
    // };

    int k = 8;
    Random rand = new Random(42);
    TreeSet<Long> keySet = new TreeSet<Long>(new Comparator<Long>() {
      @Override
      public int compare(Long o1, Long o2) {
        return Long.compareUnsigned(o1, o2);
      }
    });

    while (keySet.size() < k) {
      keySet.add(rand.nextLong());
    }

    long[] keys = new long[k];

    int i = 0;
    for (long key : keySet) {
      keys[i] = key;
      i++;
    }

    Util.println("-------------");
    Util.println("Uncompressed keys:");
    for (i = 0; i < keys.length; i++) {
      Util.println(Util.bin(keys[i], 8));
    }

    return keys;
  }

  public static long[] BRANCHandFREE(long compressedKeys, long dontCares, int numSignificantBits, int n) {
    long BRANCH = 0L;
    long FREE = 0L;

    for (int i = 0; i < n; i++) {
      long compKey = Util.getField(i, k, compressedKeys);
      // long compKey = compressedKeys[i];
      long dontCare = Util.getField(i, k, dontCares);
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

    return new long[]{BRANCH, FREE};
  }

  public static int match(long x, long compressingKey, long BRANCH, long FREE) {
    Util.println("-------------");
    Util.println("Match:");
    Util.println("x:");
    Util.println(Util.bin(x, k));


    long xCompressed = compress(x, compressingKey);
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
    long A = BRANCH | (xCompressedCopied & FREE);
    Util.println(Util.bin(A, k));

    int match = Util.rankLemma1(xCompressed, A, k, k);

    Util.println("Match of x: " + match);
    
    return match;
  }

  // public static int rankDontCares() {

  // }
    
  /**
   * Debugging.
   * 
   * @param args --
   */
  public static void main(final String[] args) {

    // long[] keys = generateKeys();
    // long significantBits = significantBits(keys); // compressing key
    // int compressedLength = Long.bitCount(significantBits); // how many bits are set in the
    // // compressing key

    // long compressedKeys = compressKeys(keys);
    // // --- fine up to this point
    // long dontCares = dontCaresR(compressedKeys, keys.length);

    // long[] BRANCHandFREE = BRANCHandFREE(compressedKeys, dontCares, compressedLength, keys.length);

    // Util.println("BRANCH = " + Util.bin(BRANCHandFREE[0], k));
    // Util.println("FREE   = " + Util.bin(BRANCHandFREE[1], k));
    // Util.println("FREE: if it is set to 1, we don't care; if it's set to 0, then we care.");

    // long x = -1;
    // match(x, significantBits, BRANCHandFREE[0], BRANCHandFREE[1]);

    DynamicFusionNodeDontCares node = new DynamicFusionNodeDontCares();
    node.insert(10);
    node.insert(12);
    // node.insert(42);
    node.insert(7);

    Util.println(node.member(10));

    // System.out.println("RANK OF 12 :");
    // Util.println(node.rank(12));
    // node.insert(42);
    // Util.println(node.rank(-1337));

    // node.insert(-1337);

    // int j = 3;
    // Util.println(Util.bin(~((1L << j) - 1), j));
    // Util.println(Util.bin((1L << j) - 1, j));

    // long significantBits = significantBits(keys);

    // Util.println("-------------");
    // Util.println("Compressing word:");
    // Util.println(Util.bin(significantBits, Util.msb(significantBits) + 1));
    // Util.println("-------------");
    

    // Util.println("Compressed keys:");
    // int compressedLength = Long.bitCount(significantBits);
    // long[] compressedKeys = new long[keys.length];
    // for (int i = 0; i < keys.length; i++) {
    //   compressedKeys[i] = compress(keys[i], significantBits);
    //   Util.println(Util.bin(compressedKeys[i], compressedLength));
    // }
    // Util.println("-------------");

    // // - Starting from the most significant bit of the sketch, if all bits are the same in all
    // // keys, then that position is a don't care for all keys

    // int bit = compressedLength - 1;

    // int transition7 = dontCares(compressedKeys, bit, 0, compressedKeys.length - 1);
    // // - Recurse, grouping the sketches that have the same bit at the position we looked that.

    // long[] dontCares = new long[keys.length];

    // Util.println("-------------");

    // if (transition7 == -1) { // then everything is in the same group, all bits are don't care
    //   for (int i = 0; i < dontCares.length; i++) {
    //     dontCares[i] = Util.setBit(bit, dontCares[i]);
    //   }
    //   // recursive call with the same range, next least significant bit
    //   dontCares(compressedKeys, bit - 1, 0, compressedKeys.length - 1);
    // } else {
    //   // don't need to delete anything in dontCares as it is zero.

    //   // 2 x recursive calls
    //   dontCares(compressedKeys, bit - 1, 0, transition7);
    //   dontCares(compressedKeys, bit - 1, transition7 + 1, compressedLength - 1);
    // }

  }
}