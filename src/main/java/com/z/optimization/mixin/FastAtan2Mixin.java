package com.z.optimization.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mth.class)
public class FastAtan2Mixin {

    @Inject(method = "atan2", at = @At("HEAD"), cancellable = true)
    private static void optimizedAtan2(double y, double x, CallbackInfoReturnable<Double> cir) {
        if (!ZOptions.fastMath) return; // Hooking into your existing Fast Math button!

        // Famous Game-Dev Fast Atan2 Approximation
        // Bypasses heavy Java native CPU overhead for a massive speedup on entity look-calculations
        double r, angle;
        double abs_y = Math.abs(y) + 1e-10f; // Add tiny float to prevent divide-by-zero crashes

        if (x < 0.0) {
            r = (x + abs_y) / (abs_y - x);
            angle = (3.0 * Math.PI / 4.0);
        } else {
            r = (x - abs_y) / (x + abs_y);
            angle = (Math.PI / 4.0);
        }

        angle += (0.1963 * r * r - 0.9817) * r;

        if (y < 0.0) {
            cir.setReturnValue(-angle);
        } else {
            cir.setReturnValue(angle);
        }
    }
}