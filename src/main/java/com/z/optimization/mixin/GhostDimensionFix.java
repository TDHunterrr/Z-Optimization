package com.z.optimization.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity; // Fixes missing Entity symbol
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class GhostDimensionFix {
    @Inject(method = "changeDimension", at = @At("HEAD"))
    private void forceSynchronousPacketFlush(ServerLevel targetWorld, CallbackInfoReturnable<Entity> cir) {
        ServerPlayer player = (ServerPlayer)(Object)this;
        player.removeAllEffects();

        // Direct cast fix for level retrieval in 26.2
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().chunkMap.move(player);
        }
    }
}