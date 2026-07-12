package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.renderer.entity.ItemEntityRenderer")
public class ClientEntityPacketMerger {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void fixItemMergerRendering(ItemEntity entity, float f, float g, Object poseStack, Object multiBufferSource, int i, CallbackInfo ci) {
        if (ZOptions.aggressiveItemStacking) {
            // GLITCH FIX: Stop calling entity.discard() or ci.cancel() on every single item!
            // If the item has been stacked into a master pile, only cancel rendering if it's marked as a sub-stack.
            if (entity.isRemoved()) {
                ci.cancel();
            }
        }
    }
}