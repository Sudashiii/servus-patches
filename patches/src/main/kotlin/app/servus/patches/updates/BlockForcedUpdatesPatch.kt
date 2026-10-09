package app.servus.patches.updates

import app.morphe.patcher.patch.bytecodePatch
import app.servus.patches.shared.Constants.COMPATIBILITY_SERVUS_TV
import app.servus.patches.shared.storeFalseInConstructors
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

@Suppress("unused")
val blockForcedUpdatesPatch = bytecodePatch(
    name = "Block forced updates",
    description = "Disables Google Play in-app update prompts and makes 'Update required' messages dismissible, " +
        "so the patched app can't be locked out by a forced update.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_SERVUS_TV)

    // A patched build is signed with a different key and can never be updated through the Play Store, so a
    // forced update would lock it out for good. The backend can force one in two independent ways:
    //
    // 1. Google Play in-app updates: the backend's AppUpdatesModel becomes an InAppUpdatesConfig, and anything
    //    that isn't FLEXIBLE becomes IMMEDIATE (Play's blocking full-screen update). Both the availability check
    //    and the update request first ask the update manager's gate, which returns false and logs
    //    "In-app updates disabled in config" when isEnabled is false. The backend can send enabled=false itself,
    //    so forcing isEnabled to false on construction reuses a path the app already handles.
    //
    // 2. Backend in-app messages (InAppMessageModel), e.g. APP_UPDATE "Update erforderlich". The app ignores the
    //    dismiss while shouldBlockUserJourney is true, so a blocking message can't be closed. With it false, the
    //    message is still shown (the update stays visible) but closes after its button opens the store.
    execute {
        val isEnabledField = InAppUpdatesConfigToStringFingerprint.instructionMatches.last()
            .getInstruction<ReferenceInstruction>().reference as FieldReference
        InAppUpdatesConfigToStringFingerprint.classDef.storeFalseInConstructors(isEnabledField)

        val shouldBlockUserJourneyField = InAppMessageModelConstructorFingerprint.instructionMatches.last()
            .getInstruction<ReferenceInstruction>().reference as FieldReference
        InAppMessageModelConstructorFingerprint.classDef.storeFalseInConstructors(shouldBlockUserJourneyField)
    }
}
