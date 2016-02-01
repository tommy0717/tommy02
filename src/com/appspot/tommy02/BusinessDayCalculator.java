package com.appspot.tommy02;

import java.util.Calendar;

/**
 * 営業日数を計算するクラス
 */
public class BusinessDayCalculator {

    /**
     * 営業日を求める基準点
     */
    public enum StartingPoint {
        THIS_YEAR,
        NEXT_YEAR,
        PREV_YEAR,
        THIS_MONTH,
        NEXT_MONTH,
        PREV_MONTH,
        THIS_WEEK,
        NEXT_WEEK,
        PREV_WEEK,
    }
    private DayOffResolver resolver_;

    /**
     * 休日判定オブジェクトを指定してBusinessDayCalculatorを生成します。
     *
     * @param resolver    休日判定オブジェクト
     * @throws NullPointerException     resolverがnullの場合
     */
    public BusinessDayCalculator(DayOffResolver resolver) {
        if (resolver == null) throw new NullPointerException();
        resolver_ = resolver;
    }

    /**
     * 指定した2つの日付の間の営業日数をカウントします。
     * カウントを開始する日付当日は含まれません。
     * 開始日付より終了日付が過去の場合は負数を返します。
     *
     * @param from    開始日付
     * @param to      終了日付
     * @return 営業日数
     */
    public int countDays(Calendar from, Calendar to) {
        int count = 0;
        Calendar cal1 = (Calendar) from.clone();
        Calendar cal2 = (Calendar) to.clone();
        int step = from.compareTo(to) <= 0 ? 1 : -1;

        if (isSameDate(cal1, cal2)) return 0;

        do {
            cal1.add(Calendar.DAY_OF_YEAR, step);
            if (!resolver_.isDayOff(cal1)) {
                count++;
            }
        } while (!isSameDate(cal1, cal2));

        return count * step;
    }

    /**
     * 指定した日付から指定営業日数を加算した日付を取得します。
     * 営業日数に負数を指定することも可能です。
     *
     * @param cal       日付
     * @param offset    加算する営業日数
     * @return 営業日数を加算した日付
     */
    public Calendar getDate(Calendar cal, int offset) {
        Calendar c = (Calendar) cal.clone();
        int count = 0;
        int step = offset > 0 ? 1 : -1;

        if (offset == 0) return c;

        do {
            c.add(Calendar.DAY_OF_YEAR, step);
            if (!resolver_.isDayOff(c)) {
                count++;
            }
        } while (count != offset * step);

        return c;
    }

    /**
     * 指定した基準点の第1営業日の日付を取得します。
     *
     * @param cal      日付
     * @param start    基準点
     * @return 第1営業日の日付
     */
    public Calendar getFirstDay(Calendar cal, StartingPoint start) {
        return getFirstDay(cal, start, 1);
    }

    /**
     * 指定した日付、基準点の第n営業日の日付を取得します。
     *
     * @param cal        日付
     * @param start      基準点
     * @param ordinal    何番目の営業日か
     * @return 第1営業日の日付
     * @throws IllegalArgumentException    ordinalが0以下の場合
     */
    public Calendar getFirstDay(Calendar cal, StartingPoint start, int ordinal) {
        if (ordinal <= 0) {
            throw new IllegalArgumentException("ordinal <= 0");
        }
        Calendar c = startingFirstDay(cal, start);
        int count = 0;

        while (true) {
            if (!resolver_.isDayOff(c)) {
                if (++count == ordinal) {
                    break;
                }
            }
            c.add(Calendar.DAY_OF_YEAR, 1);
        }

        return c;
    }

    /**
     * 指定した基準点の最後の日付からの第1営業日の日付を取得します。
     *
     * @param cal      日付
     * @param start    基準点
     * @return 第1営業日の日付
     */
    public Calendar getLastDay(Calendar cal, StartingPoint start) {
        return getLastDay(cal, start, 1);
    }

