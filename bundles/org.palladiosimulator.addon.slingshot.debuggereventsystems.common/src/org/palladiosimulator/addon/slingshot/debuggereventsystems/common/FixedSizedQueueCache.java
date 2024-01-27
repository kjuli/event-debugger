package org.palladiosimulator.addon.slingshot.debuggereventsystems.common;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class FixedSizedQueueCache<E> extends AbstractQueue<E> {

	private final Object[] items;
	private int count;
	private int head = 0;

	public FixedSizedQueueCache(final int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("Capacity must be > 0, but is " + capacity);
		}

		items = new Object[capacity];
		count = 0;
	}

	@Override
	public boolean offer(final E e) {
		Objects.requireNonNull(e, "This queue does not allow null objects");
		if (count == head && items[previous(head)] != null) {
			head = increase(head);
		}
		items[count] = e;
		count = increase(count);
		return true;
	}

	private int increase(final int num) {
		return (num + 1) % items.length;
	}

	private int decrease(final int num) {
		final int res = (num - 1) % items.length;
		if (count < 0) {
			return res + items.length;
		}
		return res;
	}

	private int previous(final int num) {
		final int res = num - 1;
		if (res < 0) {
			return res + items.length;
		} else {
			return res;
		}
	}

	@Override
	public E poll() {
		if (size() <= 0) {
			return null;
		}

		final E item = (E) items[head];
		items[head] = null;
		head = increase(head);
		return item;
	}

	@Override
	public E peek() {
		if (count <= 0) {
			return null;
		}
		return (E) items[head];
	}

	@Override
	public Iterator<E> iterator() {
		return new CacheIterator();
	}

	@Override
	public int size() {
		final int res = count - head;
		if (res < 0) {
			return res + items.length;
		}
		return res;
	}

	@Override
	public void clear() {
		head = 0;
		count = 0;
		Arrays.fill(items, null);
	}

	private class CacheIterator implements Iterator<E> {

		private int currentIndex = 0;
		private final int startIndex = head;
		private final int endIndex = count;

		@Override
		public boolean hasNext() {
			return (currentIndex + startIndex) % items.length != endIndex;
		}

		@Override
		public E next() {
			final E item = (E) items[currentIndex + startIndex];
			currentIndex++;
			return item;
		}

	}
}
