class Node<E> {

  final E data;
  Node<E> left;
  Node<E> right;

  public Node(final E key) {
    // NB!: We allow the key to be null because internal node cannot hold values
    this.data = key;
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
  public int children() {
    int res = -1;
    switch ((left == null ? 0 : 1) + 2 * (right == null ? 0 : 1)) {
      case 0:
        res = 0;
        break;
      case 1:
        res = 1;
        break;
      case 2:
        res = 2;
        break;
      case 3:
        res = 3;
        break;
      default:
        break;
    }
    return res;
  }
}

interface BinaryTree {

  /** Counts and returns the total number of nodes in the tree.
   * @return
   */
  int count();

  default <E> int count(final Node<E> curr) {
    if (curr == null) {
      return 0;
    }
    
    return count(curr.left) + count(curr.right) + 1;
  }

  /** Counts and returns the total number of leaf nodes in the tree.
   * @return the number of leaf nodes
   */
  int countLeafNodes();

  default <E> int countLeafNodes(Node<E> curr) {
    if (curr == null) {
      return 0;
    }

    if (curr.children() == 0) {
      return 1;
    }

    return countLeafNodes(curr.left) + countLeafNodes(curr.right);
  }

  /** Returns the height of the tree.
   * @return
   */
  int height();

  default <E> int height(final Node<E> node) {
    if (node == null) {
      return -1;
    }
    
    final int u = height(node.left);
    final int v = height(node.right);
    
    if (u > v) {
      return u + 1;
    } else {
      return v + 1;
    }
  }

  /** Pretty prints the contents of the tree.
   */
  void show();

  default <E> void show(final Node<E> t, final int h) {
    if (t == null) {
      printnode(null, h);
      return;
    }
    show(t.right, h + 1);
    printnode(t.data, h);
    show(t.left, h + 1);
  }

  default <E> void printnode(final E x, final int h) {
    for (int i = 0; i < h; i++) {
      System.out.print("	");
    }
    System.out.println("[" + x + "]");
  }
}