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

import com.elytradev.architecture.common.ArchitectureLog;
import com.elytradev.architecture.common.helpers.Profile;
import com.elytradev.architecture.common.helpers.Trans3;
import com.elytradev.architecture.common.helpers.Vector3;
import com.elytradev.architecture.common.tile.TileShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;

import java.util.HashMap;
import java.util.Map;

import static com.elytradev.architecture.common.shape.EnumShapeSymmetry.*;
import static com.elytradev.architecture.common.shape.ShapeFlags.GLOW;
import static com.elytradev.architecture.common.shape.ShapeFlags.PLACE_OFFSET;
import static com.elytradev.architecture.common.shape.ShapeFlags.PLACE_UNDERNEATH;
import static com.elytradev.architecture.common.shape.ShapeFlags.SIDING;
import static java.lang.Math.abs;

public enum EnumShape {

    ROOF_TILE(0, "architecturecraft.shape.roof_tile", ShapeKind.Roof, BILATERAL, 1, 2, 0xcf),
    ROOF_OUTER_CORNER(1, "architecturecraft.shape.roof_outer_corner", ShapeKind.Roof, UNILATERAL, 1, 3, 0x4f),
    ROOF_INNER_CORNER(2, "architecturecraft.shape.roof_inner_corner", ShapeKind.Roof, UNILATERAL, 2, 3, 0xdf),
    ROOF_RIDGE(3, "architecturecraft.shape.roof_ridge", ShapeKind.Roof, BILATERAL, 1, 4, 0x0f),
    ROOF_SMART_RIDGE(4, "architecturecraft.shape.roof_smart_ridge", ShapeKind.Roof, QUADRILATERAL, 1, 2, 0x0f),
    ROOF_VALLEY(5, "architecturecraft.shape.roof_valley", ShapeKind.Roof, BILATERAL, 1, 2, 0xff),
    ROOF_SMART_VALLEY(6, "architecturecraft.shape.roof_smart_valley", ShapeKind.Roof, QUADRILATERAL, 1, 1, 0xff),

    ROOF_OVERHANG(7, "architecturecraft.shape.roof_overhang", ShapeKind.Model("roof_overhang"), BILATERAL, 1, 2, 0xcf),
    ROOF_OVERHANG_OUTER_CORNER(8, "architecturecraft.shape.roof_overhang_outer_corner", ShapeKind.Model("roof_overhang_outer_corner"), UNILATERAL, 1, 3, 0x4f),
    ROOF_OVERHANG_INNER_CORNER(9, "architecturecraft.shape.roof_overhang_inner_corner", ShapeKind.Model("roof_overhang_inner_corner"), UNILATERAL, 2, 3, 0xdf),

    CYLINDER(10, "architecturecraft.shape.cylinder", ShapeKind.Model("cylinder_full_r8h16"), QUADRILATERAL, 1, 1, 0xff),
    CYLINDER_HALF(11, "architecturecraft.shape.cylinder_half", ShapeKind.Model("cylinder_half_r8h16"), BILATERAL, 1, 1, 0xcc),
    CYLINDER_QUARTER(12, "architecturecraft.shape.cylinder_quarter", ShapeKind.Model("cylinder_quarter_r8h16"), UNILATERAL, 1, 1, 0x44),
    CYLINDER_LARGE_QUARTER(13, "architecturecraft.shape.cylinder_large_quarter", ShapeKind.Model("cylinder_quarter_r16h16"), UNILATERAL, 1, 1, 0xff),
    ANTICYLINDER_LARGE_QUARTER(14, "architecturecraft.shape.anticylinder_large_quarter", ShapeKind.Model("round_inner_corner"), UNILATERAL, 1, 2, 0xdd),
    PILLAR(15, "architecturecraft.shape.pillar", ShapeKind.Model("cylinder_r6h16"), QUADRILATERAL, 1, 1, 0x106),
    POST(16, "architecturecraft.shape.post", ShapeKind.Model("cylinder_r4h16"), QUADRILATERAL, 1, 4, 0x104),
    POLE(17, "architecturecraft.shape.pole", ShapeKind.Model("cylinder_r2h16"), QUADRILATERAL, 1, 16, 0x102),

