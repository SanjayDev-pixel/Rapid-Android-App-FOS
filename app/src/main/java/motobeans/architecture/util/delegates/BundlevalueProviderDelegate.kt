package motobeans.architecture.util.delegates

import android.app.Activity
import android.util.Size
import android.util.SizeF
import java.io.Serializable
import java.util.ArrayList

/**
 * Created by munishkumarthakur on 29/12/17.
 */
inline fun <reified T : Any> androidx.fragment.app.Fragment.BundleValueProvider(activity: androidx.fragment.app.Fragment, key: String) = lazy {

    var value: T? = null

    when (T::class) {
        Int::class -> value = activity.arguments?.getInt(key) as T
        IntArray::class -> value = activity.arguments?.getIntArray(key) as T
        ArrayList::class -> value = activity.arguments?.getIntegerArrayList(key) as T
        String::class -> value = activity.arguments?.getString(key) as T
        ArrayList::class -> value = activity.arguments?.getStringArrayList(key) as T
        Boolean::class -> value = activity.arguments?.getBoolean(key) as T
        BooleanArray::class -> value = activity.arguments?.getBooleanArray(key) as T
        Char::class -> value = activity.arguments?.getChar(key) as T
        CharArray::class -> value = activity.arguments?.getCharArray(key) as T
        CharSequence::class -> value = activity.arguments?.getCharSequence(key) as T
        ArrayList::class -> value = activity.arguments?.getCharSequenceArrayList(key) as T
        Long::class -> value = activity.arguments?.getLong(key) as T
        LongArray::class -> value = activity.arguments?.getLongArray(key) as T
        Float::class -> value = activity.arguments?.getFloat(key) as T
        FloatArray::class -> value = activity.arguments?.getFloatArray(key) as T
        Double::class -> value = activity.arguments?.getDouble(key) as T
        DoubleArray::class -> value = activity.arguments?.getDoubleArray(key) as T
        Byte::class -> value = activity.arguments?.getByte(key) as T
        ByteArray::class -> value = activity.arguments?.getByteArray(key) as T
        Short::class -> value = activity.arguments?.getShort(key) as T
        ShortArray::class -> value = activity.arguments?.getShortArray(key) as T
        Size::class -> value = activity.arguments?.getSize(key) as T
        SizeF::class -> value = activity.arguments?.getSizeF(key) as T
        Serializable::class -> value = activity.arguments?.getSerializable(key) as T
    }

    value
}


inline fun <reified T> Activity.BundleValueProvider(activity: Activity, key: String) = lazy {

    var value: T? = null

    when (T::class) {
        Int::class -> value = activity.intent.extras?.getInt(key) as T
        IntArray::class -> value = activity.intent.extras?.getIntArray(key) as T
        ArrayList::class -> value = activity.intent.extras?.getIntegerArrayList(key) as T
        String::class -> value = activity.intent.extras?.getString(key) as T
        ArrayList::class -> value = activity.intent.extras?.getStringArrayList(key) as T
        Boolean::class -> value = activity.intent.extras?.getBoolean(key) as T
        BooleanArray::class -> value = activity.intent.extras?.getBooleanArray(key) as T
        Char::class -> value = activity.intent.extras?.getChar(key) as T
        CharArray::class -> value = activity.intent.extras?.getCharArray(key) as T
        CharSequence::class -> value = activity.intent.extras?.getCharSequence(key) as T
        ArrayList::class -> value = activity.intent.extras?.getCharSequenceArrayList(key) as T
        Long::class -> value = activity.intent.extras?.getLong(key) as T
        LongArray::class -> value = activity.intent.extras?.getLongArray(key) as T
        Float::class -> value = activity.intent.extras?.getFloat(key) as T
        FloatArray::class -> value = activity.intent.extras?.getFloatArray(key) as T
        Double::class -> value = activity.intent.extras?.getDouble(key) as T
        DoubleArray::class -> value = activity.intent.extras?.getDoubleArray(key) as T
        Byte::class -> value = activity.intent.extras?.getByte(key) as T
        ByteArray::class -> value = activity.intent.extras?.getByteArray(key) as T
        Short::class -> value = activity.intent.extras?.getShort(key) as T
        ShortArray::class -> value = activity.intent.extras?.getShortArray(key) as T
        Size::class -> value = activity.intent.extras?.getSize(key) as T
        SizeF::class -> value = activity.intent.extras?.getSizeF(key) as T
        Serializable::class -> value = activity.intent.extras?.getSerializable(key) as T
    }

    value
}
