package com.z.optimization.client.config; // Update this to your mod's package

import com.z.optimization.config.ZOptions; // Import your options class
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;

public final class FastCrystalOptimizer {

    public static void attemptCrystalDestruction(Minecraft mc, int entityId) {
        // 1. If the visual button is turned OFF, stop running immediately
        if (!ZOptions.fastCrystals) return;

        if (mc.level == null || mc.player == null) return;

        @SuppressWarnings("null")
        Entity target = mc.level.getEntity(entityId);
        if (!(target instanceof EndCrystal crystal)) return;

        if (canDestroyCrystal(mc.player)) {
            destroyCrystal(crystal);
            retargetCrosshair(mc, crystal);
        }
    }

    private static void retargetCrosshair(Minecraft mc, EndCrystal crystal) {
        LocalPlayer player = mc.player;
        if (player == null || mc.hitResult == null || mc.crosshairPickEntity != crystal) {
            return;
        }

        HitResult retraced = player.pick(player.blockInteractionRange(), 1.0F, false);
        mc.crosshairPickEntity = null;
        mc.hitResult = retraced;
    }

    private static boolean canDestroyCrystal(LocalPlayer player) {
        double damage = player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
        damage += getWeaponDamage(player.getMainHandItem());

        MobEffectInstance strength = player.getEffect(MobEffects.STRENGTH);
        if (strength != null) {
            damage += 3.0D * (strength.getAmplifier() + 1);
        }

        MobEffectInstance weakness = player.getEffect(MobEffects.WEAKNESS);
        if (weakness != null) {
            damage -= 4.0D * (weakness.getAmplifier() + 1);
        }

        return damage > 0.0D;
    }

    private static double getWeaponDamage(ItemStack item) {
        if (item.isEmpty()) return 0.0D;
        final double[] sum = {0.0D};
        item.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            if (Attributes.ATTACK_DAMAGE.equals(attribute)) {
                sum[0] += modifier.amount();
            }
        });
        return sum[0];
    }

    private static void destroyCrystal(EndCrystal crystal) {
        crystal.remove(Entity.RemovalReason.KILLED);
        crystal.gameEvent(GameEvent.ENTITY_DIE);
    }
}