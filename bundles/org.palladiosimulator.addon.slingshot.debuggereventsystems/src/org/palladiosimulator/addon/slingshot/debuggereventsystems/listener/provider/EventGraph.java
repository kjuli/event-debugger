package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.provider;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.tree.Node;

public class EventGraph {

	private Node<IDebugEvent> eventTree;
	
	public void addEvent(final IDebugEvent event) {
		if (eventTree == null) {
			eventTree = new Node<>(event);
		} else {
			eventTree.createChild(event);
		}
	}
	
}
