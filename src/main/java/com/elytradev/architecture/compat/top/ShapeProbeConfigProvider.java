package com.elytradev.architecture.compat.top;

import com.elytradev.architecture.common.block.BlockShape;
import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.IProbeConfig.ConfigMode;
import mcjty.theoneprobe.api.IProbeConfigProvider;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Turns off TOP's own harvest lines for shapes.
 *
 * <p>TOP asks the block for its tool from the block state alone, and a shape's
 * tool belongs to the material in its tile, so TOP can only ever answer "no
 * tool". {@link ShapeProbeInfoProvider} prints the real one instead.</p>
 */
public class ShapeProbeConfigProvider implements IProbeConfigProvider {

    @Override
    public void getProbeConfig(IProbeConfig config, EntityPlayer player, World world,
                               Entity entity, IProbeHitEntityData data) {
        // Entities are not ours.
    }

    @Override
    public void getProbeConfig(IProbeConfig config, EntityPlayer player, World world,
                               IBlockState state, IProbeHitData data) {
        if (state.getBlock() instanceof BlockShape) {
            // Hand the mod-name setting to the display override, which needs it
            // to build the same header TOP would have built.
            SHOW_MOD_NAME.set(config.getShowModName());
            // Both harvest lines are drawn by our provider instead, through
            // TOP's own helper and with the extended state, because TOP's copy
            // reads the tool from the block state alone.
            config.showHarvestLevel(ConfigMode.NOT);
            config.showCanBeHarvested(ConfigMode.NOT);
        }
    }

    /** The mod-name setting TOP resolved for the probe in progress. */
    static ConfigMode consumeShowModName() {
        ConfigMode mode = SHOW_MOD_NAME.get();
        SHOW_MOD_NAME.remove();
        return mode;
    }

    private static final ThreadLocal<ConfigMode> SHOW_MOD_NAME = new ThreadLocal<ConfigMode>();

}
