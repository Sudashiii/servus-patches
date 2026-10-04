group = "app.servus"

patches {
    about {
        name = "Servus Patches (unofficial)"
        description = "Personal Morphe patches for ServusTV On"
        source = "git@github.com:Sudashiii/servus-patches.git"
        author = "Sudashiii"
        contact = "https://github.com/Sudashiii/servus-patches/issues"
        website = "https://github.com/Sudashiii/servus-patches"
        license = "GPLv3"
    }
}

// Separate configuration so gson is available at runtime for the
// generatePatchesList task but never bundled into the APK.
val patchListGeneratorClasspath = configurations.create("patchListGeneratorClasspath")

dependencies {
    compileOnly(libs.gson)
    patchListGeneratorClasspath(libs.gson)
}

tasks {
    register<JavaExec>("generatePatchesList") {
        description = "Build patch with patch list"

        dependsOn(build)

        classpath = sourceSets["main"].runtimeClasspath + patchListGeneratorClasspath
        mainClass.set("util.PatchListGeneratorKt")
    }

    // Used by gradle-semantic-release-plugin.
    publish {
        dependsOn("generatePatchesList")
    }
}
