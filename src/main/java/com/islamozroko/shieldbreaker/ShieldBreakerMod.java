package com.islamozroko.shieldbreaker;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;

public class ShieldBreakerMod implements ClientModInitializer {
    
    // УКАЖИ СЛОТ С ТОПОРОМ (0-8)
    public static final int AXE_SLOT = 1; 

    private static Entity currentTarget = null;

    @Override
    public void onInitializeClient() {
    }

    public static void setTarget(Entity target) {
        currentTarget = target;
    }

    public static Entity getTarget() {
        return currentTarget;
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
