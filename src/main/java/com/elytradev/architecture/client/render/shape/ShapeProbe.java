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

import com.elytradev.architecture.client.render.target.RenderTargetBase;
import com.elytradev.architecture.client.render.texture.ITexture;
import com.elytradev.architecture.common.helpers.Trans3;
import com.elytradev.architecture.common.helpers.Vector3;
import com.elytradev.architecture.common.tile.TileShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Renders a shape and keeps only where its faces land, as corner keys.
 *
 * <p>Face culling needs to know what the neighbouring block looks like on the
 * shared plane, and the neighbour is often a different shape: the pieces of a
 * slope family are separately placed blocks that interlock. Running the
 * neighbour's renderer into a probe answers that exactly, without guessing from
 * shape ids or orientation fields.</p>
 */
public final class ShapeProbe extends RenderTargetBase {

    private static final double SCALE = 1e4;

    private final Set<Long>[] faces = new Set[EnumFacing.values().length];
    private final List<Long> current = new ArrayList<Long>();
    private EnumFacing currentDir;
    private EnumFacing.Axis currentAxis;
    private double currentPlane;
    private boolean currentOnPlane;

    private ShapeProbe(BlockPos pos) {
        super(pos.getX(), pos.getY(), pos.getZ(), null);
    }

    /**
     * Renders {@code te} with the textures and flags of the block being drawn.
     * The caller only probes neighbours with identical materials, so the same
     * textures and flags apply to both.
     */
    public static ShapeProbe probe(TileShape te, ITexture[] textures,
                                   boolean renderBase, boolean renderSecondary,
                                   int baseColour, int secondaryColour) {
        ShapeProbe probe = new ShapeProbe(te.getPos());
        Trans3 t = Trans3.blockCenter(te.getPos()).t(te.localToGlobalRotation());
        te.getShape().kind.renderShape(te, textures, probe, t,
                renderBase, renderSecondary, baseColour, secondaryColour);
        return probe;
    }

    /** Corners of the faces lying flat on the given boundary, or null. */
    public Set<Long> cornersOn(EnumFacing dir) {
        return this.faces[dir.ordinal()];
    }

    @Override
    protected void rawAddVertex(Vector3 p, double u, double v) {
        double x = p.x - this.blockX;
        double y = p.y - this.blockY;
        double z = p.z - this.blockZ;
        if (this.current.isEmpty()) {
            this.currentDir = this.face;
            this.currentAxis = this.face == null ? null : this.face.getAxis();
            this.currentPlane = this.face == null || this.face.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE
                    ? 0 : 1;
            this.currentOnPlane = this.currentAxis != null;
        }
        if (this.currentOnPlane) {
            double v0 = axisValue(x, y, z, this.currentAxis);
            if (Math.abs(v0 - this.currentPlane) > 1e-4)
                this.currentOnPlane = false;
        }
        this.current.add(cornerKey(x, y, z));
    }

    @Override
    public void endFace() {
        if (this.currentOnPlane && this.currentDir != null && !this.current.isEmpty()) {
            int i = this.currentDir.ordinal();
            Set<Long> set = this.faces[i];
            if (set == null) {
                set = new HashSet<Long>();
                this.faces[i] = set;
            }
            set.addAll(this.current);
        }
        this.current.clear();
        super.endFace();
    }

    private static double axisValue(double x, double y, double z, EnumFacing.Axis axis) {
        switch (axis) {
            case X:
                return x;
            case Y:
                return y;
            default:
                return z;
        }
    }

    /** Packs a block-local corner into a long; the offset keeps it positive. */
    public static long cornerKey(double x, double y, double z) {
        return cornerKey(
                (long) Math.round(x * SCALE),
                (long) Math.round(y * SCALE),
                (long) Math.round(z * SCALE));
    }

    public static long cornerKey(long x, long y, long z) {
        return ((x + 100000L) << 42) | ((y + 100000L) << 21) | (z + 100000L);
    }

}
