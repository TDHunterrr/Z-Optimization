package com.z.optimization.mixin;

import net.minecraft.world.level.lighting.SkyLightSectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkyLightSectionStorage.class)
public class LightUpdateShortCircuit {
    @Inject(method = "hasLight", at = @At("HEAD"), cancellable = true)
    private void bypassBedrockLightScans(long sectionPos, CallbackInfoReturnable<Boolean> cir) {
        long yCoordinate = (sectionPos >> 42) & 0xFFFFF;
        if (yCoordinate < 0) {
            cir.setReturnValue(false);
        }
    }
}