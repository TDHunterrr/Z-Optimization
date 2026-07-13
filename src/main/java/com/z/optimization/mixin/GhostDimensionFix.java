package com.z.optimization.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class GhostDimensionFix {
    @Inject(method = "hasChangedDimension", at = @At("HEAD"))
    private void forceSynchronousPacketFlush(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer)(Object)this;
        player.removeAllEffects();

        // Direct cast fix for level retrieval in 26.2
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().chunkMap.move(player);
        }
    }
}