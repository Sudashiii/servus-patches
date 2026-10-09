package app.servus.patches.review

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.InstructionLocation.MatchAfterWithin
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.Opcode

/**
 * `InAppReviewConfig.toString()` (Kotlin data class, obfuscated as e.g. `w45`):
 *
 * ```
 * const-string v1, "InAppReviewConfig(isEnabled="
 * invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V
 * iget-boolean v1, p0, Lw45;->a:Z   <- the isEnabled field
 * ```
 *
 * The strings survive obfuscation, so this finds the config class and its isEnabled field in any app version.
 */
object InAppReviewConfigToStringFingerprint : Fingerprint(
    name = "toString",
    returnType = "Ljava/lang/String;",
    parameters = listOf(),
    filters = listOf(
        string("InAppReviewConfig(isEnabled="),
        fieldAccess(
            opcode = Opcode.IGET_BOOLEAN,
            definingClass = "this",
            type = "Z",
            location = MatchAfterWithin(2),
        ),
    ),
)
