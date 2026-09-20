package dev.blueline.lapisreplication;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(LapisReplicationMod.MOD_ID)
public final class LapisReplicationMod {
    public static final String MOD_ID = "lapisreplication";

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);

    static {
        RECIPE_SERIALIZERS.register("lapis_replication", () -> LapisReplicationRecipe.SERIALIZER);
    }

    public LapisReplicationMod(IEventBus modEventBus) {
        RECIPE_SERIALIZERS.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(CraftingEvents::onItemCrafted);
        NeoForge.EVENT_BUS.addListener(CannedFoodEvents::onItemFinished);
    }
}
