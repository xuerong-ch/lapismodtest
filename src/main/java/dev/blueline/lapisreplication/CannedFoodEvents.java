package dev.blueline.lapisreplication;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

public final class CannedFoodEvents {
    private static final ResourceLocation CANNED_FOOD =
            ResourceLocation.fromNamespaceAndPath("ftbic", "canned_food");

    private static final int SATURATION_DURATION_TICKS = 5 * 60 * 20;

    private CannedFoodEvents() {
    }

    public static void onItemFinished(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(event.getItem().getItem());
        if (!CANNED_FOOD.equals(itemId)) {
            return;
        }

        player.addEffect(new MobEffectInstance(
                MobEffects.SATURATION,
                SATURATION_DURATION_TICKS,
                0,
                false,
                true,
                true
        ));
    }
}
