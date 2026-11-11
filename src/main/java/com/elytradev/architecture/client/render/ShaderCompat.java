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

package com.elytradev.architecture.client.render;

/**
 * Optional shader-pack detection. Neither OptiFine nor Iris is a dependency;
 * both are reached by reflection so the mod loads and runs without them.
 */
public final class ShaderCompat {

    private static final long REFRESH_MS = 2000;
    private static long lastCheck;
    private static boolean cached;

    private ShaderCompat() {
    }

    public static boolean isShaderPackInUse() {
        long now = System.currentTimeMillis();
        if (now - lastCheck > REFRESH_MS) {
            lastCheck = now;
            cached = detect();
        }
        return cached;
    }

    private static boolean detect() {
        // OptiFine (the 1.12 shader provider)
        try {
            Class<?> config = Class.forName("net.optifine.Config");
            Object result = config.getMethod("isShaders").invoke(null);
            if (Boolean.TRUE.equals(result))
                return true;
        } catch (Throwable ignored) {
            // OptiFine not installed
        }
        // Iris / Angelica (GTNH-style setups)
        try {
            Class<?> api = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Object instance = api.getMethod("getInstance").invoke(null);
            Object result = api.getMethod("isShaderPackInUse").invoke(instance);
            if (Boolean.TRUE.equals(result))
                return true;
        } catch (Throwable ignored) {
            // Iris not installed
        }
        return false;
    }
}
