package dev.blueline.lapisreplication;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public final class LapisReplicationRecipe extends CustomRecipe {
    public static final LapisReplicationRecipe INSTANCE = new LapisReplicationRecipe();
    public static final MapCodec<LapisReplicationRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, LapisReplicationRecipe> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);
    public static final RecipeSerializer<LapisReplicationRecipe> SERIALIZER =
            new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return findMatch(input) != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        Match match = findMatch(input);
        if (match == null) {
            return ItemStack.EMPTY;
        }
        return match.target().copyWithCount(match.stackable() ? 64 : 1);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        Match match = findMatch(input);

        if (match != null && !match.stackable()) {
            remaining.set(match.targetSlot(), match.target().copyWithCount(1));
        }

        return remaining;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER;
    }

    public static Match findMatch(CraftingInput input) {
        List<Integer> used = new ArrayList<>(3);
        for (int i = 0; i < input.size(); i++) {
            if (!input.getItem(i).isEmpty()) {
                used.add(i);
            }
        }

        if (used.size() != 3) {
            return null;
        }

        return findMatch(used, input::getItem);
    }

    static Match findMatch(int containerSize, java.util.function.IntFunction<ItemStack> getter) {
        List<Integer> used = new ArrayList<>(3);
        for (int i = 0; i < containerSize; i++) {
            if (!getter.apply(i).isEmpty()) {
                used.add(i);
            }
        }

        if (used.size() != 3) {
            return null;
        }

        return findMatch(used, getter);
    }

    private static Match findMatch(List<Integer> used, java.util.function.IntFunction<ItemStack> getter) {
        for (int lapisSlot : used) {
            ItemStack lapis = getter.apply(lapisSlot);
            if (!lapis.is(Items.LAPIS_LAZULI) || lapis.getCount() < 1) {
                continue;
            }

            for (int cobbleSlot : used) {
                if (cobbleSlot == lapisSlot) {
                    continue;
                }

                ItemStack cobble = getter.apply(cobbleSlot);
                if (!cobble.is(Items.COBBLESTONE)) {
                    continue;
                }

                int targetSlot = -1;
                for (int slot : used) {
                    if (slot != lapisSlot && slot != cobbleSlot) {
                        targetSlot = slot;
                        break;
                    }
                }

                if (targetSlot < 0) {
                    continue;
                }

                ItemStack target = getter.apply(targetSlot);
                if (target.isEmpty()) {
                    continue;
                }

                boolean stackable = target.getMaxStackSize() > 1;
                int cobbleCost = stackable ? 64 : 1;
                if (cobble.getCount() < cobbleCost) {
                    continue;
                }

                return new Match(lapisSlot, cobbleSlot, targetSlot, target, stackable, cobbleCost);
            }
        }

        return null;
    }

    public record Match(
            int lapisSlot,
            int cobbleSlot,
            int targetSlot,
            ItemStack target,
            boolean stackable,
            int cobbleCost
    ) {}
}
