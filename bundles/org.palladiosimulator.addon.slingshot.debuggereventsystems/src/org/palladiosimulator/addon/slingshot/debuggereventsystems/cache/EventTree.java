package org.palladiosimulator.addon.slingshot.debuggereventsystems.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.DebugEventId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;

/**
 * Manages a hierarchical structure of debug events to represent parent-child
 * relationships.
 * <p>
 * This class creates and maintains a tree of events, where each event can have
 * one or more child events, and each child event has exactly one parent event.
 * It allows adding new events to the tree, retrieving the children of an event,
 * finding the parent of an event, and obtaining a list of the latest ancestors
 * of a given event.
 * </p>
 * 
 * @author Julijan Katic
 */
public class EventTree {

	private final Map<DebugEventId, List<DebugEventId>> parentChildMap = new HashMap<>();
	private final Map<DebugEventId, EventTreeNode> childParentMap = new HashMap<>();
	
	/**
	 * Adds a child event to a parent event.
	 * <p>
	 * Establishes a parent-child relationship between two events, identified by
	 * their IDs, and associates the child event with the handler responsible for
	 * its creation.
	 * </p>
	 *
	 * @param parent    The ID of the parent event.
	 * @param byHandler The ID of the handler that created the child event.
	 * @param child     The ID of the child event.
	 */
	public void addChildToParent(final DebugEventId parent, final HandlerId byHandler,
			final DebugEventId child) {
		parentChildMap.computeIfAbsent(parent, p -> new LinkedList<>())
					  .add(child);
		childParentMap.put(child, new EventTreeNode(parent, byHandler));
	}
	
	/**
	 * Adds an event node to the tree.
	 * <p>
	 * If the event has a parent event, it adds the event to the tree as a child of
	 * its parent event.
	 * </p>
	 *
	 * @param event The debug event to add to the tree.
	 */
	public void addNode(final IDebugEvent event) {
		if (event.getParentEvent().isPresent()) {
			final EventTreeNode parent = event.getParentEvent().get();
			addChildToParent(
					parent.debuggedEvent(), parent.handler(), event.getId());
		}
	}

	/**
	 * Retrieves the children of a given parent event.
	 * <p>
	 * Returns a list of IDs representing the child events of the specified parent
	 * event.
	 * </p>
	 *
	 * @param parent The ID of the parent event.
	 * @return A list of child event IDs.
	 */
	public List<DebugEventId> getChildren(final DebugEventId parent) {
		return parentChildMap.getOrDefault(parent, Collections.emptyList());
	}

	/**
	 * Retrieves the parent of a given child event.
	 * <p>
	 * Returns an {@link Optional} containing the parent of the specified child
	 * event, if it exists.
	 * </p>
	 *
	 * @param child The ID of the child event.
	 * @return An {@link Optional} containing the {@link EventTreeNode} of the
	 *         parent event or empty if not found.
	 */
	public Optional<EventTreeNode> getParent(final DebugEventId child) {
		return Optional.ofNullable(childParentMap.get(child));
	}

	/**
	 * Retrieves the latest ancestors of a given event up to a specified depth.
	 * <p>
	 * Returns a list of {@link EventTreeNode} objects representing the latest
	 * ancestors of the specified event, limited by the given depth.
	 * </p>
	 *
	 * @param from  The ID of the event from which to start.
	 * @param depth The maximum number of ancestors to retrieve.
	 * @return A list of {@link EventTreeNode} objects representing the ancestors.
	 */
	public List<EventTreeNode> getLatestParents(final DebugEventId from, final int depth) {
		final List<EventTreeNode> result = new ArrayList<>(depth);

		int i = depth;
		Optional<EventTreeNode> parent = getParent(new DebugEventId(from.getId()));
		while (parent.isPresent() && i >= 0) {
			result.add(parent.get());
			parent = getParent(parent.get().debuggedEvent());
			--i;
		}

		return result;
	}

	/**
	 * Removes the parent relationship of a given child event.
	 * <p>
	 * Deletes the association between the specified child event and its parent
	 * event from the tree.
	 * </p>
	 *
	 * @param childId The ID of the child event whose parent relationship is to be
	 *                removed.
	 */
	public void removeParent(final DebugEventId childId) {
		final EventTreeNode parentId = childParentMap.remove(childId);
		if (parentId != null) {
			getChildren(parentId.debuggedEvent()).remove(childId);
		}
	}

}
