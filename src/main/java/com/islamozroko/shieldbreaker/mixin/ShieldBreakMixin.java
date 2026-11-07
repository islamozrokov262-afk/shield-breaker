package com.islamozroko.shieldbreaker.mixin;

import com.islamozroko.shieldbreaker.ShieldBreakerMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class ShieldBreakMixin {
    private static int originalSlot = -1;

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttackHead(Player player, Entity target, CallbackInfo ci) {
        // Если это не игрок или он не блокирует — пропускаем
        if (!(target instanceof Player targetPlayer)) return;
        if (!targetPlayer.isBlocking()) return;

        int axeSlot = ShieldBreakerMod.findAxeSlot(player);
        if (axeSlot != -1) {
            // Отменяем стандартный удар
            ci.cancel();
            
            // Запоминаем слот, свапаем на топор
            originalSlot = player.getInventory().getSelectedSlot();
            player.getInventory().setSelectedSlot(axeSlot);
            
            // Бьем топором СРАЗУ (в этом же тике)
            Minecraft mc = Minecraft.getInstance();
            if (mc.gameMode != null) {
                mc.gameMode.attack(player, target);
                player.swing(InteractionHand.MAIN_HAND);
            }
            
            // Возвращаем меч
            player.getInventory().setSelectedSlot(originalSlot);
            originalSlot = -1;
        }
    }
}
