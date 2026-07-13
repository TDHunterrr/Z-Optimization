package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Look at this! A normal, public class. No red lines, no hidden targets!
@Mixin(TextureAtlas.class)
public class GlobalAnimationThrottle {

    @Unique
    private static int z$globalTickCounter = 0;

    /**
     * This controls ALL block animations globally (water, lava, fire, portals).
     * Instead of checking 100,000 water blocks, we just pause the master clock.
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void throttleAllBlockAnimations(CallbackInfo ci) {
        if (!ZOptions.fluidRenderingOpt) return;

        z$globalTickCounter++;

        // If it's not the 4th tick, cancel the master update entirely.
        if (z$globalTickCounter % 4 != 0) {
            ci.cancel();
        }
    }
}