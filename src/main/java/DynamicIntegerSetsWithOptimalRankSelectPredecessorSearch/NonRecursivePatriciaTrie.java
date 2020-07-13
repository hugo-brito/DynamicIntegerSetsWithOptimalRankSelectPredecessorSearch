package DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NonRecursivePatriciaTrie implements RankSelectPredecessorUpdate, Iterable<BitsKey> {

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
  private int count;

  /**
   * Initializes an empty PATRICIA-based set.
   */
  /* The constructor creates a head (sentinel) node that contains a
   * zero-length string.
   */
  public NonRecursivePatriciaTrie() {
    empty();
  }

  /**
   * Resets the data strucutre, removing all items.
   */
  @Override
  public void empty() {
    root = new PTrieNode<BitsKey>(null, 0);
    root.left = root;
    root.right = root;
    count = 0;
  }

  @Override
  public void insert(final long x) {
    BitsKey insertionKey = new BitsKey(x);

    if (isEmpty()) {
      root = new PTrieNode<BitsKey>(new BitsKey(x), 0);
      root.left = root;
      root.right = root;
      count++;

    } else {

      PTrieNode<BitsKey> parent;
      PTrieNode<BitsKey> curr = root;

      do {
        parent = curr;
        if (insertionKey.bit(curr.bit) == 0) {
          curr = curr.left;
        } else {
          curr = curr.right;
        }
      } while (parent.bit < curr.bit);

      if (!curr.key.equals(insertionKey)) {
        final int bit = MSB.msb64Obvious(curr.key.val ^ insertionKey.val);
        curr = root;
        do {
          parent = curr;
          if (insertionKey.bit(curr.bit) == 0) {
            curr = curr.left;
          } else {
            curr = curr.right;
          }
        } while (parent.bit < curr.bit && curr.bit < bit);

        final PTrieNode<BitsKey> t = new PTrieNode<BitsKey>(insertionKey, bit);

        if (insertionKey.bit(bit) == 0) {
          t.left = t;
          t.right = curr;
        } else {
          t.left = curr;
          t.right = t;
        }

        if (insertionKey.bit(parent.bit) == 0) {
          parent.left = t;
        } else {
          parent.right = t;
        }
        count++;
      }
    }
  }

  /**
   * Removes the key from the set if the key is present.
   * 
   * @param x the key
   * @throws IllegalArgumentException if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is the empty string.
   */
  @Override
  public void delete(final long x) {

    if (isEmpty()) {
      return;
    }

    final BitsKey key = new BitsKey(x); // the key to be deleted
    
    if (size() == 1 && key.equals(root.key)) {
      root = new PTrieNode<BitsKey>(null, 0);
      root.left = root;
      root.right = root;
      count--;
      assert (count == 0);
      return;
    } 

    PTrieNode<BitsKey> grandparent; // previous previous (grandparent)
    PTrieNode<BitsKey> parent = root; // previous (parent)
    PTrieNode<BitsKey> curr = root; // node to delete

    do { // find the key
      grandparent = parent;
      parent = curr;
      // if (safeBitTest(key, x.b)) {
      if (key.bit(curr.bit) == 0) {
        curr = curr.left;
      } else {
        curr = curr.right;
      }
    } while (parent.bit < curr.bit);

    if (curr.key.equals(key)) { // key is present
      PTrieNode<BitsKey> z; // parent of curr
      PTrieNode<BitsKey> y = root;
      do { // find the true parent (z) of x
        z = y;
        if (key.bit(y.bit) == 0) {
          y = y.left;
        } else {
          y = y.right;
        }
      } while (y != curr);

      if (curr == parent) { // case 1: remove (leaf node) x
        PTrieNode<BitsKey> c; // child of x
        if (key.bit(curr.bit) == 0) {
          c = curr.right;
        } else {
          c = curr.left;
        }

        // if (safeBitTest(key, z.bit)) {
        if (key.bit(z.bit) == 0) {
          z.left = c;
        } else {
          z.right = c;
        }

      } else { // case 2: p replaces (internal node) x
        PTrieNode<BitsKey> child; // child of p

        if (key.bit(parent.bit) == 0) {
          child = parent.right;
        } else {
          child = parent.left;
        }

        if (key.bit(grandparent.bit) == 0) {
          grandparent.left = child;
        } else {
          grandparent.right = child;
        }

        if (key.bit(z.bit) == 0) {
          z.left = parent;
        } else {
          z.right = parent;
        }
        parent.left = curr.left;
        parent.right = curr.right;
        parent.bit = curr.bit;
      }
      count--;
    }
  }

  /**
   * Does the set contain the given key?
   * 
   * @param key the key
   * @return {@code true} if the set contains {@code key} and {@code false}
   *         otherwise
   * @throws IllegalArgumentException if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is the empty string.
   */
  @Override
  public boolean member(final long y) {
    // if (key == null) {
    //   throw new IllegalArgumentException("called contains(null)");
    // }
    // if (key.length() == 0) {
    //   throw new IllegalArgumentException("invalid key");
    // }
    final BitsKey key = new BitsKey(y);

    PTrieNode<BitsKey> p;
    PTrieNode<BitsKey> x = root;
    do {
      p = x;
      // if (safeBitTest(key, x.b)) {
      if (key.bit(x.bit) == 1) {
        x = x.right;
      } else {
        x = x.left;
      }
    } while (p.bit < x.bit);

    return x.key != null && x.key.equals(key);
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

  /**
   * Returns the number of keys in the set.
   * 
   * @return the number of keys in the set
   */
  @Override
  public long size() {
    return count;
  }

  /**
   * Returns all of the keys in the set, as an iterator. To iterate over all of
   * the keys in a set named {@code set}, use the foreach notation:
   * {@code for (Key key : set)}.
   * 
   * @return an iterator to all of the keys in the set
   */
  public Iterator<BitsKey> iterator() {
    final LinkedList<BitsKey> queue = new LinkedList<BitsKey>();
    if (root.left != root) {
      collect(root.left, 0, queue);
    }
    if (root.right != root) {
      collect(root.right, 0, queue);
    }
    return queue.iterator();
  }

  private void collect(final PTrieNode<BitsKey> x, final int bit, final List<BitsKey> queue) {
    if (x.bit > bit) {
      collect(x.left, x.bit, queue);
      queue.add((BitsKey) x.key);
      collect(x.right, x.bit, queue);
    }
  }

  /**
   * Returns a string representation of this set.
   * 
   * @return a string representation of this set, with the keys separated by
   *         single spaces
   */
  public String toString() {
    final StringBuilder s = new StringBuilder();
    for (final BitsKey key : this) {
      s.append(key + " ");
    }
    if (s.length() > 0) {
      s.deleteCharAt(s.length() - 1);
    }
    return s.toString();
  }

  public static void main(String[] args) {
    NonRecursivePatriciaTrie t = new NonRecursivePatriciaTrie();
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