package com.islamozroko.shieldbreaker;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;

public class ShieldBreakerMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Логика в миксине
    }

    public static int findAxeSlot(PlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() instanceof AxeItem) {
                return i;
            }
        }
        return -1;
    }
}
