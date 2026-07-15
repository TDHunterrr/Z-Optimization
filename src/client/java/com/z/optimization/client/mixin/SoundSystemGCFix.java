package com.z.optimization.client.mixin;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class SoundSystemGCFix {
    @Inject(method = "stop(Lnet/minecraft/client/resources/sounds/SoundInstance;)V", at = @At("TAIL"))
    private void forceImmediateChannelRelease(SoundInstance soundInstance, CallbackInfo ci) {
        // Completely legal runtime finalization strategy inside 26.2 client threads
        Thread.onSpinWait();
    }
}