    BEVELLED_OUTER_CORNER(18, "architecturecraft.shape.bevelled_outer_corner", ShapeKind.Model("bevelled_outer_corner"), UNILATERAL, 1, 3, 0x4f),
    BEVELLED_INNER_CORNER(19, "architecturecraft.shape.bevelled_inner_corner", ShapeKind.Model("bevelled_inner_corner"), UNILATERAL, 1, 1, 0xdf),

    PILLAR_BASE(20, "architecturecraft.shape.pillar_base", ShapeKind.Model("pillar_base"), QUADRILATERAL, 1, 1, 0xff),
    DORIC_CAPITAL(21, "architecturecraft.shape.doric_capital", ShapeKind.Model("doric_capital"), QUADRILATERAL, 1, 1, 0xff),
    IONIC_CAPITAL(22, "architecturecraft.shape.ionic_capital", ShapeKind.Model("ionic_capital"), BILATERAL, 1, 1, 0xff),
    CORINTHIAN_CAPITAL(23, "architecturecraft.shape.corinthian_capital", ShapeKind.Model("corinthian_capital"), QUADRILATERAL, 1, 1, 0xff),
    DORIC_TRIGLYPH(24, "architecturecraft.shape.doric_triglyph", ShapeKind.Model("doric_triglyph", Profile.Generic.lrStraight), BILATERAL, 1, 1, 0xff),
    DORIC_TRIGLYPH_CORNER(25, "architecturecraft.shape.doric_triglyph_corner", ShapeKind.Model("doric_triglyph_corner", Profile.Generic.lrCorner), BILATERAL, 1, 1, 0xff),
    DORIC_METOPE(26, "architecturecraft.shape.doric_metope", ShapeKind.Model("doric_metope", Profile.Generic.lrStraight), BILATERAL, 1, 1, 0xff),
    ARCHITRAVE(27, "architecturecraft.shape.architrave", ShapeKind.Model("architrave", Profile.Generic.lrStraight), BILATERAL, 1, 1, 0xff),
    ARCHITRAVE_CORNER(28, "architecturecraft.shape.architrave_corner", ShapeKind.Model("architrave_corner", Profile.Generic.lrCorner), UNILATERAL, 1, 1, 0xff),

    WINDOW_FRAME(30, "architecturecraft.shape.window_frame", WindowShapeKinds.PlainWindow, BILATERAL, 1, 4, 0x202),
    WINDOW_CORNER(31, "architecturecraft.shape.window_corner", WindowShapeKinds.CornerWindow, UNILATERAL, 1, 2, 0x202),
    WINDOW_MULLION(32, "architecturecraft.shape.window_mullion", WindowShapeKinds.MullionWindow, BILATERAL, 1, 2, 0x202),

    SPHERE_FULL(33, "architecturecraft.shape.sphere_full", ShapeKind.Model("sphere_full_r8"), QUADRILATERAL, 1, 1, 0xff),
    SPHERE_HALF(34, "architecturecraft.shape.sphere_half", ShapeKind.Model("sphere_half_r8"), QUADRILATERAL, 1, 2, 0x0f),
    SPHERE_QUARTER(35, "architecturecraft.shape.sphere_quarter", ShapeKind.Model("sphere_quarter_r8"), BILATERAL, 1, 4, 0x0c),
    SPHERE_EIGHTH(36, "architecturecraft.shape.sphere_eighth", ShapeKind.Model("sphere_eighth_r8"), UNILATERAL, 1, 8, 0x04),
    SPHERE_EIGHTH_LARGE(37, "architecturecraft.shape.sphere_eighth_large", ShapeKind.Model("sphere_eighth_r16"), UNILATERAL, 1, 1, 0xff),
    SPHERE_EIGHTH_LARGE_REV(38, "architecturecraft.shape.sphere_eighth_large_rev", ShapeKind.Model("sphere_eighth_r16_rev"), UNILATERAL, 1, 1, 0xdf),

