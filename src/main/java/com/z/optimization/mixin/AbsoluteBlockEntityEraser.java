package com.z.optimization.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.z.optimization.config.ZOptions;

@Mixin(BlockEntity.class)
public class AbsoluteBlockEntityEraser {

    @Unique
    private boolean z$isOutsideTrueCircleRadius(Level level, BlockPos blockPos) {

        if (level == null || level.players().isEmpty()) return false;

        // DIAMEATER SYSTEM FIX:
        // Setting value of 1.0 Chunk = 16 blocks total diameter.
        // Radius is Diameter / 2, which equals 8.0 blocks.
        double targetDiameterInBlocks = ZOptions.tileSimDistance * 16.0;
        double circleRadius = targetDiameterInBlocks / 2.0;
        double allowedDistanceSquared = circleRadius * circleRadius;

        // Cylindrical vertical allowance height so blocks right above/below you don't snap away
        double verticalMaxDistance = 32.0;

        for (Player player : level.players()) {
            BlockPos playerPos = player.blockPosition();

            // 1. Precise Vertical Check relative to Player Y + 1
            double adjustedPlayerY = playerPos.getY() + 1.0;
            double deltaY = Math.abs(adjustedPlayerY - blockPos.getY());
            if (deltaY > verticalMaxDistance) {
                continue;
            }

            // 2. TRUE GEOMETRIC CIRCLE MATH (Using raw block coordinates for a perfect round edge)
            double deltaX = playerPos.getX() - blockPos.getX();
            double deltaZ = playerPos.getZ() - blockPos.getZ();
            double horizontalDistanceSquared = (deltaX * deltaX) + (deltaZ * deltaZ);

            // If the block is inside the true mathematical circle, keep it visible!
            if (horizontalDistanceSquared <= allowedDistanceSquared) {
                return false;
            }
        }

        return true;
    }
    
    @Inject(method = "hasLevel", at = @At("HEAD"), cancellable = true)
    private void completelyErase(CallbackInfoReturnable<Boolean> cir) {
        BlockEntity self = (BlockEntity) (Object) this;
        Level level = self.getLevel();

        // Strictly execute on client-side rendering loops
        if (level != null && level.isClientSide()) {
            if (z$isOutsideTrueCircleRadius(level, self.getBlockPos())) {
                cir.setReturnValue(false);
            }
        }
    }
}