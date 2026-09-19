package dev.blueline.lapisreplication;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class CraftingEvents {
    private CraftingEvents() {
    }

    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack crafted = event.getCrafting();

        if (crafted.isEmpty() || crafted.getCount() != 64) {
            return;
        }

        Container matrix = event.getInventory();
        LapisReplicationRecipe.Match match = LapisReplicationRecipe.findMatch(
                matrix.getContainerSize(), matrix::getItem
        );

        if (match == null || !match.stackable() || match.cobbleCost() != 64) {
            return;
        }

        ItemStack cobble = matrix.getItem(match.cobbleSlot());
        if (cobble.getCount() < 64) {
            return;
        }

        cobble.shrink(63);
        matrix.setChanged();
    }
}
