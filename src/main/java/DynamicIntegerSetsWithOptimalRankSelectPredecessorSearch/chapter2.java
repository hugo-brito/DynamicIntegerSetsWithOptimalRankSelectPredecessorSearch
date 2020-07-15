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
   * @param i the position of the field. Field 0 is the right most field in the word x.
  Field 1 the second most right field of x.
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
  public int m(int i) {
    return ((1 << F) - 1) << (i * F);
  }

  /** Leave fields from i to j intact and everything else is made 0 (and shifted for convenience).
   * @param x the word where the fields should be extracted from
   * @param i the smallest field (inclusive), the right most field to be included
   * @param j the largest field (inclusive), the left most field to be included
   * @return the field interval, shifted all the way to the right
   */
  public int getFields(int x, int i, int j) {
    return (x >>> (i * F)) & ((1 << ((j - i) * F)) - 1);
  }

  /**
   * Shift field i to the right most position (and all the larger fields). All the smaller fields
   * are filled with 0.
   * @param x the word where to operate
   * @param i the first field to keep
   * @return the remaining fields after the operation
   */
  public int getFieldsUntilTheEndOfTheWord(int x, int i) {
    return (x >>> (i * F));
  }
}