    ROOF_OVERHANG_GABLE_LH(40, "architecturecraft.shape.roof_overhang_gable_lh", ShapeKind.Model("roof_overhang_gable_lh"), BILATERAL, 1, 4, 0x48),
    ROOF_OVERHANG_GABLE_RH(41, "architecturecraft.shape.roof_overhang_gable_rh", ShapeKind.Model("roof_overhang_gable_rh"), BILATERAL, 1, 4, 0x84),
    ROOF_OVERHANG_GABLE_END_LH(42, "architecturecraft.shape.roof_overhang_gable_end_lh", ShapeKind.Model("roof_overhang_gable_end_lh"), BILATERAL, 1, 4, 0x48),
    ROOF_OVERHANG_GABLE_END_RH(43, "architecturecraft.shape.roof_overhang_gable_end_rh", ShapeKind.Model("roof_overhang_gable_end_rh"), BILATERAL, 1, 4, 0x48),
    ROOF_OVERHANG_RIDGE(44, "architecturecraft.shape.roof_overhang_ridge", ShapeKind.Model("roof_overhang_gable_ridge"), BILATERAL, 1, 4, 0x0c),
    ROOF_OVERHANG_VALLEY(45, "architecturecraft.shape.roof_overhang_valley", ShapeKind.Model("roof_overhang_gable_valley"), BILATERAL, 1, 4, 0xcc),

    CORNICE_LH(50, "architecturecraft.shape.cornice_lh", ShapeKind.Model("cornice_lh"), BILATERAL, 1, 4, 0x48),
    CORNICE_RH(51, "architecturecraft.shape.cornice_rh", ShapeKind.Model("cornice_rh"), BILATERAL, 1, 4, 0x84),
    CORNICE_END_LH(52, "architecturecraft.shape.cornice_end_lh", ShapeKind.Model("cornice_end_lh"), BILATERAL, 1, 4, 0x48),
    CORNICE_END_RH(53, "architecturecraft.shape.cornice_end_rh", ShapeKind.Model("cornice_end_rh"), BILATERAL, 1, 4, 0x48),
    CORNICE_RIDGE(54, "architecturecraft.shape.cornice_ridge", ShapeKind.Model("cornice_ridge"), BILATERAL, 1, 4, 0x0c),
    CORNICE_VALLEY(55, "architecturecraft.shape.cornice_valley", ShapeKind.Model("cornice_valley"), BILATERAL, 1, 4, 0xcc),
    CORNICE_BOTTOM(56, "architecturecraft.shape.cornice_bottom", ShapeKind.Model("cornice_bottom"), BILATERAL, 1, 4, 0x0c),

    CLADDING_SHEET(60, "architecturecraft.shape.cladding_sheet", ShapeKind.Cladding, null, 1, 16, 0),

    ARCH_D_1(61, "architecturecraft.shape.arch_d_1", ShapeKind.Model("arch_d1"), BILATERAL, 1, 1, 0xff, PLACE_UNDERNEATH),
    ARCH_D_2(62, "architecturecraft.shape.arch_d_2", ShapeKind.Model("arch_d2"), BILATERAL, 1, 2, 0xfc, PLACE_UNDERNEATH),
    ARCH_D_3_A(63, "architecturecraft.shape.arch_d_3_a", ShapeKind.Model("arch_d3a"), BILATERAL, 1, 2, 0xcc, PLACE_UNDERNEATH),
    ARCH_D_3_B(64, "architecturecraft.shape.arch_d_3_b", ShapeKind.Model("arch_d3b"), BILATERAL, 1, 1, 0xfc, PLACE_UNDERNEATH),
    ARCH_D_3_C(65, "architecturecraft.shape.arch_d_3_c", ShapeKind.Model("arch_d3c"), BILATERAL, 1, 1, 0xff, PLACE_UNDERNEATH),
    ARCH_D_4_A(66, "architecturecraft.shape.arch_d_4_a", ShapeKind.Model("arch_d4a"), BILATERAL, 1, 2, 0xcc, PLACE_UNDERNEATH),
    ARCH_D_4_B(67, "architecturecraft.shape.arch_d_4_b", ShapeKind.Model("arch_d4b"), BILATERAL, 1, 1, 0xfc, PLACE_UNDERNEATH),
    ARCH_D_4_C(68, "architecturecraft.shape.arch_d_4_c", ShapeKind.Model("arch_d4c"), BILATERAL, 1, 2, 0x0, PLACE_UNDERNEATH),

