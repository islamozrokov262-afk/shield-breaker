package com.islamozroko.shieldbreaker.mixin;

import com.islamozroko.shieldbreaker.ShieldBreakerMod;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ShieldBreakMixin {
    private static int originalSlot = -1;

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void onAttackHead(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (target instanceof PlayerEntity targetPlayer && targetPlayer.isBlocking()) {
            int axeSlot = ShieldBreakerMod.findAxeSlot(player);
            if (axeSlot != -1) {
                originalSlot = player.getInventory().getSelectedSlot();
                player.getInventory().setSelectedSlot(axeSlot);
            }
        }
    }

    @Inject(method = "attackEntity", at = @At("RETURN"))
    private void onAttackReturn(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (originalSlot != -1) {
            player.getInventory().setSelectedSlot(originalSlot);
            originalSlot = -1;
        }
    }
}
