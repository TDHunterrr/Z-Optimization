package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityShadowMixin {

    @Inject(method = "getShadowStrength", at = @At("HEAD"), cancellable = true)
    private void killShadowRadius(CallbackInfoReturnable<Float> cir) {
        if (!ZOptions.renderEntityShadows) {
            cir.setReturnValue(0.0F);
        }
    }
}