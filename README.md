# SlimeSellwands
SlimeSellwands is a fork of [AxSellwand](https://github.com/Artillex-Studios/AxSellwands) that lets players sell items from normal inventories and Slimefun storage blocks. It supports Slimefun 5 through reflective APIs, so the base plugin does not link against a particular addon or server implementation.

Named storage integrations are included for BetterInfinityExpansion, InfinityExpansion, and FluffyMachines. Other Slimefun 5 expansions can work automatically when their storage item exposes a compatible `getContents` or `getStoredItem` API; unsupported addon APIs are skipped safely and reported at startup.

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
> The plugin itself was smoke-tested on PaperSpigot 1.8.8 (Java 8) and Paper 26.2 (Java 25). Slimefun 5.3.3 was an unofficial test build; its 1.8.8 compatibility is experimental. The plugin bundles its own Java 8 compatibility layer. Separate builds are available for AxAPI's Java 8 core and a limited AxIntegrations legacy bundle; the full modern AxAPI/AxIntegrations module sets are not a single Java 8 artifact. See [`compat/DEPENDENCY-STATUS.md`](compat/DEPENDENCY-STATUS.md) for coverage and limits.

## Build

- `mvn -DskipTests clean package` compiles against the Spigot 1.8.8 API.
- `mvn -Ppaper-26.2 -DskipTests clean package` compiles the same Java 8 bytecode against Paper 26.2's API.

The separate compatibility artifacts can be built from their directories:

- `cd compat/AxAPI && ./gradlew :api:shadowJar` builds the Java 8 AxAPI core.
- `cd compat/AxIntegrations && ./gradlew :legacy:jar` builds the shared AxIntegrations core with Vault and built-in currencies for Spigot 1.8.8.
 
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
| `./GenericSlimefunStorage`   | Reflective Slimefun 5 storage fallback |
| `./SlimefunHook`              | DEPRECATED: Old Slimefun Hook    |

# Credits
[@Artillex-Studios](https://github.com/Artillex-Studios/) - providing AxSellwand
[@devJuban](https://github.com/devJuban/SlimeSellwands) - Making the main part of the plugin
