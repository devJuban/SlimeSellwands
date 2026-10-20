package com.artillexstudios.axintegrations.functions;

import java.util.List;

@FunctionalInterface
public interface CurrencySetupFunction {

        List<String> getCurrencyList(String name);
}
