package com.islamozroko.shieldbreaker.mixin;

import com.islamozroko.shieldbreaker.ShieldBreakerMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ShieldBreakMixin {
    
    private static long lastBreakTime = 0;

    @Inject(method = "handleEntityEvent", at = @At("TAIL"))
    private void onEntityEvent(ClientboundEntityEventPacket packet, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.getConnection() == null) return;

        // 35 - ID события "Начал использовать предмет" (поднял щит)
        if (packet.getEventId() != 35) return;

        Entity entity = packet.getEntity(mc.level);
        if (!(entity instanceof Player target)) return;
        if (target == mc.player) return;
        
        // Проверяем дистанцию
        if (target.distanceTo(mc.player) > 4.0) return;

        // Проверяем щит
        if (!(target.getUseItem().getItem() == Items.SHIELD)) return;

        // Кулдаун
        long now = System.currentTimeMillis();
        if (now - lastBreakTime < ShieldBreakerMod.getConfig().breakDelay) return;
        lastBreakTime = now;

        int axeSlot = ShieldBreakerMod.getConfig().axeSlot;

        // Проверяем, что в слоте реально топор
        if (!(mc.player.getInventory().getItem(axeSlot).getItem() instanceof AxeItem)) {
            return; 
        }

        int oldSlot = mc.player.getInventory().getSelectedSlot();

        // 1. Свапаем на топор
        mc.getConnection().send(new ServerboundSetCarriedItemPacket(axeSlot));
        
        // 2. Бьем
        mc.getConnection().send(ServerboundInteractPacket.createAttackPacket(target, false));
        
        // 3. Возвращаем меч
        mc.getConnection().send(new ServerboundSetCarriedItemPacket(oldSlot));
        
        // Визуальный свап для синхронизации
        mc.player.getInventory().setSelectedSlot(axeSlot);
        mc.player.getInventory().setSelectedSlot(oldSlot);
    }
}
