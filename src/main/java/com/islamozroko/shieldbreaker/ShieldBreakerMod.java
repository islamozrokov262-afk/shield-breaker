package com.islamozroko.shieldbreaker;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;

public class ShieldBreakerMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
    }

    public static int findAxeSlot(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof AxeItem) {
                return i;
            }
        }
        return -1;
    }
}
