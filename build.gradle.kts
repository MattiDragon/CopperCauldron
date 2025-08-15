plugins {
	alias(libs.plugins.loom)
	id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base.archivesName = project.property("archives_base_name") as String

repositories {
}

loom {
	splitEnvironmentSourceSets()

	mods {
		register("copper_cauldron") {
			sourceSet(sourceSets["main"])
			sourceSet(sourceSets["client"])
            modFiles.from(files("src/main/generated"))
		}
	}
}

fabricApi {
	configureDataGeneration {
		client = true
        addToResources = false
        createSourceSet = true
	}
}

dependencies {
	minecraft(libs.minecraft)
	mappings(variantOf(libs.fabric.yarn) {
        classifier("v2")
    })
	modImplementation(libs.fabric.loader)

	modImplementation(libs.fabric.api)

    runtimeOnly(files("src/main/generated"))
}

// Delete datagen on clean
tasks.clean {
    delete("src/main/generated")
}

tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand("version" to inputs.properties["version"])
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 21
    // Enable details for deprecation warnings
    options.isDeprecation = true
}

java {
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
	inputs.property("archivesName", project.base.archivesName)

	from("LICENSE") {
		rename { "${it}_${inputs.properties["archivesName"]}"}
	}

    // Copy datagen to jar
    dependsOn (tasks["runDatagen"])
    from("src/main/generated") {
        exclude("README.md")
        exclude(".cache")
    }
}

// configure the maven publication
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = base.archivesName.get()
			from(components["java"])
		}
	}
}