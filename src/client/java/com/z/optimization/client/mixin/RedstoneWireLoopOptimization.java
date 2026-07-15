package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 1. CRITICAL CHANGE: We now target ClientLevel instead of LevelRenderer
@Mixin(ClientLevel.class)
public class RedstoneWireLoopOptimization {

    // 2. We inject into sendBlockUpdated, which is what notifies the renderer of block changes
    @Inject(method = "sendBlockUpdated", at = @At("HEAD"), cancellable = true)
    private void throttleRedstoneRenderSpam(BlockPos pos, BlockState old, BlockState current, int updateFlags, CallbackInfo ci) {
        if (!ZOptions.tileRender) return; // Linked to your custom toggle variable

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || current == null) return;

        // Verify if the block update is a fast-flickering Redstone Wire or Redstone Lamp
        String blockId = current.getBlock().getDescriptionId();
        if (blockId.contains("redstone_wire") || blockId.contains("redstone_lamp")) {

            // Distance check to stop remote lag machines from hammering your rendering thread
            @SuppressWarnings("null")
            double distanceSq = mc.player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());
            if (distanceSq > 256.0) { // 16 blocks squared
                ci.cancel(); // Canceling here means the chunk will NOT be visually rebuilt
            }
        }
    }
}
