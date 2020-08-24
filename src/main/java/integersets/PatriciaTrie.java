package integersets;

public class PatriciaTrie implements RankSelectPredecessorUpdate {

  static class PTrieNode<E> extends Node<E> {

    PTrieNode<E> left;
    PTrieNode<E> right;
    int bit;

    public PTrieNode(final E key, final int bit) {
      super(key);
      this.bit = bit;
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

  private PTrieNode<BitsKey> root;
  private long count;

  public PatriciaTrie() {
    root = new PTrieNode<BitsKey>(null, -1);
    root.left = root;
    count = 0;
  }

  @Override
  public void reset() {
    root.bit = -1;
    root.left = root;
    count = 0;
  }

  @Override
  public long size() {
    return count;
  }

  @Override
  public void insert(final long x) {
    BitsKey v = new BitsKey(x);
    BitsKey w = search(root.left, v, -1);
    if (v.equals(w)) {
      return;
    }

    int i = 0;
    if (w != null) {
      while (v.bit(i) == w.bit(i)) {
        i++;
      }
    }

    root.left = insert(root.left, v, i, root);
    count++;
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

    final BitsKey v = new BitsKey(x);
    final BitsKey w = search(root.left, v, -1);

    if (!v.equals(w)) {
      return; // Perform a deletion only if key only is not present
    }

    int i = 0;
    if (w != null) {
      // Find the most significant digit where the keys differ
      while (v.bit(i) == w.bit(i)) {
        i++;
      }
    }

    root.left = delete(root.left, v, i, root);

    count--;
  }

  private PTrieNode<BitsKey> delete(final PTrieNode<BitsKey> curr, final BitsKey v, final int i,
        final PTrieNode<BitsKey> prev) {

    if ((curr.bit >= i) || (curr.bit <= prev.bit)) {
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
    final BitsKey res = search(root.left, searchKey, -1);
    return res != null && res.equals(searchKey);
  }

  private BitsKey search(final PTrieNode<BitsKey> curr, final BitsKey v, final int i) {
    if (curr.bit <= i) {
      return curr.key;
    }
    if (v.bit(curr.bit) == 0) {
      return search(curr.left, v, curr.bit);
    } else {
      return search(curr.right, v, curr.bit);
    }
  }

  @Override
  public long rank(final long x) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Long select(final long rank) {
    // TODO Auto-generated method stub
    return 0L;
  }

  /** Debugging.
   * 
   * @param args --
   */
  public static void main(final String[] args) {
    PatriciaTrie t = new PatriciaTrie();
    System.out.println(t.member(1));
    t.insert(1);
    System.out.println(t.member(1));
    t.insert(2);
    System.out.println(t.member(2));
    t.insert(732493);
    System.out.println(t.member(732493));
    // t.insert(-1);
    // t.insert(10);
    // t.insert(20);
    System.out.println(t.member(6046118547395480652L));
    t.insert(6046118547395480652L);
    System.out.println(t.member(6046118547395480652L));
    System.out.println(new BitsKey(6046118547395480652L).bin());
  }
}