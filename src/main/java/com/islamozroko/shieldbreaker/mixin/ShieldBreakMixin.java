package com.islamozroko.shieldbreaker.mixin;

import com.islamozroko.shieldbreaker.ShieldBreakerMod;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ShieldBreakMixin {
    private static int originalSlot = -1;

    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttackHead(Entity target, CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        if (target instanceof PlayerEntity targetPlayer && targetPlayer.isBlocking()) {
            int axeSlot = ShieldBreakerMod.findAxeSlot(player);
            if (axeSlot != -1) {
                PlayerInventory inventory = player.getInventory();
                originalSlot = inventory.getSelectedSlot();
                inventory.setSelectedSlot(axeSlot);
            }
        }
    }

    @Inject(method = "attack", at = @At("RETURN"))
    private void onAttackReturn(Entity target, CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        if (originalSlot != -1) {
            player.getInventory().setSelectedSlot(originalSlot);
            originalSlot = -1;
        }
    }
}
