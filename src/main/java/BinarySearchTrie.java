class BinarySearchTrie implements BinaryTree, RankSelectPredecessorUpdate {

	private Node<BitsKey> root;
	private long N;

	public BinarySearchTrie() {
		root = null;
		N = 0;
	}

	public long size() {return N;}

	@Override
	public void insert(final long x) {

		// We create the BitsKey at this stage because later we conveniently have access
		// to the bit(d) method
		root = insert(root, new BitsKey(x), 0);

		// Upon successful insertion, we update the total number of keys in the set
		N++; // problem here
	}

	private Node<BitsKey> insert(final Node<BitsKey> curr, final BitsKey v, final int d) {
		if (curr == null)
			return new Node<BitsKey>(v);

		if (curr.left == null && curr.right == null) {
			if (curr.data != null && curr.data.val == v.val)
				return curr;
			return split(new Node<BitsKey>(v), curr, d);
		}

		if (v.bit(d) == 0)
			curr.left = insert(curr.left, v, d + 1);
		else
			curr.right = insert(curr.right, v, d + 1);

		return curr;
	}

	private Node<BitsKey> split(final Node<BitsKey> p, final Node<BitsKey> q, final int d) {
		final Node<BitsKey> t = new Node<BitsKey>(null);
		final BitsKey v = p.data, w = q.data;

		/**
		 * The switch statement in split converts the two bits that it is testing into a
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
		}
		return t;
	}

	@Override
	public void delete(final long x) {
		final BitsKey delete = new BitsKey(x);
		root = delete(root, delete, 0);
	}

	private Node<BitsKey> delete(final Node<BitsKey> curr, final BitsKey v, final int d) {

		// if (I'm null) return null
		if (curr == null)
			return null;

		// else if (key of current node is the one we want to delete) return null
		else if (curr.data != null) {
			if (curr.data.val == v.val) {
				N--;
				return null;
			}

			// else if (current node has a different key [and thus is a leaf]) return curr
			else
				return curr;
		}

		// else if (next bit says to go left) leftchild = deleteR(leftchild, ..)
		else if (v.bit(d) == 0)
			curr.left = delete(curr.left, v, d + 1);

		// else rightchild = deleteR(rightchild, ..)
		else
			curr.right = delete(curr.right, v, d + 1);
		
		// if there is only one child AND the child is a leaf, then return the child (either curr.left or curr.right). Otherwise return curr.
		if (children(curr) == 1 || children(curr) == 2) { // has a single child
			if (children(curr) == 1 && children(curr.left) == 0) return curr.left; // has a single child and that child is a leaf
			else if (children(curr) == 2 && children(curr.right) == 0) return curr.right;
		}

		return curr;

	}

	/**
	 * Returns:
	 * 	*  0 if it is a leaf node
	 *  *  1 if it has a single left child
	 *  *  2 if it has a single right child
	 *  *  3 if it has 2 children
	 * @param node
	 * @return
	 */
	private int children (final Node<BitsKey> node) {
		int res = -1;
		switch ((node.left == null ? 0 : 1) + 2 * (node.right == null ? 0 : 1)) {
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
		}

		return res;
	}

	@Override
	public boolean member(final long x) {
		final Node<BitsKey> res = search(root, new BitsKey(x), 0);
		return res != null && res.data.val == x;
	}

	private Node<BitsKey> search(final Node<BitsKey> curr, final BitsKey v, final int d) {
		// Current node is null. Unsuccessful search.
		if (curr == null)
			return null;

		// Node with no children. We are at a leaf node. Return it for key comparison.
		if (curr.left == null && curr.right == null)
			return curr;

		// Node with children, keep searching recursively.
		if (v.bit(d) == 0)
			return search(curr.left, v, d + 1);
		else
			return search(curr.right, v, d + 1);
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
		return count(root);
	}

	public int height() {
		return height(root);
	}

	@Override
	public void show() {
		show(root, 0);

	}

	public static void main(final String[] args) {
		// BitsKey v = new BitsKey(6917529027641081855L);
		// BitsKey w = new BitsKey(Long.MAX_VALUE);
		// BitsKey x = new BitsKey(4611686018427387903L);
		// System.out.println(v.toString()); // [Bin =
		// 0101111111111111111111111111111111111111111111111111111111111111, Val =
		// 6917529027641081855]
		// System.out.println(w.toString()); // [Bin =
		// 0111111111111111111111111111111111111111111111111111111111111111, Val =
		// 9223372036854775807]
		// System.out.println(x.toString()); // [Bin =
		// 0011111111111111111111111111111111111111111111111111111111111111, Val =
		// 4611686018427387903]

		final BinarySearchTrie t = new BinarySearchTrie();

		t.insert(6917529027641081855L);
		t.insert(4611686018427387903L);
		System.out.println("10 is member = " + t.member(10));
		System.out.println("6917529027641081855 is member = " + t.member(6917529027641081855L));
		t.insert(Long.MAX_VALUE);
		System.out.println("10 is member = " + t.member(10));
		System.out.println("Long.MAX_VALUE is member = " + t.member(Long.MAX_VALUE));
		t.delete(Long.MAX_VALUE);
		System.out.println("Long.MAX_VALUE is member = " + t.member(Long.MAX_VALUE));
		System.out.println("6917529027641081855 is member = " + t.member(6917529027641081855L));
		System.out.println("4611686018427387903 is member = " + t.member(4611686018427387903L));



	}


}