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

package com.elytradev.architecture.client.render.shape;

import com.elytradev.architecture.client.render.ICustomRenderer;
import com.elytradev.architecture.client.render.target.RenderTargetBase;
import com.elytradev.architecture.client.render.target.RenderTargetWorld;
import com.elytradev.architecture.client.render.texture.ITexture;
import com.elytradev.architecture.client.render.texture.TextureBase;
import com.elytradev.architecture.common.ArchitectureLog;
import com.elytradev.architecture.common.helpers.Trans3;
import com.elytradev.architecture.common.helpers.Utils;
import com.elytradev.architecture.common.tile.TileShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;

public class ShapeRenderDispatch implements ICustomRenderer {

    // Cannot have any per-render state, because it may be
    // called from more than one thread.

    @Override
    public void renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, RenderTargetBase target,
                            BlockRenderLayer layer, Trans3 t) {
        TileShape shape = TileShape.get(world, pos);
        if (shape != null) {
            Trans3 transform = t.t(shape.localToGlobalRotation());
            boolean renderBase = this.canRenderInLayer(shape.getBaseBlockState(), layer);
            boolean renderSecondary = this.canRenderInLayer(shape.getSecondaryBlockState(), layer);

            int baseColour = renderBase ? Utils.getColourFromState(shape.getBaseBlockState()) : -1;
            int secondaryColour = renderSecondary ? Utils.getColourFromState(shape.getSecondaryBlockState()) : baseColour;

            this.renderShapeTE(shape, target, transform, renderBase, renderSecondary, baseColour, secondaryColour);
        }
    }

    private boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        if (layer == null)
            return true;
        return state != null && state.getBlock().canRenderInLayer(state, layer);
    }

    @Override
    public void renderItemStack(ItemStack stack, RenderTargetBase target, Trans3 t) {
        TileShape te = new TileShape();
        te.readFromItemStack(stack);
        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
        ItemStack baseStack = this.getStackFromState(te.getBaseBlockState());
        ItemStack secondaryStack = this.getStackFromState(te.getSecondaryBlockState());
        int baseColour = baseStack != null ?
                itemColors.colorMultiplier(baseStack, 0) : -1;
        int secondaryColour = secondaryStack != null ?
                itemColors.colorMultiplier(secondaryStack, 0) : baseColour;

        // Apply the orientation the item will get when placed, so siding
        // stacks preview upright instead of lying flat like roofing. The item
        // transform also carries a half turn for the north-facing item
        // convention; siding is authored with its long face at +Z, so undo it
        // and let that face look at the player.
        Trans3 base = te.getShape() != null && te.getShape().isSiding() ? t.turn(2) : t;
        if (te.getShape() != null)
            base = base.turn(te.getShape().kind.itemTurnOffset());
        Trans3 transform = base.t(Trans3.sideTurn(te.getSide(), te.getTurn()));
        this.renderShapeTE(te, target, transform,
                te.hasBaseBlockState(),
                te.hasSecondaryBlockState(),
                baseColour, secondaryColour);
    }

    @Nullable
    public ItemStack getStackFromState(IBlockState state) {
        Item itemBlock = Item.getItemFromBlock(state.getBlock());
        if (!Objects.equals(state.getBlock(), Blocks.AIR)) {
            ItemStack defaultInstance = itemBlock.getDefaultInstance();
            defaultInstance.setItemDamage(state.getBlock().damageDropped(state));
            return defaultInstance;
        }

        return ItemStack.EMPTY;
    }

    public void renderShapeTE(TileShape te, RenderTargetBase target, Trans3 t,
                              boolean renderBase, boolean renderSecondary,
                              int baseColour, int secondaryColour) {
        if (te.getShape() != null && (renderBase || renderSecondary)) {
            IBlockState base = te.getBaseBlockState();
            if (base != null) {
                TextureAtlasSprite baseSprite = Utils.getSpriteForBlockState(base);
                TextureAtlasSprite secondarySprite = Utils.getSpriteForBlockState(te.getSecondaryBlockState());
                if (baseSprite != null) {
                    ITexture[] textures = new ITexture[4];
                    // Siding is mounted on its side, so a projection aligned to the
                    // world axes would lay the texture across the wall. Keep the
                    // model's own UVs there so the grain follows the shape.
                    boolean projected = !te.getShape().isSiding();
                    if (renderBase) {
                        textures[0] = TextureBase.fromSprite(baseSprite);
                        textures[1] = projected ? textures[0].projected() : textures[0];
                    }
                    if (renderSecondary) {
                        if (secondarySprite != null) {
                            textures[2] = TextureBase.fromSprite(secondarySprite);
                            textures[3] = projected ? textures[2].projected() : textures[2];
                        } else
                            renderSecondary = false;
                    }
                    int orient = te.getMirror();
                    if (orient != 0) {
                        int rot = orient & 3;
                        boolean flipped = (orient & 4) != 0;
                        for (int i = 0; i < 4; i++)
                            if (textures[i] instanceof TextureBase)
                                textures[i] = ((TextureBase) textures[i]).transformed(rot, flipped);
                    }
                    if (renderBase && te.getShape().kind.secondaryDefaultsToBase()) {
                        if (secondarySprite == null || (te.hasSecondaryBlockState() &&
                                te.getSecondaryBlockState().getBlock().getRenderLayer() != BlockRenderLayer.SOLID)) {
                            textures[2] = textures[0];
                            textures[3] = textures[1];
                            renderSecondary = renderBase;
                        }
                    }
                    // The world target drops faces buried between two blocks,
                    // and its neighbour is often a different shape, so give it
                    // a way to look at the neighbour's actual geometry.
                    if (target instanceof RenderTargetWorld) {
                        final ITexture[] probeTextures = textures;
                        final boolean probeBase = renderBase;
                        final boolean probeSecondary = renderSecondary;
                        final int probeBaseColour = baseColour;
                        final int probeSecondaryColour = secondaryColour;
                        ((RenderTargetWorld) target).setNeighbourGeometry(
                                new RenderTargetWorld.NeighbourGeometry() {
                                    @Override
                                    public Set<Long> cornersOn(TileShape neighbour, EnumFacing boundary) {
                                        return ShapeProbe.probe(neighbour, probeTextures,
                                                probeBase, probeSecondary,
                                                probeBaseColour, probeSecondaryColour).cornersOn(boundary);
                                    }
                                });
                    }
                    te.getShape().kind.renderShape(te, textures, target, t,
                            renderBase, renderSecondary,
                            baseColour, secondaryColour);
                }
            }
        }
    }

    @Override
    public void renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, RenderTargetBase target,
                            BlockRenderLayer layer, Trans3 t, boolean renderBase, boolean renderSecondary) {
        TileShape shape = TileShape.get(world, pos);
        if (shape != null) {
            Trans3 t2 = t.t(shape.localToGlobalRotation());
            int baseColour = renderBase ? Utils.getColourFromState(shape.getBaseBlockState()) : -1;
            int secondaryColour = renderSecondary ? Utils.getColourFromState(shape.getBaseBlockState()) : baseColour;

            this.renderShapeTE(TileShape.get(world, pos), target, t2, renderBase, renderSecondary, baseColour, secondaryColour);
        }
    }

}
