package com.z.optimization.client;

import org.jspecify.annotations.NonNull;

import com.z.optimization.config.ZOptions;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphicsExtractor; // Using the correct 26.2 graphic extractor type
import net.minecraft.network.chat.Component;

public class ZOptimizationClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ZOptions.load(); // Reads saved parameters right as the game loads up
    }

    public static Screen getConfigScreen(Screen parent) {
        return new Screen(Component.literal("Z+ Engine Optimization Settings")) {
            
            @Override
            protected void init() {
                int columnWidth = 180;
                int rowHeight = 24;
                int leftColX = this.width / 2 - columnWidth - 10;
                int rightColX = this.width / 2 + 10;
                int startY = this.height / 5;

                // --- COLUMN 1: World & Entity Tweaks ---
                this.addRenderableWidget(Button.builder(
                    Component.literal("Entity Culling: " + (ZOptions.entityCulling ? "ON" : "OFF")),
                    button -> {
                        ZOptions.entityCulling = !ZOptions.entityCulling;
                        button.setMessage(Component.literal("Entity Culling: " + (ZOptions.entityCulling ? "ON" : "OFF")));
                    }
                ).bounds(leftColX, startY, columnWidth, rowHeight).build());

                this.addRenderableWidget(Button.builder(
                    Component.literal("Item Merger: " + (ZOptions.aggressiveItemStacking ? "ON" : "OFF")),
                    button -> {
                        ZOptions.aggressiveItemStacking = !ZOptions.aggressiveItemStacking;
                        button.setMessage(Component.literal("Item Merger: " + (ZOptions.aggressiveItemStacking ? "ON" : "OFF")));
                    }
                ).bounds(leftColX, startY + (rowHeight + 4), columnWidth, rowHeight).build());

                this.addRenderableWidget(Button.builder(
                    Component.literal("Particle Optimizer: " + (ZOptions.particleBudget ? "ON" : "OFF")),
                    button -> {
                        ZOptions.particleBudget = !ZOptions.particleBudget;
                        button.setMessage(Component.literal("Particle Optimizer: " + (ZOptions.particleBudget ? "ON" : "OFF")));
                    }
                ).bounds(leftColX, startY + (rowHeight + 4) * 2, columnWidth, rowHeight).build());

                this.addRenderableWidget(Button.builder(
                    Component.literal("Dynamic Lights: " + (ZOptions.dynamicLightsOpt ? "ON" : "OFF")),
                    button -> {
                        ZOptions.dynamicLightsOpt = !ZOptions.dynamicLightsOpt;
                        button.setMessage(Component.literal("Dynamic Lights: " + (ZOptions.dynamicLightsOpt ? "ON" : "OFF")));
                    }
                ).bounds(leftColX, startY + (rowHeight + 4) * 3, columnWidth, rowHeight).build());

                this.addRenderableWidget(Button.builder(
                    Component.literal("Fluid Optimization: " + (ZOptions.fluidRenderingOpt ? "ON" : "OFF")),
                    button -> {
                        ZOptions.fluidRenderingOpt = !ZOptions.fluidRenderingOpt;
                        button.setMessage(Component.literal("Fluid Optimization: " + (ZOptions.fluidRenderingOpt ? "ON" : "OFF")));
                    }
                ).bounds(leftColX, startY + (rowHeight + 4) * 4, columnWidth, rowHeight).build());

                // --- COLUMN 2: Fast & Enhanced Rendering ---
                this.addRenderableWidget(Button.builder(
                    Component.literal("Fast Crystals: " + (ZOptions.fastCrystals ? "ON" : "OFF")),
                    button -> {
                        ZOptions.fastCrystals = !ZOptions.fastCrystals;
                        button.setMessage(Component.literal("Fast Crystals: " + (ZOptions.fastCrystals ? "ON" : "OFF")));
                    }
                ).bounds(rightColX, startY, columnWidth, rowHeight).build());

                // --- CONTROL ELEMENT: Done Button ---
                this.addRenderableWidget(Button.builder(
                    Component.translatable("gui.done"),
                    button -> {
                        ZOptions.save(); // CRITICAL: This saves settings to your disk storage folder!
                        this.minecraft.setScreenAndShow(parent);
                    }
                ).bounds(this.width / 2 - 100, startY + (rowHeight + 4) * 5 + 10, 200, 20).build());

                // --- CONTROL ELEMENT: Done Button ---
                this.addRenderableWidget(Button.builder(
                    Component.translatable("gui.done"),
                    button -> this.minecraft.setScreenAndShow(parent)
                ).bounds(this.width / 2 - 100, startY + (rowHeight + 4) * 5 + 10, 200, 20).build());
            }

            // FIXED: Corrected parameter signature matching the 26.2 GuiGraphicsExtractor setup
            @Override
            public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
                super.extractRenderState(graphics, mouseX, mouseY, delta);
                graphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
            }
        };
    }

    // FIXED: Added safe null-checking to clean up the Eclipse/Loom null type safety warning
    public static Object provideRawConfigScreen(Object parentScreen) {
        if (parentScreen instanceof Screen screen) {
            return getConfigScreen(screen);
        }
        return null;
    }
}