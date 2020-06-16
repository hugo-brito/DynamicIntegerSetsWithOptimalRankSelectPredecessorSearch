interface RankSelectPredecessorUpdate {

	/**
	 * Sets S = S union {x}
	 * @param x the integer to insert
	 */
	void insert(long x);

	/**
	 * Sets S = S / {x}
	 * @param x the integer to delete
	 */
	Long delete(long x);

	/**
	 * Returns to true iff the integer is in the set
	 * @param x the integer to be used in the evaluation
	 * @return true if the integer is in the set, false otherwise
	 */
	boolean member(long x);

	/**
	 * Returns the largest integer that is in the set but that is still smaller than the integer queried.
	 * It follows the rule predecessor(x) = select(rank(x)-1)
	 * @param x the integer queried
	 * @return the largest integer that is in the set but that is still smaller than the integer queried
	 */
	long predecessor(long x);

	/**
	 * Returns x if it is in the set or the smallest integer that is in the set that is larger than x.
	 * It follows the rule successor(x) = select(rank(x))
	 * @param x the integer queried
	 * @return x if it is in the set or the smallest integer that is in the set that is larger than x
	 */
	long successor(long x);

	/**
	 * Returns the number of integers in the set up to position x.
	 * @param x the position in the set to be queried
	 * @return the number of integers in the set up to position x
	 */
	long rank(long x);

	/**
	 * Returns the position in the set of the i-th element if any. It follows the rule:
	 * - predecessor(x) = select(rank(x)-1)
	 * - successor(x) = select(rank(x))
	 * @param i
	 * @return
	 */
	long select (long i);
}
