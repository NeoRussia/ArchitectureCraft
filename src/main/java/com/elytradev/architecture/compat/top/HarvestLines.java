package com.elytradev.architecture.compat.top;

import com.elytradev.architecture.common.ArchitectureLog;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.Method;

/**
 * Draws TOP's harvest lines with TOP's own code.
 *
 * <p>{@code HarvestInfoTools.showHarvestInfo} is package-private, so it can
 * only be reached by reflection. It is still the right thing to call rather
 * than reimplement: it draws the tool icon, the "Tool: name (level n)" line and
 * the harvestable check or cross, and we cannot see the icon texture or the
 * text style it uses from outside.</p>
 *
 * <p>The state passed in is the <em>extended</em> one. That is the whole trick:
 * the extended state carries the world and position, so the block's material
 * and tool hooks can answer for the shape instead of falling back to defaults.
 * If the method ever moves or disappears, the caller falls back to plain
 * text.</p>
 */
final class HarvestLines {

    private static final String CLASS = "mcjty.theoneprobe.apiimpl.providers.HarvestInfoTools";

    private static Method showHarvestInfo;
    private static boolean resolved;

    private HarvestLines() {
    }

    /** True when TOP drew the lines. */
    static boolean showHarvestInfo(IProbeInfo info, World world, BlockPos pos, Block block,
                                   IBlockState state, EntityPlayer player) {
        Method method = resolve();
        if (method == null)
            return false;
        IBlockState extended = block.getExtendedState(state, world, pos);
        try {
            method.invoke(null, info, world, pos, block, extended, player);
            return true;
        } catch (ReflectiveOperationException | RuntimeException e) {
            ArchitectureLog.error("The One Probe: harvest lines failed, using plain text instead", e);
            return false;
        }
    }

    private static Method resolve() {
        if (resolved)
            return showHarvestInfo;
        resolved = true;
        try {
            Method found = Class.forName(CLASS).getDeclaredMethod("showHarvestInfo",
                    IProbeInfo.class, World.class, BlockPos.class, Block.class,
                    IBlockState.class, EntityPlayer.class);
            found.setAccessible(true);
            showHarvestInfo = found;
        } catch (ReflectiveOperationException | RuntimeException e) {
            ArchitectureLog.info("The One Probe: harvest helper not found, using plain text lines. {}", e.toString());
        }
        return showHarvestInfo;
    }

}
