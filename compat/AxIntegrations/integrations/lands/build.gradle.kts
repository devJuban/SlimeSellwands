plugins {
    id("java")
}

// The optional Lands API is accessed reflectively to support its Java 17 line
// without raising AxIntegrations' Java 8 bytecode baseline.
