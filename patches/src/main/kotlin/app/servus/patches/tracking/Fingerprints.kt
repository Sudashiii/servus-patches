package app.servus.patches.tracking

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall

/**
 * `ConsentManager.isConsentGrantedFor(ConsentCategory)`, the app-facing consent check. Used by the ad manager
 * (`isAdTrackingLimited`), Chromecast and the video metrics config. Class and method name are not obfuscated.
 */
object IsConsentGrantedForFingerprint : Fingerprint(
    definingClass = "Lrbak/dtv/foundation/android/managers/ConsentManager;",
    name = "isConsentGrantedFor",
    returnType = "Z",
    parameters = listOf("L"),
)

/**
 * Group consent check of the OneTrust wrapper (obfuscated, e.g. `rz1.b(v52)`):
 *
 * ```
 * invoke-virtual {p0, p1}, Lcom/onetrust/otpublishers/headless/Public/OTPublishersHeadlessSDK;->getConsentStatusForGroupId(Ljava/lang/String;)I
 * move-result p0
 * if-lez p0, :cond_0
 * ```
 *
 * `ConsentManager.isConsentGrantedFor` delegates here, and so does the per-SDK check below.
 */
object ConsentGrantedForCategoryFingerprint : Fingerprint(
    returnType = "Z",
    parameters = listOf("L"),
    filters = listOf(
        methodCall(
            smali = "Lcom/onetrust/otpublishers/headless/Public/OTPublishersHeadlessSDK;->" +
                "getConsentStatusForGroupId(Ljava/lang/String;)I",
        ),
    ),
)

/**
 * Per-SDK consent check of the OneTrust wrapper (obfuscated, e.g. `rz1.c(nn9)`). Asks OneTrust by SDK id or
 * by category, and its result is passed to every SDK initializer's `init(enable)`: Braze, Datadog,
 * Crashlytics/Firebase Analytics, Red Bull analytics, GfK and Datazoom. It talks to OneTrust directly and does
 * not go through `ConsentManager.isConsentGrantedFor`.
 */
object ConsentGrantedForSdkFingerprint : Fingerprint(
    returnType = "Z",
    parameters = listOf("L"),
    filters = listOf(
        methodCall(
            smali = "Lcom/onetrust/otpublishers/headless/Public/OTPublishersHeadlessSDK;->" +
                "getConsentStatusForSDKId(Ljava/lang/String;)I",
        ),
    ),
)
