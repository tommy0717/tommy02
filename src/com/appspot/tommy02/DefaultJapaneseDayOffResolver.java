package com.appspot.tommy02;

import java.util.Calendar;

/**
 * 日本の一般的な休日を判定するクラス
 *
 * 土日、および国民の祝日、振替休日、国民の休日を休日と判定します。
 */
public class DefaultJapaneseDayOffResolver implements DayOffResolver {

    @Override
    public boolean isDayOff(Calendar cal) {
        // 土日か？
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        }

        // 国民の祝日か？
        if (JapaneseHolidayUtils.isHoliday(cal)) {
            return true;
        }

        return false;
    }
}