package com.islamozroko.shieldbreaker.mixin;

import com.islamozroko.shieldbreaker.ShieldBreakerMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
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

    // Ловим ЛЮБЫЕ пакеты событий сущностей, чтобы понять, приходит ли нужный
    @Inject(method = "handleEntityEvent", at = @At("TAIL"))
    private void onEntityEvent(ClientboundEntityEventPacket packet, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.getConnection() == null) return;

        // Отладка: пишем в чат ID события (только для теста!)
        // Если увидишь 35 - значит пакет приходит.
        if (packet.getEventId() == 35) {
            mc.player.displayClientMessage(Component.literal("§e[DEBUG] Пойман пакет 35!"), true);
            
            Entity entity = packet.getEntity(mc.level);
            if (entity instanceof Player target && target != mc.player) {
                if (target.distanceTo(mc.player) <= 4.0) {
                    if (target.getUseItem().getItem() == Items.SHIELD) {
                        
                        long now = System.currentTimeMillis();
                        if (now - lastBreakTime < ShieldBreakerMod.getConfig().breakDelay) return;
                        lastBreakTime = now;

                        int axeSlot = ShieldBreakerMod.getConfig().axeSlot;

                        if (!(mc.player.getInventory().getItem(axeSlot).getItem() instanceof AxeItem)) {
                            mc.player.displayClientMessage(Component.literal("§c[DEBUG] В слоте " + axeSlot + " нет топора!"), true);
                            return; 
                        }

                        int oldSlot = mc.player.getInventory().getSelectedSlot();

                        mc.getConnection().send(new ServerboundSetCarriedItemPacket(axeSlot));
                        mc.getConnection().send(ServerboundInteractPacket.createAttackPacket(target, false));
                        mc.getConnection().send(new ServerboundSetCarriedItemPacket(oldSlot));
                        
                        mc.player.getInventory().setSelectedSlot(axeSlot);
                        mc.player.getInventory().setSelectedSlot(oldSlot);
                        
                        mc.player.displayClientMessage(Component.literal("§a[DEBUG] Отправлен пакет ломания!"), true);
                    }
                }
            }
        }
    }
}
