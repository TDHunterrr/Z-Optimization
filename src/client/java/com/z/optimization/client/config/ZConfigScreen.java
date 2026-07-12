package com.z.optimization.client.config;

import com.z.optimization.config.ZOptions;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ZConfigScreen {

    public static Screen create(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            .title(Component.literal("Z+ Optimization"))
            
            // --- ALL SETTINGS IN ONE CATEGORY ---
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("Settings"))
                
                // --- TICK BOXES (Clean Sodium-style Checkboxes) ---
                .option(Option.<Boolean>createBuilder()
                    .name(Component.literal("Entity Culling"))
                    .description(OptionDescription.of(Component.literal("Stops rendering entities that are obscured by blocks, heavily improving FPS in crowded areas.")))
                    .binding(true, () -> ZOptions.entityCulling, newVal -> ZOptions.entityCulling = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                    
                .option(Option.<Boolean>createBuilder()
                    .name(Component.literal("Item Stacking"))
                    .description(OptionDescription.of(Component.literal("Aggressively merges dropped items on the ground to reduce rendering and server processing load.")))
                    .binding(false, () -> ZOptions.aggressiveItemStacking, newVal -> ZOptions.aggressiveItemStacking = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                    
                .option(Option.<Boolean>createBuilder()
                    .name(Component.literal("Particle Optimizer"))
                    .description(OptionDescription.of(Component.literal("Reduces the number of rendered particles in intense scenes to maintain a stable frame rate.")))
                    .binding(false, () -> ZOptions.particleBudget, newVal -> ZOptions.particleBudget = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                    
                .option(Option.<Boolean>createBuilder()
                    .name(Component.literal("Dynamic Lights Engine"))
                    .description(OptionDescription.of(Component.literal("Enables fast, real-time lighting updates for glowing held items and moving entities.")))
                    .binding(false, () -> ZOptions.dynamicLightsOpt, newVal -> ZOptions.dynamicLightsOpt = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                
                .option(Option.<Boolean>createBuilder()
                    .name(Component.literal("Fluid Rendering Matrix"))
                    .description(OptionDescription.of(Component.literal("Optimizes how water and lava are drawn, improving performance around oceans and large lava lakes.")))
                    .binding(false, () -> ZOptions.fluidRenderingOpt, newVal -> ZOptions.fluidRenderingOpt = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                    
                .option(Option.<Boolean>createBuilder()
                    .name(Component.literal("Fast End Crystals"))
                    .description(OptionDescription.of(Component.literal("Simplifies the rendering math for End Crystals to prevent lag spikes during combat.")))
                    .binding(false, () -> ZOptions.fastCrystals, newVal -> ZOptions.fastCrystals = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                    
                .option(Option.<Boolean>createBuilder()
                    .name(Component.literal("Render Tiles"))
                    .description(OptionDescription.of(Component.literal("Toggles the rendering of complex block entities like chests, signs, and banners.")))
                    .binding(true, () -> ZOptions.tileRender, newVal -> ZOptions.tileRender = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())

                // --- SLIDERS ---
                .option(Option.<Integer>createBuilder()
                    .name(Component.literal("Tile Simulation Distance"))
                    .description(OptionDescription.of(Component.literal("Controls how far away block entities (tiles) will be processed and rendered. Lower values improve performance.")))
                    .binding(12, () -> (int) ZOptions.tileSimDistance, newVal -> ZOptions.tileSimDistance = (double) newVal)
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(1, 32)
                        .step(1)
                        .formatValue(val -> Component.literal(val + " Chunks")))
                    .build())

                .option(Option.<Integer>createBuilder()
                    .name(Component.literal("Entity Render Distance"))
                    .description(OptionDescription.of(Component.literal("Controls the maximum distance at which entities are drawn. Lowering this can massively boost frame rates.")))
                    .binding(12, () -> (int) ZOptions.entityRenderDistance, newVal -> ZOptions.entityRenderDistance = newVal)
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(0, 32)
                        .step(1)
                        .formatValue(val -> Component.literal(val == 0 ? "NONE" : val + " Chunks")))
                    .build())

                    // Entity Shadows Button
                    .option(Option.<Boolean>createBuilder()
                            .name(Component.literal("Render Entity Shadows"))
                            .description(OptionDescription.of(Component.literal("Draws a circular shadow under players and mobs. Turn OFF for a massive FPS boost in crowded areas.")))
                            .binding(false, () -> ZOptions.renderEntityShadows, newVal -> ZOptions.renderEntityShadows = newVal)
                            .controller(TickBoxControllerBuilder::create)
                            .build())

// Entity Collision Button
                    .option(Option.<Boolean>createBuilder()
                            .name(Component.literal("Entity Push Collisions"))
                            .description(OptionDescription.of(Component.literal("Calculates physics when walking into other players. Turn OFF to walk through crowds without lagging.")))
                            .binding(false, () -> ZOptions.enableEntityPushing, newVal -> ZOptions.enableEntityPushing = newVal)
                            .controller(TickBoxControllerBuilder::create)
                            .build())
                    .option(Option.<Boolean>createBuilder()
                            .name(Component.literal("Fast Math"))
                            .description(OptionDescription.of(Component.literal("Improves calculations for game")))
                            .binding(false, () -> ZOptions.fastMath, newVal -> ZOptions.fastMath = newVal)
                            .controller(TickBoxControllerBuilder::create)
                            .build())
                .build())
            
            .save(ZOptions::save) 
            .build()
            .generateScreen(parent);
    }
}