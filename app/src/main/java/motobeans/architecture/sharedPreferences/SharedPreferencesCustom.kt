package motobeans.architecture.sharedPreferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by munishkumarthakur on 04/11/17.
 */

class SharedPreferencesCustom {

    private var context: Context? = null
    private var KEY_PREFERENCE: String? = null

    private val sharedPreferences: SharedPreferences
        get() = context!!.getSharedPreferences(KEY_PREFERENCE, Context.MODE_PRIVATE)

    constructor(context: Context, KEY_PREFERENCE: String) {
        this.KEY_PREFERENCE = KEY_PREFERENCE
        this.context = context
    }

    private fun getFromSharedPreferences(KEY: String): String? {
        var VALUE: String?
        try {
            val SP = sharedPreferences
            VALUE = SP.getString(KEY, null)
        } catch (e: Exception) {
            VALUE = null
        }

        return VALUE
    }

    private fun putInSharedPreferences(KEY: String, VALUE: String) {
        try {
            val SP = context!!.getSharedPreferences(KEY_PREFERENCE, Context.MODE_PRIVATE)

            val SP_editor = SP.edit()
            SP_editor.putString(KEY, VALUE)
            SP_editor.commit()
        } catch (e: Exception) {

        }

    }

    fun putString(KEY: String, VALUE: String) {
        putInSharedPreferences(KEY, VALUE)
    }

    fun getString(KEY: String): String? {
        return getFromSharedPreferences(KEY)
    }

    fun clearSharedPreferences() {

        val SP = context!!.getSharedPreferences(KEY_PREFERENCE, Context.MODE_PRIVATE)
        val editor = SP.edit()
        editor.clear()
        editor.apply()
    }
}