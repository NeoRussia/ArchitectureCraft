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

package com.elytradev.architecture.client.render.target;

import com.elytradev.architecture.client.render.ShaderCompat;
import com.elytradev.architecture.client.render.shape.ShapeProbe;
import com.elytradev.architecture.common.ArchitectureLog;
import com.elytradev.architecture.common.helpers.Vector3;
import com.elytradev.architecture.common.tile.TileShape;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.elytradev.architecture.common.utils.MiscUtils.ifloor;
import static com.elytradev.architecture.common.utils.MiscUtils.iround;
import static java.lang.Math.floor;

public class RenderTargetWorld extends RenderTargetBase {

    protected IBlockAccess world;
    protected BlockPos blockPos;
    protected IBlockState blockState;
    protected Block block;
    protected float cmr = 1, cmg = 1, cmb = 1;
    protected boolean ao;
    protected boolean axisAlignedNormal;
    protected boolean renderingOccurred;
    protected final boolean inPreview;
    protected final boolean shaderPackInUse;
    protected float vr, vg, vb, va; // Colour to be applied to next vertex
    protected int vlm1, vlm2; // Light map values to be applied to next vertex
    /**
     * The block's own light value. It cannot change while one shape is being
     * drawn, but the old code re-read it through the block state for every
     * sampled cell.
     */
    private final int lightValue;
    /** Memo of this shape's light samples; null when ambient occlusion is off. */
    private final LightSamples samples;
    private BufferBuilder buff;
    /** Asks another shape what its faces look like, for the culling check. */
    private NeighbourGeometry neighbourGeometry;
    /** Faces are held back until the whole shape is known, so hidden ones can be dropped. */
    private final List<Face> faces = new ArrayList<Face>();
    private Face currentFace;

    public interface NeighbourGeometry {
        /** Corner keys of the neighbour's faces on the given boundary, or null. */
        Set<Long> cornersOn(TileShape neighbour, EnumFacing boundary);
    }

    public void setNeighbourGeometry(NeighbourGeometry geometry) {
        this.neighbourGeometry = geometry;
    }

    /** One buffered face: which way it faces plus its fully lit vertices. */
    protected static final class Face {

        EnumFacing dir;
        int count;
        double[] xs = new double[4], ys = new double[4], zs = new double[4];
        double[] us = new double[4], vs = new double[4];
        float[] rs = new float[4], gs = new float[4], bs = new float[4], as = new float[4];
        int[] l1 = new int[4], l2 = new int[4];

        Face(EnumFacing dir) {
            this.dir = dir;
        }

        void add(Vector3 p, double u, double v, float r, float g, float b, float a, int lm1, int lm2) {
            if (this.count == this.xs.length) {
                int n = this.count * 2;
                this.xs = Arrays.copyOf(this.xs, n);
                this.ys = Arrays.copyOf(this.ys, n);
                this.zs = Arrays.copyOf(this.zs, n);
                this.us = Arrays.copyOf(this.us, n);
                this.vs = Arrays.copyOf(this.vs, n);
                this.rs = Arrays.copyOf(this.rs, n);
                this.gs = Arrays.copyOf(this.gs, n);
                this.bs = Arrays.copyOf(this.bs, n);
                this.as = Arrays.copyOf(this.as, n);
                this.l1 = Arrays.copyOf(this.l1, n);
                this.l2 = Arrays.copyOf(this.l2, n);
            }
            int i = this.count++;
            this.xs[i] = p.x;
            this.ys[i] = p.y;
            this.zs[i] = p.z;
            this.us[i] = u;
            this.vs[i] = v;
            this.rs[i] = r;
            this.gs[i] = g;
            this.bs[i] = b;
            this.as[i] = a;
            this.l1[i] = lm1;
            this.l2[i] = lm2;
        }

    }

    public RenderTargetWorld(IBlockAccess world, BlockPos pos, BufferBuilder buff, TextureAtlasSprite overrideIcon) {
        this(world, pos, buff, overrideIcon, false);
    }

