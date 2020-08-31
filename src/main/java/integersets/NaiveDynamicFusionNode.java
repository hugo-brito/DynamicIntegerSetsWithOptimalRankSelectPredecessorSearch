package integersets;

/**
 * Implementation of the {@code NaiveDynamicFusionNode} data structure, as described in Section 3.3
 * of the report.
 */
public class NaiveDynamicFusionNode implements RankSelectPredecessorUpdate {

  private final int k;
  private final long[] key;
  private int n;

  /**
   * Constructs an empty {@code NaiveDynamicFusionNode} with capacity for {@code k} elements.
   * @param k the capacity limit of the set
   */
  public NaiveDynamicFusionNode(int k) {
    this.k = k;
    this.key = new long[k];
    reset();
  }

  @Override
  public void insert(final long x) {
    if (n > 0 && member(x)) {
      return;
    }

    if (n == k) {
      throw new RuntimeException("Cannot insert. Node is full.");
    }

    final int i = (int) rank(x);

    for (int j = i + 1; j < key.length; j++) {
      key[j] = key[j - 1];
    }

    key[i] = x;

    n++;
  }

  @Override
  public void delete(final long x) {
    if (!member(x)) {
      return;
    }

    final int i = (int) rank(x);

    for (int j = i; j < key.length - 1; j++) {
      key[j] = key[j + 1];
    }

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

    return key[(int) rank];
  }

  @Override
  public long size() {
    return n;
  }

  @Override
  public void reset() {
    n = 0;
  }

  /** Helper method than provides the rank of a key {@code x} resorting to binary search.
   * For this reason, it takes O(lg N) time.
   *
   * @param x the key to be used to compute the rank
   * @return the rank of {@code x} in S
   */
  private int binaryRank(final long x) {
    if (n == 0) {
      return 0;
    }

    int lo = 0;
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
}