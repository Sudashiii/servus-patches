package app.servus.patches.shared

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.util.proxy.mutableTypes.MutableClass
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

/**
 * Makes every constructor of this class store false into the boolean [field], whatever was passed in.
 */
internal fun MutableClass.storeFalseInConstructors(field: FieldReference) {
    var patched = 0
    methods.filter { it.name == "<init>" }.forEach { constructor ->
        val stores = constructor.implementation!!.instructions.withIndex().filter { (_, instruction) ->
            instruction.opcode == Opcode.IPUT_BOOLEAN &&
                (instruction as ReferenceInstruction).reference.let {
                    it is FieldReference && it.name == field.name && it.type == field.type &&
                        it.definingClass == field.definingClass
                }
        }

        // Last to first, so earlier indices stay valid while inserting.
        stores.reversed().forEach { (index, instruction) ->
            // iput-boolean vA, p0, field  ->  const/4 vA, 0x0 ; iput-boolean vA, p0, field
            val register = (instruction as TwoRegisterInstruction).registerA
            val const = if (register < 16) "const/4" else "const/16"
            constructor.addInstruction(index, "$const v$register, 0x0")
            patched++
        }
    }

    if (patched == 0) throw PatchException("No constructor of $type stores $field")
}
