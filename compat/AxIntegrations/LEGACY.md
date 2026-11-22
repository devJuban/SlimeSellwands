# Java 8 integration bundle

The `legacy` Gradle target builds a Java 8 AxIntegrations bundle for Spigot 1.8.8. It includes the shared integration registry, Vault currency integration, and built-in experience and level currencies:

```sh
./gradlew :legacy:jar
```

The resulting JAR is `legacy/build/libs/legacy-35.jar`. The bundled providers have no AxAPI runtime calls; Bukkit and Vault are provided by the server and Vault plugin at runtime. AxAPI's Java 8 `api` module remains available on the compile classpath for shared source compatibility.

This target does not include the full provider matrix, including WorldGuard. The separate `AxIntegrations-35.jar` build includes all 61 provider modules and compiles with Java 8 bytecode, but those optional providers have not had per-provider runtime checks. AxSellwands does not consume either framework artifact; its own hook supports WorldGuard 6 and 7 through reflection.
