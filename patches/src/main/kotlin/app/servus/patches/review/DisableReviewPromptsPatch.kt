package app.servus.patches.review

import app.morphe.patcher.patch.bytecodePatch
import app.servus.patches.shared.Constants.COMPATIBILITY_SERVUS_TV
import app.servus.patches.shared.storeFalseInConstructors
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

@Suppress("unused")
val disableReviewPromptsPatch = bytecodePatch(
    name = "Disable review prompts",
    description = "Stops the app from asking for a Play Store rating after watching videos or adding favorites.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_SERVUS_TV)

    // The only automatic rating prompt is Google Play's in-app review sheet, triggered after adding favorites and
    // after watching videos. The backend config becomes an InAppReviewConfig, and the review repository checks its
    // isEnabled both when counting a trigger and when deciding whether to show the prompt; with isEnabled false it
    // logs "In-app review is disabled" and never shows it. The app's own default config already has isEnabled
    // false, so forcing it on construction reuses a path the app already handles.
    //
    // The "Rate this app" settings entry opens the store listing directly and is not affected.
    execute {
        val isEnabledField = InAppReviewConfigToStringFingerprint.instructionMatches.last()
            .getInstruction<ReferenceInstruction>().reference as FieldReference
        InAppReviewConfigToStringFingerprint.classDef.storeFalseInConstructors(isEnabledField)
    }
}
