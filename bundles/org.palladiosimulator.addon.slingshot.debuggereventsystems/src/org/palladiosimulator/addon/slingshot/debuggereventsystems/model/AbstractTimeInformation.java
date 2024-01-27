package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

public class AbstractTimeInformation implements TimeInformation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2669905007499866828L;
	private final double time;

	public AbstractTimeInformation(final double time) {
		this.time = time;
	}


	@Override
	public double getTime() {
		// TODO Auto-generated method stub
		return time;
	}

}