    public RenderTargetWorld(IBlockAccess world, BlockPos pos, BufferBuilder buff,
                             TextureAtlasSprite overrideIcon, boolean inPreview) {
        super(pos.getX(), pos.getY(), pos.getZ(), overrideIcon);
        //ArchitectureLog.info("BaseWorldRenderTarget(%s)\n", pos);
        this.world = world;
        this.blockPos = pos;
        this.blockState = world.getBlockState(pos);
        this.block = this.blockState.getBlock();
        this.setBuff(buff);
        this.inPreview = inPreview;
        this.shaderPackInUse = !inPreview && ShaderCompat.isShaderPackInUse();
        this.ao = Minecraft.isAmbientOcclusionEnabled() && this.block.getLightValue(this.blockState) == 0;
        this.expandTrianglesToQuads = true;
        this.lightValue = this.blockState.getLightValue();
        this.samples = this.ao ? AO_SAMPLES.get() : null;
        if (this.samples != null)
            this.samples.begin(this.world);
    }


    // ---------------------------- IRenderTarget ----------------------------

    @Override
    public void setNormal(Vector3 n) {
//      ArchitectureLog.info("BaseWorldRenderer.setNormal: %s (%.3f, %.3f, %.3f)\n",
//          vertexCount, n.x, n.y, n.z);
        super.setNormal(n);
        this.axisAlignedNormal = n.dot(this.face) >= 0.99;
    }

    @Override
    protected void rawAddVertex(Vector3 p, double u, double v) {
        this.lightVertex(p);
        if (this.currentFace == null)
            this.currentFace = new Face(this.face);
        this.currentFace.add(p, u, v, this.vr, this.vg, this.vb, this.va, this.vlm1, this.vlm2);
        this.renderingOccurred = true;
    }

    @Override
    public void endFace() {
        if (this.currentFace != null) {
            this.faces.add(this.currentFace);
            this.currentFace = null;
        }
        super.endFace();
    }

    //-----------------------------------------------------------------------------------------

    protected void lightVertex(Vector3 p) {
        // TODO: Colour multiplier
        if (this.ao)
            this.aoLightVertex(p);
        else
            this.brLightVertex(p);
    }

