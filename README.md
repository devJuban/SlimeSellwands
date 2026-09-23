# SlimeSellwands
SlimeSellwands is a fork of [AxSellwand](https://github.com/Artillex-Studios/AxSellwands) which lets you sell items in storage units from slimefun. BetterInfinityExpansion, InfinityExpansion
and FluffyMachines are currently supported. I will NOT add 1.16 support due to AxAPI not supporting it sadly.

## Requirements
- Spigot (Paper recommended) 1.20.6 or higher
- [Slimefun](https://github.com/Slimefun/Slimefun4)
  - [BetterInfinityExpansion (Any)](https://github.com/devJuban/BetterInfinityExpansion)
  - [InfinityExpansion (Latest)](https://github.com/Riley31415/InfinityExpansion)
  - [FluffyMachines (Latest)](https://github.com/NCBPFluffyBear/FluffyMachines)

> [!NOTE]
> Despite IE being supported I recommend that you use BIE (V1.2.3 or higher).
 
# Changes

| File Changed                  | Changed                          |
|-------------------------------|----------------------------------|
| `./AxSellwands`               | Added Informative Messages       |
| `./pom.xml`                   | Added dependencies               |
| `./lang.yml`                  | Added `block-has-viewer`         |
| `./config.yml`                | Added `slimefun-integration`     |
| `./plugin.yml`                | Added soft depenecies            |
| `./SellwandUseListener`       | Added compatibilty with Slimefun |
| `./BetterInfinityExpansion`   | Hook for BetterInfinityExpansion |
| `./FluffyMachines`            | Hook for FluffyMachines          |
| `./InfinityExpansion`         | Hook for InfinityExpansion       |
| `./StorageHook`               | Blueprint for API                |
| `./StorageIntegrationManager` | StorageHook manager              |
| `./SlimefunHook`              | DEPRECATED: Old Slimefun Hook    |

# Credits
[@Artillex-Studios](https://github.com/Artillex-Studios/) - providing AxSellwand