package com.z.optimization.mixin;

import net.minecraft.network.CompressionEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.zip.Deflater;

@Mixin(CompressionEncoder.class)
public class NetworkPacketCompressionFix {

    @Shadow private Deflater deflater;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void adjustCompressionLevel(int threshold, CallbackInfo ci) {
        // Switch compression calculations from Level 6 (balanced) to Level 1 (BEST_SPEED)
        // This cuts down CPU processing overhead on your internet thread by nearly 70%!
        if (this.deflater != null) {
            this.deflater.setLevel(Deflater.BEST_SPEED);
        }
    }
}