package com.elytradev.architecture.compat.top;

import net.minecraftforge.fml.common.event.FMLInterModComms;

/**
 * Optional integration with The One Probe.
 *
 * <p>TOP is not a dependency: nothing here is touched unless the probe is
 * installed. The handshake is an inter-mod message, and the class it names is a
 * separate file, so this one names no TOP type and loads safely without TOP.</p>
 */
public final class TopCompatibility {

    private static final String MOD_ID = "theoneprobe";
    private static final String FUNCTION_MESSAGE = "getTheOneProbe";

    private TopCompatibility() {
    }

    public static void register() {
        FMLInterModComms.sendFunctionMessage(MOD_ID, FUNCTION_MESSAGE,
                GetTheOneProbe.class.getName());
    }

}