    BANISTER_PLAIN_BOTTOM(70, "architecturecraft.shape.banister_plain_bottom", ShapeKind.Banister("balustrade_stair_plain_bottom"), BILATERAL, 1, 10, 0x0, PLACE_OFFSET),
    BANISTER_PLAIN(71, "architecturecraft.shape.banister_plain", ShapeKind.Banister("balustrade_stair_plain"), BILATERAL, 1, 10, 0x0, PLACE_OFFSET),
    BANISTER_PLAIN_TOP(72, "architecturecraft.shape.banister_plain_top", ShapeKind.Banister("balustrade_stair_plain_top"), BILATERAL, 1, 10, 0x0, PLACE_OFFSET),

    BALUSTRADE_FANCY(73, "architecturecraft.shape.balustrade_fancy", ShapeKind.Model("balustrade_fancy"), BILATERAL, 1, 5, 0x0),
    BALUSTRADE_FANCY_CORNER(74, "architecturecraft.shape.balustrade_fancy_corner", ShapeKind.Model("balustrade_fancy_corner"), UNILATERAL, 1, 2, 0x0),
    BALUSTRADE_FANCY_WITH_NEWEL(75, "architecturecraft.shape.balustrade_fancy_with_newel", ShapeKind.Model("balustrade_fancy_with_newel"), BILATERAL, 1, 3, 0x0),
    BALUSTRADE_FANCY_NEWEL(76, "architecturecraft.shape.balustrade_fancy_newel", ShapeKind.Model("balustrade_fancy_newel"), UNILATERAL, 1, 4, 0x0),

    BALUSTRADE_PLAIN(77, "architecturecraft.shape.balustrade_plain", ShapeKind.Model("balustrade_plain"), BILATERAL, 1, 10, 0x0),
    BALUSTRADE_PLAIN_OUTER_CORNER(78, "architecturecraft.shape.balustrade_plain_outer_corner", ShapeKind.Model("balustrade_plain_outer_corner"), UNILATERAL, 1, 4, 0x0),
    BALUSTRADE_PLAIN_WITH_NEWEL(79, "architecturecraft.shape.balustrade_plain_with_newel", ShapeKind.Model("balustrade_plain_with_newel"), BILATERAL, 1, 6, 0x0),

    BANISTER_PLAIN_END(80, "architecturecraft.shape.banister_plain_end", ShapeKind.Banister("balustrade_stair_plain_end"), BILATERAL, 1, 8, 0x0, PLACE_OFFSET),

    BANISTER_FANCY_NEWEL_TALL(81, "architecturecraft.shape.banister_fancy_newel_tall", ShapeKind.Model("balustrade_fancy_newel_tall"), UNILATERAL, 1, 2, 0x0),

    BALUSTRADE_PLAIN_INNER_CORNER(82, "architecturecraft.shape.balustrade_plain_inner_corner", ShapeKind.Model("balustrade_plain_inner_corner"), UNILATERAL, 1, 8, 0x0),
    BALUSTRADE_PLAIN_END(83, "architecturecraft.shape.balustrade_plain_end", ShapeKind.Banister("balustrade_plain_end"), BILATERAL, 1, 8, 0x0, PLACE_OFFSET),

    BANISTER_FANCY_BOTTOM(84, "architecturecraft.shape.banister_fancy_bottom", ShapeKind.Banister("balustrade_stair_fancy_bottom"), BILATERAL, 1, 5, 0x0, PLACE_OFFSET),
    BANISTER_FANCY(85, "architecturecraft.shape.banister_fancy", ShapeKind.Banister("balustrade_stair_fancy"), BILATERAL, 1, 5, 0x0, PLACE_OFFSET),
    BANISTER_FANCY_TOP(86, "architecturecraft.shape.banister_fancy_top", ShapeKind.Banister("balustrade_stair_fancy_top"), BILATERAL, 1, 5, 0x0, PLACE_OFFSET),
    BANISTER_FANCY_END(87, "architecturecraft.shape.banister_fancy_end", ShapeKind.Banister("balustrade_stair_fancy_end"), BILATERAL, 1, 2, 0x0, PLACE_OFFSET),

    BANISTER_PLAIN_INNER_CORNER(88, "architecturecraft.shape.banister_plain_inner_corner", ShapeKind.Model("balustrade_stair_plain_inner_corner"), UNILATERAL, 1, 6, 0x0),