    protected void aoLightVertex(Vector3 v) {
        Vector3 n = this.normal;
        double brSum1 = 0, brSum2 = 0, lvSum = 0, wt = 0;

        // Shifted, truncated 2x2x2 sampling cube. This generalises vanilla AO
        // for shapes so their shading blends into full blocks instead of
        // producing a dark rim around them.
        double shift = 1.1;
        double vx = v.x + n.x * shift;
        double vy = v.y + n.y * shift;
        double vz = v.z + n.z * shift;

        double minX = Math.max(v.x - 1.0, vx - 1.0);
        double maxX = Math.min(v.x + 1.0, vx + 1.0);
        double minY = Math.max(v.y - 1.0, vy - 1.0);
        double maxY = Math.min(v.y + 1.0, vy + 1.0);
        double minZ = Math.max(v.z - 1.0, vz - 1.0);
        double maxZ = Math.min(v.z + 1.0, vz + 1.0);
        double volInv = 1.0 / ((maxX - minX) * (maxY - minY) * (maxZ - minZ));

        // Each axis contributes at most three candidates, and only the ones
        // with an overlap are kept. Working them out once per vertex rather
        // than once per combination of the other two axes leaves the same
        // cells in the same order, so the sums are bit-identical.
        LightSamples scratch = this.samples;
        int[] xs = scratch.xs, ys = scratch.ys, zs = scratch.zs;
        double[] wx = scratch.wx, wy = scratch.wy, wz = scratch.wz;
        int nx = axisCells(v.x, minX, maxX, xs, wx);
        int ny = axisCells(v.y, minY, maxY, ys, wy);
        int nz = axisCells(v.z, minZ, maxZ, zs, wz);
        BlockPos.MutableBlockPos pos = scratch.pos;
        int blockX = this.blockPos.getX(), blockY = this.blockPos.getY(), blockZ = this.blockPos.getZ();

        for (int ix = 0; ix < nx; ix++) {
            int X = xs[ix];
            double wox = wx[ix];
            for (int iy = 0; iy < ny; iy++) {
                int Y = ys[iy];
                double woy = wy[iy];
                for (int iz = 0; iz < nz; iz++) {
                    int Z = zs[iz];
                    double w = volInv * wox * woy * wz[iz];
                    // Every vertex of a shape samples the same handful of cells,
                    // so remember what each cell said instead of asking the world
                    // again for each of the shape's vertices.
                    int slot = scratch.slot(X - blockX, Y - blockY, Z - blockZ);
                    int br;
                    float lv;
                    if (slot >= 0 && scratch.used[slot]) {
                        br = scratch.light[slot];
                        lv = scratch.ao[slot];
                    } else {
                        pos.setPos(X, Y, Z);
                        try {
                            br = this.sampleLight(pos);
                        } catch (RuntimeException e) {
                            ArchitectureLog.info("BaseWorldRenderTarget.aoLightVertex: getMixedBrightnessForBlock(%s) with weight %s for block at %s: %s\n",
                                    pos, w, this.blockPos, e);
                            throw e;
                        }
                        if (X != blockX || Y != blockY || Z != blockZ) {
                            IBlockState state = this.world.getBlockState(pos);
                            lv = state.getBlock().getAmbientOcclusionLightValue(state);
                        } else
                            lv = 1.0f;
                        if (slot >= 0) {
                            scratch.used[slot] = true;
                            scratch.light[slot] = br;
                            scratch.ao[slot] = lv;
                        }
                    }
                    if (br != 0) {
                        double br1 = ((br >> 16) & 0xff) / 240.0;
                        double br2 = (br & 0xff) / 240.0;
                        brSum1 += w * br1;
                        brSum2 += w * br2;
                        wt += w;
                    }
                    lvSum += w * lv;
                }
            }
        }
        int brv;
        if (wt > 0)
            brv = (iround(brSum1 / wt * 0xf0) << 16) | iround(brSum2 / wt * 0xf0);
        else
            brv = this.sampleLight(this.blockPos);
        float lvv = (float) lvSum;
        this.setLight((this.shaderPackInUse ? 1.0f : this.shade) * lvv, brv);
    }

    /**
     * Collects the cells on one axis with a non-zero overlap and their weights,
     * returning how many there are. Ascending, as the old nested loops were.
     */
    private static int axisCells(double v, double min, double max, int[] cells, double[] weights) {
        int count = 0;
        for (int d = -1; d <= 1; d++) {
            int cell = ifloor(v + d);
            double weight = Math.max(0, Math.min(max, cell + 1.0) - Math.max(min, (double) cell));
            if (weight > 0) {
                cells[count] = cell;
                weights[count] = weight;
                count++;
            }
        }
        return count;
    }

    /**
     * The light map value at a position. Equivalent to
     * {@code block.getPackedLightmapCoords(state, world, pos)} for the blocks
     * this target renders: their light value is fixed for the whole shape, and
     * none of them are slabs, which is the only case that method treats
     * specially.
     */
    protected int sampleLight(BlockPos pos) {
        return this.world.getCombinedLight(pos, this.lightValue);
    }

    protected void brLightVertex(Vector3 p) {
        Vector3 n = this.normal;
        BlockPos pos;
        if (this.axisAlignedNormal)
            pos = new BlockPos(
                    (int) floor(p.x + 0.01 * n.x),
                    (int) floor(p.y + 0.01 * n.y),
                    (int) floor(p.z + 0.01 * n.z));
        else
            pos = this.blockPos;
        int br = this.sampleLight(pos);
        this.setLight(this.shaderPackInUse ? 1.0f : this.shade, br);
    }

