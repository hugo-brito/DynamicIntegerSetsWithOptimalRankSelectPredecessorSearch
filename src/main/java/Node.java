class Node<E> {

	final E key;
	Node<E> left, right;

	public Node(E key){
		// NB!: We allow the key to be null because internal node cannot hold values
			this.key = key;
	}
}