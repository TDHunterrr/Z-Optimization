package com.z.optimization.mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(Level.class)
public class CollisionLoopOptimization {
    @Inject(method = "getBlockCollisions", at = @At("HEAD"), cancellable = true)
    private void shortCircuitAirCollisions(Entity entity, AABB box, CallbackInfoReturnable<List<?>> cir) {
        if (entity != null && entity.getY() > 320.0) {
            cir.setReturnValue(List.of());
        }
    }
}