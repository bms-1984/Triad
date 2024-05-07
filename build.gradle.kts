/*
 *     Triad, a tech mod for Minecraft
 *     Copyright (C) 2024  Ben M. Sutter
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    idea
    `maven-publish`
    id("net.neoforged.gradle.userdev") version "7.0.109"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
}

val version: String by project
val mod_group_id: String by project
val group = mod_group_id
val mod_id: String by project
val neo_version: String by project
val kotlinforforge_version: String by project
val kotlinforforge_version_range: String by project
val minecraft_version: String by project
val mappings_version: String by project
val minecraft_version_range: String by project
val neo_version_range: String by project
val loader_version_range: String by project
val mod_name: String by project
val mod_license: String by project
val mod_version: String by project
val mod_authors: String by project
val mod_description: String by project
val pack_format_id: String by project
val jei_version: String by project
val top_version: String by project
val localRuntime by configurations.creating

repositories {
    mavenLocal()
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
    maven {
        name = "JEI"
        setUrl("https://maven.blamejared.com")
    }
    maven {
        name = "The One Probe"
        setUrl("https://maven.k-4u.nl")
    }
}

base {
    archivesName = mod_id
}

minecraft {
    mappings
}

subsystems {
    parchment {
        minecraftVersion(minecraft_version)
        mappingsVersion(mappings_version)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
}

runs {
    configureEach {
        systemProperty("forge.logging.markers", "REGISTRIES")
        systemProperty("forge.logging.console.level", "debug")
        modSource(sourceSets["main"])
        jvmArgument("-XX:+AllowEnhancedClassRedefinition")
    }

    create("client") {
        systemProperty("forge.enabledGameTestNamespaces", mod_id)
    }

    create("server") {
        systemProperty("forge.enabledGameTestNamespaces", mod_id)
        programArgument("--nogui")
    }

    create("gameTestServer") {
        systemProperty("forge.enabledGameTestNamespaces", mod_id)
    }

    create("data") {
        programArguments.addAll("--mod", mod_id, "--all", "--output", file("src/generated/resources/").absolutePath, "--existing", file("src/main/resources/").absolutePath)
    }
}

configurations {
    localRuntime.extendsFrom(runtimeClasspath.get())
}

dependencies {
    implementation("net.neoforged:neoforge:${neo_version}")

    implementation("thedarkcolour:kotlinforforge-neoforge:${kotlinforforge_version}")

//    compileOnly("mezz.jei:jei-1.20.4-common-api:${jei_version}")
//    compileOnly("mezz.jei:jei-1.20.4-neoforge-api:${jei_version}")
//    localRuntime("mezz.jei:jei-1.20.4-neoforge:${jei_version}")

    implementation("mcjty.theoneprobe:theoneprobe:1.20.5_neo-${top_version}")
}

tasks {
    withType<ProcessResources>().configureEach {
        val replaceProperties: MutableMap<String, Any> = mutableMapOf (
            "minecraft_version" to minecraft_version,
            "minecraft_version_range" to minecraft_version_range,
            "neo_version" to neo_version,
            "neo_version_range" to neo_version_range,
            "loader_version_range" to loader_version_range,
            "kotlinforforge_version_range" to kotlinforforge_version_range,
            "mod_id" to mod_id,
            "mod_name" to mod_name,
            "mod_license" to mod_license,
            "mod_version" to mod_version,
            "mod_authors" to mod_authors,
            "mod_description" to mod_description,
            "pack_format_id" to pack_format_id
        )
        inputs.properties(replaceProperties)

        filesMatching(mutableListOf("META-INF/neoforge.mods.toml", "pack.mcmeta")) {
            expand(replaceProperties)
        }
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "21"
        kotlinOptions.verbose = true
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}

publishing {
    publications {
        register<MavenPublication>("java") {
            from(components["java"])
            artifactId = mod_id
        }
    }
    repositories {
        maven {
            setUrl("file://${project.projectDir}/repo")
        }
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
