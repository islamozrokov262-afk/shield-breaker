package com.islamozroko.shieldbreaker;

import net.fabricmc.api.ClientModInitializer;

public class ShieldBreakerMod implements ClientModInitializer {
    private static ShieldBreakerConfig config;

    @Override
    public void onInitializeClient() {
        config = ShieldBreakerConfig.load();
    }

    public static ShieldBreakerConfig getConfig() {
        return config;
    }
}
