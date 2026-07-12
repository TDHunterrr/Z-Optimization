package com.z.optimization.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ZOptions {
    public static boolean entityCulling = true;
    public static boolean aggressiveItemStacking = true;
    public static boolean particleBudget = true;
    public static boolean dynamicLightsOpt = true;
    public static boolean fluidRenderingOpt = true;
    public static boolean fastCrystals = true;
    public static boolean tileRender = true;
    public static double tileSimDistance = 2.0; // Default: 2 Chunk
    public static boolean forceUpdateTriggered = false;
    public static double entityRenderDistance = 5.0;
    public static boolean renderEntityShadows = true;
    public static boolean enableEntityPushing = false;
    public static boolean fastMath = true;

    private static final File CONFIG_FILE = new File(System.getProperty("user.dir"), "config/z-optimization.properties");
    // Change the type from int to double inside ZOptions.java

    public static void save() {
        try {
            Properties props = new Properties();
            props.setProperty("entityCulling", String.valueOf(entityCulling));
            props.setProperty("aggressiveItemStacking", String.valueOf(aggressiveItemStacking));
            props.setProperty("particleBudget", String.valueOf(particleBudget));
            props.setProperty("dynamicLightsOpt", String.valueOf(dynamicLightsOpt));
            props.setProperty("fluidRenderingOpt", String.valueOf(fluidRenderingOpt));
            props.setProperty("fastCrystals", String.valueOf(fastCrystals));
            props.setProperty("tileRender",String.valueOf(tileRender));
            props.setProperty("tileSimDistance", String.valueOf(tileSimDistance));
            props.setProperty("entityRenderDistance", String.valueOf(entityRenderDistance));
            props.setProperty("renderEntityShadows", String.valueOf(renderEntityShadows));
            props.setProperty("enableEntityPushing", String.valueOf(enableEntityPushing));
            props.setProperty("fastMath", String.valueOf(fastMath));

            if (!CONFIG_FILE.getParentFile().exists()) CONFIG_FILE.getParentFile().mkdirs();
            try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
                props.store(out, "Z+ Engine Config Configuration Settings");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }
        try {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
                props.load(in);
            }
            entityCulling = Boolean.parseBoolean(props.getProperty("entityCulling", "true"));
            aggressiveItemStacking = Boolean.parseBoolean(props.getProperty("aggressiveItemStacking", "true"));
            particleBudget = Boolean.parseBoolean(props.getProperty("particleBudget", "true"));
            dynamicLightsOpt = Boolean.parseBoolean(props.getProperty("dynamicLightsOpt", "true"));
            fluidRenderingOpt = Boolean.parseBoolean(props.getProperty("fluidRenderingOpt", "true"));
            fastCrystals = Boolean.parseBoolean(props.getProperty("fastCrystals", "true"));
            tileRender = Boolean.parseBoolean(props.getProperty("tileRender", "true"));
            tileSimDistance = Double.parseDouble(props.getProperty("tileSimDistance", "2.0"));
            entityRenderDistance = Double.parseDouble(props.getProperty("entityRenderDistance", "5.0"));
            renderEntityShadows = Boolean.parseBoolean(props.getProperty("renderEntityShadows", "true"));
            enableEntityPushing = Boolean.parseBoolean(props.getProperty("enableEntityPushing", "false"));
            fastMath = Boolean.parseBoolean(props.getProperty("fastMath", "true"));
        } catch (Exception e) {
            e.printStackTrace(); // Log trace but fall back automatically to memory defaults safely
        }
    }
}