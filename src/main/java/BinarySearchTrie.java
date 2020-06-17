import sun.security.util.ArrayUtil;

class BinarySearchTrie implements RankSelectPredecessorUpdate {

	Node<BitsKey> head;
	private long N;

	public BinarySearchTrie() {
		head = null;
		N = 0;
	}

	public long size() {return N;}

	@Override
	public void insert(long x) {

		// We create the BitsKey at this stage because later we conveniently have access to the bit(d) method
		head = insertR(head, new BitsKey(x), 0);

		// Upon successful insertion, we update the total number of keys in the set
		N++;
	}

	private Node<BitsKey> insertR(Node<BitsKey> curr, BitsKey v, int d) {
		if (curr == null) return new Node<BitsKey>(v);
		
		if (curr.left == null && curr.right == null) {
			if (curr.key != null && curr.key.val == v.val) return curr;
			return split(new Node<BitsKey>(v), curr, d);
		}

		if (v.bit(d) == 0) curr.left = insertR(curr.left, v, d+1);
		else 			   curr.right = insertR(curr.right, v, d+1);
		
		return curr;
	}

	private Node<BitsKey> split(Node<BitsKey> p, Node<BitsKey> q, int d) {
		Node<BitsKey> t = new Node<BitsKey>(null);
		BitsKey v = p.key, w = q.key;

		/** The switch statement in split converts the two bits that it is testing into a number to handle the four possible cases.
		 * If the bits are the same (case 00 = 0 or 11 = 3), then we continue splitting;
		 * if the bits are different (case 01 = 1 or 10 = 2), then we stop splitting.
		*/
		switch(v.bit(d)*2 + w.bit(d)) {
			case 0: t.left = split(p, q, d+1); break;
			case 1: t.left = p; t.right = q; break;
			case 2: t.right = p; t.left = q; break;
			case 3: t.right = split(p, q, d+1); break;
		}
		return t;
	}


	@Override
	public Long delete(long x) {

		// Holger:
		// It sounds like a normal delete procedure in binary search trees:
		// Since you implemented this using a Node class with explicit left and right children,
		// you need to delete the leaf and then fix the parent.
		// If the parent had only one child, the parent should be deleted entirely and the parent's parent needs to be
		// fixed recursively. Otherwise the parent should be deleted and the sibling should be reattached one level higher.

		BitsKey delete = new BitsKey(x);

		head = deleteR(head, delete, 0);

		return x;
	}

	private Node<BitsKey> deleteR(Node<BitsKey> curr, BitsKey v, int d) {

//		if (I'm null) return null
		if (curr == null) return null;

//		else if (key of current node is the one we want to delete) return null
		else if (curr.key != null) {
			if (curr.key.val == v.val) {
				N--;
				return null;
			}

//		else if (current node has a different key [and thus is a leaf]) return curr
			else return curr;
		}

//		else if (next bit says to go left) leftchild = deleteR(leftchild, ..)
		else if (v.bit(d) == 0) curr.left = deleteR(curr.left, v, d+1);

//		else rightchild = deleteR(rightchild, ..)
		else                    curr.right = deleteR(curr.right, v, d+1);

		// if both ("new") children are not null, return the current node.
		//
		// Otherwise one child is null, in that case return the other child.

		if (curr.left != null && curr.right != null) return curr;
		else {
			if (curr.left == null)  return curr.right;
			else                    return curr.left;
		}

	}

	@Override
	public boolean member(long x) {
		Node<BitsKey> res = search(head, new BitsKey(x), 0);
		return res != null && res.key.val == x;
	}

	private Node<BitsKey> search(Node<BitsKey> curr, BitsKey v, int d) {
		// Current node is null. Unsuccessful search.
		if (curr == null) return null;

		// Node with no children. We are at a leaf node. Return it for key comparison.
		if (curr.left == null && curr.right == null) return curr;
		
		// Node with children, keep searching recursively.
		if (v.bit(d) == 0) return search(curr.left, v, d+1);
		else 			   return search(curr.right, v, d+1);
	}

	@Override
	public long predecessor(long x) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long successor(long x) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long rank(long x) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long select(long i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void main(String[] args) {
//		BitsKey v = new BitsKey(6917529027641081855L);
//		BitsKey w = new BitsKey(Long.MAX_VALUE);
//		BitsKey x = new BitsKey(4611686018427387903L);
//		System.out.println(v.toString()); // [Bin = 0101111111111111111111111111111111111111111111111111111111111111, Val = 6917529027641081855]
//		System.out.println(w.toString()); // [Bin = 0111111111111111111111111111111111111111111111111111111111111111, Val = 9223372036854775807]
//		System.out.println(x.toString()); // [Bin = 0011111111111111111111111111111111111111111111111111111111111111, Val = 4611686018427387903]

		BinarySearchTrie t = new BinarySearchTrie();

		t.insert(6917529027641081855L);
		t.insert(4611686018427387903L);
		System.out.println("10 is member =" + t.member(10));
		System.out.println("6917529027641081855 is member = " + t.member(6917529027641081855L));
		t.insert(Long.MAX_VALUE);
		System.out.println("10 is member =" + t.member(10));
		System.out.println("Long.MAX_VALUE is member = " + t.member(Long.MAX_VALUE));
		t.delete(Long.MAX_VALUE);
		System.out.println("Long.MAX_VALUE is member = " + t.member(Long.MAX_VALUE));
		System.out.println("6917529027641081855 is member = " + t.member(6917529027641081855L));
		System.out.println("4611686018427387903 is member = " + t.member(4611686018427387903L));



	}
}