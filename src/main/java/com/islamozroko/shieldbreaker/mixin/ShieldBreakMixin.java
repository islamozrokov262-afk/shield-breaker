package com.islamozroko.shieldbreaker.mixin;

import com.islamozroko.shieldbreaker.ShieldBreakerMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class ShieldBreakMixin {
    private static int originalSlot = -1;

    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttackHead(Player player, Entity target, CallbackInfo ci) {
        if (!(target instanceof Player targetPlayer)) return;
        
        // Проверяем, блокирует ли враг
        if (!targetPlayer.isBlocking()) return;

        // Ищем топор
        int axeSlot = ShieldBreakerMod.findAxeSlot(player);
        if (axeSlot != -1) {
            originalSlot = player.getInventory().getSelectedSlot();
            
            // 1. Свапаем слот в клиенте
            player.getInventory().setSelectedSlot(axeSlot);
            
            // 2. ОТПРАВЛЯЕМ ПАКЕТ смены слота на сервер СРАЗУ (до пакета удара)
            if (Minecraft.getInstance().getConnection() != null) {
                Minecraft.getInstance().getConnection().send(new ServerboundSetCarriedItemPacket(axeSlot));
            }
        }
    }

    @Inject(method = "attack", at = @At("RETURN"))
    private void onAttackReturn(Player player, Entity target, CallbackInfo ci) {
        if (originalSlot != -1) {
            // Возвращаем меч и отправляем пакет обратно
            player.getInventory().setSelectedSlot(originalSlot);
            if (Minecraft.getInstance().getConnection() != null) {
                Minecraft.getInstance().getConnection().send(new ServerboundSetCarriedItemPacket(originalSlot));
            }
            originalSlot = -1;
        }
    }
}
