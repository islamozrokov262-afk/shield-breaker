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
    
    // Задержка, чтобы не спамить пакетами (античит запалит)
    private static long lastBreakTime = 0;

    @Inject(method = "handleEntityEvent", at = @At("TAIL"))
    private void onEntityEvent(ClientboundEntityEventPacket packet, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // 35 - это ID события "Начал использовать предмет" (поднял щит)
        if (packet.getEventId() != 35) return;

        Entity entity = packet.getEntity(mc.level);
        if (!(entity instanceof Player target)) return;
        if (target == mc.player) return;
        
        // Проверяем дистанцию (до 4 блоков)
        if (target.distanceTo(mc.player) > 4.0) return;

        // Проверяем, что у врага реально в руке щит
        if (!(target.getUseItem().getItem() == Items.SHIELD)) return;

        // Кулдаун 200 мс, чтобы не отправлять 100 пакетов в секунду
        long now = System.currentTimeMillis();
        if (now - lastBreakTime < 200) return;
        lastBreakTime = now;

        // Ищем топор в хотбаре
        int axeSlot = ShieldBreakerMod.findAxeSlot(mc.player);
        if (axeSlot == -1) return;

        int oldSlot = mc.player.getInventory().getSelectedSlot();

        // 1. Свапаем на топор (отправляем пакет серверу)
        mc.getConnection().send(new ServerboundSetCarriedItemPacket(axeSlot));
        
        // 2. Наносим удар (отправляем пакет серверу)
        mc.getConnection().send(ServerboundInteractPacket.createAttackPacket(target, false));
        
        // 3. Возвращаем меч обратно (отправляем пакет серверу)
        mc.getConnection().send(new ServerboundSetCarriedItemPacket(oldSlot));
        
        // Визуально свапаем на клиенте, чтобы сервер и клиент не рассинхронились
        mc.player.getInventory().setSelectedSlot(axeSlot);
        mc.player.getInventory().setSelectedSlot(oldSlot);
    }
}
