class PatriciaKey {

  /** Encapsulating class for the Patricia trie.
   * The necessary bit for the nodes is stored in an
   * object that also holds the BitsKey.
   */
  
  final BitsKey key;
  int bit;

  public PatriciaKey(final BitsKey key, final int i) {
    this.key = key;
    this.bit = i;
  }
}

class PatriciaTrie implements BinaryTree, RankSelectPredecessorUpdate {

  private final Node<PatriciaKey> root;
  private long N;

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
    root = new Node<PatriciaKey>(new PatriciaKey(null, -1));
    root.left = root;
    N = 0;
  }

  public long size() {
    return N;
  }

  @Override
  public void insert(final long x) {
    int i = 0;

    final BitsKey v = new BitsKey(x);
    final Node<PatriciaKey> t = search(root.left, v, -1);

    final BitsKey w = (t == null) ? null : t.data.key;
    if (v.val == w.val) {
      return; // Perform an insertion only if key only is not present
    }

    // Find the most significant digit where the keys differ
    while (v.bit(i) == w.bit(i)) {
      i++;
    }

    root.left = insert(root.left, v, i, root);

    // Upon successful insertion, we update the total number of keys in the set
    N++; // problem here
  }

  private Node<PatriciaKey> insert(final Node<PatriciaKey> curr, final BitsKey v, final int i,
      final Node<PatriciaKey> prev) {
    // KEY v = x.key();
    if ((curr.data.bit >= i) || (curr.data.bit <= prev.data.bit)) {
      final Node<PatriciaKey> newNode = new Node<PatriciaKey>(new PatriciaKey(v, i));
      newNode.left = v.bit(newNode.data.bit) == 0 ? newNode : curr;
      newNode.right = v.bit(newNode.data.bit) == 0 ? curr : newNode;
      return newNode;
    }

    if (v.bit(curr.data.bit) == 0) {
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
    final Node<PatriciaKey> res = search(root.left, new BitsKey(x), -1);
    return res != null && res.data.key.val == x;
  }

  private Node<PatriciaKey> search(final Node<PatriciaKey> curr, final BitsKey v, final int i) {
    if (curr.data.bit <= i) {
      return curr;
    }
    if (v.bit(curr.data.bit) == 0) {
      return search(curr.left, v, curr.data.bit);
    } else {
      return search(curr.right, v, curr.data.bit);
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

  @Override
  public int count() {
    return count(root);
  }

  @Override
  public int height() {
    return height(root);
  }

  @Override
  public void show() {
    show(root, 0);
  }

  public static void main(final String[] args) {
    
  }
}