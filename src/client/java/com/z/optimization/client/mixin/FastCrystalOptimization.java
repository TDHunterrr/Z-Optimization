package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCrystalRenderer.class)
public class FastCrystalOptimization {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;Lnet/minecraft/client/renderer/entity/state/EndCrystalRenderState;F)V", at = @At("RETURN"))
    private void optimizeCrystalVisuals(EndCrystal entity, EndCrystalRenderState state, float partialTicks, CallbackInfo ci) {

        if (ZOptions.crystalAnimation) return;

        state.ageInTicks = 0.0F;

    }
}
