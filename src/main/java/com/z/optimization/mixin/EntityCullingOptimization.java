package com.z.optimization.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.vehicle.boat.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class, ArmorStand.class, Boat.class})
public class EntityCullingOptimization {

    @Inject(method = "shouldRenderAtSqrDistance", at = @At("HEAD"), cancellable = true)
    private void onShouldRenderDistanceCheck(double distance, CallbackInfoReturnable<Boolean> cir) {
        if (!ZOptions.entityCulling) return;
        
        // NONE Option Handling: If the slider is set to 0 chunks, turn off entity rendering completely!
        if (ZOptions.entityRenderDistance == 0) {
            cir.setReturnValue(false);
            return;
        }
        
        // Convert dynamic configured Chunks value directly into standard linear block measurements
        double blockDistance = (ZOptions.entityRenderDistance * 16.0) / 2;
        double maxAllowedDistanceSqr = blockDistance * blockDistance;

        if (distance > maxAllowedDistanceSqr) {
            cir.setReturnValue(false);
        }
    }
}