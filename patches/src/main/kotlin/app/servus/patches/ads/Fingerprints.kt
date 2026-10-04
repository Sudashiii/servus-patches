package app.servus.patches.ads

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.InstructionLocation.MatchAfterWithin
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.Opcode

/**
 * `PlayerMediaModel.toString()` (Kotlin data class, obfuscated as e.g. `j98`):
 *
 * ```
 * const-string v1, ", adUrl="
 * invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
 * iget-object v1, p0, Lj98;->e:Ljava/lang/String;   <- the adUrl field
 * ```
 *
 * The strings survive obfuscation, so this finds the model class and its adUrl field in any app version.
 */
object PlayerMediaModelToStringFingerprint : Fingerprint(
    name = "toString",
    returnType = "Ljava/lang/String;",
    parameters = listOf(),
    filters = listOf(
        string("PlayerMediaModel(videoId="),
        string(", adUrl="),
        fieldAccess(
            opcode = Opcode.IGET_OBJECT,
            definingClass = "this",
            type = "Ljava/lang/String;",
            location = MatchAfterWithin(2),
        ),
    ),
)