    /**
     * 指定した日付、基準点の最後の日付からの第n営業日の日付を取得します。
     *
     * @param cal        日付
     * @param start      基準点
     * @param ordinal    何番目の営業日か
     * @return 第1営業日の日付
     * @throws IllegalArgumentException    ordinalが0以下の場合
     */
    public Calendar getLastDay(Calendar cal, StartingPoint start, int ordinal) {
        if (ordinal <= 0) {
            throw new IllegalArgumentException("ordinal <= 0");
        }
        Calendar c = startingLastDay(cal, start);
        int count = 0;

        while (true) {
            if (!resolver_.isDayOff(c)) {
                if (++count == ordinal) {
                    break;
                }
            }
            c.add(Calendar.DAY_OF_YEAR, -1);
        }

        return c;
    }

    private static boolean isSameDate(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private static Calendar startingFirstDay(Calendar cal, StartingPoint start) {
        Calendar c = (Calendar) cal.clone();
        switch (start) {
            case THIS_YEAR:
                c.set(c.get(Calendar.YEAR), Calendar.JANUARY, 1);
                break;
            case NEXT_YEAR:
                c.set(c.get(Calendar.YEAR), Calendar.JANUARY, 1);
                c.add(Calendar.YEAR, 1);
                break;
            case PREV_YEAR:
                c.set(c.get(Calendar.YEAR), Calendar.JANUARY, 1);
                c.add(Calendar.YEAR, -1);
                break;
            case THIS_MONTH:
                c.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case NEXT_MONTH:
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.add(Calendar.MONTH, 1);
                break;
            case PREV_MONTH:
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.add(Calendar.MONTH, -1);
                break;
            case THIS_WEEK:
                c.add(Calendar.DAY_OF_YEAR, Calendar.SUNDAY - c.get(Calendar.DAY_OF_WEEK));
                break;
            case NEXT_WEEK:
                c.add(Calendar.DAY_OF_YEAR, Calendar.SUNDAY - c.get(Calendar.DAY_OF_WEEK));
                c.add(Calendar.DAY_OF_YEAR, 7);
                break;
            case PREV_WEEK:
                c.add(Calendar.DAY_OF_YEAR, Calendar.SUNDAY - c.get(Calendar.DAY_OF_WEEK));
                c.add(Calendar.DAY_OF_YEAR, -7);
                break;
            default:
                break;
        }
        return c;
    }

    private static Calendar startingLastDay(Calendar cal, StartingPoint start) {
        Calendar c = (Calendar) cal.clone();
        switch (start) {
            case THIS_YEAR:
                c.set(c.get(Calendar.YEAR), Calendar.DECEMBER, 31);
                break;
            case NEXT_YEAR:
                c.set(c.get(Calendar.YEAR), Calendar.DECEMBER, 31);
                c.add(Calendar.YEAR, 1);
                break;
            case PREV_YEAR:
                c.set(c.get(Calendar.YEAR), Calendar.DECEMBER, 31);
                c.add(Calendar.YEAR, -1);
                break;
            case THIS_MONTH:
                c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DATE));
                break;
            case NEXT_MONTH:
                c.add(Calendar.MONTH, 1);
                c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DATE));
                break;
            case PREV_MONTH:
                c.add(Calendar.MONTH, -1);
                c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DATE));
                break;
            case THIS_WEEK:
                c.add(Calendar.DAY_OF_YEAR, Calendar.SATURDAY - c.get(Calendar.DAY_OF_WEEK));
                break;
            case NEXT_WEEK:
                c.add(Calendar.DAY_OF_YEAR, Calendar.SATURDAY - c.get(Calendar.DAY_OF_WEEK));
                c.add(Calendar.DAY_OF_YEAR, 7);
                break;
            case PREV_WEEK:
                c.add(Calendar.DAY_OF_YEAR, Calendar.SATURDAY - c.get(Calendar.DAY_OF_WEEK));
                c.add(Calendar.DAY_OF_YEAR, -7);
                break;
            default:
                break;
        }
        return c;
    }
}