package com.z.optimization.client.mixin;

import com.z.optimization.client.config.FastCrystalOptimizer; // Make sure this matches where you put the file!
import com.z.optimization.config.ZOptions; // Make sure this matches your config location!
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ClientConnectionMixin {

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"))
    private void onPacketSend(Packet<?> packet, CallbackInfo ci) {

        // 1. Check if the player turned the setting OFF in the config
        if (!ZOptions.fastCrystals) {
            return; // Do nothing, let vanilla Minecraft handle it
        }

        // 2. If it IS an attack packet, run your custom optimization logic
        if (packet instanceof ServerboundAttackPacket(int entityId)) {
            FastCrystalOptimizer.attemptCrystalDestruction(Minecraft.getInstance(), entityId);
        }
    }
}