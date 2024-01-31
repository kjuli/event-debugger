package org.palladiosimulator.addon.slingshot.debuggereventsystems.cache;

import java.io.Serializable;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.DebugEventId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerId;

/**
 * A node in the tree that corresponds to a parent event of an event and the
 * handler that created this event.
 * 
 * @author Julijan Katic
 */
public record EventTreeNode(DebugEventId debuggedEvent, HandlerId handler) implements Serializable {

}
