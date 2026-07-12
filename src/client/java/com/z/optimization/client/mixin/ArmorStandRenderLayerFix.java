package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.world.entity.decoration.ArmorStand;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandRenderer.class)
public class ArmorStandRenderLayerFix {

    @Unique private static String z$lastBlockKey = "";
    @Unique private static int z$renderCountPerBlock = 0;
    @Unique private static int z$lastFrameId = -1;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void hardCapArmorStandSpam(ArmorStand entity, float entityYaw, float partialTicks, PoseStack poseStack, Object buffer, int packedLight, CallbackInfo ci) {
        if (!ZOptions.entityCulling) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 1. FRAME COUNTER TICK RESET
        int currentFrame = mc.getFps(); 
        if (currentFrame != z$lastFrameId) {
            z$lastFrameId = currentFrame;
            z$renderCountPerBlock = 0;
            z$lastBlockKey = "";
        }

        // 2. O(1) COMPLEXITY TRACKER (Zero processing delay)
        // Generates a simple text key based on the exact block location (e.g., "X102_Y64_Z-45")
        String currentBlockKey = entity.getBlockX() + "_" + entity.getBlockY() + "_" + entity.getBlockZ();

        if (currentBlockKey.equals(z$lastBlockKey)) {
            z$renderCountPerBlock++;
            // If more than 4 armor stands are stacked in this exact block coordinate,
            // drop the remaining 496 instantly without running ANY entity search logic!
            if (z$renderCountPerBlock > 4) {
                ci.cancel();
                return;
            }
        } else {
            z$lastBlockKey = currentBlockKey;
            z$renderCountPerBlock = 1;
        }
    }
}
