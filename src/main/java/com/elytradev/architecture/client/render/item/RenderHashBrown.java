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

package com.elytradev.architecture.client.render.item;

import com.elytradev.architecture.client.proxy.ClientProxy;
import com.elytradev.architecture.client.render.ICustomRenderer;
import com.elytradev.architecture.client.render.model.IArchitectureModel;
import com.elytradev.architecture.client.render.target.RenderTargetBase;
import com.elytradev.architecture.client.render.texture.TextureBase;
import com.elytradev.architecture.common.ArchitectureMod;
import com.elytradev.architecture.common.helpers.Trans3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Draws the Hash Brown item from its own model and stitched texture. The model
 * is a normal block-space objson, so the item transform supplied by the render
 * manager already puts it in the right pose.
 */
public class RenderHashBrown implements ICustomRenderer {

    public static final ResourceLocation TEXTURE =
            new ResourceLocation(ArchitectureMod.MOD_ID, "items/hash_brown");
    private static final String MODEL = "item/hash_brown.objson";

    @Override
    public void renderBlock(IBlockAccess world, BlockPos pos, IBlockState state,
                            RenderTargetBase target, BlockRenderLayer layer, Trans3 t) {
        // Not a block.
    }

    @Override
    public void renderBlock(IBlockAccess world, BlockPos pos, IBlockState state,
                            RenderTargetBase target, BlockRenderLayer layer, Trans3 t,
                            boolean renderPrimary, boolean renderSecondary) {
        // Not a block.
    }

    @Override
    public void renderItemStack(ItemStack stack, RenderTargetBase target, Trans3 t) {
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks()
                .getAtlasSprite(TEXTURE.toString());
        IArchitectureModel model = ClientProxy.RENDERING_MANAGER.getModel(MODEL);
        model.render(t, target, 0xFFFFFF, 0xFFFFFF, TextureBase.fromSprite(sprite));
    }
}
