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
                .setTitle(Component.literal("Shield Breaker Настройки"))
                .setSavingRunnable(config::save);
        
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("Основное"));
        
        general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Включить"), config.enabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enabled = newValue)
                .build());
                
        general.addEntry(entryBuilder.startIntSlider(Component.literal("Слот топора (0-8)"), config.axeSlot, 0, 8)
                .setDefaultValue(1)
                .setTooltip(Component.literal("0 - клавиша 1, 1 - клавиша 2, и т.д."))
                .setSaveConsumer(newValue -> config.axeSlot = newValue)
                .build());

        general.addEntry(entryBuilder.startIntField(Component.literal("Задержка (мс)"), config.breakDelay)
                .setDefaultValue(200)
                .setMin(0).setMax(1000)
                .setTooltip(Component.literal("Минимальное время между ломаниями щита"))
                .setSaveConsumer(newValue -> config.breakDelay = newValue)
                .build());
        
        return builder.build();
    }
}
