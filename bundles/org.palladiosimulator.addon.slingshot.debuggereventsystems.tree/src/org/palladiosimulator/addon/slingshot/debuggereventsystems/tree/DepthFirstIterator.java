package org.palladiosimulator.addon.slingshot.debuggereventsystems.tree;

import java.util.Iterator;
import java.util.Stack;

public class DepthFirstIterator<T> implements Iterator<Node<T>> {

	private final Stack<Node<T>> stack = new Stack<>();
	
	public DepthFirstIterator(final Node<T> root) {
		this.stack.push(root);
	}
	
	@Override
	public boolean hasNext() {
		return !stack.isEmpty();
	}

	@Override
	public Node<T> next() {
		final Node<T> node = stack.pop();
		stack.addAll(node.getChildren());
		return node;
	}

}
