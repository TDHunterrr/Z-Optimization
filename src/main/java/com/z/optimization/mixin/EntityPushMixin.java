package com.z.optimization.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class EntityPushMixin {

    // This intercepts the physics collision loop when two entities touch
    @Inject(method = "doPush", at = @At("HEAD"), cancellable = true)
    private void onPush(Entity entity, CallbackInfo ci) {
        if (!ZOptions.enableEntityPushing) {
            // Cancel the collision physics entirely.
            // Players will seamlessly walk right through massive crowds without dropping a single frame!
            ci.cancel();
        }
    }
}