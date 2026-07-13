package com.z.optimization.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public class TileRendering {

    @Inject(method = "hasLevel", at = @At("HEAD"), cancellable = true)
    private void restoreVisibilityInRadius(CallbackInfoReturnable<Boolean> cir) {
        if (ZOptions.tileRender) return;
        cir.setReturnValue(false); 
    }
}