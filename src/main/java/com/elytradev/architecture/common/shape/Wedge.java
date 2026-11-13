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
import com.elytradev.architecture.common.helpers.Utils;
import com.elytradev.architecture.common.helpers.Vector3;
import com.elytradev.architecture.common.tile.TileShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

/**
 * A band of a corner pyramid: the base fills the block, the bands above it
 * shrink towards the apex, which sits on the top corner of the tip block. The
 * nine shapes are three such pyramids (2, 3 and 4 bands) with the apex always
 * over the same corner, so a set joins into one straight hip.
 */
public class Wedge extends ShapeKind.Model {

    private static final int SLICES = 4;

    public Wedge(String modelName) {
        super(modelName, null);
    }

    /** How many blocks tall the family's pyramid is. */
    static int bands(EnumShape shape) {
        switch (shape) {
            case WEDGE_A_BASE: case WEDGE_A_TIP:
            case WEDGE_A_BASE_SE: case WEDGE_A_TIP_SE:
                return 2;
            case WEDGE_B_BASE: case WEDGE_B_MIDDLE: case WEDGE_B_TIP:
            case WEDGE_B_BASE_SE: case WEDGE_B_MIDDLE_SE: case WEDGE_B_TIP_SE:
                return 3;
            default:
                return 4;
        }
    }

    /** Which band of the pyramid this piece is, counting up from the base. */
    static int band(EnumShape shape) {
        switch (shape) {
            case WEDGE_A_TIP: case WEDGE_A_TIP_SE:
            case WEDGE_B_MIDDLE: case WEDGE_B_MIDDLE_SE:
            case WEDGE_C_MIDDLE_1: case WEDGE_C_MIDDLE_1_SE:
                return 1;
            case WEDGE_B_TIP: case WEDGE_B_TIP_SE:
            case WEDGE_C_MIDDLE_2: case WEDGE_C_MIDDLE_2_SE:
                return 2;
            case WEDGE_C_TIP: case WEDGE_C_TIP_SE:
                return 3;
            default:
                return 0;
        }
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
            TileShape neighbour = (TileShape) neighbourTile;
            if (neighbour.getShape() != null && bands(neighbour.getShape()) == bands(shape.getShape())) {
                shape.setSide(neighbour.getSide());
                shape.setTurn(neighbour.getTurn());
                shape.setOffsetX(neighbour.getOffsetX());
                return true;
            }
        }
        return false;
    }

    @Override
    public void addCollisionBoxesToList(TileShape te, IBlockAccess world, BlockPos pos, IBlockState state,
                                        Entity entity, Trans3 t, List list) {
        int n = bands(te.getShape());
        int k = band(te.getShape());
        double step = 1.0 / SLICES;
        for (int i = 0; i < SLICES; i++) {
            double mid = (i + 0.5) * step;
            double u = 1.0 - (k + mid) / n;
            double y0 = -0.5 + i * step;
            this.addBox(new Vector3(0.5 - u, y0, 0.5 - u), new Vector3(0.5, y0 + step, 0.5), t, list);
        }
    }

    @Override
    public AxisAlignedBB getBounds(TileShape te, IBlockAccess world, BlockPos pos, IBlockState state,
                                   Entity entity, Trans3 t) {
        List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
        this.addCollisionBoxesToList(te, world, pos, state, entity, t, list);
        return Utils.unionOfBoxes(list);
    }
}
