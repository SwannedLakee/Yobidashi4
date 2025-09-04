package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek

class DayLabelViewModel {

    fun makeText(date: Int): String {
        return if (date == -1) "" else "$date"
    }

    fun textColor(dayOfWeek: DayOfWeek, offDay: Boolean, today: Boolean): Color? = when (dayOfWeek) {
        DayOfWeek.SUNDAY -> OFF_DAY_FG
        DayOfWeek.SATURDAY -> SATURDAY_FG
        else -> if (offDay) OFF_DAY_FG else if (today) Color.White else null
    }

    fun labelSize(label: String?): TextUnit {
        return if (label.isNullOrEmpty()) 12.sp else 10.sp
    }

    fun labelColor(): Color {
        return OFF_DAY_FG
    }

    fun useOffDayBackground(offDay: Boolean, dayOfWeek: DayOfWeek): Boolean {
        return offDay || dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
    }

}

private val OFF_DAY_FG:  Color = Color(220, 50, 55)
private val SATURDAY_FG:  Color = Color(165, 160, 255)
