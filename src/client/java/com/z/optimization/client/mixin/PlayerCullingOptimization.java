package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// FIX: Target LivingEntityRenderer because PlayerRenderer was removed in modern versions
@Mixin(LivingEntityRenderer.class)
public class PlayerCullingOptimization {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cullHiddenPlayers(LivingEntity entity, float f, float g, PoseStack poseStack, MultiNoiseBiomeSource multiBufferSource, int i, CallbackInfo ci) {
        // Only run if our global entity culling switch is enabled in ModMenu
        if (ZOptions.entityCulling) {
            
            // Explicitly filter for Player characters to replace old class targets
            if (entity instanceof Player player) {
                Minecraft mc = Minecraft.getInstance();
                
                // Never cull yourself (the local player perspective)
                if (player == mc.player) return;

                if (mc.gameRenderer != null && mc.gameRenderer.mainCamera() != null) {
                    var cameraPos = mc.gameRenderer.mainCamera().position();

                    // Calculate distance to other multiplayer entities
                    double dx = Math.abs(player.getX() - cameraPos.x);
                    double dy = Math.abs(player.getY() - cameraPos.y);
                    double dz = Math.abs(player.getZ() - cameraPos.z);

                    // Aggressive Player Culling Zone: If an opponent/player is further than 32 blocks away,
                    // stop executing their high-polygon models, armor layer attachments, and cape calculations.
                    if (dx > 32.0 || dy > 32.0 || dz > 32.0) {
                        ci.cancel(); // Kills the multi-layered rendering step for this player model instantly
                    }
                }
            }
        }
    }
}