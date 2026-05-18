plugins {
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.9-SNAPSHOT" apply false
    java
}

architectury {
    minecraft = property("minecraft_version").toString()
}

allprojects {
    group = property("maven_group").toString()
    version = property("mod_version").toString()
}

val javaRelease: Int = (rootProject.findProperty("java_release") as? String)?.toIntOrNull() ?: 21
val javaVersion: JavaVersion = JavaVersion.toVersion(javaRelease.toString())

subprojects {
    apply(plugin = "java")

    java {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(javaRelease)
    }

    tasks.withType<Jar> {
        manifest {
            attributes(
                "Implementation-Title" to rootProject.property("mod_name").toString(),
                "Implementation-Version" to rootProject.property("mod_version").toString(),
                "Implementation-Vendor" to "gnustella-lab"
            )
        }
    }

    if (name == "helper" || name == "common") {
        repositories { mavenCentral() }
        return@subprojects
    }

    apply(plugin = "architectury-plugin")

    repositories {
        mavenCentral()
        maven("https://maven.architectury.dev/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.neoforged.net/releases/")
    }
}

tasks.register("buildAll") {
    dependsOn(":fabric:build")
    doLast {
        println("=== OpenFriend Plus mod loaders built ===")
    }
}


tasks.register("printBuildInfo") {
    doLast {
        println("mod_name=${project.property("mod_name")}")
        println("mod_id=${project.property("mod_id")}")
        println("mod_version=${project.property("mod_version")}")
        println("minecraft_version=${project.property("minecraft_version")}")
        println("loaders=fabric")
    }
}
