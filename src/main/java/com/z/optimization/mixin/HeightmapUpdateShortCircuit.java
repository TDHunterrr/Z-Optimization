package com.z.optimization.mixin;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Heightmap.class)
public class HeightmapUpdateShortCircuit {
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void bypassAirHeightmapCalculations(int localX, int localY, int localZ, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.isAir()) {
            cir.setReturnValue(false);
        }
    }
}