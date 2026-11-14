package com.elytradev.architecture.compat.top;

import mcjty.theoneprobe.api.ITheOneProbe;

import java.util.function.Function;

/**
 * The function TOP resolves from the inter-mod message. Nothing else in the mod
 * references this class, so it is only loaded when The One Probe is installed.
 */
public final class GetTheOneProbe implements Function<ITheOneProbe, Void> {

    @Override
    public Void apply(ITheOneProbe probe) {
        if (probe != null) {
            probe.registerProvider(new ShapeProbeInfoProvider());
            probe.registerBlockDisplayOverride(new ShapeDisplayOverride());
            probe.registerProbeConfigProvider(new ShapeProbeConfigProvider());
        }
        return null;
    }

}