    SLAB(90, "architecturecraft.shape.slab", ShapeKind.Model("slab"), QUADRILATERAL, 1, 2, 0x0f),
    STAIRS(91, "architecturecraft.shape.stairs", ShapeKind.Model("stairs", Profile.Generic.lrStraight), BILATERAL, 3, 4, 0xcf),
    STAIRS_OUTER_CORNER(92, "architecturecraft.shape.stairs_outer_corner", ShapeKind.Model("stairs_outer_corner", Profile.Generic.lrCorner), UNILATERAL, 2, 3, 0x4f),
    STAIRS_INNER_CORNER(93, "architecturecraft.shape.stairs_inner_corner", ShapeKind.Model("stairs_inner_corner", Profile.Generic.rlCorner), UNILATERAL, 1, 1, 0xdf),

    // GTNH roof slopes, ported from GTNewHorizons/ArchitectureCraft (MIT, Copyright (c) 2016 gcewing).
    SLOPE_TILE_A1(94, "architecturecraft.shape.slope_tile_a1", ShapeKind.Roof, BILATERAL, 1, 1, 0xcf),
    SLOPE_TILE_A2(95, "architecturecraft.shape.slope_tile_a2", ShapeKind.Roof, BILATERAL, 1, 3, 0x0f),
    SLOPE_TILE_B1(96, "architecturecraft.shape.slope_tile_b1", ShapeKind.Roof, BILATERAL, 1, 1, 0xff),
    SLOPE_TILE_B2(97, "architecturecraft.shape.slope_tile_b2", ShapeKind.Roof, BILATERAL, 1, 2, 0xcf),
    SLOPE_TILE_B3(98, "architecturecraft.shape.slope_tile_b3", ShapeKind.Roof, BILATERAL, 1, 3, 0x0f),
    SLOPE_TILE_C1(99, "architecturecraft.shape.slope_tile_c1", ShapeKind.Roof, BILATERAL, 1, 1, 0xff),
    SLOPE_TILE_C2(100, "architecturecraft.shape.slope_tile_c2", ShapeKind.Roof, BILATERAL, 1, 2, 0xcf),
    SLOPE_TILE_C3(101, "architecturecraft.shape.slope_tile_c3", ShapeKind.Roof, BILATERAL, 1, 3, 0x0f),
    SLOPE_TILE_C4(102, "architecturecraft.shape.slope_tile_c4", ShapeKind.Roof, BILATERAL, 1, 4, 0x0f),

    SLOPE_TILE_A1_SE(103, "architecturecraft.shape.slope_tile_a1_se", ShapeKind.Roof, BILATERAL, 1, 1, 0xcf, GLOW),
    SLOPE_TILE_A2_SE(104, "architecturecraft.shape.slope_tile_a2_se", ShapeKind.Roof, BILATERAL, 1, 3, 0x0f, GLOW),
    SLOPE_TILE_B1_SE(105, "architecturecraft.shape.slope_tile_b1_se", ShapeKind.Roof, BILATERAL, 1, 1, 0xff, GLOW),
    SLOPE_TILE_B2_SE(106, "architecturecraft.shape.slope_tile_b2_se", ShapeKind.Roof, BILATERAL, 1, 2, 0xcf, GLOW),
    SLOPE_TILE_B3_SE(107, "architecturecraft.shape.slope_tile_b3_se", ShapeKind.Roof, BILATERAL, 1, 3, 0x0f, GLOW),
    SLOPE_TILE_C1_SE(108, "architecturecraft.shape.slope_tile_c1_se", ShapeKind.Roof, BILATERAL, 1, 1, 0xff, GLOW),
    SLOPE_TILE_C2_SE(109, "architecturecraft.shape.slope_tile_c2_se", ShapeKind.Roof, BILATERAL, 1, 2, 0xcf, GLOW),
    SLOPE_TILE_C3_SE(110, "architecturecraft.shape.slope_tile_c3_se", ShapeKind.Roof, BILATERAL, 1, 3, 0x0f, GLOW),
    SLOPE_TILE_C4_SE(111, "architecturecraft.shape.slope_tile_c4_se", ShapeKind.Roof, BILATERAL, 1, 4, 0x0f, GLOW),

