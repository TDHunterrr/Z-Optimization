package com.z.optimization.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// ONLY target the base Entity class. This automatically applies to Boats, ArmorStands, etc.
@Mixin(Entity.class)
public class EntityCullingOptimization {

    @Inject(method = "shouldRenderAtSqrDistance", at = @At("HEAD"), cancellable = true)
    private void onShouldRenderDistanceCheck(double distance, CallbackInfoReturnable<Boolean> cir) {
        if (!ZOptions.entityCulling) return;

        // NONE Option Handling: If the slider is set to 0 chunks, turn off entity rendering
        if (ZOptions.entityRenderDistance == 0) {
            cir.setReturnValue(false);
            return;
        }

        // Convert chunks to blocks (1 chunk = 16 blocks)
        double blockDistance = ZOptions.entityRenderDistance * 16.0;
        double maxAllowedDistanceSqr = blockDistance * blockDistance;

        // If the entity is further than the slider allows, stop rendering it
        if (distance > maxAllowedDistanceSqr) {
            cir.setReturnValue(false);
        }
    }
}