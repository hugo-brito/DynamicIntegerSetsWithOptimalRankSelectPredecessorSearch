package DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch;

public class PatriciaTrie implements RankSelectPredecessorUpdate {

  static class PTrieNode<E> extends Node<E> {

    PTrieNode<E> left;
    PTrieNode<E> right;
    int bit;

    public PTrieNode(final E key, final int bit) {
      super(key);

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

  private final PTrieNode<BitsKey> root;

  public PatriciaTrie() {
    root = new PTrieNode<BitsKey>(null, -1);
    root.left = root;
  }

  /** Returns the number of keys present in the set.
   * 
   */
  @Override
  public long size() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void insert(final long x) {
    int i = 0;

    final BitsKey v = new BitsKey(x);
    final PTrieNode<BitsKey> t = search(root.left, v, -1);

    final BitsKey w = (t == null) ? null : t.key;
    if (w != null && v.equals(w)) {
      return; // Perform an insertion only if key only is not present
    }

    // Find the most significant digit where the keys differ
    while (v.bit(i) == w.bit(i)) {
      i++;
    }

    root.left = insert(root.left, v, i, root);
  }

  private PTrieNode<BitsKey> insert(final PTrieNode<BitsKey> curr, final BitsKey v, final int i,
      final PTrieNode<BitsKey> prev) {
    // KEY v = x.key();
    if ((curr.bit >= i) || (curr.bit <= prev.bit)) {
      final PTrieNode<BitsKey> newNode = new PTrieNode<BitsKey>(v, i);
      newNode.left = v.bit(newNode.bit) == 0 ? newNode : curr;
      newNode.right = v.bit(newNode.bit) == 0 ? curr : newNode;
      return newNode;
    }

    if (v.bit(curr.bit) == 0) {
      curr.left = insert(curr.left, v, i, curr);
    } else {
      curr.right = insert(curr.right, v, i, curr);
    }

    return curr;
  }

  @Override
  public void delete(final long x) {
    int i = 0;

    final BitsKey v = new BitsKey(x);
    final PTrieNode<BitsKey> t = search(root.left, v, -1);

    final BitsKey w = (t == null) ? null : t.key;
    if (v.equals(w)) {
      return; // Perform a deletion only if key only is not present
    }

    // Find the most significant digit where the keys differ
    while (v.bit(i) == w.bit(i)) {
      i++;
    }

    root.left = delete(root.left, v, i, root);
  }

  private PTrieNode<BitsKey> delete(final PTrieNode<BitsKey> curr, final BitsKey v, final int i, final PTrieNode<BitsKey> prev) {
    if ((curr.bit >= i) || (curr.bit <= prev.bit)) {
      // if (v.equals(curr.key)) {
      //   if (prev.left == curr) {

      //   } else {

      //   }
      // }
      // do the deletion here!
      final PTrieNode<BitsKey> newNode = new PTrieNode<BitsKey>(v, i);
      newNode.left = v.bit(newNode.bit) == 0 ? newNode : curr;
      newNode.right = v.bit(newNode.bit) == 0 ? curr : newNode;
      return newNode;
    }

    if (v.bit(curr.bit) == 0) {
      curr.left = delete(curr.left, v, i, curr);
    } else {
      curr.right = delete(curr.right, v, i, curr);
    }

    return curr;

  }

  @Override
  public boolean member(final long x) {
    final BitsKey searchKey = new BitsKey(x);
    final PTrieNode<BitsKey> res = search(root.left, searchKey, -1);
    return res != null && res.key.equals(searchKey);
  }

  private PTrieNode<BitsKey> search(final PTrieNode<BitsKey> curr, final BitsKey v, final int i) {
    if (curr.bit <= i) {
      return curr;
    }
    if (v.bit(curr.bit) == 0) {
      return search(curr.left, v, curr.bit);
    } else {
      return search(curr.right, v, curr.bit);
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
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long select(final long i) {
    // TODO Auto-generated method stub
    return 0;
  }

  /* Useful functions */

  public int count() {
    return root.count();
  }

  public int height() {
    return root.height();
  }

  public void show() {
    root.left.show();
  }

  public static void main(final String[] args) {
    PatriciaTrie t = new PatriciaTrie();
    t.insert(1);
    t.insert(-1);
    t.insert(10);
    t.insert(20);
  }
}