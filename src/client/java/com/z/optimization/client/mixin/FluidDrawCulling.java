package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidRenderer.class)
public class FluidDrawCulling {

    // FIXED: Adjusted parameters and replaced CallbackInfoReturnable with CallbackInfo to prevent descriptor mismatches
    @Inject(method = "tesselate", at = @At("HEAD"), cancellable = true)
    private void cullHiddenFluidDrawing(BlockAndTintGetter level, BlockPos blockPos, FluidRenderer.Output output, BlockState blockState, FluidState fluidState, CallbackInfo ci) {
        if (!ZOptions.fluidRenderingOpt) return;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        // Run the fast frustum boundary cone test
        if (z$isFluidOutsideDirectionalCone(client, blockPos)) {
            ci.cancel(); // FIXED: Uses cancel() for void methods instead of setting a return value
        }
    }

    @Unique
    private boolean z$isFluidOutsideDirectionalCone(Minecraft client, BlockPos pos) {
        // Fast skip safety: Keep immediate block configurations visible to prevent visual flashing
        @SuppressWarnings("null")
        double distSq = client.player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());
        if (distSq < 64.0) return false; 

        @SuppressWarnings("null")
        Vec3 cameraPos = client.player.getEyePosition(1.0F);
        @SuppressWarnings("null")
        Vec3 lookVector = client.player.getViewVector(1.0F);
        
        Vec3 targetVector = new Vec3(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z).normalize();
        
        double dotProduct = lookVector.dot(targetVector);

        // Safe peripheral clearance angle
        return dotProduct < -0.30;
    }
}