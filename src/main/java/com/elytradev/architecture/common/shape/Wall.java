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

import com.elytradev.architecture.client.render.model.IArchitectureModel;
import com.elytradev.architecture.client.render.target.RenderTargetBase;
import com.elytradev.architecture.client.render.texture.ITexture;
import com.elytradev.architecture.common.ArchitectureMod;
import com.elytradev.architecture.common.helpers.Trans3;
import com.elytradev.architecture.common.helpers.Vector3;
import com.elytradev.architecture.common.tile.TileShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.List;

/**
 * A thin wall with a central post and horizontal arms.
 *
 * <p>Straight north-south or east-west runs use a slimmer core; isolated walls,
 * corners, T junctions and crossings use the full post.</p>
 */
public class Wall extends ConnectedShape {

    private static IArchitectureModel postModel;
    private static IArchitectureModel coreModel;
    private static IArchitectureModel armModel;

    private static IArchitectureModel post() {
        if (postModel == null)
            postModel = ArchitectureMod.PROXY.getModel("shape/wall_post.objson");
        return postModel;
    }

    private static IArchitectureModel core() {
        if (coreModel == null)
            coreModel = ArchitectureMod.PROXY.getModel("shape/wall_core.objson");
        return coreModel;
    }

    private static IArchitectureModel arm() {
        if (armModel == null)
            armModel = ArchitectureMod.PROXY.getModel("shape/wall_arm.objson");
        return armModel;
    }

    @Override
    public void renderShape(TileShape te,
                            ITexture[] textures,
                            RenderTargetBase target,
                            Trans3 t,
                            boolean renderBase,
                            boolean renderSecondary,
                            int baseColourMult,
                            int secondaryColourMult) {
        if (!renderBase)
            return;

        boolean north = isConnected(te, EnumFacing.NORTH);
        boolean south = isConnected(te, EnumFacing.SOUTH);
        boolean east = isConnected(te, EnumFacing.EAST);
        boolean west = isConnected(te, EnumFacing.WEST);

        boolean straight = (north && south && !east && !west)
                || (!north && !south && east && west);

        (straight ? core() : post()).render(t, target, baseColourMult, secondaryColourMult, textures);

        // The arm model points north; turn() rotates around Y in 90 degree steps.
        if (north)
            arm().render(t.turn(0), target, baseColourMult, secondaryColourMult, textures);
        if (west)
            arm().render(t.turn(1), target, baseColourMult, secondaryColourMult, textures);
        if (south)
            arm().render(t.turn(2), target, baseColourMult, secondaryColourMult, textures);
        if (east)
            arm().render(t.turn(3), target, baseColourMult, secondaryColourMult, textures);
    }

    @Override
    public void addCollisionBoxesToList(TileShape te,
                                        IBlockAccess world,
                                        BlockPos pos,
                                        IBlockState state,
                                        Entity entity,
                                        Trans3 t,
                                        List list) {
        double x0 = 0.25, z0 = 0.25, x1 = 0.75, z1 = 0.75;
        if (isConnected(te, EnumFacing.WEST))
            x0 = 0;
        if (isConnected(te, EnumFacing.EAST))
            x1 = 1;
        if (isConnected(te, EnumFacing.NORTH))
            z0 = 0;
        if (isConnected(te, EnumFacing.SOUTH))
            z1 = 1;
        this.addBox(new Vector3(x0 - 0.5, -0.5, z0 - 0.5),
                new Vector3(x1 - 0.5, 0.5, z1 - 0.5), t, list);
    }

    @Override
    public boolean secondaryDefaultsToBase() {
        return false;
    }
}
