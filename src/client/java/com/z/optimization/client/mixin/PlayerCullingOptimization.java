package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class PlayerCullingOptimization {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void cullHiddenPlayers(Entity entity, Frustum culler, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {

        if (!ZOptions.entityCulling) return;

        if (entity instanceof Player player) {
            Minecraft mc = Minecraft.getInstance();
            if (player == mc.player) return; // Don't cull yourself

            // FIX: Use the camX, camY, camZ parameters provided by the method arguments!
            // This completely eliminates cross-version GameRenderer crashes.
            if (player.distanceToSqr(camX, camY, camZ) > 1024.0) {
                cir.setReturnValue(false); // Tell the engine to skip drawing this player
            }
        }
    }
}
