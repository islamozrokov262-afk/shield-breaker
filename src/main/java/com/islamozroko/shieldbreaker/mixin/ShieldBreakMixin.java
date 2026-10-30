package com.islamozroko.shieldbreaker.mixin;

import com.islamozroko.shieldbreaker.ShieldBreakerMod;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class ShieldBreakMixin {
    private static int originalSlot = -1;

    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttackHead(Player player, Entity target, CallbackInfo ci) {
        if (target instanceof Player targetPlayer && targetPlayer.isBlocking()) {
            int axeSlot = ShieldBreakerMod.findAxeSlot(player);
            if (axeSlot != -1) {
                originalSlot = player.getInventory().getSelectedSlot();
                player.getInventory().setSelectedSlot(axeSlot);
            }
        }
    }

    @Inject(method = "attack", at = @At("RETURN"))
    private void onAttackReturn(Player player, Entity target, CallbackInfo ci) {
        if (originalSlot != -1) {
            player.getInventory().setSelectedSlot(originalSlot);
            originalSlot = -1;
        }
    }
}
