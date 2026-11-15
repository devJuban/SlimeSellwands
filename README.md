# SlimeSellwands
SlimeSellwands is a fork of [AxSellwand](https://github.com/Artillex-Studios/AxSellwands) which lets you sell items in storage units from slimefun. BetterInfinityExpansion, InfinityExpansion
and FluffyMachines are currently supported.

## Requirements
- Spigot (Paper recommended) 1.20.2 or higher
- [Slimefun](https://github.com/Slimefun/Slimefun4)
  - [BetterInfinityExpansion (Any)](https://github.com/devJuban/BetterInfinityExpansion)
  - [InfinityExpansion (Latest)](https://github.com/Riley31415/InfinityExpansion)
  - [FluffyMachines (Latest)](https://github.com/NCBPFluffyBear/FluffyMachines)
 
# Changes

| File Changed                  | Reason/Changed                   |
|-------------------------------|----------------------------------|
| `./AxSellwands`               | Added Informative Messages       |
| `./pom.xml`                   | Added dependencies               |
| `./lang.yml`                  | Added `block-has-viewer`         |
| `./config.yml`                | Added `slimefun-integration`     |
| `./plugin.yml`                | Added soft depenecies            |
| `./SellwandAction`            | Added compatibilty with Slimefun |
| `./PlayerRightClickListener`  | Fix for #1                       |
| `./FluffyMachines`            | Hook for FluffyMachines          |
| `./InfinityExpansion`         | Hook for InfinityExpansion       |
| `./StorageHook`               | Interface for Storage API        |
| `./StorageIntegrationManager` | StorageHook manager              |

# Credits
[@Artillex-Studios](https://github.com/Artillex-Studios/) - providing AxSellwand