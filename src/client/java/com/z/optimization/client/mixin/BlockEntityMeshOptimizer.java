package com.z.optimization.client.mixin;

import com.z.optimization.config.ZOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityMeshOptimizer {

    @Unique private static Field z$cachedScreenField = null;
    @Unique private static boolean z$failedFieldLookup = false;

    @Inject(method = "tryExtractRenderState", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void optimizeTileMeshes(E blockEntity, float partialTicks, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, boolean isGloballyRendered, CallbackInfoReturnable<Boolean> cir) {
        if (ZOptions.tileRender) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // ZERO-OVERHEAD CACHED LOOKUP: Bypasses Yarn vs Mojang mapping naming conflicts completely
        if (!z$failedFieldLookup) {
            try {
                if (z$cachedScreenField == null) {
                    for (Field field : Minecraft.class.getDeclaredFields()) {
                        // Dynamically matches whatever name the Screen field has (screen, currentScreen, field_1234)
                        if (field.getType().getSimpleName().contains("Screen")) {
                            field.setAccessible(true);
                            z$cachedScreenField = field;
                            break;
                        }
                    }
                    if (z$cachedScreenField == null) z$failedFieldLookup = true;
                }

                if (z$cachedScreenField != null && z$cachedScreenField.get(mc) != null) {
                    cir.cancel(); // Drop rendering calculations while an active UI screen is open
                    return;
                }
            } catch (Exception e) {
                z$failedFieldLookup = true;
            }
        }

        // Peripheral View Culling
        @SuppressWarnings("null")
        Vec3 cameraPos = mc.player.getEyePosition(partialTicks);
        @SuppressWarnings("null")
        Vec3 lookVector = mc.player.getViewVector(partialTicks);
        Vec3 targetVector = new Vec3(
            blockEntity.getBlockPos().getX() - cameraPos.x,
            blockEntity.getBlockPos().getY() - cameraPos.y,
            blockEntity.getBlockPos().getZ() - cameraPos.z
        ).normalize();

        if (lookVector.dot(targetVector) < 0.0) { 
            cir.cancel();
        }
    }
}