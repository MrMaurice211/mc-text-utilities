plugins {
    id("fabric-loom") version "1.13-SNAPSHOT"
    id("maven-publish")
}


group = project.property("maven_group") as String
val minecraft = property("deps.minecraft") as String

base {
    archivesName.set(project.property("archives_base_name") as String + "-fabric")
    version = "${project.property("mod_version") as String}+${minecraft}"
}

val javaVersion = (property("java.version") as String).toInt()
java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    withSourcesJar()
}

repositories {
    maven { url = uri("https://maven.shedaniel.me/") }
    maven { url = uri("https://maven.terraformersmc.com/") }
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${minecraft}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

    modApi("com.terraformersmc:modmenu:16.0.0-rc.1")
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:20.0.149") {
        exclude(group = "net.fabricmc.fabric-api")
    }
}

val versionRange = property("version_range") as String
tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", minecraft)
    inputs.property("version_range", versionRange)
    inputs.property("java_version", "JAVA_${javaVersion}")
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching(listOf("fabric.mod.json", "textutilities.client.mixins.json", "textutilities.mixins.json")) {
        expand(
            "version" to project.version,
            "version_range" to versionRange,
            "java_version" to "JAVA_${javaVersion}",
            "loader_version" to project.property("loader_version")
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    options.release.set(javaVersion)
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}
