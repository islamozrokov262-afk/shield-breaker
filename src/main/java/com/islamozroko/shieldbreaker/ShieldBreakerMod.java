package com.islamozroko.shieldbreaker;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.world.entity.Entity;

public class ShieldBreakerMod implements ClientModInitializer {
    private static ShieldBreakerConfig config;
    private static Entity currentTarget = null;

    @Override
    public void onInitializeClient() {
        config = ShieldBreakerConfig.load();
    }

    public static ShieldBreakerConfig getConfig() {
        return config;
    }

    public static void setTarget(Entity target) {
        currentTarget = target;
    }

    public static Entity getTarget() {
        return currentTarget;
    }
}
