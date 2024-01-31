package org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages;

import java.io.Serializable;

/**
 * A message that is sent between the front- and back-end.
 * 
 * Sub-Types of this interface must be <b>completely</b> serializable to work.
 * It is not allowed to use <i>interfaces</i> of the model, because it could be
 * that the receiver does not have the concrete class implementing the
 * interface, even though both receiver and sender have access to the interface
 * itself.
 * 
 * It is recommended to use Java Records in order to directly state the fields
 * of the message without the need to implement the necessary methods (such as
 * .equals(), ...).
 * 
 * @author Julijan Katic
 */
public interface Message extends Serializable {

}
