package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

public class IncrementalTimeInformation implements TimeInformation {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5502046121648796653L;

	private static int instanceCounter = 0;
	
	private final double time;
	
	public IncrementalTimeInformation() {
		time = instanceCounter;
		instanceCounter++;
	}

	@Override
	public double getTime() {
		return time;
	}

}
