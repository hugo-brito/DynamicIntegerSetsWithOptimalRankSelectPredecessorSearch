class BinarySearchTrie implements RankSelectPredecessorUpdate {

	private Node head;
	private long N;

	static class Node{
		final BitsKey key;
		Node left, right;

		public Node(BitsKey key){
		// NB!: We allow the key to be null because internal node cannot hold values
			this.key = key;
		}
	}

	public BinarySearchTrie() {
		head = null;
		N = 0;
	}

	public long size() {return N;}

	@Override
	public void insert(long x) {
		// Insert the new key only if not present
		if (member(x)) return;

		// We create the BitsKey at this stage because later we conveniently have access to the bit(d) method
		head = insertR(head, new BitsKey(x), 0);

		// Upon successful insertion, we update the total number of keys in the set
		N++;
	}

	private Node insertR(Node curr, BitsKey v, int d) {
		if (curr == null) return new Node(v);
		
		if (curr.left == null && curr.right == null)
			return split(new Node(v), curr, d);

		if (v.bit(d) == 0) curr.left = insertR(curr.left, v, d+1);
		else 			   curr.right = insertR(curr.right, v, d+1);
		
		return curr;
	}

	private Node split(Node p, Node q, int d) {
		Node t = new Node(null);
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
	public void delete(long x) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean member(long x) {
		Node res = search(head, x, 0);
		if (res == null || res.key.val != x) return false;
		return true;
	}

	private Node search(Node curr, long v, int d) {
		// Current node is null. Unsuccessful search.
		if (curr == null) return null;

		// Node with no children. We are at a leaf node. Return it for key comparison.
		if (curr.left == null && curr.right == null) return curr;
		
		// Node with children, keep searching recursively.
		if (curr.key.bit(d) == 0) return search(curr.left, v, d+1);
		else 					  return search(curr.right, v, d+1);
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
}