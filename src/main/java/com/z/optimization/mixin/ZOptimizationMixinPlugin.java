package com.z.optimization.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import net.fabricmc.loader.api.FabricLoader;
import java.util.List;
import java.util.Set;

public class ZOptimizationMixinPlugin implements IMixinConfigPlugin {

    // Safely gets the version, or defaults to "unknown" if the container fails to load
    private static final String MC_VERSION = FabricLoader.getInstance()
            .getModContainer("minecraft")
            .map(mod -> mod.getMetadata().getVersion().getFriendlyString())
            .orElse("unknown");

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

        // Isolate the specific mixin we want to version-gate
        if (mixinClassName.contains("RedstoneWireLoopOptimization")) {
            // Null check first! If MC_VERSION is null, this safely returns false.
            // If it is NOT null, it checks if it starts with 26.1.
            return MC_VERSION != null && !MC_VERSION.startsWith("26.1");
        }

        // For all other mixins, apply them normally
        return true;
    }

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}