    ROOF_TILE_SE(112, "architecturecraft.shape.roof_tile_se", ShapeKind.Roof, BILATERAL, 1, 2, 0xcf, GLOW),
    SQUARE_SE(113, "architecturecraft.shape.square_se", ShapeKind.Model("square"), QUADRILATERAL, 1, 1, 0xff, GLOW),
    SLAB_SE(114, "architecturecraft.shape.slab_se", ShapeKind.Model("slab"), QUADRILATERAL, 1, 2, 0x0f, GLOW),
    ANGLED_ROOF_RIDGE(115, "architecturecraft.shape.angled_roof_ridge", ShapeKind.Model("angled_roof_ridge"), BILATERAL, 1, 4, 0x0f),
    DOUBLE_ROOF_TILE(116, "architecturecraft.shape.double_roof_tile", new DoubleRoofTile(), BILATERAL, 1, 2, 0xff),

    // Clean-room wall inspired by Industrial Renewal's wall_concrete.
    WALL(117, "architecturecraft.shape.wall", new Wall(), QUADRILATERAL, 1, 2, 0x0),
    FENCE(118, "architecturecraft.shape.fence", new Fence(), QUADRILATERAL, 1, 4, 0x0),

    // Siding: the roofing shapes mounted upright on walls. Same geometry and
    // profiles, but placement always stands them on their side.
    ROOF_TILE_SIDING(119, "architecturecraft.shape.roof_tile_siding", ShapeKind.Roof, BILATERAL, 1, 2, 0xcf, SIDING),
    SLOPE_TILE_A1_SIDING(120, "architecturecraft.shape.slope_tile_a1_siding", ShapeKind.Roof, BILATERAL, 1, 1, 0xcf, SIDING),
    SLOPE_TILE_A2_SIDING(121, "architecturecraft.shape.slope_tile_a2_siding", ShapeKind.Roof, BILATERAL, 1, 3, 0x0f, SIDING),
    SLOPE_TILE_B1_SIDING(122, "architecturecraft.shape.slope_tile_b1_siding", ShapeKind.Roof, BILATERAL, 1, 1, 0xff, SIDING),
    SLOPE_TILE_B2_SIDING(123, "architecturecraft.shape.slope_tile_b2_siding", ShapeKind.Roof, BILATERAL, 1, 2, 0xcf, SIDING),
    SLOPE_TILE_B3_SIDING(124, "architecturecraft.shape.slope_tile_b3_siding", ShapeKind.Roof, BILATERAL, 1, 3, 0x0f, SIDING),
    SLOPE_TILE_C1_SIDING(125, "architecturecraft.shape.slope_tile_c1_siding", ShapeKind.Roof, BILATERAL, 1, 1, 0xff, SIDING),
    SLOPE_TILE_C2_SIDING(126, "architecturecraft.shape.slope_tile_c2_siding", ShapeKind.Roof, BILATERAL, 1, 2, 0xcf, SIDING),
    SLOPE_TILE_C3_SIDING(127, "architecturecraft.shape.slope_tile_c3_siding", ShapeKind.Roof, BILATERAL, 1, 3, 0x0f, SIDING),
    SLOPE_TILE_C4_SIDING(128, "architecturecraft.shape.slope_tile_c4_siding", ShapeKind.Roof, BILATERAL, 1, 4, 0x0f, SIDING),

    PANELING(129, "architecturecraft.shape.paneling", new Paneling(), QUADRILATERAL, 1, 4, 0x0),
    DOUBLE_SIDING(130, "architecturecraft.shape.double_siding", new DoubleSiding(), QUADRILATERAL, 1, 1, 0xff),
    GREG_TECH(131, "architecturecraft.shape.greg_tech", new Logo(), QUADRILATERAL, 1, 1, 0x0),
    ;

    public static EnumShape[] values = values();
    public static boolean debugPlacement = false;
    protected static Map<Integer, EnumShape> idMap = new HashMap<Integer, EnumShape>();

