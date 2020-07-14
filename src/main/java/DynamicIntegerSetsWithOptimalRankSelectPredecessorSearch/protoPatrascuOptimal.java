package DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch;

public class ProtoPatrascuOptimal implements RankSelectPredecessorUpdate {

  static class DynamicFusionNode { // this will potentially be a node

    private static final int BITSWORD = 63; // how many bits are stored in a word


    private final int k; // capacity
  
    /* We will store our key set S in an unsorted array KEY with room for k w-bit integers */
    private final long[] KEY;

    /* We will also maintain an array INDEX of ceil(lg k)-bit indices */
    private long INDEX;
    private final int ceil_log_2_k;
    
    private long bKEY; // a bit map containing the empty spots in KEY
  
    private int n; // the current number of elements in S
  
  
    DynamicFusionNode() {
      /*
      We will ourselves use k = w^(1/4)
      The exact value makes no theoretical difference:
      We choose k = 16, such that we can fill a whole word to index all the 
      */
      k = 16;
      KEY = new long[k];
      
      // We use the logarithm rules to change the base from 10 to 2. We make ceil(log2_k) to depend on k
      ceil_log_2_k = (int) Math.ceil(Math.log10(k) / Math.log10(2));

      // We start with no items.
      INDEX = 0;
      n = 0;

      // bitmap containing the empty spots. 1 if it is empty, 0 if it is taken.
      bKEY = -1; // because -1 in java binary is 1111..11
      
      System.out.println("ceil(lg k) = " + ceil_log_2_k); // we get 4 when w = 64
      System.out.println(ceil_log_2_k * k);

      System.out.println("(" + Long.toBinaryString(bKEY).length() + ") " + Long.toBinaryString(bKEY));
  
    }
  
    public static void main(String[] args) {
      DynamicFusionNode h = new DynamicFusionNode();
      System.out.println(h.firstEmptySlot());
    }

    /**
     * Returns the index of the first empty slot in KEY.
     * @return the index in KEY of the first empty slot.
     */
    private int firstEmptySlot() {
      if (n == k) { // no empty spot
        return -1;
      }
      return Util.msb64LookupDistributedInput(bKEY);
    }

    private void fillEmptySlot(int j) {
      if (j > 0 && j < k) {
        bKEY &= ~(1L << (BITSWORD - j));
      } else {
        throw new IndexOutOfBoundsException("j must be between 0 and k (" + k + ")!");
      }
    }

    /**
     * Gets the index of the key x in KEY such that its rank is {@code rank}.
     * @param rank The rank of the key in the S
     * @return the index in KEY of rank {@code rank}
     */
    private int getINDEX(int rank) {
      return (int) (INDEX << (ceil_log_2_k * rank)) >>> (ceil_log_2_k * (k - 1));
    }

    public Long select(int rank) {
      if (rank < 0 || rank > n) {
        return null;
      }

      return KEY[getINDEX(rank)];
    }

    public void insert(long x) {
      // 1. Find the rank of the key
      long i = rank(x);

      // 2. Find a free slot
      int j = firstEmptySlot();

      // 3. Set KEY[j] = key
      KEY[j] = x;

      // 4. Set bKEY[j] = 0
      fillEmptySlot(j);

      // 5. update INDEX according to the new key
      // this operation consists of moving all the indices of the keys that have rank smaller than
      // the new key one position to the left to make room for the index of the new key
      // After this is done, store the index in KEY in INDEX of the new key, respecting the rank of
      // the keys.

      // Need to implement method to move all indices one position to the front

    }

    public long rank(long x){
      return 0;
    }

  }

  @Override
  public void insert(final long x) {
    // TODO Auto-generated method stub

  }

  @Override
  public void delete(final long x) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean member(final long x) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public long rank(final long x) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Long select(final long rank) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public long size() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void empty() {
    // TODO Auto-generated method stub

  }

}