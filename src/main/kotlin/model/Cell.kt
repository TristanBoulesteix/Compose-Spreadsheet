package fr.tb_lab.model

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.tb_lab.model.parser.scan
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = CellSerializer::class)
class Cell {
    var content by mutableStateOf("")

    val tokenizedContent by derivedStateOf { scan(content) }
}

private object CellSerializer : KSerializer<Cell> {
    override val descriptor = PrimitiveSerialDescriptor("cellValue", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder) = Cell().apply {
        content = decoder.decodeString()
    }

    override fun serialize(encoder: Encoder, value: Cell) = encoder.encodeString(value.content)
}