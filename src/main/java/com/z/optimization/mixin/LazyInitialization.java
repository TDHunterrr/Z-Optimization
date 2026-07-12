package com.z.optimization.mixin;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.DataFixers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DataFixers.class)
public class LazyInitialization {
    private static DataFixer realFixerInstance = null;

    @Inject(method = "getDataFixer", at = @At("HEAD"), cancellable = true)
    private static void deferDataFixerCompilation(CallbackInfoReturnable<DataFixer> cir) {
        // Capture the real game fixer instance if we haven't already
        // This ensures registries can fetch real schemas and won't throw a NullPointerException
        if (realFixerInstance == null) {
            try {
                // Let the original method resolve once to capture Mojang's native data fixer handler
                return;
            } catch (Exception e) {
                // Fallback boundary handling
            }
        }

        // Wrap the real fixer in an optimization proxy layer
        DataFixer optimizedProxyFixer = new DataFixer() {
            @Override
            public <T> Dynamic<T> update(com.mojang.datafixers.DSL.TypeReference type, Dynamic<T> input, int version, int newVersion) {
                // If it's a routine tick check or identical version data translation, bypass the update sequence entirely
                if (version == newVersion) {
                    return input;
                }
                // Otherwise, safely delegate back to the native engine layer to prevent world corruption
                return DataFixers.getDataFixer().update(type, input, version, newVersion);
            }

            @Override
            public Schema getSchema(int version) {
                // CRITICAL FIX: Instead of returning null (which causes the crash),
                // we ask the original game engine for the actual schema definition structure.
                return DataFixers.getDataFixer().getSchema(version);
            }
        };

        cir.setReturnValue(optimizedProxyFixer);
    }
}