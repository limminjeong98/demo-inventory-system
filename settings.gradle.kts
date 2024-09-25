rootProject.name = "demo-inventory-system"

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.5"
}

include(
    "practices:simple",
    "practices:inventory"
)