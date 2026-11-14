package com.elytradev.architecture.compat.top;

import com.elytradev.architecture.common.block.BlockShape;
import com.elytradev.architecture.common.tile.TileShape;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IBlockDisplayOverride;
import mcjty.theoneprobe.api.IProbeConfig.ConfigMode;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Draws the block header TOP cannot build for a shape.
 *
 * <p>TOP's default provider throws partway through its standard block info for
 * our blocks, and the overlay showed "Error: theoneprobe:default" instead of
 * the usual header. It also takes the item from the pick block, which for a
 * shape carries the whole tile, so this rebuilds the same layout: icon, name,
 * and the mod name in TOP's own blue style. The styling comes from
 * {@link TextStyleClass#getCode()} and the mod name from TOP's own
 * {@link Tools#getModName}, so it matches other blocks rather than resembling
 * them.</p>
 */
public class ShapeDisplayOverride implements IBlockDisplayOverride {

    @Override
    public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world,
                                        IBlockState state, IProbeHitData data) {
        if (!(state.getBlock() instanceof BlockShape))
            return false;
        TileShape te = TileShape.get(world, data.getPos());
        if (te == null)
            return false;

        ItemStack pick = data.getPickBlock();
        if (pick == null || pick.isEmpty())
            pick = te.newItemStack(1);
        if (pick == null || pick.isEmpty()) {
            info.text(TextStyleClass.NAME.toString() + state.getBlock().getLocalizedName());
            return true;
        }

        IProbeInfo row = info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
        row.item(pick);
        IProbeInfo column = row.vertical();
        column.text(TextStyleClass.NAME.toString() + pick.getDisplayName());
        if (showModName(mode)) {
            String modName = Tools.getModName(state.getBlock());
            if (modName != null && !modName.isEmpty())
                column.text(TextStyleClass.MODNAME.toString() + modName);
        }
        return true;
    }

    /**
     * TOP decides whether to show the mod name per player and mode, and tells
     * the config providers before it draws. Our provider records that answer
     * for this probe, since the display override is not given the config.
     */
    private static boolean showModName(ProbeMode mode) {
        ConfigMode configured = ShapeProbeConfigProvider.consumeShowModName();
        return configured == null || Tools.show(mode, configured);
    }

}

