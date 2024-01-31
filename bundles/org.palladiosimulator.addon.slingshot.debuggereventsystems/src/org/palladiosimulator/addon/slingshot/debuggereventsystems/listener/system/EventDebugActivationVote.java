package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.system;

/**
 * An interface to ask whether the event-debug system shall be activated.
 * 
 * This is often implemented by both parties, but especially at the back-end: It
 * should see whether the current process is a debug process, and then vote on
 * it to activate.
 * 
 * @author Julijan Katic
 */
public interface EventDebugActivationVote {

	/**
	 * Tests whether the system should be activated.
	 * 
	 * @return true if it should be activated.
	 */
	public boolean shouldActivateDebugger();

}
