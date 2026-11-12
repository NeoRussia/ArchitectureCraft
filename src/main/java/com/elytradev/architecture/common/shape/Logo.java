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
 * A thin plaque carrying a pixel pattern, mounted flat on the face you click
 * with its patterned side out. On the floor or ceiling it lies flat, pattern
 * up or down, with its top pointing away from the player.
 */
public class Logo extends ShapeKind.Model {

    public Logo() {
        super("gt_logo", null);
    }

    @Override
    public boolean orientOnPlacement(EntityPlayer player,
                                     TileShape shape,
                                     BlockPos neighbourPos,
                                     IBlockState neighbourState,
                                     TileEntity neighbourTile,
                                     EnumFacing otherFace,
                                     Vector3 hit) {
        if (otherFace.getAxis().isHorizontal()) {
            // On a wall: local +Z is the patterned side, so it faces out of
            // the clicked face, into the room.
            shape.setSide(0);
            shape.setTurn(turnForDirection(otherFace));
        } else {
            // On the floor or a ceiling it lies flat. Local +Z is the
            // patterned side, so it points up for a floor click and down for a
            // ceiling click. Local +Y is the top of the pattern and is kept
            // pointing away from the player.
            shape.setSide(EnumShape.sideForUpDirection(player.getHorizontalFacing()));
            shape.setTurn(otherFace == EnumFacing.UP ? 2 : 0);
        }
        shape.setOffsetX(0);
        return true;
    }

    @Override
    public int itemTurnOffset() {
        // The plaza is a flat plate: seen from behind, its pattern reads
        // mirrored. Turn the item half round so the item shows the face the
        // block shows.
        // The item transform already turns the piece half round, so three
        // quarters here put the patterned face toward the player, the same way
        // the sawbench icon shows it.
        return 3;
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
