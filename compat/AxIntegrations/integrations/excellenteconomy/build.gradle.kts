plugins {
    id("java")
}

repositories {
    maven("https://repo.nightexpressdev.com/releases")
}

// The optional provider API is resolved reflectively; avoid the upstream
// CoinsEngine dependency that was incorrectly declared for this module.
