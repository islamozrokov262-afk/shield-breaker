package com.islamozroko.shieldbreaker.mixin;

import com.islamozroko.shieldbreaker.ShieldBreakerMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class ShieldBreakMixin {
    private static int originalSlot = -1;
    private static boolean isBreaking = false;

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttackHead(Player player, Entity target, CallbackInfo ci) {
        if (isBreaking) return;
        
        if (!(target instanceof Player targetPlayer)) return;
        if (!targetPlayer.isBlocking()) return;

        int axeSlot = ShieldBreakerMod.AXE_SLOT;
        
        if (player.getInventory().getItem(axeSlot).getItem() instanceof AxeItem) {
            ci.cancel(); 
            
            isBreaking = true; 
            originalSlot = player.getInventory().getSelectedSlot();
            player.getInventory().setSelectedSlot(axeSlot);
            
            Minecraft mc = Minecraft.getInstance();
            if (mc.gameMode != null) {
                mc.gameMode.attack(player, target);
                player.swing(InteractionHand.MAIN_HAND);
            }
            
            player.getInventory().setSelectedSlot(originalSlot);
            originalSlot = -1;
            isBreaking = false; 
        }
    }
}
