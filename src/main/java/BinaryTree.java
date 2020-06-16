interface BinaryTree {

	static int count(Node curr) {
		if (curr == null) return 0;
		return count(curr.left) + count(curr.right) + 1;
	}
	
	// default int count() { return count(head); }
	
	static int height(Node curr) {
		if (curr == null) return -1;
		int u = height(curr.left), v = height(curr.right);
		if (u > v) return u+1; else return v+1;
	}
	
	// default int height() { return height(head); }
	
}