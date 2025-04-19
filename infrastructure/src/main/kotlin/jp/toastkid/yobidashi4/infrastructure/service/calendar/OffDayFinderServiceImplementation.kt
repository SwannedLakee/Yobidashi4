package jp.toastkid.yobidashi4.infrastructure.service.calendar

import jp.toastkid.yobidashi4.domain.model.calendar.FixedJapaneseHoliday
import jp.toastkid.yobidashi4.domain.service.article.OffDayFinderService
import jp.toastkid.yobidashi4.domain.service.calendar.EquinoxDayCalculator
import jp.toastkid.yobidashi4.domain.service.calendar.MoveableHolidayCalculatorService
import jp.toastkid.yobidashi4.domain.service.calendar.SpecialCaseOffDayCalculatorService
import jp.toastkid.yobidashi4.domain.service.calendar.UserOffDayService
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.DayOfWeek

@Single
class OffDayFinderServiceImplementation : OffDayFinderService, KoinComponent {

    private val equinoxDayCalculator: EquinoxDayCalculator = EquinoxDayCalculator()

    private val moveableHolidayCalculatorService: MoveableHolidayCalculatorService = MoveableHolidayCalculatorService()

    private val specialCaseOffDayCalculator: SpecialCaseOffDayCalculatorService = SpecialCaseOffDayCalculatorService()

    private val userOffDayService: UserOffDayService by inject()

    override operator fun invoke(year: Int, month: Int, date: Int, dayOfWeek: DayOfWeek, useUserOffDay: Boolean): Boolean {
        if (month == 6) {
            return false
        }

        if (month == 3 && isVernalEquinoxDay(year, date)) {
            return true
        }

        if (month == 9 && isAutumnalEquinoxDay(year, date)) {
            return true
        }

        val isSpecialCases = specialCaseOffDayCalculator(year, month)
        if (isSpecialCases.any { it.month == month && it.day == date }) {
            return true
        }

        if (moveableHolidayCalculatorService(year, month, date)) {
            return true
        }

        if (useUserOffDay && userOffDayService(month, date)) {
            return true
        }

        var firstOrNull = FixedJapaneseHoliday.entries
            .firstOrNull { month == it.month && date == it.date }
        if (firstOrNull == null) {
            if (month == 5 && date == 6 && dayOfWeek <= DayOfWeek.WEDNESDAY) {
                return true
            }
            if (dayOfWeek == DayOfWeek.MONDAY) {
                firstOrNull = FixedJapaneseHoliday.entries
                    .firstOrNull { month == it.month && (date - 1) == it.date }
            }
        }
        return firstOrNull != null
    }

    private fun isVernalEquinoxDay(year: Int, date: Int): Boolean {
        val vernalEquinoxDay = equinoxDayCalculator.calculateVernalEquinoxDay(year) ?: return false
        return date == vernalEquinoxDay.day
    }

    private fun isAutumnalEquinoxDay(year: Int, date: Int): Boolean {
        val autumnalEquinoxDay = equinoxDayCalculator.calculateAutumnalEquinoxDay(year) ?: return false
        return date == autumnalEquinoxDay.day
    }

}
