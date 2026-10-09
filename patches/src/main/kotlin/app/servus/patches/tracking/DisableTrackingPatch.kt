package app.servus.patches.tracking

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.servus.patches.shared.Constants.COMPATIBILITY_SERVUS_TV

@Suppress("unused")
val disableTrackingPatch = bytecodePatch(
    name = "Disable tracking",
    description = "Makes the app behave as if all tracking consent was declined, so Braze, Datadog, " +
        "Red Bull analytics, GfK and Datazoom never start and Crashlytics and Firebase Analytics don't collect data.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_SERVUS_TV)

    // Every tracking SDK sits behind an initializer whose init(enable) only starts the SDK when enable is true.
    // The OneTrust wrapper passes the answer of its own consent checks as enable, and the app answers false
    // itself whenever OneTrust isn't ready yet, so "no consent" is a path the app already handles. Firebase
    // Analytics and Crashlytics are additionally off by default in the manifest until init(true) enables them.
    //
    // All consent checks return false: the two in the OneTrust wrapper (they decide the init(enable) values,
    // including the GfK and Datazoom video metrics, which are filed as "strictly necessary") and the app-facing
    // ConsentManager.isConsentGrantedFor. The OneTrust banner and preference center keep working, but no
    // choice made there turns tracking on.
    execute {
        listOf(
            IsConsentGrantedForFingerprint,
            ConsentGrantedForCategoryFingerprint,
            ConsentGrantedForSdkFingerprint,
        ).forEach { fingerprint ->
            // p0 instead of v0: the category check declares no locals.
            fingerprint.method.addInstructions(
                0,
                """
                    const/4 p0, 0x0
                    return p0
                """,
            )
        }
    }
}