    /**
     * Per-shape memo of the light samples {@link #aoLightVertex} takes.
     *
     * <p>Ambient occlusion walks a 3x3x3 cube around every vertex, and a shape
     * has dozens of vertices over the same small neighbourhood, so the same
     * cells are sampled over and over. Each entry holds what the world said
     * the first time.</p>
     *
     * <p>The table is reused between shapes on a thread to avoid allocating it
     * per block, and is reset for every shape because a light value cached
     * across a rebuild could outlive the block change that altered it.</p>
     */
    private static final class LightSamples {

        private static final int R = 3;
        private static final int N = 2 * R + 1;

        final int[] light = new int[N * N * N];
        final float[] ao = new float[N * N * N];
        final boolean[] used = new boolean[N * N * N];
        /** Per-axis candidates for one vertex; three is the most an axis can have. */
        final int[] xs = new int[3], ys = new int[3], zs = new int[3];
        final double[] wx = new double[3], wy = new double[3], wz = new double[3];
        /** Reused lookup position, so sampling a cell does not allocate. */
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        IBlockAccess world;

        void begin(IBlockAccess world) {
            this.world = world;
            Arrays.fill(this.used, false);
        }

        /** Slot for a block offset from the shape, or -1 when it cannot be cached. */
        int slot(int dx, int dy, int dz) {
            if (dx < -R || dx > R || dy < -R || dy > R || dz < -R || dz > R)
                return -1;
            return ((dx + R) * N + (dy + R)) * N + (dz + R);
        }

    }

    private static final ThreadLocal<LightSamples> AO_SAMPLES = new ThreadLocal<LightSamples>() {
        @Override
        protected LightSamples initialValue() {
            return new LightSamples();
        }
    };

    protected void setLight(float shadow, int br) {
        this.vr = shadow * this.cmr * this.r();
        this.vg = shadow * this.cmg * this.g();
        this.vb = shadow * this.cmb * this.b();
        this.va = this.a();
        this.vlm1 = br >> 16;
        this.vlm2 = br & 0xffff;
        // Additive blending makes a zero-brightness preview invisible.
        if (this.inPreview) {
            this.vlm1 = Math.max(this.vlm1, 80);
            this.vlm2 = Math.max(this.vlm2, 80);
        }
    }

    public boolean end() {
        super.finish();
        if (this.currentFace != null) {
            this.faces.add(this.currentFace);
            this.currentFace = null;
        }
        boolean[] hidden = null;
        if (this.faces.size() > 1) {
            // Never let the culling check cost us the geometry: if anything
            // goes wrong, draw every face as before.
            try {
                hidden = this.findHiddenFaces();
            } catch (RuntimeException e) {
                ArchitectureLog.error("ArchitectureCraft: face culling failed at {}, drawing all faces. {}",
                        this.blockPos, e.toString());
            }
        }
        for (int i = 0; i < this.faces.size(); i++) {
            if (hidden == null || !hidden[i])
                this.emit(this.faces.get(i));
        }
        this.faces.clear();
        return this.renderingOccurred;
    }

    protected void emit(Face f) {
        BufferBuilder out = this.getWorldRenderer();
        for (int i = 0; i < f.count; i++) {
            out.pos(f.xs[i], f.ys[i], f.zs[i]);
            out.color(f.rs[i], f.gs[i], f.bs[i], f.as[i]);
            out.tex(f.us[i], f.vs[i]);
            out.lightmap(f.l1[i], f.l2[i]);
            out.endVertex();
        }
    }

    //-----------------------------------------------------------------------------------------

