plugins {
    id("java")
}

// The provider is resolved reflectively so this module can compile without a
// specific CoinsEngine release on the classpath.
