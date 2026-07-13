package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class RedstoneWireLoopOptimization {

    // FIXED: Replaced Object with net.minecraft.world.level.BlockGetter to perfectly match the method descriptor
    @Inject(method = "blockChanged", at = @At("HEAD"), cancellable = true)
    private void throttleRedstoneRenderSpam(BlockGetter blockGetter, BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci) {
        if (ZOptions.tileRender) return; 

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || newState == null) return;

        // Verify if the block update is a fast-flickering Redstone Wire or Redstone Lamp
        String blockId = newState.getBlock().getDescriptionId();
        if (blockId.contains("redstone_wire") || blockId.contains("redstone_lamp")) {
            
            // Distance check to stop remote lag machines from hammering your rendering thread
            @SuppressWarnings("null")
            double distanceSq = mc.player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());
            if (distanceSq > 256.0) { // 16 blocks squared
                ci.cancel(); 
            }
        }
    }
}