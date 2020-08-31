package integersets;

/**
 * Implementation of the {@code RankSelectPredecessorUpdate} interface, as described in Section
 * 3.1 of the report.
 */
public interface RankSelectPredecessorUpdate {

  /** Inserts {@code x} in the set if it is not a member (and there is room for it).
   * <br>Sets S = S &cup; {{@code x}}.
   * @param x The query.
   */
  void insert(long x);

  /** If {@code x} is member of the set, it is removed.
   * <br>Sets S = S \ {{@code x}}.
   * @param x The query.
   */
  void delete(long x);

  /** Returns {@code true} iff the {@code x} is in the set.
   * @param x The query.
   * @return {@code true} if the integer is in the set, and {@code false} otherwise.
   */
  default boolean member(final long x) {
    if (isEmpty()) {
      return false;
    }
    final Long res = successor(x);
    return res != null && res == x;
  }

  /** Returns the largest key in the subset of keys that are strictly smaller than {@code x}.
   * <br>predecessor({@code x}) = max{{@code y} in S &#124; {@code y} &lt; {@code x}}
   * <br>It follows the rule predecessor({@code x}) = select(rank({@code x})-1)
   * @param x The query.
   * @return The largest key in the set that is smaller than the query.
   */
  default Long predecessor(final long x) {
    return select(rank(x) - 1);
  }

  /**
   * Returns the smallest key in the subset of keys that are larger or equal to
   * {@code x}. <br>
   * succ({@code x}) = min{{@code y} &isin; S &#124; {@code y} &le; {@code x}}
   * <br>
   * It follows the rule successor({@code x}) = select(rank({@code x}))
   * 
   * @param x The query.
   * @return {@code x} if it is in the set. Otherwise returns the smallest key in
   *         the set that is larger than {@code x}.
   */
  default Long successor(final long x) {
    return select(rank(x));
  }

  /** Returns the number of keys in the set that are strictly smaller than {@code x}.
   * <br>rank({@code x}) = &#35;{{@code y} &isin; S &#124; {@code y} &lt; {@code x}}
   * @param x The query.
   * @return The number of keys in the whose rank is smaller than the rank of {@code x}.
   */
  long rank(long x);

  /** Assuming the natural ordering of keys, returns the key with rank {@code rank}.
   * @param rank The query.
   * @return The key in the set whose rank is {@code rank}.
   */
  Long select(long rank);

  /** Returns the current cardinality of the set.
   * 
   * @return The number of keys in the set.
   */
  long size();

  /** Returns {@code true} if the set is empty.
   * 
   * @return {@code true} if the set is empty, and {@code false} otherwise.
   */
  default boolean isEmpty() {
    return size() == 0;
  }

  /** Resets the data structure, removing all elements.
   */
  void reset();
}