    /**
     * Marks faces that are buried between two copies of the same shape.
     *
     * <p>Shapes draw every face of their own block, so a wall of glass cubes
     * draws the touching face of each block as well. Opaque blocks hide those
     * behind the depth buffer, but a translucent material blends them in and
     * they show up as a band of extra glass along every block boundary.</p>
     *
     * <p>A face is dropped only when the neighbouring block has a face with the
     * same corners on the same plane. The neighbour is asked by rendering it
     * into a {@link com.elytradev.architecture.client.render.shape.ShapeProbe},
     * because it is often a different shape: the pieces of a slope family are
     * separate blocks that interlock, and a pair of identical corner sets is
     * exactly what proves the two faces are one surface drawn twice.</p>
     */
    protected boolean[] findHiddenFaces() {
        boolean[] hidden = new boolean[this.faces.size()];
        if (this.inPreview || this.neighbourGeometry == null)
            return hidden;
        TileShape te = TileShape.get(this.world, this.blockPos);
        if (te == null || te.getShape() == null)
            return hidden;
        for (EnumFacing dir : EnumFacing.values()) {
            List<Integer> here = this.boundaryFaces(dir);
            if (here.isEmpty())
                continue;
            TileShape nte = TileShape.get(this.world, this.blockPos.offset(dir));
            if (nte == null || nte.getShape() == null)
                continue;
            // Both blocks must draw in this pass, or one of the two faces is
            // missing and removing the other would leave a hole.
            if (!sameMaterials(te, nte))
                continue;
            Set<Long> theirs;
            try {
                theirs = this.neighbourGeometry.cornersOn(nte, dir.getOpposite());
            } catch (RuntimeException e) {
                continue;
            }
            if (theirs == null || theirs.isEmpty())
                continue;
            if (!shift(theirs, dir).equals(this.corners(here)))
                continue;
            for (int i : here)
                hidden[i] = true;
        }
        return hidden;
    }

    private static boolean sameMaterials(TileShape a, TileShape b) {
        return Objects.equals(a.getBaseBlockState(), b.getBaseBlockState())
                && Objects.equals(a.getSecondaryBlockState(), b.getSecondaryBlockState());
    }

    /**
     * Moves a corner set one block along {@code dir}. The packing keeps the
     * three axes in separate bit ranges, so the shift is a plain addition.
     */
    private static Set<Long> shift(Set<Long> keys, EnumFacing dir) {
        long delta = ((long) dir.getDirectionVec().getX() * 10000L << 42)
                + ((long) dir.getDirectionVec().getY() * 10000L << 21)
                + (long) dir.getDirectionVec().getZ() * 10000L;
        Set<Long> out = new HashSet<Long>(keys.size() * 2);
        for (long key : keys)
            out.add(key + delta);
        return out;
    }

    /** Faces that lie flat on the given boundary plane of the block. */
    protected List<Integer> boundaryFaces(EnumFacing dir) {
        List<Integer> found = new ArrayList<Integer>(4);
        for (int i = 0; i < this.faces.size(); i++) {
            Face f = this.faces.get(i);
            if (f.dir == dir && this.onBoundary(f, dir))
                found.add(i);
        }
        return found;
    }

    protected boolean onBoundary(Face f, EnumFacing dir) {
        double plane = dir.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 1 : 0;
        for (int i = 0; i < f.count; i++) {
            if (Math.abs(this.local(f, i, dir.getAxis()) - plane) > 1e-4)
                return false;
        }
        return true;
    }

    /**
     * The set of quantised corners of the given faces. Corners rather than whole
     * polygons, so a square counts the same whether it was drawn as a quad or
     * as two triangles.
     */
    protected Set<Long> corners(List<Integer> indices) {
        Set<Long> keys = new HashSet<Long>();
        for (int index : indices) {
            Face f = this.faces.get(index);
            for (int i = 0; i < f.count; i++) {
                keys.add(ShapeProbe.cornerKey(
                        this.local(f, i, EnumFacing.Axis.X),
                        this.local(f, i, EnumFacing.Axis.Y),
                        this.local(f, i, EnumFacing.Axis.Z)));
            }
        }
        return keys;
    }

    protected double local(Face f, int i, EnumFacing.Axis axis) {
        switch (axis) {
            case X:
                return f.xs[i] - this.blockX;
            case Y:
                return f.ys[i] - this.blockY;
            default:
                return f.zs[i] - this.blockZ;
        }
    }

    public BufferBuilder getWorldRenderer() {
        return this.buff;
    }

    public void setBuff(BufferBuilder buff) {
        this.buff = buff;
    }
}
