package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityLagMachineFilter {

    @Inject(method = "tryExtractRenderState", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void cullSpamBlockEntities(E blockEntity, float partialTicks, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, boolean isGloballyRendered, CallbackInfoReturnable<Boolean> cir) {
        if (!ZOptions.tileRender) return; // Linked to your custom toggle variable

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Proximity Filter: If you are more than 32 blocks away from the lag signs/skulls, completely ignore them
        double distanceSq = mc.player.distanceToSqr(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ());
        if (distanceSq > 1024.0) { // 32 blocks squared = 1024
            cir.cancel();
            return;
        }

        // Peripheral Vision Check: Drop rendering if the lag tiles are behind your eyes
        if (z$isTileOutsidePeripheralVision(mc, blockEntity.getBlockPos())) {
            cir.cancel();
        }
    }

    @Unique
    private boolean z$isTileOutsidePeripheralVision(Minecraft mc, net.minecraft.core.BlockPos pos) {
        assert mc.player != null;
        @SuppressWarnings("null")
        Vec3 cameraPos = mc.player.getEyePosition(1.0F);
        @SuppressWarnings("null")
        Vec3 lookVector = mc.player.getViewVector(1.0F);
        Vec3 targetVector = new Vec3(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z).normalize();

        return lookVector.dot(targetVector) < -0.30; // Keeps a safe peripheral vision padding window
    }
}