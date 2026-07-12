package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(ClientLevel.class)
public class ClientSideTickItemMerger {

    @Inject(method = "tickNonPassenger", at = @At("HEAD"), cancellable = true)
    private void optimizeClientTicks(Entity entity, CallbackInfo ci) {
        if (entity == null || entity.isRemoved()) {
            return;
        }

        ClientLevel level = (ClientLevel)(Object)this;

        // 1. SIMPLE ENGLISH ITEM MERGER (No math symbols!)
        if (entity instanceof ItemEntity currentItem && ZOptions.aggressiveItemStacking) {
            if (currentItem.tickCount >= 40 && currentItem.tickCount % 20 == 0) {
                List<ItemEntity> nearbyItems = level.getEntitiesOfClass(
                        ItemEntity.class,
                        currentItem.getBoundingBox().inflate(2.0, 1.0, 2.0),
                        e -> e != currentItem && !e.isRemoved() && e.tickCount >= 40
                );

                for (ItemEntity otherItem : nearbyItems) {
                    if (ItemStack.isSameItem(currentItem.getItem(), otherItem.getItem())) {
                        int totalCount = currentItem.getItem().getCount() + otherItem.getItem().getCount();

                        ItemStack mergedStack = currentItem.getItem().copy();
                        mergedStack.setCount(totalCount);
                        currentItem.setItem(mergedStack);

                        // Plain text format builder
                        int fullStacks = totalCount / 64;
                        int remainder = totalCount % 64;

                        String label;
                        if (fullStacks > 0) {
                            label = fullStacks + " stacks, " + remainder;
                        } else {
                            label = remainder + " items";
                        }

                        currentItem.setCustomName(Component.literal(label));
                        currentItem.setCustomNameVisible(true);

                        otherItem.discard();
                    }
                }
            }
            return;
        }

        // 2. CLIENT-SIDE MOB CLUSTER FREEZER
        if (entity instanceof Mob currentMob) {
            if (currentMob.tickCount % 10 == 0) {
                List<Mob> crowdedCluster = level.getEntitiesOfClass(Mob.class, currentMob.getBoundingBox().inflate(1.0, 1.0, 1.0));
                if (crowdedCluster.size() > 15) {
                    ci.cancel();
                }
            }
            return;
        }

        // 3. CLIENT-SIDE BOAT LAG REDUCER
        if (entity instanceof Boat currentBoat) {
            if (currentBoat.getDeltaMovement().lengthSqr() < 0.001) {
                List<Boat> stackedBoats = level.getEntitiesOfClass(Boat.class, currentBoat.getBoundingBox().inflate(0.5, 0.5, 0.5));
                if (stackedBoats.size() > 5) {
                    ci.cancel();
                }
            }
        }
    }
}