package org.palladiosimulator.addon.slingshot.debuggereventsystems.tree;

import java.util.Iterator;
import java.util.Queue;
import java.util.LinkedList;

public class BreadthFirstIterator<T> implements Iterator<Node<T>> {
	
	private final Queue<Node<T>> queue = new LinkedList<>();
	
	public BreadthFirstIterator(final Node<T> root) {
		this.queue.add(root);
	}

	@Override
	public boolean hasNext() {
		return !queue.isEmpty();
	}

	@Override
	public Node<T> next() {
		final Node<T> node = queue.poll();
		queue.addAll(node.getChildren());
		return node;
	}

}
