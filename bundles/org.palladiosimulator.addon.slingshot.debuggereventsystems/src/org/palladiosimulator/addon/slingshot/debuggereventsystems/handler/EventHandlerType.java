package org.palladiosimulator.addon.slingshot.debuggereventsystems.handler;

/**
 * Describes how an event handler looks like statically.
 * <p>
 * More specifically, the given fields are types of the respective AST Node to
 * look for (i.e. Eclipse JDT's IType.class).
 * 
 * @author Julijan Katic
 */
public record EventHandlerType(Class<?> javaEventType, Class<?> javaMethodType) {

}
