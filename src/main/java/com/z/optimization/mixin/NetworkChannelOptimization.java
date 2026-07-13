package com.z.optimization.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class NetworkChannelOptimization {

    @Unique private static int z$packetBurstCounter = 0;
    @Unique private static long z$lastPacketTime = 0L;

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void throttleOutboundPacketSpam(Packet<?> packet, CallbackInfo ci) {
        long currentTime = System.currentTimeMillis();
        
        // Reset pool counters every 50ms window loop
        if (currentTime - z$lastPacketTime > 50L) {
            z$packetBurstCounter = 0;
            z$lastPacketTime = currentTime;
        }

        z$packetBurstCounter++;

        // PACKET BURST GUARD: If a loop machine tries to force your client to send 
        // more than 120 packets per tick window (like an active auto-clicker or packet loop),
        // throttle the excess packets to stabilize your ping and keep you from getting kicked!
        if (z$packetBurstCounter > 120) {
            String id = packet.getClass().getSimpleName();
            if (id.contains("ServerboundPlayerInputPacket") || id.contains("ServerboundMovePlayerPacket")) {
                ci.cancel(); // Drop redundant movement updates safely
            }
        }
    }
}