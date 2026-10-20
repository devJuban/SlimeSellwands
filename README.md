# SlimeSellwands
SlimeSellwands is a fork of [AxSellwand](https://github.com/Artillex-Studios/AxSellwands) which lets you sell items in storage units from slimefun. BetterInfinityExpansion, InfinityExpansion
and FluffyMachines are currently supported.

## Requirements
- Bukkit-compatible server; compiled against the Spigot 1.8.8 API and Java 8 bytecode
- A server runtime supported by the chosen Minecraft version (newer Paper releases require newer Java)
- Vault plus an economy provider to pay players for sales; the built-in price list is selected by default
- Slimefun and its storage addons are optional and must match the server version:
  - [BetterInfinityExpansion](https://github.com/devJuban/BetterInfinityExpansion)
  - [InfinityExpansion](https://github.com/Riley31415/InfinityExpansion)
  - [FluffyMachines](https://github.com/NCBPFluffyBear/FluffyMachines)

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
| `./SlimefunHook`              | DEPRECATED: Old Slimefun Hook    |

# Credits
[@Artillex-Studios](https://github.com/Artillex-Studios/) - providing AxSellwand
