package com.artillexstudios.axsellwands.hooks;

import com.artillexstudios.axsellwands.hooks.integration.ShopIntegration;

import static com.artillexstudios.axsellwands.AxSellwands.HOOKS;

public class HookManager {

    public static void setup() {
        BuiltinPrices prices = new BuiltinPrices();
        if (prices.setup()) ShopIntegration.register(prices);
    }

    public static void reload() {
        setup();
    }
}
