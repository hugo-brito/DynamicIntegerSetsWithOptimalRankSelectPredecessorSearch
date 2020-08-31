package integersets;

/**
 * Implementation of the {@code BinarySearchTrie} data structure, as described in Section A.2.1 of
 * the report.
 */
public class BinarySearchTrie implements RankSelectPredecessorUpdate {

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

  /**
   * Constructs an empty {@code BinarySearchTrie}.
   */
  public BinarySearchTrie() {
    reset();
  }

  @Override
  public void reset() {
    root = null;
  }

  @Override
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
    root = insert(root, new BitsKey(x), BitsKey.w - 1);

    updateLeavesBelow(root);

  }

  private BSTrieNode<BitsKey> insert(final BSTrieNode<BitsKey> curr, final BitsKey v, final int d) {
    if (curr == null) {
      final BSTrieNode<BitsKey> newNode = new BSTrieNode<BitsKey>(v);
      return newNode;
    }

    if (curr.children() == 0) {
      if (curr.key != null && curr.key.equals(v)) {
        return curr;
      }
      return split(new BSTrieNode<BitsKey>(v), curr, d);
    }

    if (v.bit(d) == 0) {
      curr.left = insert(curr.left, v, d - 1);
    } else {
      curr.right = insert(curr.right, v, d - 1);
    }

    // after insertion checking the number of leaves before insertion
    updateLeavesBelow(curr);

    return curr;
  }

  private BSTrieNode<BitsKey> split(final BSTrieNode<BitsKey> p, final BSTrieNode<BitsKey> q,
      final int d) {
    final BSTrieNode<BitsKey> t = new BSTrieNode<BitsKey>(null);
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
        t.left = split(p, q, d - 1);
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
        t.right = split(p, q, d - 1);
        break;
      default:
        break;
    }
    return t;
  }

  private void updateLeavesBelow(final BSTrieNode<BitsKey> node) {
    switch (node.children()) {
      case 1:
        node.leavesBelow = node.left.leavesBelow;
        return;
      case 2:
        node.leavesBelow = node.right.leavesBelow;
        return;
      case 3:
        node.leavesBelow = node.left.leavesBelow + node.right.leavesBelow;
        return;
      default:
        return;
    }
  }

  @Override
  public void delete(final long x) {
    final BitsKey delete = new BitsKey(x);
    root = delete(root, delete, BitsKey.w - 1);
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

    } else if (v.bit(d) == 0) {
      // else if (next bit says to go left) leftchild = deleteR(leftchild, ..)
      curr.left = delete(curr.left, v, d - 1);
    } else {
      curr.right = delete(curr.right, v, d - 1);
    }

    // if there is only one child AND the child is a leaf,
    // then return the child (either curr.left or curr.right).
    // Otherwise return curr.
    if (curr.children() == 1 || curr.children() == 2) { // has a single child
      if (curr.children() == 1 && curr.left.children() == 0) {
        return curr.left; // has a single child and that child is a leaf
      } else if (curr.children() == 2 && curr.right.children() == 0) {
        return curr.right;
      }
    }

    updateLeavesBelow(curr);

    return curr;
  }

  @Override
  public boolean member(final long x) {
    final BitsKey searchKey = new BitsKey(x);
    final BSTrieNode<BitsKey> res = search(root, searchKey, BitsKey.w - 1);
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
      return search(curr.left, v, d - 1);
    } else {
      return search(curr.right, v, d - 1);
    }
  }

  @Override
  public long rank(final long x) {
    return rank(root, new BitsKey(x), BitsKey.w - 1);
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
      return rank(curr.left, v, d - 1);
    } else {
      // If the bit is one go right, we add the number of keys on the left subtree rank.
      if (curr.children() == 1 || curr.children() == 3) {
        return curr.left.leavesBelow + rank(curr.right, v, d - 1);
      } else {
        return rank(curr.right, v, d - 1);
      }
    }
  }

  @Override
  public Long select(final long rank) {
    if (rank < 0 || rank >= size()) {
      return null;
    }

    return select(root, rank, 0).key.val;   
  }

  private BSTrieNode<BitsKey> select(final BSTrieNode<BitsKey> curr,
      final long rank, final long keySoFar) {

    switch (curr.children()) {
      case 1:
        return select(curr.left, rank, keySoFar);

      case 2:
        return select(curr.right, rank, keySoFar);

      case 3:
        if (curr.left.leavesBelow + keySoFar <= rank) {
          return select(curr.right, rank, keySoFar + curr.left.leavesBelow);
        } else {
          return select(curr.left, rank, keySoFar);
        }
      default:
        return curr;

    }
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

  private int countLeafNodes(final BSTrieNode<BitsKey> curr) {
    if (curr == null) {
      return 0;
    }

    if (curr.children() == 0) {
      return 1;
    }

    return countLeafNodes(curr.left) + countLeafNodes(curr.right);
  }
}