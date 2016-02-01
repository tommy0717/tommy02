package com.appspot.tommy02;

import java.util.Calendar;

/**
 * 指定された日付が休日か判定するインターフェース
 */
public interface DayOffResolver {

    /**
     * 指定された日付が休日か判定します。
     *
     * @param cal    日付
     * @return 日付が休日の場合は true
     */
    boolean isDayOff(Calendar cal);
}