package com.z.optimization.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import net.fabricmc.loader.api.FabricLoader;
import java.util.List;
import java.util.Set;

public class ZOptimizationMixinPlugin implements IMixinConfigPlugin {

    private static final String MC_VERSION = FabricLoader.getInstance()
            .getModContainer("minecraft").get().getMetadata().getVersion().getFriendlyString();

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Redstone and Network signatures slightly diverged between 26.1 and 26.2 iterations
        if (mixinClassName.contains("RedstoneWireLoopOptimization") && MC_VERSION.startsWith("26.1")) {
            return false;
        }
        return true;
    }

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}