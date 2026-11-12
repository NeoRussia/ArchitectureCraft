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
 * A block clad on one half: the model's local -X half carries the cladding,
 * the +X half the base material.
 *
 * <p>Placement keeps the block upright and turns the clad half towards the
 * player, so the material you asked for is the one you see.</p>
 */
public class DoubleSiding extends ShapeKind.Model {

    public DoubleSiding() {
        super("double_siding", null);
    }

    @Override
    public boolean orientOnPlacement(EntityPlayer player,
                                     TileShape shape,
                                     BlockPos neighbourPos,
                                     IBlockState neighbourState,
                                     TileEntity neighbourTile,
                                     EnumFacing otherFace,
                                     Vector3 hit) {
        // On a wall the clicked face is the one facing the player; on the
        // floor or ceiling the player's own facing gives it.
        EnumFacing out = otherFace.getAxis().isHorizontal()
                ? otherFace
                : player.getHorizontalFacing().getOpposite();
        shape.setSide(0);
        shape.setTurn(turnForCladdingSide(out));
        shape.setOffsetX(0);
        return true;
    }

    /** Turn that makes the clad half (local -X) face {@code dir}. */
    private static int turnForCladdingSide(EnumFacing dir) {
        switch (dir) {
            case EAST:
                return 2;
            case NORTH:
                return 3;
            case WEST:
                return 0;
            default:
                return 1;
        }
    }
}
