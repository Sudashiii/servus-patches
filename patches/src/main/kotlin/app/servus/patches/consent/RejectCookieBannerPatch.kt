package app.servus.patches.consent

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.smali.ExternalLabel
import app.servus.patches.shared.Constants.COMPATIBILITY_SERVUS_TV
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Suppress("unused")
val rejectCookieBannerPatch = bytecodePatch(
    name = "Reject cookie banner",
    description = "Skips the cookie consent banner on first launch and saves 'Reject all' instead, " +
        "as if you declined it. The privacy settings in the app stay available.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_SERVUS_TV)

    // showConsentIfNeeded asks OneTrust whether the banner is needed. If not (every launch after the first), it
    // inits the SDKs from the stored consent. If it is, it shows the banner, and the banner's "Reject all" button
    // calls saveConsent("Banner - Reject All").
    //
    // When the banner would be shown, the patch saves that same "Reject all" itself and then takes the "no banner
    // needed" path. In OneTrust, saveConsent("Banner - Reject All") runs saveRejectAll (every non-essential group
    // and vendor rejected) and sends the OTConsentUpdated broadcast. The app's OneTrust wrapper listens for it and
    // then marks the banner as done (deep links wait for that) and inits the SDKs again, which they ignore once
    // initialized. The preference center reads the stored consent, so the choice can still be viewed and changed.
    execute {
        val (shouldShowBanner, moveResult) = ShowConsentIfNeededFingerprint.instructionMatches
        val sdkRegister = shouldShowBanner.getInstruction<FiveRegisterInstruction>().registerC
        val resultRegister = moveResult.getInstruction<OneRegisterInstruction>().registerA

        // invoke-virtual and const/4 only take v0-v15.
        if (sdkRegister > 15 || resultRegister > 15) {
            throw PatchException("Unexpected registers v$sdkRegister/v$resultRegister in showConsentIfNeeded")
        }

        // shouldShowBanner() -> if true: saveConsent("Banner - Reject All"); then always continue as false.
        // The branch target is added first and referenced as an external label, because labels defined inside
        // an addInstructions snippet are resolved relative to the snippet, not to the method.
        ShowConsentIfNeededFingerprint.method.apply {
            val insertIndex = moveResult.index + 1
            addInstruction(insertIndex, "const/4 v$resultRegister, 0x0")
            addInstructionsWithLabels(
                insertIndex,
                """
                    if-eqz v$resultRegister, :servus_no_banner
                    const-string v$resultRegister, "Banner - Reject All"
                    invoke-virtual { v$sdkRegister, v$resultRegister }, Lcom/onetrust/otpublishers/headless/Public/OTPublishersHeadlessSDK;->saveConsent(Ljava/lang/String;)V
                """,
                ExternalLabel("servus_no_banner", getInstruction(insertIndex)),
            )
        }
    }
}
