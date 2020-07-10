package DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch;

/** Often we view words as divided into fields of some length f. We then use x(i)_f to denote the
 * ith field, starting from the right with x(0)_f the right most field. Thus x represents the
 * integer E^(w−1)_(i=0) 2^i x(i)1.
 * Note that fields can easily be masked out using regular instructions, e.g
 */

class FieldsOfWords {
  /** Often we view words as divided into fields of some length f. We then use x(i)_f to denote the
   * ith field, starting from the right with x(0)_f the right most field. Thus x represents the
   * integer E^(w−1)_(i=0) 2^i x(i)1.
   * Note that fields can easily be masked out using regular instructions, e.g
   */

  private final int F; // the number of bits in a field.

  public FieldsOfWords(int f) {
    F = f;
  }

  /** Assign y to field number i in the word x.
   * @param x the word, where field y will be assigned to
   * @param i the position of the field. Field 0 is the right most field in the word x. Field 1 the second most right field of x
   * @param y the field to be assigned in x
   * @return returns the word x after the operation
   */
  public int fieldAssignment(int x, int i, int y){
    return (x & (~(m(i))) | (y << (i * F) & m(i)));
  }

  /** Function defined in the paper. Used for field assignments within words.
   * @param i the position of the field.
   * @return
   */
  public int m(int i){
    return ((1 << F) - 1) << (i * F);
  }

  /** Leave fields from i to j intact and everything else is made 0 (and shifted for convenience).
   * @param x the word where the fields should be extracted from
   * @param i the smallest field (inclusive), the right most field to be included
   * @param j the largest field (inclusive), the left most field to be included
   * @return the field interval, shifted all the way to the right
   */
  public int getFields(int x, int i, int j){
    return (x >>> (i * F)) & ((1 << ((j - i) * F)) - 1);
  }

  /**
   * Shift field i to the right most position (and all the larger fields). All the smaller fields
   * are filled with 0.
   * @param x the word where to operate
   * @param i the first field to keep
   * @return the remaining fields after the operation
   */
  public int getFieldsUntilTheEndOfTheWord(int x, int i){
    return (x >>> (i * F));
  }
}

class Indexing64bit implements RankSelectPredecessorUpdate {

  /**
   * 64-bit version.
   * 
   * For this version of indexing, which comes described in page 5 of the paper,
   * we have selected w = 64, e. g. with word is made up of 64 bits. The reason
   * for us to pick this size of word length is that we can conveniently use
   * Java's >> << | & ^ operations in constant time.
   * 
   * The problem with this size for the word length is that they can only
   * guarantee the fast running times when the capacity of the set is of w^(1/4)
   * which is very small. Let w = be 64, then w^(1/4) = 2,8..... This is a set
   * of very small size. So we drift away from this limit to a focus on the part
   * where they say "We assume that our set S is stored in an unordered array KEY
   * of k words plus a single word INDEX such that KEY[INDEX<i>ceilLogK] is the
   * key of rank i in S.". So what happens is that we max out the capacity for
   * storing keys in INDEX given the premise that INDEX shall not exceed 64 bits
   * in size. We calculate k such that k*ceil(log k) = 64, and we know that
   * k = 16.
   * 
   * To help us storing the integers in the set, we use:
   * - 1 array of words KEYS, which in this particular case is a long[]
   * - An additional "array" which we called KEY with k bits, where each "cell"
   * takes ceil(log k) bits. The paper tells us that all of this information needs
   * to fit in one word, e. g. 64 bits. So, doing the math we have 64 = k * ceil(log k)
   * k = 16 (https://www.wolframalpha.com/input/?i=k+ceil%28log_2%28k%29%29+%3D+64)
   * This means that our set will have capaticity for 16 elements.
   * 
   * An important note is that each "slot" in INDEX will have ceil(log k) size and
   * k slots in total. This means that with this specific word size, we will have
   * 16 slots of 4 bits each.
   */

  private int k; // capacity
  private int ceil_log_2_k;

  /* We will store our key set S in an unsorted array KEY with room for k w-bit integers */
  private long[] KEY;
  private long INDEX;
  private long bKEY; // a bit map containing the empty spots in KEY

  private int N; // the current number of elements in S


  public Indexing64bit() {
    k = 16; // specific for when w = 64!!

    ceil_log_2_k = (int) Math.ceil(Math.log10(k) / Math.log10(2));
    // We use the logarithm rules to change the base from 10 to 2. We make ceil(log2_k) to depend on k
    System.out.println(ceil_log_2_k); // we get 4 when w = 64

    KEY = new long[k];
    INDEX = 0;

    N = 0;

    bKEY = -1; // because -1 in java binary is 1111..11

  }

  /** Sets the i-th ceil(log_2 k) of index with the least significant 4 digits of j. Index
   * has k slots.
   * @param i
   */
  private void setINDEX(int i, int v){
    v = v << (32 - ceil_log_2_k);
    INDEX = INDEX ^ v;
  }

  private int getINDEX(long i){
    return (int) (INDEX << (i - 1) * ceil_log_2_k) >> ((k - 1) * ceil_log_2_k);
  }

  @Override
  public void insert(long x) {
    // TODO Auto-generated method stub

  }

  @Override
  public void delete(long x) {
    // TODO Auto-generated method stub
  }

  @Override
  public boolean member(long x) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public long predecessor(long x) {
    return select(rank(x) - 1);
  }

  @Override
  public long successor(long x) {
    return select(rank(x));
  }

  @Override
  public long rank(long x) {  
    // return getINDEX(i);
    return -1;
  }

  @Override
  public long select(long i) {
    return KEY[getINDEX(i)];
  }

  @Override
  public long size() {
    // TODO Auto-generated method stub
    return 0;
  }

  /** Helper function.
   * Given a 64-bit word, val, return the value (0 or 1) of the d-th digit.
   * Digits are indexed 0..63.
   * 
   * @param val the long value to have the digit extracted from
   * @param d the digit index
   * @return 0 or 1 depending on if it's 0 or 1 at the specified index d.
   */
  public static int bit(long val, int d) {
    return (int) (val >> (63 - d)) & 1;
  }

  /** Helper function.
   * Returns a binary representation of the long x in a String containing leading zeroes.
   * It uses the helper function bit to do so and the StringBuilder for fast concatenation.
   * @param x
   * @return
   */
  public static String bin(long x) {
    StringBuilder res = new StringBuilder(64);
    for (int i = 0; i < 64; i++){
      res.append(bit(x, i));
    }
    return res.toString();
  }

  public static void main(String[] args) {
    // new Indexing64bit();

    // for (long i = Long.MIN_VALUE; i == Long.MAX_VALUE; i++){
    //   if (!Long.toBinaryString(i).equals(bin(i))) {
    //     System.err.println("ERROR!\nFor " + i + " expected:\n	" + Long.toBinaryString(i) + "\nbut got:\n	" + bin(i));
    //   }
    // }
    // System.out.println("bin(x) works for the full range of long!");

    // System.out.println(Long.toBinaryString(-1));
  }
}