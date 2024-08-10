package ink.chyk.navigationswitcher

import android.content.Context

class StorageHelper {
    fun getRhythmGamePackageNames(context: Context): List<String> {
        val sharedPref = context.getSharedPreferences("rhythm_game_packages", Context.MODE_PRIVATE)
        return sharedPref.getString("packages",
            "com.Reflektone.AstroDX\n" +
                    "com.PigeonGames.Phigros\n" +
                    "moe.low.arc\n" +
                    "sh.ppy.osulazer\n" +
                    "com.sega.pjsekai\n" +
                    "com.hermes.mk.asia"
        )!!.split("\n")
    }

    fun setRhythmGamePackageNames(context: Context, packages: List<String>) {
        val sharedPref = context.getSharedPreferences("rhythm_game_packages", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString("packages", packages.joinToString("\n"))
            apply()
        }
    }
}