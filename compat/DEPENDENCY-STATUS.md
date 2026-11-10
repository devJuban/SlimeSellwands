# Java 8 and Minecraft 1.8.8 compatibility

## What the distributable currently builds

The root Maven project now compiles the plugin with `--release 8` against the Spigot 1.8.8 API. It does not bundle AxAPI, AxIntegrations, MiniMessage, or other Java 21 libraries. The plugin includes a small Java 8 compatibility implementation under `src/main/java/com/artillexstudios/axapi` for the specific API calls AxSellwands uses. This is an application compatibility layer, not a rebuilt AxAPI release.

The app-facing replacements cover lifecycle, YAML configuration, messages, item formatting, item metadata, scheduling, action bars, titles, and file utilities. NBT metadata uses Bukkit persistent data where available, legacy NMS reflection on 1.8, and a private lore fallback for generic item copies.

Optional runtime integrations currently include Bukkit inventories, the built-in price list, Vault economy, WorldGuard protection checks (legacy and current APIs by reflection), and reflective Slimefun/InfinityExpansion storage hooks. The former AxIntegrations framework provided many more shop, currency, container, and protection providers; those integrations are not included by this compatibility layer.

## Framework compatibility builds

The root plugin still does not consume the full AxAPI or AxIntegrations distributions. Separate Java 8 builds now cover AxAPI's common API core and a small AxIntegrations bundle.

- AxAPI snapshot: `9c4c33c3d1682b2ef45a90ab68e887481bc0e7d4` (upstream version 2.1.8). `compat/AxAPI/api` compiles and shades with Java 8 bytecode and HikariCP 4.0.3. Ordinary classes in the shaded JAR are version 52; Java 9 multi-release entries remain under `META-INF/versions/9`.
- AxAPI's Paper and Folia modules remain modern-server adapters. `:paper:compileJava` and `:folia:compileJava` pass, and `:nms:v26_2:compileJava` passes on Java 25. The full AxAPI distribution is not a Java 8 artifact because it includes modern server and NMS modules.
- AxIntegrations snapshot: `12c7126eec16818874cc5046862368e575254dc9` (upstream version 35). The `legacy` target compiles shared integration code, Vault currency, and built-in experience/level currencies against Spigot 1.8.8. Its JAR contains 39 Java 8 classes. Other providers are not included in that target.
- The full AxIntegrations build now passes across all 61 provider modules and produces `compat/AxIntegrations/build/libs/AxIntegrations-35.jar`. Several adapters load provider APIs reflectively to avoid compile-time dependence on missing artifacts or newer-Java libraries. The build used the locally cached Spigot 1.8.8 API JAR via Gradle's `spigotApiJar` property because the upstream BungeeCord 1.8 snapshot dependency could not be resolved from the configured repositories. Provider adapters have compile coverage but no integration-specific runtime tests; only the separate `legacy` bundle and the plugin's own hooks have runtime coverage described below. The AxIntegrations framework is still not a single runtime artifact validated across every server and provider version.

These framework artifacts remain separate from the plugin. AxSellwands bundles a smaller Java 8 compatibility layer to avoid loading modern Paper, Folia, and NMS classes on legacy servers.

## Verified runtime checks

- PaperSpigot 1.8.8 build 445 on Java 8: plugin enabled with Slimefun 5.3.3. An offline protocol-47 player ran help, give, and reload; the wand metadata survived the inventory packet.
- A player-backed sell event on 1.8.8, using a temporary Vault-compatible test provider, deposited 5.0 for five priced cobblestone and left the chest empty.
- Paper 26.2 build 134 on Java 25: plugin enabled with Slimefun 5.3.3, its help command ran, and a temporary runtime probe confirmed the configured glowing sellwand builds with the modern enchantment API.
- The downloaded Slimefun 5.3.3 artifact is an unofficial test build and logs experimental 1.8 compatibility warnings. One early shutdown hit a Slimefun backup error; later test shutdowns completed normally.
- The production Vault plugin and live WorldGuard claim checks have not been exercised yet; the sale check used a temporary compatible provider, and no WorldGuard server was installed.
- AxAPI's HTTP utility now uses `HttpURLConnection` and `Requests.Response` to target Java 8; consumers of the previous `java.net.http.HttpResponse` signatures need to adapt.
