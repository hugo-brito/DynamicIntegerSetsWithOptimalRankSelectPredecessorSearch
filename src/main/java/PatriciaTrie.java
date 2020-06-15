class PatriciaTrie implements RankSelectPredecessorUpdate {

	private Node head;
	private long N;

	static class Node {
		final BitsKey key;
		Node left, right;
		int bit;
		public Node(BitsKey key, int i) {
			this.key = key;
			this.bit = i;
		}
	}

	public PatriciaTrie() {
		head = new Node(null, -1);
		head.left = head;
		N = 0;
	}

	public long size() {return N;}

	@Override
	public void insert(long x) {
		int i = 0;
		
		BitsKey v = new BitsKey(x);
		Node t = search(head.left, v, -1);

		BitsKey w = (t == null) ? null : t.key;
		if (v.val == w.val) return; // Perform an insertion only if key only is not present
		
		// Find the most significant digit where the keys differ
		while (v.bit(i) == w.bit(i)) i++;

		head.left = insertR(head.left, v, i, head);

		// Upon successful insertion, we update the total number of keys in the set
		N++;
	}

	private Node insertR(Node curr, BitsKey v, int i, Node prev) {
		// KEY v = x.key();
		if ((curr.bit >= i) || (curr.bit <= prev.bit)) {
			Node newNode = new Node(v, i);
			newNode.left = v.bit(newNode.bit) == 0 ? newNode : curr;
			newNode.right = v.bit(newNode.bit) == 0 ? curr : newNode;
			return newNode;
		}
		
		if (v.bit(curr.bit) == 0) curr.left = insertR(curr.left, v, i, curr);
		else 					  curr.right = insertR(curr.right, v, i, curr);

		return curr;
	}

	@Override
	public void delete(long x) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean member(long x) {
		Node res = search(head.left, new BitsKey(x), -1);
		if (res == null || res.key.val != x) return false;
		return true;
	}

	private Node search(Node h, BitsKey v, int i) {
		if (h.bit <= i) return h;
		if (v.bit(h.bit) == 0)  return search(h.left, v, h.bit);
		else 					return search(h.right, v, h.bit);
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
	
}