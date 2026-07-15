package com.z.optimization.client;

import com.z.optimization.config.ZOptions;
import com.z.optimization.client.config.ZConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen; // Required for Reflection matching
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ZOptimizationClient implements ClientModInitializer {

    private static KeyMapping configKeyBinding;

    @Override
    public void onInitializeClient() {
        ZOptions.load();

        configKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.zoptimization.config",
                GLFW.GLFW_KEY_Z,
                KeyMapping.Category.register(Identifier.parse("category.zoptimization"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (configKeyBinding.consumeClick()) {

                // Use our custom Reflection method to check the screen state!
                if (isScreenNull(client)) {
                    client.setScreenAndShow(ZConfigScreen.create(null));
                }

            }
        });
    }

    /**
     * Uses Java Reflection to dynamically check if a screen is open.
     * Adapts automatically to 26.1 (Fields) and 26.2 (Methods) while bypassing obfuscation!
     */
    private boolean isScreenNull(Minecraft client) {
        try {
            // Attempt 1: The 26.1 Method (Look for a direct Screen field)
            for (Field field : client.getClass().getDeclaredFields()) {
                if (field.getType() == Screen.class) {
                    field.setAccessible(true); // Bypass private/protected visibility
                    return field.get(client) == null;
                }
            }

            // Attempt 2: The 26.2 Method (Look for gui.screen())
            for (Method method : client.getClass().getMethods()) {
                // Find the getter that returns the Gui class
                if (method.getReturnType().getSimpleName().equals("Gui") && method.getParameterCount() == 0) {
                    Object guiInstance = method.invoke(client);

                    if (guiInstance != null) {
                        // Find the method inside the Gui class that returns a Screen
                        for (Method guiMethod : guiInstance.getClass().getMethods()) {
                            if (guiMethod.getReturnType() == Screen.class && guiMethod.getParameterCount() == 0) {
                                return guiMethod.invoke(guiInstance) == null;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[Z+ Optimization] Reflection failed to detect screen state.");
        }

        // If all else fails, assume no screen is open so the user can still open the config menu
        return true;
    }
}
