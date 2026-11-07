package com.islamozroko.shieldbreaker.mixin;

import com.islamozroko.shieldbreaker.ShieldBreakerMod;
import net.minecraft.client.Minecraft;
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
    private static int delayTicks = 0;

    // 1. Перехватываем клик по врагу
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttackHead(Player player, Entity target, CallbackInfo ci) {
        if (!(target instanceof Player targetPlayer)) return;
        if (!targetPlayer.isBlocking()) return;
        if (delayTicks > 0) return; // Если процесс уже идет

        int axeSlot = ShieldBreakerMod.findAxeSlot(player);
        if (axeSlot != -1) {
            ci.cancel(); // Отменяем обычный удар мечом
            
            originalSlot = player.getInventory().getSelectedSlot();
            player.getInventory().setSelectedSlot(axeSlot); // Свапаем на топор
            
            delayTicks = 1; // Ждем 1 тик (50 мс)
            ShieldBreakerMod.setTarget(target); // Запоминаем цель
        }
    }

    // 2. Ждем тик и бьем
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (delayTicks > 0) {
            delayTicks--;
            if (delayTicks == 0 && originalSlot != -1) {
                Minecraft mc = Minecraft.getInstance();
                Entity target = ShieldBreakerMod.getTarget();
                
                if (target != null && mc.player != null && mc.gameMode != null) {
                    mc.gameMode.attack(mc.player, target); // Бьем топором
                    mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                }
                
                // Возвращаем меч
                mc.player.getInventory().setSelectedSlot(originalSlot);
                originalSlot = -1;
                ShieldBreakerMod.setTarget(null);
            }
        }
    }
}