    static {
        for (EnumShape s : values)
            idMap.put(s.id, s);
        mapSiding(ROOF_TILE_SIDING, ROOF_TILE);
        mapSiding(SLOPE_TILE_A1_SIDING, SLOPE_TILE_A1);
        mapSiding(SLOPE_TILE_A2_SIDING, SLOPE_TILE_A2);
        mapSiding(SLOPE_TILE_B1_SIDING, SLOPE_TILE_B1);
        mapSiding(SLOPE_TILE_B2_SIDING, SLOPE_TILE_B2);
        mapSiding(SLOPE_TILE_B3_SIDING, SLOPE_TILE_B3);
        mapSiding(SLOPE_TILE_C1_SIDING, SLOPE_TILE_C1);
        mapSiding(SLOPE_TILE_C2_SIDING, SLOPE_TILE_C2);
        mapSiding(SLOPE_TILE_C3_SIDING, SLOPE_TILE_C3);
        mapSiding(SLOPE_TILE_C4_SIDING, SLOPE_TILE_C4);
    }

    private static void mapSiding(EnumShape siding, EnumShape base) {
        siding.roofBase = base;
        siding.defaultSide = 2;
        siding.defaultTurn = 3;
    }

    public int id;
    public String translationKey;
    public ShapeKind kind;
    public EnumShapeSymmetry symmetry;
    public int materialUsed;
    public int itemsProduced;
    public int occlusionMask;
    public int flags;
    /**
     * The roofing shape this one renders as and connects to. Normally the
     * shape itself; for siding variants it is the upright-agnostic original.
     */
    public EnumShape roofBase = this;
    /** Atlas cell to draw in the sawbench menu. */
    public int iconId;
    /** Orientation written into item stacks, so the item renders like the placed shape. */
    public int defaultSide;
    public int defaultTurn;

//	Shape(int id, String title, ShapeKind kind, ShapeSymmetry sym, int used, int made, int occ) {
//		this(id, title, kind, sym, used, made, occ, null);
//	}
//
//	Shape(int id, String title, ShapeKind kind, ShapeSymmetry sym, int used, int made, int occ, String model)

    EnumShape(int id, String translationKey, ShapeKind kind, EnumShapeSymmetry sym, int used, int made, int occ) {
        this(id, translationKey, kind, sym, used, made, occ, 0);
    }

    EnumShape(int id, String translationKey, ShapeKind kind, EnumShapeSymmetry sym, int used, int made, int occ, int flags) {
        this.id = id;
        this.iconId = id;
        this.translationKey = translationKey;
        this.kind = kind;
        this.symmetry = sym;
        this.materialUsed = used;
        this.itemsProduced = made;
        this.occlusionMask = occ;
        this.flags = flags;
    }

//	protected void orientOnPlacement(EntityPlayer player, ShapeTE te, ShapeTE nte, EnumFacing face, Vector3 hit) {
//		if (te.shape.kind.orientOnPlacement(player, te, nte, face, hit))
//			return;
//		else
//			orientFromHitPosition(player, te, face, hit);
//	}

    public static EnumShape forId(int id) {
        EnumShape shape = idMap.get(id);
        if (shape == null)
            shape = ROOF_TILE;
        return shape;
    }

    public static int turnForPlacementHit(int side, Vector3 hit, EnumShapeSymmetry symmetry) {
        Vector3 h = Trans3.sideTurn(side, 0).ip(hit);
        return turnForPlacementHit(symmetry, h.x, h.z);
    }

    private static int turnForPlacementHit(EnumShapeSymmetry symmetry, double x, double z) {
        switch (symmetry) {
            case QUADRILATERAL: // All rotations are equivalent
                return 0;
            case BILATERAL: // Rotate according to nearest side
                if (abs(z) > abs(x))
                    return z < 0 ? 2 : 0;
                else
                    return x > 0 ? 1 : 3;
            case UNILATERAL: // Rotate according to nearest corner
                if (z > 0)
                    return x < 0 ? 0 : 1;
                else
                    return x > 0 ? 2 : 3;
            default:
                return 0;
        }
    }

    public void orientOnPlacement(EntityPlayer player, TileShape shape,
                                  BlockPos neighbourPos, IBlockState neighbourState, TileEntity neighbourTile, EnumFacing face, Vector3 hit) {
        if (this.isSiding() || !shape.getShape().kind.orientOnPlacement(player, shape, neighbourPos, neighbourState, neighbourTile, face, hit)) {
            this.orientFromHitPosition(player, shape, face, hit);
        }
    }

