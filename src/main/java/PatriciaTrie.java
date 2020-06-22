class PatriciaTrie implements RankSelectPredecessorUpdate {

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

  // static class Node {
  // final BitsKey key;
  // Node left, right;
  // int bit;
  // public Node(BitsKey key, int i) {
  // this.key = key;
  // this.bit = i;
  // }
  // }

  public PatriciaTrie() {
    root = new PTrieNode<BitsKey>(null, -1);
    root.left = root;
  }

  @Override
  public void insert(final long x) {
    int i = 0;

    final BitsKey v = new BitsKey(x);
    final PTrieNode<BitsKey> t = search(root.left, v, -1);

    final BitsKey w = (t == null) ? null : t.key;
    if (v.val == w.val) {
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
    // TODO Auto-generated method stub
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
    
  }
}