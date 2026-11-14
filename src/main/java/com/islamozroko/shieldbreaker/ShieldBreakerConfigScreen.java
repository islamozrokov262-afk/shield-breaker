package com.islamozroko.shieldbreaker;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ShieldBreakerConfigScreen {
    public static Screen create(Screen parent) {
        ShieldBreakerConfig config = ShieldBreakerMod.getConfig();
        
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Shield Breaker"))
                .setSavingRunnable(config::save);
        
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("Настройки"));
        
        general.addEntry(entryBuilder.startIntSlider(Component.literal("Слот топора (0-8)"), config.axeSlot, 0, 8)
                .setDefaultValue(3)
                .setTooltip(Component.literal("0 - первый слот, 3 - четвертый (клавиша 4)"))
                .setSaveConsumer(newValue -> config.axeSlot = newValue)
                .build());
        
        return builder.build();
    }
}
