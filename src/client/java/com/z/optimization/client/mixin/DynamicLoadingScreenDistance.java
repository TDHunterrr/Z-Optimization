package com.z.optimization.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class DynamicLoadingScreenDistance {

    @Shadow public LocalPlayer player;

    @Unique private static boolean z$wasInPortal = false;
    @Unique private static int z$savedRenderDistance = -1;
    @Unique private static int z$savedSimulationDistance = -1;

    @Inject(method = "tick", at = @At("HEAD"))
    private void adjustDimensionsTicking(CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;
        
        // Safety checks to ensure we are actively in a level
        if (player == null || mc.options == null) return;

        // Condition check: Is the player currently transitioning or cooling down on a portal?
        if (player.isOnPortalCooldown()) {
            if (!z$wasInPortal) {
                // Step 1: Cache the original user settings before changing them
                z$savedRenderDistance = mc.options.renderDistance().get();
                z$savedSimulationDistance = mc.options.simulationDistance().get();
                
                // Step 2: Instantly drop the scaling metrics down to minimum (1 Chunk)
                mc.options.renderDistance().set(1);
                mc.options.simulationDistance().set(1);
                
                z$wasInPortal = true;
            }
        } else {
            // Step 3: Once portal cooldown is completely cleared, restore original user options safely
            if (z$wasInPortal) {
                if (z$savedRenderDistance != -1) {
                    mc.options.renderDistance().set(z$savedRenderDistance);
                }
                if (z$savedSimulationDistance != -1) {
                    mc.options.simulationDistance().set(z$savedSimulationDistance);
                }
                
                // Reset tracking markers
                z$wasInPortal = false;
                z$savedRenderDistance = -1;
                z$savedSimulationDistance = -1;
            }
        }
    }
}