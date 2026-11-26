# SlimeSellwands
SlimeSellwands is a fork of [AxSellwand](https://github.com/Artillex-Studios/AxSellwands) that lets you sell items from Slimefun storage units.

## Requirements
- Bukkit-compatible server; compiled against the Spigot 1.8.8 API and Java 8 bytecode
- A server runtime supported by the chosen Minecraft version (newer Paper releases require newer Java)
- Vault plus an economy provider to pay players for sales when `hooks.economy-plugin` is set to Vault
- Slimefun 5 is required when `slimefun-integration` is enabled; compatible storage addons are optional:
  - [BetterInfinityExpansion](https://github.com/devJuban/BetterInfinityExpansion)
  - [InfinityExpansion](https://github.com/Riley31415/InfinityExpansion)
  - [FluffyMachines](https://github.com/NCBPFluffyBear/FluffyMachines)

At startup, the plugin reports missing required dependencies and the status of optional dependencies (PlaceholderAPI, WorldGuard, and supported Slimefun storage addons) in the server console. Optional integrations never prevent the core plugin from loading.

Admins can run `/axsellwands status` in-game to display the same dependency report.

The plugin is compiled to Java 8 bytecode against the Spigot 1.8.8 API and uses Bukkit-compatible APIs and reflection for cross-version behavior. It is intended for Spigot, Paper, and Purpur from 1.8.8 onward, subject to the server's Java requirement and the installed Slimefun/addon build. Test the exact Slimefun 5 and addon versions used by your server before production deployment.

> [!NOTE]
> This version has only been tested on 1.20.6.
## Build

## Future 1.8.8-1.15 Support
In the near future 1.8.8 - 1.15.x support will be removed due it just being extremely old and unstable.

## Credits
[@Artillex-Studios](https://github.com/Artillex-Studios/) - providing AxSellwand

[@ponk11](https://github.com/ponk11) - Adding 1.8.8+ support and few cools things