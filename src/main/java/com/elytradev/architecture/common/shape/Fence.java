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
import com.elytradev.architecture.common.helpers.Utils;
import com.elytradev.architecture.common.helpers.Vector3;
import com.elytradev.architecture.common.tile.TileShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

/**
 * A fence that behaves like a vanilla fence: a 4x16x4 post with two 2x3 rails
 * towards every side that connects to another fence or to a full cube.
 *
 * <p>Collision is 1.5 blocks high, as in vanilla, so the fence cannot simply be
 * jumped over. The selection box stays the post, again matching vanilla.</p>
 */
public class Fence extends ConnectedShape {

    private static IArchitectureModel postModel;
    private static IArchitectureModel railModel;

    private static IArchitectureModel post() {
        if (postModel == null)
            postModel = ArchitectureMod.PROXY.getModel("shape/fence_post.objson");
        return postModel;
    }

    private static IArchitectureModel rail() {
        if (railModel == null)
            railModel = ArchitectureMod.PROXY.getModel("shape/fence_rail.objson");
        return railModel;
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

        post().render(t, target, baseColourMult, secondaryColourMult, textures);

        // The rail model points north; turn() rotates around Y in 90 degree steps.
        if (te.getWorld() == null || te.getPos() == null) {
            // Item stack: no neighbours to read, so show a plain fence section.
            rail().render(t.turn(1), target, baseColourMult, secondaryColourMult, textures);
            rail().render(t.turn(3), target, baseColourMult, secondaryColourMult, textures);
            return;
        }

        if (isConnected(te, EnumFacing.NORTH))
            rail().render(t.turn(0), target, baseColourMult, secondaryColourMult, textures);
        if (isConnected(te, EnumFacing.WEST))
            rail().render(t.turn(1), target, baseColourMult, secondaryColourMult, textures);
        if (isConnected(te, EnumFacing.SOUTH))
            rail().render(t.turn(2), target, baseColourMult, secondaryColourMult, textures);
        if (isConnected(te, EnumFacing.EAST))
            rail().render(t.turn(3), target, baseColourMult, secondaryColourMult, textures);
    }

    @Override
    public void addCollisionBoxesToList(TileShape te,
                                        IBlockAccess world,
                                        BlockPos pos,
                                        IBlockState state,
                                        Entity entity,
                                        Trans3 t,
                                        List list) {
        // Entity collision only: post and arms, 1.5 blocks tall, as vanilla's
        // BlockFence.addCollisionBoxToList. Picking never uses these boxes.
        this.addBox(new Vector3(-0.125, -0.5, -0.125),
                new Vector3(0.125, 1.0, 0.125), t, list);
        if (isConnected(te, EnumFacing.NORTH))
            this.addBox(new Vector3(-0.125, -0.5, -0.5),
                    new Vector3(0.125, 1.0, 0.0), t, list);
        if (isConnected(te, EnumFacing.SOUTH))
            this.addBox(new Vector3(-0.125, -0.5, 0.0),
                    new Vector3(0.125, 1.0, 0.5), t, list);
        if (isConnected(te, EnumFacing.WEST))
            this.addBox(new Vector3(-0.5, -0.5, -0.125),
                    new Vector3(0.0, 1.0, 0.125), t, list);
        if (isConnected(te, EnumFacing.EAST))
            this.addBox(new Vector3(0.0, -0.5, -0.125),
                    new Vector3(0.5, 1.0, 0.125), t, list);
    }

    @Override
    public AxisAlignedBB getBounds(TileShape te, IBlockAccess world, BlockPos pos, IBlockState state,
                                   Entity entity, Trans3 t) {
        // Selection and picking box: post and arms, one block tall, 4 px wide
        // bars that run from the post to the block edge. This is vanilla's
        // BlockFence.getBoundingBox, which Block.collisionRayTrace uses; the
        // 1.5 collision above is for entity movement only.
        List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
        this.addBox(new Vector3(-0.125, -0.5, -0.125),
                new Vector3(0.125, 0.5, 0.125), t, list);
        if (isConnected(te, EnumFacing.NORTH))
            this.addBox(new Vector3(-0.125, -0.5, -0.5),
                    new Vector3(0.125, 0.5, 0.0), t, list);
        if (isConnected(te, EnumFacing.SOUTH))
            this.addBox(new Vector3(-0.125, -0.5, 0.0),
                    new Vector3(0.125, 0.5, 0.5), t, list);
        if (isConnected(te, EnumFacing.WEST))
            this.addBox(new Vector3(-0.5, -0.5, -0.125),
                    new Vector3(0.0, 0.5, 0.125), t, list);
        if (isConnected(te, EnumFacing.EAST))
            this.addBox(new Vector3(0.0, -0.5, -0.125),
                    new Vector3(0.5, 0.5, 0.125), t, list);
        return Utils.unionOfBoxes(list);
    }

    @Override
    public boolean secondaryDefaultsToBase() {
        return false;
    }

    @Override
    public PathNodeType getPathNodeType() {
        return PathNodeType.FENCE;
    }


}
