package integersets;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

/** This version of DynamicFusionNode iterates from the DynamicFusionNodeBinaryRank by introducing
 * key sketching and "don't cares", as described in pages 7--9 of the paper.
 * The sketches and the "don't cares" are stored in 2 words of k^2 size. For this reason, we limit
 * k to 8, because sqrt(64) = 8, and k must also be a power of 2. This way we maximize the number
 * of keys in the node but keep the 
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
  private long compressedKeys;
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
    // rank algorithm
    int rank;

    if (!isEmpty()) {
      // Run rank(x). If x is already a member, do nothing. Otherwise, continue.
      // Util.println("---------- Insert(" + x + "): ----------");

      int i = match(x);
      final long y = select(i);
      final int comp = Long.compareUnsigned(x, y);
      // Util.println("compareUnsigned(" + x + ", " + y + ") = " + comp);

      if (comp == 0) { // already in the set
        return;
      }

      if (size() == k) {
        throw new RuntimeException("Cannot insert. Node is full.");
      }

      final int j = Util.msb(x ^ y);

      // rank of j among the significant positions
      final int h = Long.bitCount(compressingKey & ((1L << j) - 1)); // works but it's not constant time
      // Util.println("h = rank of j = " + h);

      final int i_0 = match(x & ~((1L << j) - 1));
      // Util.println("i_0 = match(x & ~((1L << j) - 1)) = " + i_0);

      final int i_1 = match(x | ((1L << j) - 1));
      // Util.println("i_1 = match(x | ((1L << j) - 1)) = " + i_1);

      rank = (comp < 0 ? i_0 : i_1 + 1); // rank of x
      // Util.println("rank(" + x + ") = match(x | ((1L << j) - 1)) = " + r);

      final long matrixM_h = matrixM(h); // matrix where only h is set
      if (Util.bit(j, compressingKey) != 1) {
        // If j is not yet a significant position, then we have to do some work:
        // mark it as a significant position.
        // (This is so that the compression function will compute correct sketches in
        // the future)
        compressingKey = Util.setBit(j, compressingKey);
        /*
        Since the compressing key has been updated, we need to add a new column of 0s in branch and
        a column of 1s in free.
        */

        // Util.println("Range matrices -----");
        final long Mlo = matrixMColumnRange(0, h - 1);


        final long Mhi = matrixMColumnRange(h, k - 1);
    
        // Util.println("adding column h in branch and free, shifting every column > h to the left ---");

        // shift all columns >= h one to the left
        branch = (branch & Mlo) | (((branch & Mhi) << 1) & ~Mlo);

        free = (free & Mlo) | (((free & Mhi) << 1) & ~Mlo);

        // fix the new column h:
        free |= matrixM_h;
        branch &= ~matrixM_h;

      }

      //fixing the range of keys i_0 ... i_1 in the j column

      long matrixMi0_Mi1_h = matrixMRowRange(i_0, i_1);

      matrixMi0_Mi1_h &= matrixM_h;

      free &= ~matrixMi0_Mi1_h;
      branch |= (matrixMi0_Mi1_h * Util.bit(j, y));


      // if j was already a significant bit, then i is just match(x), that is, i is
      // the position of y
      // if j was not already a significant bit, then i is the new position of y (so
      // either it is the same, or it was shifted by 1).

      // Making room for hat(x^?) with rank r in branch and free

      branch = (branch & matrixMRowRange(0, rank - 1)) | ((branch & (matrixMRowRange(rank, k - 1))) << k);

      free = (free & matrixMRowRange(0, rank - 1)) | ((free & (matrixMRowRange(rank, k - 1))) << k);

      if (comp < 0) { // then x < y. So y is the succ of x. we increment i
        i++;
      }
      

      branch = Util.setField(rank, Util.getField(rank, k, branch) & ~((1L << h) - 1), k, branch);

      free = Util.setField(rank, Util.getField(rank, k, free) | ((1L << h) - 1), k, free);

      branch = Util.setField2d(rank, h, (long) Util.bit(j, x), 1, k, branch);

      free = Util.setField2d(rank, h, 0L, 1, k, free);

      //Get h+1 ... k-1 bits of row r
      long rowR = Util.getField(rank, k, free);

      //Deleting h+1...k-1 bits of r:
      rowR &= ((1L << (h + 1)) - 1);

      // we get the field we want to copy from:
      long rowI = Util.getField(i, k, free);

      // we keep only the h+1...k-1 bits of i:
      rowI &= ~((1L << (h + 1)) - 1);

      // we merge both results
      // and write it to free
      free = Util.setField(rank, rowI | rowR, k, free);

      //Get h+1 ... k-1 bits of row r
      rowR = Util.getField(rank, k, branch);

      //Deleting h+1...k-1 bits of r:
      rowR &= ((1L << (h + 1)) - 1);

      // we get the field we want to copy from:
      rowI = Util.getField(i, k, branch);

      // we keep only the h+1...k-1 bits of i:
      rowI &= ~((1L << (h + 1)) - 1);

      // we merge both results
      // and write it to branch
      branch = Util.setField(rank, rowI | rowR, k, branch);
    } else {
      rank = 0;
    }

    final int indexInKey = firstEmptySlot();
    key[indexInKey] = x;
    fillSlot(indexInKey);
    updateIndex(rank, indexInKey);
    n++;

  }
  
  public void insertVerbose(final long x) {
    // rank algorithm

    if(isEmpty()) {
      final int indexInKey = firstEmptySlot();
      key[indexInKey] = x;
      fillSlot(indexInKey);
      updateIndex(0, indexInKey);
      n++;
      return;
    }
    
    // Run rank(x). If x is already a member, do nothing. Otherwise, continue.
    // Util.println("---------- Insert(" + x + "): ----------");

    int i = match(x);
    // Util.println("i = match(" + x + ") = " + i);

    final long y = select(i);
    // Util.println("y = select(" + i + ") = " + y);

    final int comp = Long.compareUnsigned(x, y);
    // Util.println("compareUnsigned(" + x + ", " + y + ") = " + comp);

    if (comp == 0) { // already in the set
      Util.println("comp = " + comp + " | " + x + " is already in the set! Stopping.");
      return;
    }
    if (size() == k) {
      Util.println("size() = " + k + " | Cannot insert.");
      throw new RuntimeException("Cannot insert. Node is full.");
    }

    // In rank(x), you have computed y, j, i_0, i_1 - these values are needed below,
    // so I guess
    // there needs to be an option so that rank can return them.
    final int j = Util.msb(x ^ y);
    // Util.println("j = msb(x ^ y) = " + j + " | branching bit");
    // Util.println("j = " + Util.bin(Util.setBit(j, 0L), k));

    // Util.println("compressingKey = " + Util.bin(compressingKey, k));

    // rank of j among the significant positions
    final int h = Long.bitCount(compressingKey & ((1L << j) - 1)); // works but it's not constant time
    // Util.println("h = rank of j = " + h);

    final int i_0 = match(x & ~((1L << j) - 1));
    // Util.println("i_0 = match(x & ~((1L << j) - 1)) = " + i_0);

    final int i_1 = match(x | ((1L << j) - 1));
    // Util.println("i_1 = match(x | ((1L << j) - 1)) = " + i_1);

    final int r =  (comp < 0 ? i_0 : i_1 + 1); // rank of x
    // Util.println("rank(" + x + ") = match(x | ((1L << j) - 1)) = " + r);

    // inserting the key in key and updating the rest
    final int indexInKey = firstEmptySlot();
    key[indexInKey] = x;
    fillSlot(indexInKey);
    updateIndex(r, indexInKey);

    // Util.println("select(" + r + ") = " + select(r));
    final long matrixM_h = matrixM(h); // matrix where only h is set
    // If j is not yet a significant position, then we have to do some work:
    if (Util.bit(j, compressingKey) != 1) {
      // Util.println("--- Start of operations based on  j was not being a significant position ---");
      // mark it as a significant position.
      // (This is so that the compression function will compute correct sketches in
      // the future)

      // Util.println(j + " was not a significant bit, updating compressing key:");
      // Util.println("compressingKey before update = " + Util.bin(compressingKey, k));
      compressingKey = Util.setBit(j, compressingKey);
      // Util.println("compressingKey after update = " + Util.bin(compressingKey, k));

      /*
      Since the compressing key has been updated, we need to add a new column of 0s in branch and
      a column of 1s in free.
       */
      
      // final long matrixM_h = matrixM(h); // matrix where only h is set
      // Util.println("Matrix M(h=" + h + "), only h is set:");
      // Util.println(matrixView(k, k, matrixM_h));

      // Util.println("Range matrices -----");
      final long Mlo = matrixMColumnRange(0, h - 1);
      Util.println("Mlo(h=" + h + "), only 0 <-> h-1 colums are set:");
      Util.println(Util.matrixToString(k, k, Mlo));

      final long Mhi = matrixMColumnRange(h, k - 1);
      // Util.println("Mhi(h=" + h + "), only h <-> k-1 colums are set:");
      // Util.println(matrixView(k, k, Mhi));
      // Util.println("\n");
  
      // Util.println("adding column h in branch and free, shifting every column > h to the left ---");
      Util.println("Branch before new h (" + h + ") column:");
      Util.println(Util.matrixToString(k, k, branch));
      // shift all columns >= h one to the left
      branch = (branch & Mlo) | (((branch & Mhi) << 1) & ~Mlo);
      Util.println("Branch after new h (" + h + ") column:");
      Util.println(Util.matrixToString(k, k, branch));

      Util.println("Free before new h (" + h + ") column:");
      Util.println(Util.matrixToString(k, k, free));
      Util.println("Free & Mlo:");
      Util.println(Util.matrixToString(k, k, free & Mlo));
      free = (free & Mlo) | (((free & Mhi) << 1) & ~Mlo);
      Util.println("Free after new h (" + h + ") column:");
      Util.println(Util.matrixToString(k, k, free));
      Util.println("\n");
  
      // fix the new column h:
      free |= matrixM_h;
      branch &= ~matrixM_h;
      Util.println("Branch after \"fixing\" h (" + h + "):");
      Util.println(Util.matrixToString(k, k, branch));
      Util.println("Free after \"fixing\" h (" + h + "):");
      Util.println(Util.matrixToString(k, k, free));

    }
      Util.println("i_0 = " + i_0 + " | i_1 = " + i_1);
      Util.println("We now look at rows i_0:i_1");
      long matrixMi0_Mi1_h = matrixMRowRange(i_0, i_1);
      Util.println(Util.matrixToString(k, k, matrixMi0_Mi1_h));
  
      Util.println("We're interested in column h = " + h + " of these rows:");
      matrixMi0_Mi1_h &= matrixM_h;
      Util.println(Util.matrixToString(k, k, matrixMi0_Mi1_h));

      free &= ~matrixMi0_Mi1_h;
      branch |= (matrixMi0_Mi1_h * Util.bit(j, y));
      Util.println("branch |= (matrixMi0_Mi1_h * (bit(j, y) = bit(" + j + ", " + y + ") = " + Util.bit(j, y) + "))");
      Util.println(Util.matrixToString(k, k, branch));
      Util.println("free &= ~matrixMi0_Mi1_h");
      Util.println(Util.matrixToString(k, k, free));

      // Util.println("--- End of operations based on  j was not being a significant position ---");

    // }

    // if j was already a significant bit, then i is just match(x), that is, i is
    // the position of
    // y
    // if j was not already a significant bit, then i is the new position of y (so
    // either it is
    // the same, or it was shifted by 1).

    // Making room for hat(x^?) with rank r in branch and free
    Util.println("Making room for hat(x^?) with rank r = " + r + " in branch and free");
    Util.println("branch:");
    branch = (branch & matrixMRowRange(0, r - 1)) | ((branch & (matrixMRowRange(r, k - 1))) << k);
    Util.println(Util.matrixToString(k, k, branch));
    Util.println("free:");
    free = (free & matrixMRowRange(0, r - 1)) | ((free & (matrixMRowRange(r, k - 1))) << k);
    Util.println(Util.matrixToString(k, k, free));
    
    if (comp < 0) { // then x < y. So y is the succ of x. we increment i
      // Util.println("Since x = " + x + " < " + y + " = y, then y = succ(x). Since rank(x) = " + r
          // + ", then rank(y) = rank(x) + 1, and it is now = " + (r + 1));
      i++;
      // i = r + 1;
    }
    

    branch = Util.setField(r, Util.getField(r, k, branch) & ~((1L << h) - 1), k, branch);

    free = Util.setField(r, Util.getField(r, k, free) | ((1L << h) - 1), k, free);

    branch = Util.setField2d(r, h, (long) Util.bit(j, x), 1, k, branch);

    free = Util.setField2d(r, h, 0L, 1, k, free);

    // ------------------------------
    // Util.println(matrixView(k, k, free));

    //Get h+1 ... k-1 bits of row r
    long rowR = Util.getField(r, k, free);
    Util.println("row r(" + r + ") of FREE:");
    Util.println(Util.matrixToString(k, k, rowR));

    //Deleting h+1...k-1 bits of r:
    rowR &= ((1L << (h + 1)) - 1);
    Util.println("row r of FREE with (h=" + h + ") h+1...k-1 bits set to 0:");
    Util.println(Util.matrixToString(k, k, rowR));

    // we get the field we want to copy from:
    long rowI = Util.getField(i, k, free);
    Util.println("row i(" + i + ") of FREE:");
    Util.println(Util.matrixToString(k, k, rowI));

    // we keep only the h+1...k-1 bits of i:
    rowI &= ~((1L << (h + 1)) - 1);
    Util.println("row i of FREE with (h=" + h + ") 0...h bits set to 0:");
    Util.println(Util.matrixToString(k, k, rowI));

    // we merge both results
    Util.println("rowI | rowR:");
    Util.println(Util.matrixToString(k, k, rowI | rowR));

    // and write it to free
    free = Util.setField(r, rowI | rowR, k, free);
    Util.println("FREE: wrote rowI | rowR in row " + r + ":");
    Util.println(Util.matrixToString(k, k, free));


    //Get h+1 ... k-1 bits of row r
    rowR = Util.getField(r, k, branch);
    // Util.println("row r(" + r + ") of BRANCH:");
    // Util.println(matrixView(k, k, rowR));

    //Deleting h+1...k-1 bits of r:
    rowR &= ((1L << (h + 1)) - 1);
    // Util.println("row r of BRANCH with (h=" + h + ") h+1...k-1 bits set to 0:");
    // Util.println(matrixView(k, k, rowR));

    // we get the field we want to copy from:
    rowI = Util.getField(i, k, branch);
    // Util.println("row i(" + i + ") of BRANCH:");
    // Util.println(matrixView(k, k, rowI));

    // we keep only the h+1...k-1 bits of i:
    rowI &= ~((1L << (h + 1)) - 1);
    // Util.println("row i of BRANCH with (h=" + h + ") 0...h bits set to 0:");
    // Util.println(matrixView(k, k, rowI));

    // we merge both results
    // Util.println("rowI | rowR:");
    // Util.println(matrixView(k, k, rowI | rowR));

    // and write it to branch
    branch = Util.setField(r, rowI | rowR, k, branch);
    Util.println("BRANCH: wrote rowI | rowR in row " + r + ":");
    Util.println(Util.matrixToString(k, k, branch));

    // Util.println("BRANCH after insertion of " + x + " in row " + r + ":");
    // Util.println(matrixView(k, k, branch));
    // Util.println("FREE after insertion of " + x + " in row " + r + ":");
    // Util.println(matrixView(k, k, free));

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

    // Update:
    // Compressing key
    updateCompressingKey();
    // compressed Keys
    updateKeyCompression();
    // dontcares
    updateFree();
    // branch
    // free
    updateBranch();
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

    branch = 0;
    free = -1;
  }

  /**
   * Returns the index of the first empty slot in KEY.
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
    return Util.rankLemma1(xCompressed, branch | ((xCompressed * Util.getFields(0, n, k, M)) & free), n, k);
  }

  private int dontCaresRank(final long x) {
    if (isEmpty()) {
      return 0;
    }

    final int i = match(x);
    // Util.println("MATCH ("+x+")=" + i);
    final long y = select(i);
    final int comp = Long.compareUnsigned(x, y);

    if (comp == 0) {
      return i;
    }

    final int j = Util.msb(x ^ y);

    if (comp < 0) {
      return match(x & ~((1L << j) - 1));
    }

    return 1 + match(x | ((1L << j) - 1));
  }

  // Write a function matrix_M(a,b) to compute the matrix M_{a:b} (as a word).
  // The function has two cases: if a=0 and b=k-1, then is it 1^{k^2}.
  // Otherwise it is computed as M_{b+1} - M_{a}.
  private static long matrixM(final int h) {
    return M << h;
  }

  // Write a function matrix_M(a,b) to compute the matrix M_{a:b} (as a word).
  // The function has two cases: if a=0 and b=k-1, then is it 1^{k^2}.
  // Otherwise it is computed as M_{b+1} - M_{a}.
  private static long matrixMColumnRange(final int lo, final int hi) {
    if (lo == 0 && hi == k - 1) {
      return -1;
    } else {
      // long M_h = (0000001) << k
      return matrixM(hi + 1) - matrixM(lo);
    }
  }

  private static long matrixMRowRange(final int lo, final int hi) {
    if (hi < lo) {
      return 0;
    }
    // if (lo == 0 && hi == k - 1) {
    //   return -1;
    // }
    // ((-1L) << (lo * k)) & (-1L>>> ((k-hi)*k);
    // return (1L << ((hi + 1) * k)) - (1L << (lo * k));
    return ((-1L) << (lo * k)) & (-1L >>> ((k - hi - 1) * k));
  }

  /**
   * Adds a column to both {@code branch} and {@code free}, and returns matrixM(h)
   * for further computations.
   * 
   * @param h the index (rank) of the new column
   * @return matrixM(h)
   */
  private long addColumn(final int h) {
    final long Mlo = matrixMColumnRange(0, h - 1);
    final long Mhi = matrixMColumnRange(h, k - 1);

    // shift all columns >= h one to the left
    branch = (branch & Mlo) | ((branch & Mhi) << 1);
    free = (free & Mlo) | ((free & Mhi) << 1);

    // fix the new column h:
    final long matrixM_h = matrixM(h);
    free |= matrixM_h;
    branch &= ~matrixM_h;

    return matrixM_h;
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

  private void updateKeyCompression() {
    for (int i = 0; i < n; i++) {
      compressedKeys = Util.setField(i, compress(select(i)), k, compressedKeys);
    }
  }

  private void updateBranch() {
    final int numSignificantBits = Long.bitCount(compressingKey); // how many bits are set in the
    // compressing key

    for (int i = 0; i < n; i++) {
      final long compKey = Util.getField(i, k, compressedKeys);
      final long dontCare = Util.getField(i, k, free);
      long keyBranch = 0L;

      for (int bit = 0; bit < numSignificantBits; bit++) {
        if (Util.bit(bit, dontCare) == 0 && Util.bit(bit, compKey) == 1) { // we care
          keyBranch = Util.setBit(bit, keyBranch);
        }
      }
      branch = Util.setField(i, keyBranch, k, branch);
    }
  }

  private void updateFree() {
    free = dontCares(0, k - 1, 0, n);
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
  private long dontCares(long free, final int bit, final int lo, final int hi) {
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
      return dontCares(free, bit - 1, lo, hi);
    } else {
      // don't need to delete anything in dontCares as it is zero.
      // 2 x recursive calls
      return dontCares(free, bit - 1, lo, mid) | dontCares(free, bit - 1, mid, hi);
    }
  }

  /**
   * Debugging.
   * 
   * @param args --
   */
  public static void main(final String[] args) {
    final DynamicFusionNodeDontCaresInsert insertNode = new DynamicFusionNodeDontCaresInsert();
    final DynamicFusionNodeDontCaresRank naiveNode = new DynamicFusionNodeDontCaresRank();

    final long[] keys = new long[] {
        // 0b0110011100,
        // 0b0110100100,
        // 0b0110111100,
        // 0b0111010100,
        // 0b1111110010,
        // 0b1111111000,
        // 0b1111111010,
        // 0b1111111100,
        // 0b1111111110
        274331023036983859L, 1332553966501575478L, 8099992861240298690L, -5548310020734087825L, -5419625727381872464L,
        -4865736599853602242L, -2984670664859312928L, -443923248454636511L };

    // Random rand = new Random(-1488139573943419793L);
    // Random rand = new Random();
    final HashSet<Long> set = new HashSet<>();
    // while (set.size() < k) {
    // set.add(rand.nextLong());
    // }
    for (int i = 0; i < keys.length; i++) {
      set.add(keys[i]);
    }

    Util.println("keys: " + set);
    final long[] keyArray = new long[k];
    int i = 0;
    for (final long key : set) {
      if (i == 3) {
        Util.println("debug");
      }
      keyArray[i] = key;
      insertNode.insert(key);
      naiveNode.insert(key);
      if (insertNode.branch != naiveNode.branch() || insertNode.free != naiveNode.free()) {
        Util.println("ERROR INTRODUCED WHEN INSERTING " + key + " (iteration " + i + ")!!");
        Util.println("Keys = " + Arrays.toString(keyArray));
        Util.println("naive key = " + Util.bin(naiveNode.compressingKey(), k));
        Util.println(" O(1) key = " + Util.bin(insertNode.compressingKey, k));
        Util.println("------ branch in the naive node ------");
        Util.println(Util.matrixToString(k, k, naiveNode.branch()));
        Util.println("------ branch in the O(1) node ------");
        Util.println(Util.matrixToString(k, k, insertNode.branch));
        Util.println("naiveBranch == O(1)branch: " + (insertNode.branch == naiveNode.branch()) + "\n");
        Util.println("------ free in the naive node ------");
        Util.println(Util.matrixToString(k, k, naiveNode.free()));
        Util.println("------ free in the O(1) node ------");
        Util.println(Util.matrixToString(k, k, insertNode.free));
        Util.println("naiveFree == O(1)free: " + (insertNode.free == naiveNode.free()) + "\n");
        break;
      }
      i++;
    }

    // for (long key : set) {
    //   Util.println(key + " inserted = " + node.member(key));
    // }

    // When adding a new row for a new key in free, if the branching bit was already set in the compressingKey
    // We need to check the lower bits of the compressed keys. There is a bug there.

    
  }
}