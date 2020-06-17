class Node<E> {

	final E data;
	Node<E> left, right;

	public Node(final E key) {
		// NB!: We allow the key to be null because internal node cannot hold values
		this.data = key;
	}
}

interface BinaryTree {

	/**
	 * Counts and returns the total number of nodes in the tree.
	 * @return
	 */
	int count();

	default <E> int count(final Node<E> curr) {
		if (curr == null)
			return 0;
		return count(curr.left) + count(curr.right) + 1;
	}

	/**
	 * Returns the height of the tree
	 * @return
	 */
	int height();

	default <E> int height(final Node<E> node) {
		if (node == null)
			return -1;
		final int u = height(node.left), v = height(node.right);
		if (u > v)
			return u + 1;
		else
			return v + 1;
	}

	/**
	 * Pretty prints the contents of the tree.
	 */
	void show();

	default <E> void show(final Node<E> t, final int h) {
		if (t == null) {
			printnode(null, h);
			return;
		}
		show(t.right, h+1);
		printnode(t.data, h);
		show(t.left, h+1);
	}

	default <E> void printnode (final E x, final int h) {
		for (int i = 0; i < h; i++)
			System.out.print("	");
		System.out.println("[" + x + "]");
	}

}