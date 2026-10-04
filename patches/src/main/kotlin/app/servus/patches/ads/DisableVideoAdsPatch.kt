package app.servus.patches.ads

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.servus.patches.shared.Constants.COMPATIBILITY_SERVUS_TV
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

@Suppress("unused")
val disableVideoAdsPatch = bytecodePatch(
    name = "Disable video ads",
    description = "Removes pre-roll and mid-roll ads (Google IMA) from videos and live channels.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_SERVUS_TV)

    // Every video/channel is described by a PlayerMediaModel. Its adUrl is the IMA ad tag; when it is
    // null, PlayerController builds the MediaItem without an AdsConfiguration and ExoPlayer plays the
    // content directly. That is the same path the app takes when ads are disabled server-side or for
    // brands without ads, so nulling adUrl on construction reuses a code path the app already supports.
    execute {
        val adUrlField = PlayerMediaModelToStringFingerprint.instructionMatches.last()
            .getInstruction<ReferenceInstruction>().reference as FieldReference

        var patched = 0
        PlayerMediaModelToStringFingerprint.classDef.methods
            .filter { it.name == "<init>" }
            .forEach { constructor ->
                val stores = constructor.implementation!!.instructions.withIndex().filter { (_, instruction) ->
                    instruction.opcode == Opcode.IPUT_OBJECT &&
                        (instruction as ReferenceInstruction).reference.let {
                            it is FieldReference && it.name == adUrlField.name && it.type == adUrlField.type &&
                                it.definingClass == adUrlField.definingClass
                        }
                }

                // Last to first, so earlier indices stay valid while inserting.
                stores.reversed().forEach { (index, instruction) ->
                    // iput-object vA, p0, adUrl  ->  const/4 vA, 0x0 ; iput-object vA, p0, adUrl
                    val register = (instruction as TwoRegisterInstruction).registerA
                    val const = if (register < 16) "const/4" else "const/16"
                    constructor.addInstruction(index, "$const v$register, 0x0")
                    patched++
                }
            }

        if (patched == 0) throw PatchException("No constructor stores PlayerMediaModel.adUrl ($adUrlField)")
    }
}
