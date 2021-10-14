package motobeans.architecture.util

import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator


/**
 * Created by munishkumarthakur on 04/01/18.
 */
class TextDrawableUtil {
    fun buildRound(key: String?): TextDrawable {
        var character = "~"
        key?.let {
            if (key.exIsNotEmptyOrNullOrBlank()) {
                character = key.substring(0, 1)
            }
            character = character.capitalize()

            return TextDrawable.builder()
                .buildRound(character, getRandomColor(key))
        }

        return TextDrawable.builder()
            .buildRound(character, getRandomColor(character))
    }

    fun buildRect(key: String?): TextDrawable {
        var character = "~"
        key?.let {
            if (key.exIsNotEmptyOrNullOrBlank()) {
                character = key.substring(0, 1)
            }
            character = character.capitalize()

            return TextDrawable.builder()
                .buildRect(character, getRandomColor(key))
        }

        return TextDrawable.builder()
            .buildRect(character, getRandomColor(character))
    }

    private fun getRandomColor(key: String?): Int {
        val generator = ColorGenerator.MATERIAL

        // generate color based on a key (same key returns the same color), useful for list/grid views
        return generator.getColor(key)
    }
}