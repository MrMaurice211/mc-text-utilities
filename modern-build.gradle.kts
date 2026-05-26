plugins {
    id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
    id("maven-publish")
    id("dev.kikugie.stonecutter")
}

group = project.property("maven_group") as String
val minecraft = property("deps.minecraft") as String

base {
    archivesName.set(project.property("archives_base_name") as String + "-fabric")
    version = "${project.property("mod_version") as String}+${minecraft}"
}

val requiredJava = JavaVersion.VERSION_25

java {
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}

repositories {
    maven { url = uri("https://maven.shedaniel.me/") }
    maven { url = uri("https://maven.terraformersmc.com/") }
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft}")
    implementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${project.property("modern_fabric_version")}")

    api("com.terraformersmc:modmenu:18.0.0-beta.1")
    implementation("me.shedaniel.cloth:cloth-config-fabric:26.1.154") {
        exclude(group = "net.fabricmc.fabric-api")
    }
}

val versionRange = property("version_range") as String
tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", minecraft)
    inputs.property("version_range", versionRange)
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching(listOf("fabric.mod.json")) {
        expand(
            "version" to project.version,
            "version_range" to versionRange,
            "loader_version" to (project.property("loader_version") ?: "0.19.2")
        )
    }

    val mixinJava = "JAVA_${requiredJava.majorVersion}"
    filesMatching(listOf("*.mixins.json")) { expand("java" to mixinJava) }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(requiredJava.majorVersion.toInt())
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }

    repositories {}
}
