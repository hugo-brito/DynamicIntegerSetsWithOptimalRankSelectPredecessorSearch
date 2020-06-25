package DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch;

abstract class Node<E> {

  final E key;

  public Node(final E key) {
    // NB!: We allow the key to be null because internal node cannot hold values
    this.key = key;
  }

  abstract Node<E> left();

  abstract Node<E> right();

  int children() {
    return children(this);
  }

  /** Returns the number of children, if any, and which.
   * Returns:
   *  *  0 if it is a leaf node
   *  *  1 if it has a single left child
   *  *  2 if it has a single right child
   *  *  3 if it has 2 children
   * @param node The node to be evaluated.
   * @return
   */
  public final int children(final Node<E> node) {
    return ((node.left() == null ? 0 : 1) + 2 * (node.right() == null ? 0 : 1));
  }

  /** Counts and returns the total number of nodes in this sub-tree including self.
   * @return
   */
  public final int count() {
    return count(this);
  }

  private int count(final Node<E> curr) {
    if (curr == null) {
      return 0;
    }
    
    return count(curr.left()) + count(curr.right()) + 1;
  }

  /** Returns the height of the sub-tree rooted at this node.
   * @return
   */
  public int height() {
    return height(this);
  }

  protected int height(final Node<E> node) {
    if (node == null) {
      return -1;
    }
    
    final int u = height(left());
    final int v = height(right());
    
    if (u > v) {
      return u + 1;
    } else {
      return v + 1;
    }
  }

  /** Pretty prints the contents of the sub tree rooted at this node.
   */
  public void show() {
    show(this, 0);
  }

  protected void show(final Node<E> t, final int h) {
    if (t == null) {
      printnode(null, h);
      return;
    }
    show(t.right(), h + 1);
    printnode(t.key, h);
    show(t.left(), h + 1);
  }

  protected void printnode(final E x, final int h) {
    for (int i = 0; i < h; i++) {
      System.out.print("  ");
    }
    System.out.println("[" + x + "]");
  } 
}