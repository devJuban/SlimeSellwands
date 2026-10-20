# AxAPI compatibility fork

This directory contains upstream AxAPI source revision `9c4c33c3d1682b2ef45a90ab68e887481bc0e7d4` (version 2.1.8), kept as a separate compatibility fork. The original application used a different 2.2 development artifact.

The `api` module has been ported to Java 8 language and runtime APIs and builds a shaded Java 8 JAR with `./gradlew :api:shadowJar`. HikariCP is pinned to 4.0.3, and HTTP uses `HttpURLConnection` because `java.net.http` is unavailable on Java 8. HTTP calls return the fork's `Requests.Response` type.

Paper and Folia remain modern-server adapters. `:paper:compileJava`, `:folia:compileJava`, and `:nms:v26_2:compileJava` compile successfully, with the 26.2 NMS adapter built on Java 25. The full AxAPI distribution still cannot run on Java 8 because its server and NMS modules target newer runtimes.

AxSellwands itself does not depend on this fork at runtime; the plugin bundles the smaller compatibility implementation under `src/main/java/com/artillexstudios/axapi`.
