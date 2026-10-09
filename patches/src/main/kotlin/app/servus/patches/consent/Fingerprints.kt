package app.servus.patches.consent

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.InstructionLocation.MatchAfterImmediately
import app.morphe.patcher.methodCall
import app.morphe.patcher.opcode
import com.android.tools.smali.dexlib2.Opcode

/**
 * `ConsentManager.showConsentIfNeeded(activity)`, which shows the OneTrust banner on first launch. Class and method
 * name are not obfuscated:
 *
 * ```
 * invoke-virtual {v2}, Lcom/onetrust/otpublishers/headless/Public/OTPublishersHeadlessSDK;->shouldShowBanner()Z
 * move-result v1
 * if-eqz v1, :cond_4   <- no banner needed: init the SDKs from the stored consent
 * ...
 * invoke-virtual {v2, p1, p0}, Lcom/onetrust/otpublishers/headless/Public/OTPublishersHeadlessSDK;->showBannerUI(...)V
 * ```
 */
object ShowConsentIfNeededFingerprint : Fingerprint(
    definingClass = "Lrbak/dtv/foundation/android/managers/ConsentManager;",
    name = "showConsentIfNeeded",
    returnType = "V",
    filters = listOf(
        methodCall(
            smali = "Lcom/onetrust/otpublishers/headless/Public/OTPublishersHeadlessSDK;->shouldShowBanner()Z",
        ),
        opcode(Opcode.MOVE_RESULT, MatchAfterImmediately()),
    ),
)
