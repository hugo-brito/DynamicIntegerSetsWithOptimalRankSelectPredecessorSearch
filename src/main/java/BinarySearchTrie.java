class BinarySearchTrie implements RankSelectPredecessorUpdate {

  static class BSTrieNode<E> extends Node<E> {

    BSTrieNode<E> left;
    BSTrieNode<E> right;
    int leavesBelow;

    public BSTrieNode(final E key) {
      super(key);
      leavesBelow = 1;
    }

    @Override
    Node<E> left() {
      return left;
    }

    @Override
    Node<E> right() {
      return right;
    }
  }

  private BSTrieNode<BitsKey> root;

  public BinarySearchTrie() {
    root = null;
  }

  public long size() {
    if (root != null) {
      return root.leavesBelow;
    }
    return 0;
  }

  @Override
  public void insert(final long x) {

    // We create the BitsKey at this stage because later we conveniently have access
    // to the bit(d) method
    root = insert(root, new BitsKey(x), 0);

    updateLeavesBelow(root);

  }

  private BSTrieNode<BitsKey> insert(final BSTrieNode<BitsKey> curr, final BitsKey v, final int d) {
    if (curr == null) {
      final BSTrieNode<BitsKey> newNode = new BSTrieNode(v);
      return newNode;
    }

    if (curr.children() == 0) {
      if (curr.key != null && curr.key.equals(v)) {
        return curr;
      }
      return split(new BSTrieNode(v), curr, d);
    }

    if (v.bit(d) == 0) {
      curr.left = insert(curr.left, v, d + 1);
    } else {
      curr.right = insert(curr.right, v, d + 1);
    }

    // after insertion checking the number of leaves before insertion
    updateLeavesBelow(curr);

    return curr;
  }

  private BSTrieNode<BitsKey> split(final BSTrieNode<BitsKey> p, final BSTrieNode<BitsKey> q, final int d) {
    final BSTrieNode<BitsKey> t = new BSTrieNode(null);
    t.leavesBelow = 2;
    final BitsKey v = p.key;
    final BitsKey w = q.key;

    /* The switch statement in split converts the two bits that it is testing into a
     * number to handle the four possible cases. If the bits are the same (case 00 =
     * 0 or 11 = 3), then we continue splitting; if the bits are different (case 01
     * = 1 or 10 = 2), then we stop splitting.
     */
    switch (v.bit(d) * 2 + w.bit(d)) {
      case 0:
        t.left = split(p, q, d + 1);
        break;
      case 1:
        t.left = p;
        t.right = q;
        break;
      case 2:
        t.right = p;
        t.left = q;
        break;
      case 3:
        t.right = split(p, q, d + 1);
        break;
      default:
        break;
    }
    return t;
  }

  private void updateLeavesBelow(final BSTrieNode node) {
    switch (node.children()) {
      case 1:
        node.leavesBelow = node.left.leavesBelow;
        break;
      case 2:
        node.leavesBelow = node.right.leavesBelow;
        break;
      case 3:
        node.leavesBelow = node.left.leavesBelow + node.right.leavesBelow;
        break;
      default:
        break;
    }
  }

  @Override
  public void delete(final long x) {
    final BitsKey delete = new BitsKey(x);
    root = delete(root, delete, 0);
  }

  private BSTrieNode<BitsKey> delete(final BSTrieNode<BitsKey> curr, final BitsKey v, final int d) {

    // if (I'm null) return null
    if (curr == null) {
      return null;

    } else if (curr.key != null) {
      if (curr.key.equals(v)) {
        return null;
      } else {
        return curr;
      }

    } else if (v.bit(d) == 0) { // else if (next bit says to go left) leftchild = deleteR(leftchild, ..)
      curr.left = delete(curr.left, v, d + 1);
    } else {
      curr.right = delete(curr.right, v, d + 1);
        
    // if there is only one child AND the child is a leaf, then return the child (either curr.left or curr.right). Otherwise return curr.
    } if (curr.children() == 1 || curr.children() == 2) { // has a single child
      if (curr.children() == 1 && curr.left.children() == 0) {
        return curr.left; // has a single child and that child is a leaf
      } else if (curr.children() == 2 && curr.right.children() == 0) {
        return curr.right;
      }
    }

    return curr;

  }

  @Override
  public boolean member(final long x) {
    final BitsKey searchKey = new BitsKey(x);
    final BSTrieNode<BitsKey> res = search(root, searchKey, 0);
    return res != null && res.key.equals(searchKey);
  }

  private BSTrieNode<BitsKey> search(final BSTrieNode<BitsKey> curr, final BitsKey v, final int d) {
    // Current node is null. Unsuccessful search.
    if (curr == null) {
      return null;
    }

    // Node with no children. We are at a leaf node. Return it for key comparison.
    if (curr.children() == 0) {
      return curr;
    }

    // Node with children, keep searching recursively.
    if (v.bit(d) == 0) {
      return search(curr.left, v, d + 1);
    } else {
      return search(curr.right, v, d + 1);
    }
  }

  @Override
  public long predecessor(final long x) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long successor(final long x) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long rank(final long x) {
    return rank(root, new BitsKey(x), 0);
  }

  private long rank(final BSTrieNode<BitsKey> curr, final BitsKey v, final int d) {

    if (curr == null) {
      return 0;
    }

    if (curr.children() == 0) { // leaf node, there will be a key.
      if (curr.key.compareTo(v) < 0) {
        return 1;
      } else {
        return 0;
      }
    }

    if (v.bit(d) == 0) {
      // If the bit is zero go left, and don't do anything,
      return rank(curr.left, v, d + 1);
    } else {
      // If the bit is one go right, we add the number of keys on the left subtree to
      // a
      // local counter.
      return countLeafNodes(curr.left) + rank(curr.right, v, d + 1);
    }
  }

  @Override
  public long select(final long i) {
    // if (i > N || i < 0) {
    //   return -1; // invalid query
    // }

    if (i == 0) {
      return 0; // convention
    }

    // long lowerBound = Long.MIN_VALUE; // the smallest key
    // long upperBound = Long.MAX_VALUE; // the largest key.
    // long candidate = -1;

    // while (lowerBound <= upperBound) {
    //   final long middle = (upperBound - lowerBound) / 2 + lowerBound;

    //   if (rank(middle) > i) {
    //     upperBound = middle - 1;
    //   }

    //   else if (rank(middle) == i) {
    //     candidate = middle;
    //     upperBound = middle - 1;
    //   }

    //   else {
    //     lowerBound = middle + 1;
    //   }
    // }
    
    // return candidate;
    
    return -1;
  }

  /* Useful functions */

  public int count() {
    return root.count();
  }

  public int height() {
    return root.height();
  }

  public void show() {
    root.show();
  }

  /** Counts and returns the total number of leaf nodes in the tree.
   * @return the number of leaf nodes
   */
  public int countLeafNodes() {
    return countLeafNodes(root);
  }

  private int countLeafNodes(final BSTrieNode curr) {
    if (curr == null) {
      return 0;
    }

    if (curr.children() == 0) {
      return 1;
    }

    return countLeafNodes(curr.left) + countLeafNodes(curr.right);
  }

  public static void main(final String[] args) {
    // BitsKey v = new BitsKey(6917529027641081855L);
    // BitsKey w = new BitsKey(Long.MAX_VALUE);
    // BitsKey x = new BitsKey(4611686018427387903L);
    // System.out.println(v.toString());
    // [Bin = 0101111111111111111111111111111111111111111111111111111111111111, Val = 6917529027641081855]
    // System.out.println(w.toString());
    // [Bin = 0111111111111111111111111111111111111111111111111111111111111111, Val = 9223372036854775807]
    // System.out.println(x.toString());
    // [Bin = 0011111111111111111111111111111111111111111111111111111111111111, Val = 4611686018427387903]

    final BinarySearchTrie t = new BinarySearchTrie();
    t.insert(6917529027641081855L); // 01011
    t.insert(4611686018427387903L); // 00111
    t.insert(9223372036854775807L); // 01111
    t.insert(9223372036854775807L); // 01111
    t.insert(9223372036854775807L); // 01111
    System.out.println("10 is member = " + t.member(10));
    System.out.println("6917529027641081855 is member = " + t.member(6917529027641081855L));
//    t.insert(Long.MAX_VALUE);
//    System.out.println("10 is member = " + t.member(10));
//    System.out.println("Long.MAX_VALUE is member = " + t.member(Long.MAX_VALUE));
//    t.delete(Long.MAX_VALUE);
//    System.out.println("Long.MAX_VALUE is member = " + t.member(Long.MAX_VALUE));
//    System.out.println("6917529027641081855 is member = " + t.member(6917529027641081855L));
//    System.out.println("4611686018427387903 is member = " + t.member(4611686018427387903L));
//    t.show();
    // System.out.println(t.height());

//    t.insert(10);
//    t.insert(11);
//    t.insert(12);

//    System.out.println(t.rank(13) == 3);
//    System.out.println(t.select(3));
    
//     t.insert(5764607523034234880L); // 01010 (10)
//     t.insert(6341068275337658368L); // 01011 (11)
//     t.insert(6917529027641081856L); // 01100 (12)
//     t.insert(7493989779944505344L); // 01101 (13) 000....
//    System.err.println(t.rank(7493989779944505344L));
    
//    System.out.println("Inserting Long.MAX_VALUE");
//    t.insert(Long.MAX_VALUE);
//    t.insert(Long.MAX_VALUE - 1);
//    System.out.println(t.rank(-1));

    
  }
}