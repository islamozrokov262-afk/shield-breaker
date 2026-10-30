package com.islamozroko.shieldbreaker;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;

public class ShieldBreakerMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Базовая инициализация (логика будет вызываться из миксина)
    }

    public static void swapToAxe(PlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() instanceof AxeItem) {
                player.getInventory().selectedSlot = i;
                return;
            }
        }
    }

    public static int findAxeSlot(PlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).getItem() instanceof AxeItem) return i;
        }
        return -1;
    }
}
