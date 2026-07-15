package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer; // Changed target class
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Target the base class where shouldRender actually exists!
@Mixin(EntityRenderer.class)
public class PlayerCullingOptimization {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void cullHiddenPlayers(Entity entity, Frustum culler, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {

        if (!ZOptions.entityCulling) return;

        // Since we are in the base class, we just check if the entity is a player
        if (entity instanceof Player player) {
            Minecraft mc = Minecraft.getInstance();
            if (player == mc.player) return; // Don't cull yourself

            // Camera check
            var cameraPos = mc.gameRenderer.mainCamera().position();

            // If they are further than 32 blocks (32*32 = 1024), kill the render completely
            if (player.distanceToSqr(cameraPos) > 1024.0) {
                cir.setReturnValue(false); // Game skips extracting AND drawing!
            }
        }
    }
}