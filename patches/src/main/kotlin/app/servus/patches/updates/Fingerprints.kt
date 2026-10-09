package app.servus.patches.updates

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.InstructionLocation.MatchAfterWithin
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.Opcode

/**
 * `InAppUpdatesConfig.toString()` (Kotlin data class, obfuscated as e.g. `f55`):
 *
 * ```
 * const-string v1, "InAppUpdatesConfig(isEnabled="
 * invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V
 * iget-boolean v1, p0, Lf55;->a:Z   <- the isEnabled field
 * ```
 *
 * The strings survive obfuscation, so this finds the config class and its isEnabled field in any app version.
 */
object InAppUpdatesConfigToStringFingerprint : Fingerprint(
    name = "toString",
    returnType = "Ljava/lang/String;",
    parameters = listOf(),
    filters = listOf(
        string("InAppUpdatesConfig(isEnabled="),
        fieldAccess(
            opcode = Opcode.IGET_BOOLEAN,
            definingClass = "this",
            type = "Z",
            location = MatchAfterWithin(2),
        ),
    ),
)

/**
 * `InAppMessageModel(shouldBlockUserJourney, titleKey, titleFallback, messageKey, messageFallback, messageType)`,
 * the backend in-app message (e.g. "Update erforderlich"). Class, constructor and field names are not obfuscated:
 *
 * ```
 * iput-boolean p1, p0, Lrbak/dtv/foundation/android/models/shared/InAppMessageModel;->shouldBlockUserJourney:Z
 * ```
 */
object InAppMessageModelConstructorFingerprint : Fingerprint(
    definingClass = "Lrbak/dtv/foundation/android/models/shared/InAppMessageModel;",
    name = "<init>",
    returnType = "V",
    filters = listOf(
        fieldAccess(
            opcode = Opcode.IPUT_BOOLEAN,
            definingClass = "this",
            name = "shouldBlockUserJourney",
            type = "Z",
        ),
    ),
)
