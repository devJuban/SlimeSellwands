plugins {
    id("java")
}

// PlotSquared and WorldEdit APIs are resolved reflectively to retain Java 8
// bytecode when those optional modern plugins are installed.
