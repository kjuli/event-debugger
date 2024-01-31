package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

/**
 * This class is used to represent a {@link TimeInformation}. It is mostly used
 * either by the front-end to save the retrieved information from a message, or
 * to serialize it into a message.
 * 
 * @author Julijan Katic
 */
public class ConcreteTimeInformation implements TimeInformation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2669905007499866828L;
	private final double time;

	public ConcreteTimeInformation(final double time) {
		this.time = time;
	}


	@Override
	public double getTime() {
		// TODO Auto-generated method stub
		return time;
	}

}
