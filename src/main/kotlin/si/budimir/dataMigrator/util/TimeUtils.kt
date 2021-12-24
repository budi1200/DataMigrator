package si.budimir.dataMigrator.util

import org.joda.time.DateTimeZone
import org.joda.time.Instant
import org.joda.time.format.DateTimeFormat

class TimeUtils {
    companion object {
        fun prettyPrintTimestamp(timestamp: Long): String {
            val formatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").withZone(DateTimeZone.forID("Europe/Ljubljana"))
            val converted = Instant.ofEpochMilli(timestamp)

            return formatter.print(converted)
        }
    }
}