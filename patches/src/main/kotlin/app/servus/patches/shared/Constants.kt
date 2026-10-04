package app.servus.patches.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Constants {
    val COMPATIBILITY_SERVUS_TV = Compatibility(
        name = "ServusTV On",
        packageName = "com.mautilus.servus",
        // APKMirror only offers this app as a split bundle.
        apkFileType = ApkFileType.APKM,
        // Background of the launcher icon (res/drawable/ic_launcher_background.xml).
        appIconColor = 0xBC1D1D,
        targets = listOf(
            // Developed and tested against this release (APKMirror, versionCode 2024000101).
            AppTarget(version = "7.5.2.0"),
            // Fingerprints only rely on strings, not on obfuscated names, so later releases likely work too.
            AppTarget(version = null, isExperimental = true),
        ),
    )
}
