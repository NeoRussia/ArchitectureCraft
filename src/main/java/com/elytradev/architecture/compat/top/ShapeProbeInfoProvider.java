package com.elytradev.architecture.compat.top;

import com.elytradev.architecture.common.ArchitectureLog;
import com.elytradev.architecture.common.ArchitectureMod;
import com.elytradev.architecture.common.block.BlockShape;
import com.elytradev.architecture.common.helpers.Utils;
import com.elytradev.architecture.common.shape.ConnectedShape;
import com.elytradev.architecture.common.shape.EnumShape;
import com.elytradev.architecture.common.tile.TileShape;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Adds what a shape's item name cannot carry to The One Probe overlay.
 *
 * <p>The probe's default block line already comes from the pick block, whose
 * name is "shape: material", so this only reports the cladding, the light a
 * glowing shape gives off, and — when sneaking — the connection arms and the
 * texture orientation the paintbrush set.</p>
 */
public class ShapeProbeInfoProvider implements IProbeInfoProvider {


    @Override
    public String getID() {
        return ArchitectureMod.MOD_ID + ":shape";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world,
                             IBlockState state, IProbeHitData data) {
        if (!(state.getBlock() instanceof BlockShape))
            return;
        try {
            this.describe(mode, info, player, world, state, data);
        } catch (RuntimeException e) {
            // A probe line is not worth an exception in the HUD: report it once
            // in the log and leave TOP with whatever it already has.
            ArchitectureLog.error("The One Probe: could not describe the shape at " + data.getPos(), e);
        }
    }

    private void describe(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world,
                          IBlockState state, IProbeHitData data) {
        TileShape te = TileShape.get(world, data.getPos());
        if (te == null || te.getShape() == null)
            return;

        if (te.hasSecondaryBlockState()) {
            IBlockState secondary = te.getSecondaryBlockState();
            info.text(I18n.translateToLocalFormatted("tooltip.architecturecraft.secondarymaterial",
                    Utils.displayNameOnlyOfBlock(secondary.getBlock(),
                            secondary.getBlock().getMetaFromState(secondary))));
        }

        int light = state.getValue(BlockShape.LIGHT);
        if (light > 0)
            info.text(I18n.translateToLocalFormatted("tooltip.architecturecraft.light", light));

        if (!HarvestLines.showHarvestInfo(info, world, data.getPos(), state.getBlock(), state, player)) {
            String tool = toolLine(te);
            if (tool != null)
                info.text(tool);
        }

        if (mode != ProbeMode.EXTENDED)
            return;

        String arms = this.arms(te);
        if (arms != null)
            info.text(I18n.translateToLocalFormatted("tooltip.architecturecraft.connections", arms));

        String texture = textureOrientation(te);
        if (texture != null)
            info.text(texture);
    }

    /** Connected sides, or null when the shape has no connections or none set. */
    private String arms(TileShape te) {
        EnumShape shape = te.getShape();
        if (!(shape.kind instanceof ConnectedShape))
            return null;
        ConnectedShape kind = (ConnectedShape) shape.kind;
        StringBuilder sides = new StringBuilder();
        for (EnumFacing dir : EnumFacing.HORIZONTALS) {
            if (!kind.isConnected(te, dir))
                continue;
            if (sides.length() > 0)
                sides.append(", ");
            sides.append(I18n.translateToLocal("tooltip.architecturecraft.direction." + dir.getName()));
        }
        return sides.length() == 0 ? null : sides.toString();
    }

    /**
     * The tool line TOP would print if it could see the material, in its own
     * wording. TOP draws the harvestable check or cross itself, from
     * {@code canHarvestBlock(world, pos, player)}, which is why only this line
     * is replaced.
     */
    private static String toolLine(TileShape te) {
        if (!te.hasBaseBlockState())
            return null;
        IBlockState base = te.getBaseBlockState();
        Block block = base.getBlock();
        if (block.getMaterial(base).isToolNotRequired())
            return null;
        String tool = block.getHarvestTool(base);
        if (tool == null)
            return null;
        return I18n.translateToLocalFormatted("tooltip.architecturecraft.tool",
                I18n.translateToLocal("tooltip.architecturecraft.tool." + tool),
                block.getHarvestLevel(base));
    }

    /** The paintbrush's turn and mirror, or null when the texture is untouched. */
    private static String textureOrientation(TileShape te) {        int turn = te.getTexRot();
        boolean mirrored = te.isTexFlipped();
        if (!mirrored && turn == 0)
            return null;
        int degrees = turn * 90;
        if (mirrored && turn != 0)
            return I18n.translateToLocalFormatted("tooltip.architecturecraft.texture.rotated_mirrored", degrees);
        if (mirrored)
            return I18n.translateToLocalFormatted("tooltip.architecturecraft.texture.mirrored");
        return I18n.translateToLocalFormatted("tooltip.architecturecraft.texture.rotated", degrees);
    }

}




