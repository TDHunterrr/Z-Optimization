package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientEntityTrackerDisabler {

    @Unique private static String z$lastCoordKey = "";
    @Unique private static int z$clusterCount = 0;
    @Unique private static long z$lastTickTime = 0L;

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void interceptAndDiscardLagEntities(Entity entity, CallbackInfo ci) {
        if (!ZOptions.entityCulling) return;

        // Target the standard client-side lag machine utility objects
        if (entity instanceof ArmorStand || entity instanceof AbstractMinecart || entity instanceof ItemFrame) {
            long now = System.currentTimeMillis();
            if (now - z$lastTickTime > 50L) {
                z$clusterCount = 0;
                z$lastCoordKey = "";
                z$lastTickTime = now;
            }

            // Create a spatial grid key based on the block position
            String currentKey = entity.getBlockX() + "_" + entity.getBlockY() + "_" + entity.getBlockZ();

            if (currentKey.equals(z$lastCoordKey)) {
                z$clusterCount++;
                // HARD MEMORY LIMIT: If more than 8 utility entities try to load into the exact same block,
                // completely block them from being added to the client world instance entirely!
                if (z$clusterCount > 8) {
                    ci.cancel();
                }
            } else {
                z$lastCoordKey = currentKey;
                z$clusterCount = 1;
            }
        }
    }
}