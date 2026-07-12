package com.z.optimization.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mth.class)
public class FastMathEngineMixin {

    // 1. Hardware Accelerated Sine
    @Inject(method = "sin", at = @At("HEAD"), cancellable = true)
    private static void fastSin(double i, CallbackInfoReturnable<Float> cir) {
        if (ZOptions.fastMath) { // Assumes you named your variable 'fastMath'
            cir.setReturnValue((float) Math.sin(i));
        }
    }

    // 2. Hardware Accelerated Cosine
    @Inject(method = "cos", at = @At("HEAD"), cancellable = true)
    private static void fastCos(double i, CallbackInfoReturnable<Float> cir) {
        if (ZOptions.fastMath) {
            cir.setReturnValue((float) Math.cos(i));
        }
    }
}