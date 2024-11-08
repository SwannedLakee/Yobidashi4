package jp.toastkid.yobidashi4.domain.model.tab

import java.time.LocalDate

class CalendarTab(
    private val year: Int = LocalDate.now().year,
    private val month: Int = LocalDate.now().month.value
): Tab {

    override fun title(): String {
        return "Calendar"
    }

    override fun closeable(): Boolean {
        return true
    }

    fun localDate(): LocalDate = LocalDate.of(year, month, 1)

}