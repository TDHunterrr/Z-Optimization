package com.z.optimization.client.mixin;

import net.minecraft.client.renderer.texture.TextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureManager.class)
public class TextureMipmapMemoryLeakFix {
    @Inject(method = "tick", at = @At("HEAD"))
    private void clearLeakingGraphicsPools(CallbackInfo ci) {
        org.lwjgl.opengl.GL11.glFlush();
    }
}