package motobeans.architecture.util

import motobeans.architecture.util.DateUtil.dateFormattingType.DEFAULT
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by munishkumarthakur on 17/11/17.
 */
class DateUtil {

    enum class dateFormattingType constructor(value: String) {
        TYPE_API_REQUEST("MM/dd/yyyy HH:mm.ss a"), TYPE_API_RESPONSE("MM/dd/yyyy HH:mm.ss a"),
        TYPE_API_REQUEST_2("yyyy-MM-dd"),
        TYPE_NORMAL_1("dd-MMM-yyyy"),
        DEFAULT("MMMM dd, yyyy");

        internal var value: String? = null

        init {
            this.value = value
        }
    }

    fun getCurrentFormattedDate(type: dateFormattingType): String {
        val cal = Calendar.getInstance()
        return getFormattedDate(type, cal)
    }

    fun getDynamicFormattedDate(type: dateFormattingType, days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, days)
        return getFormattedDate(type, cal)
    }

    fun getCalender(dateInString: String, formatType: dateFormattingType): Calendar? {
        var formatter = SimpleDateFormat(DEFAULT.value)

        formatType?.let {
            formatter = SimpleDateFormat(formatType.value)
        }
        var cal: Calendar? = null
        var datetime: Date? = null
        try {

            datetime = formatter.parse(dateInString)
            // create a calendar
            cal = Calendar.getInstance()
            cal!!.time = datetime
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return cal
    }

    fun getCalender(year: Int, monthOfYear: Int, dayOfMonth: Int): Calendar {
        val cal = GregorianCalendar()
        cal.set(year, monthOfYear, dayOfMonth, 0, 0)
        return cal
    }

    fun getFormattedDate(type: dateFormattingType, cal: Calendar?): String {
        return SimpleDateFormat(type.value!!).format(cal!!.time)
    }

    fun getFormattedDate(from: dateFormattingType, to: dateFormattingType,
                         dateInString: String?): String {
        if (dateInString == null) return ""

        var formattedDate: String? = null
        try {
            val formatter = SimpleDateFormat(from.value!!)
            var cal: Calendar? = null

            var datetime: Date? = null
            try {

                datetime = formatter.parse(dateInString)
                // create a calendar
                cal = Calendar.getInstance()
                cal!!.time = datetime
            } catch (e: Exception) {
                e.printStackTrace()
            }

            formattedDate = getFormattedDate(to, cal)
        } catch (e: Exception) {

        }

        return if (formattedDate != null) formattedDate else dateInString
    }

    //Convert Date to Calendar
    fun dateToCalendar(date: Date): Calendar {

        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

    //Convert Calendar to Date
    fun calendarToDate(calendar: Calendar): Date {
        return calendar.time
    }

    //Convert Calendar to Date
    fun getDateFromString(dateInString: String, formatType: dateFormattingType): Date {
        return calendarToDate(getCalender(dateInString, formatType)!!)
    }

    fun getCurrentDate(): String {
        return getCurrentFormattedDate(DEFAULT)
    }

    fun getDynamicDate(days: Int): String {
        return getDynamicFormattedDate(DEFAULT, days)
    }

    fun getCurrentDateInMillis(): Long {
        return System.currentTimeMillis()
    }

    fun getCurrentDateInMillisAsString(): String {
        return "${System.currentTimeMillis()}"
    }

    fun getCurrentDateInMillisOnlyDate(): Long {
        val currentDate = DateUtil().getDateFromString(DateUtil().getCurrentDate(), DEFAULT)
        return currentDate.time
    }

    fun getDynamicDateInMillisOnlyDate(dayCountFromNow: Int): Long {
        val currentDate = DateUtil().getDateFromString(DateUtil().getDynamicDate(dayCountFromNow),
                DEFAULT)
        return currentDate.time
    }

    fun getMillisFromString(dateInString: String, formatType: dateFormattingType): Long {
        return getCalender(dateInString = dateInString, formatType = formatType)?.timeInMillis ?: 0
    }

    fun getAppFormattedDate(timeInMillisToFormat: Long): String {
        when (timeInMillisToFormat > 0) {
            true -> {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = timeInMillisToFormat

                val currentDate = getFormattedDate(DEFAULT, calendar)
                return currentDate
            }
            false -> {
                return "NA"
            }
        }
    }
}