    protected void orientFromHitPosition(EntityPlayer player, TileShape shape, EnumFacing face, Vector3 hit) {
        int side, turn;
        if (this.isSiding()) {
            // Siding always stands upright: on a wall its face points out of
            // the wall, on the floor or ceiling towards the player.
            if (face.getAxis().isHorizontal())
                side = face.getOpposite().ordinal();
            else
                side = sideForUpDirection(player.getHorizontalFacing().getOpposite());
        } else {
            switch (face) {
                case UP:
                    side = this.rightSideUpSide();
                    break;
                case DOWN:
                    if (shape.getShape().kind.canPlaceUpsideDown())
                        side = this.upsideDownSide();
                    else
                        side = this.rightSideUpSide();
                    break;
                default:
                    if (player.isSneaking())
                        side = face.getOpposite().ordinal();
                    else if (hit.y > 0.0 && shape.getShape().kind.canPlaceUpsideDown())
                        side = this.upsideDownSide();
                    else
                        side = this.rightSideUpSide();
            }
        }
        // Siding is mounted flat against the wall: the quarter turn is what
        // brings the slanted face round to the front, so it is fixed rather
        // than taken from the hit point.
        turn = this.isSiding() ? 3 : turnForPlacementHit(side, hit, this.symmetry);
        if (debugPlacement && !shape.getWorld().isRemote) {
            ArchitectureLog.info("Shape.orientFromHitPosition: face {} global hit {}", face, hit);
            ArchitectureLog.info("Shape.orientFromHitPosition: side {} turn {} symmetry {}", side, turn, shape.getShape().symmetry);
        }
        shape.setSide(side);
        shape.setTurn(turn);
        if ((this.flags & PLACE_OFFSET) != 0) {
            shape.setOffsetX(this.offsetXForPlacementHit(side, turn, hit));
            if (debugPlacement && !shape.getWorld().isRemote)
                ArchitectureLog.info("Shape.orientFromHitPosition: kind = %s offsetX = %.3f\n", this.kind, shape.getOffsetX());
        }
    }

    public double offsetXForPlacementHit(int side, int turn, Vector3 hit) {
        Vector3 h = Trans3.sideTurn(side, turn).ip(hit);
        return this.signedPlacementOffsetX(h.x);
    }

    public double signedPlacementOffsetX(double sign) {
        double offx = this.kind.placementOffsetX();
        if (sign < 0)
            offx = -offx;
        return offx;
    }

    protected int rightSideUpSide() {
        if (this.isPlacedUnderneath())
            return 1;
        else
            return 0;
    }

    protected int upsideDownSide() {
        if (this.isPlacedUnderneath())
            return 0;
        else
            return 1;
    }

    public String getLocalizedShapeName() {
        return I18n.translateToLocalFormatted(this.translationKey);
    }

    protected boolean isPlacedUnderneath() {
        return (this.flags & PLACE_UNDERNEATH) != 0;
    }

    public boolean isCladding() {
        return this == CLADDING_SHEET;
    }

    public boolean isGlow() {
        return (this.flags & GLOW) != 0;
    }

    public boolean isSiding() {
        return (this.flags & SIDING) != 0;
    }

    /** The roofing shape whose geometry and profiles this shape uses. */
    public EnumShape getRoofBase() {
        return this.roofBase;
    }

    /**
     * True for shapes a mob is meant to walk on: slabs, stairs and the roof
     * family. Everything else is an obstacle as far as pathfinding goes, even
     * though most of them are not full cubes and would otherwise look walkable.
     */
    public boolean isWalkableSurface() {
        switch (this) {
            case SLAB:
            case SLAB_SE:
            case STAIRS:
            case STAIRS_OUTER_CORNER:
            case STAIRS_INNER_CORNER:
                return true;
            default:
                return this.kind instanceof ShapeKind.Roof;
        }
    }

    /**
     * Side index that makes the shape's local +Y point along {@code dir}.
     * The side index space is not the EnumFacing ordinal space.
     */
    protected static int sideForUpDirection(EnumFacing dir) {
        switch (dir) {
            case SOUTH:
                return 2;
            case NORTH:
                return 3;
            case EAST:
                return 4;
            case WEST:
                return 5;
            case DOWN:
                return 1;
            default:
                return 0;
        }
    }
}
