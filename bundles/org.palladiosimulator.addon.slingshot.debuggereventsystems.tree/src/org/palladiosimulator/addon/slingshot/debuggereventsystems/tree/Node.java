package org.palladiosimulator.addon.slingshot.debuggereventsystems.tree;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.LinkedList;

public class Node<T> {

	private T data;
	private Node<T> parent;
	private List<Node<T>> children;
	private final String id;
	
	public Node() { 
		this(null); 
	}
	
	public Node(T data) {
		this(data, null);
	}
	
	public Node(T data, Node<T> parent) {
		this(UUID.randomUUID().toString(), data, null);
	}

	public Node(final String id, T data, Node<T> parent) {
		this(id, data, parent, new LinkedList<>());
	}

	public Node(final String id, T data, Node<T> parent, List<Node<T>> children) {
		this.data = data;
		this.parent = parent;
		this.children = children;
		this.id = id;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Node<T> getParent() {
		return parent;
	}

	public void setParent(Node<T> parent) {
		this.parent = parent;
	}

	public List<Node<T>> getChildren() {
		return children;
	}
	
	public Node<T> createChild(final T data) {
		final Node<T> child = new Node<>(data, this);
		children.add(child);
		return child;
	}
	
	public void addChild(final Node<T> child) {
		child.setParent(this);
		this.children.add(child);
	}
	
	public Iterator<Node<T>> breadthFirstIterator() {
		return new BreadthFirstIterator<>(this);
	}
	
	public Iterator<Node<T>> depthFirstIterator() {
		return new DepthFirstIterator<>(this);
	}
	
	public String getId() {
		return id;
	}
	
	public void forEach(final Iterator<Node<T>> iterator, final Consumer<Node<T>> consumer) {
		while (iterator.hasNext()) {
			final Node<T> n = iterator.next();
			consumer.accept(n);
		}
	}
}
