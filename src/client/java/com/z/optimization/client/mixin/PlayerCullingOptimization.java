package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class PlayerCullingOptimization {

    // 1. Target extractRenderState
    // 2. Use Object for the 'S' (state) parameter to avoid import errors
    @Inject(method = "extractRenderState*", at = @At("HEAD"), cancellable = true)
    private <T extends LivingEntity> void cullHiddenPlayers(T entity, Object state, float partialTicks, CallbackInfo ci) {

        if (!ZOptions.entityCulling) return;

        if (entity instanceof Player player) {
            Minecraft mc = Minecraft.getInstance();
            if (player == mc.player) return; // Don't cull yourself

            // Camera check
            var cameraPos = mc.gameRenderer.mainCamera().position();

            // If they are further than 32 blocks (32*32 = 1024), kill the state extraction
            if (player.distanceToSqr(cameraPos) > 1024.0) {
                ci.cancel();
            }
        }
    }
}