/*
 * MIT License
 *
 * Copyright (c) 2017 Benjamin K
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.elytradev.architecture.common.shape;

import com.elytradev.architecture.common.helpers.Trans3;
import com.elytradev.architecture.common.helpers.Vector3;
import com.elytradev.architecture.common.tile.TileShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Base for shapes whose look depends on their neighbours (walls, fences).
 *
 * <p>Connections are horizontal only and are derived from the world at render
 * time, so one shape ID covers every connection layout. A shape connects to
 * another shape of the same kind and to any full cube, and respects the
 * {@code disabledConnections} bits set by the chisel.</p>
 */
public abstract class ConnectedShape extends ShapeKind {

    public boolean isConnected(TileShape te, EnumFacing dir) {
        // Item stacks render a detached TileShape with no world and no position.
        if (te.getWorld() == null || te.getPos() == null)
            return false;
        if (!te.connectionIsEnabledGlobal(dir))
            return false;

        TileShape neighbour = te.getNeighbourGlobal(dir);
        if (neighbour != null) {
            if (neighbour.getShape() != null
                    && neighbour.getShape().kind.getClass() == this.getClass()
                    && neighbour.connectionIsEnabledGlobal(dir.getOpposite()))
                return true;
            // Another ArchitectureCraft shape is not a full cube; do not connect.
            if (neighbour.getShape() != null)
                return false;
        }

        IBlockState state = te.getWorld().getBlockState(te.getPos().offset(dir));
        return state.isFullCube();
    }

    @Override
    public boolean orientOnPlacement(EntityPlayer player,
                                     TileShape shape,
                                     BlockPos neighbourPos,
                                     IBlockState neighbourState,
                                     TileEntity neighbourTile,
                                     EnumFacing otherFace,
                                     Vector3 hit) {
        // Connections are world-relative, so the shape itself never rotates.
        shape.setSide(0);
        shape.setTurn(0);
        shape.setOffsetX(0);
        return true;
    }

    @Override
    public boolean canPlaceUpsideDown() {
        return false;
    }

    @Override
    public void onHammerUse(TileShape te, EntityPlayer player, EnumFacing face, Vector3 hit) {
        // Nothing to rotate; use the chisel to cut connections instead.
    }

    @Override
    public boolean usesConnections() {
        return true;
    }

    @Override
    public boolean acceptsCladding() {
        return false;
    }
}
