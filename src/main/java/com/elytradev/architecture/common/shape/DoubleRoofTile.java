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

import com.elytradev.architecture.common.helpers.Vector3;
import com.elytradev.architecture.common.tile.TileShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * A full block split along its local X-Y diagonal: one wedge carries the base
 * material, the other the secondary one, so the block reads as a diagonal join
 * and repeats in runs.
 *
 * <p>Placement follows three rules in order: a tile placed against another one
 * copies its orientation; a floor or ceiling click turns the tile to face the
 * player; a wall click keeps the click-point diagonal that every bilateral
 * shape uses.</p>
 */
public class DoubleRoofTile extends ShapeKind.Model {

    public DoubleRoofTile() {
        super("double_roof_tile", null);
    }

    @Override
    public boolean orientOnPlacement(EntityPlayer player,
                                     TileShape shape,
                                     BlockPos neighbourPos,
                                     IBlockState neighbourState,
                                     TileEntity neighbourTile,
                                     EnumFacing otherFace,
                                     Vector3 hit) {
        if (!player.isSneaking() && neighbourTile instanceof TileShape) {
            // The model declares no profiles, so the generic profile match
            // can never join two tiles; copying the neighbour does. Sneaking
            // skips it, as it does for the profile match on ordinary shapes.
            TileShape neighbour = (TileShape) neighbourTile;
            if (neighbour.getShape() != null && neighbour.getShape().kind == this) {
                shape.setSide(neighbour.getSide());
                shape.setTurn(neighbour.getTurn());
                shape.setOffsetX(neighbour.getOffsetX());
                shape.setMirror(neighbour.getMirror());
                return true;
            }
        }
        if (otherFace.getAxis().isHorizontal()) {
            // On a wall there is no player direction worth using: fall back to
            // the usual click-point diagonal, sneak and upside-down included.
            return false;
        }
        // Floor or ceiling: local +Z is the tile's front, so point it where
        // the player is looking and the join reads the same from any side.
        shape.setSide(0);
        shape.setTurn(turnForDirection(player.getHorizontalFacing()));
        shape.setOffsetX(0);
        return true;
    }

    /** Turn that maps the model's local +Z to {@code dir}. */
    private static int turnForDirection(EnumFacing dir) {
        switch (dir) {
            case EAST:
                return 1;
            case NORTH:
                return 2;
            case WEST:
                return 3;
            default:
                return 0;
        }
    }
}
