package org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.settings.Settings;

public record InitialCall(Settings settings) implements Message {
}
