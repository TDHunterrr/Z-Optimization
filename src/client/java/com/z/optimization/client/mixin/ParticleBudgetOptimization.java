package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.particle.ParticleEngine; // Change to ParticleEngine if using Mojang mappings
import net.minecraft.core.particles.ParticleLimit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class) 
public abstract class ParticleBudgetOptimization {

    @Unique
    private int z$currentTickParticleCount = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void resetParticleCounter(CallbackInfo ci) {
        this.z$currentTickParticleCount = 0;
    }

    @Inject(method = "hasSpaceInParticleLimit", at = @At("HEAD"), cancellable = true)
    private void limitParticleSpam(ParticleLimit limit, CallbackInfoReturnable<Boolean> cir) {
        if (!ZOptions.particleBudget) return;

        this.z$currentTickParticleCount++;

        if (this.z$currentTickParticleCount > 150) {
            cir.setReturnValue(false);
        }
    }
}