package com.z.optimization.client;

import com.z.optimization.config.ZOptions;
import com.z.optimization.client.config.ZConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ZOptimizationClient implements ClientModInitializer {

    private static KeyMapping configKeyBinding;

    @Override
    public void onInitializeClient() {
        ZOptions.load();

        // 1. Register the 'Z' KeyBinding
        configKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.zoptimization.config",
                GLFW.GLFW_KEY_Z,
                KeyMapping.Category.register(Identifier.parse("category.zoptimization"))
        ));

        // 2. Listen for the key press
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (configKeyBinding.consumeClick()) {

                // Use the exact screen method your 26.2 environment expects!
                // (If client.gui.screen() is red, delete the dot, type a dot '.', and let autocomplete find it!)

                if (client.gui.screen() == null) {
                    client.setScreenAndShow(ZConfigScreen.create(null));
                }
            }
        });
    }
}
