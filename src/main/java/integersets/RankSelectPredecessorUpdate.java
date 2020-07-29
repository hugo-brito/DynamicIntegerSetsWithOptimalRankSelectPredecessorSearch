package integersets;

public interface RankSelectPredecessorUpdate {

  /** Sets S = S union {x}.
   * @param x the integer to insert
   */
  void insert(long x);

  /** Sets S = S / {x}.
   * @param x the integer to delete
   */
  void delete(long x);

  /** Returns to true iff the integer is in the set.
   * @param x the integer to be used in the evaluation
   * @return true if the integer is in the set, false otherwise
   */
  boolean member(long x);

  /** Returns the largest key in the subset of keys that are strictly smaller than x.
   * pred(x) = max{y in S | y < x}
   * It follows the rule predecessor(x) = select(rank(x)-1)
   * @param x the integer queried
   * @return the largest integer that is in the set but that is still smaller than the
  integer queried.
   */
  default Long predecessor(long x) {
    return select(rank(x) - 1);
  }

  /** Returns the smallest key in the subset of keys that are larger or equal to x.
   * succ(x) = min{y in S | y >= x}
   * It follows the rule successor(x) = select(rank(x))
   * @param x the key
   * @return x if it is in the set, or the smallest integer that is in the set that is larger than x
   */
  default Long successor(long x) {
    return select(rank(x));
  }

  /** Returns the number of keys in the set that are strictly smaller than x.
   * rank(x) = #{y in S | y < x}
   * @param x the position in the set to be queried
   * @return the number of integers in the set up to position x
   */
  long rank(long x);

  /** Assuming the natural ordering of keys, returns the key in position i.
   * @param rank the position
   * @return the key at position i
   */
  Long select(long rank);

  /** Returns the number of keys present in the set.
   * 
   * @return the number of keys present in the set
   */
  long size();

  /** Returns true if the set if empty.
   * 
   * @return {@code true} if the set is empty, and {@code false} otherwise
   */
  default boolean isEmpty() {
    return size() == 0;
  }

  /** Resets the data structure, removing all elements.
   */
  void reset();
}
