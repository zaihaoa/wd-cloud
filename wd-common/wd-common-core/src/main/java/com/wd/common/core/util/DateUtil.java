package com.wd.common.core.util;



import com.wd.common.core.annotions.NotNull;
import com.wd.common.core.annotions.Nullable;
import com.wd.common.core.model.RequestDateRange;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 时间处理工具列
 *
 * @author huangwenda
 * @date 2022/9/16 11:15
 **/
public class DateUtil {


    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String UTC_SECOND = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final String UTC_MS = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    public static final String TIME_ZONE_UTC = "UTC";
    public static final String TIME_ZONE_SHANGHAI = "Asia/Shanghai";
    public static final String TIME_ZONE_AMERICA_LOS_ANGELES = "America/Los_Angeles";

    /**
     * UTC时间转北京时间(秒)
     * 北京时间时区：Asia/Shanghai
     *
     * @param dateStr 时间字符串,2021-12-29T16:52:05+07:00
     * @return 2021-12-29T17:52:05
     */
    public static LocalDateTime utcSecondToShanghai(@Nullable String dateStr) {
        if (isEmpty(dateStr)) {
            return null;
        }
        OffsetDateTime parse = OffsetDateTime.parse(dateStr, DateTimeFormatter.ofPattern(UTC_SECOND));
        return LocalDateTime.ofInstant(parse.toInstant(), ZoneOffset.of("+08:00"));
    }

    /**
     * UTC时间转北京时间(毫秒)
     * 北京时间时区：Asia/Shanghai
     *
     * @param dateStr 时间字符串,2021-12-29T16:52:05.234+07:00
     * @return 2021-12-29T17:52:05
     */
    public static LocalDateTime utcMsToShanghai(@Nullable String dateStr) {
        if (isEmpty(dateStr)) {
            return null;
        }
        OffsetDateTime parse = OffsetDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return LocalDateTime.ofInstant(parse.toInstant(), ZoneOffset.of("+08:00"));
    }

    /**
     * UTC时间转北京时间(毫秒)
     * 北京时间时区：Asia/Shanghai
     *
     * @param dateStr 时间字符串,2021-12-29T16:52:05.234+07:00
     * @return 2021-12-29T17:52:05
     */
    public static LocalDateTime toUtc(@Nullable String dateStr) {
        if (isEmpty(dateStr)) {
            return null;
        }
        OffsetDateTime parse = OffsetDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return LocalDateTime.ofInstant(parse.toInstant(), ZoneOffset.UTC);
    }

    /**
     * 北京时间转为UTC时间格式
     *
     * @param shanghaiTime 北京时间
     * @return UTC时间格式(字符串)
     */
    public static String shanghaiToUtc(@Nullable LocalDateTime shanghaiTime) {
        if (shanghaiTime == null) {
            return null;
        }
        OffsetDateTime offsetDateTime = OffsetDateTime.of(shanghaiTime, ZoneOffset.of("+8"));
        return offsetDateTime.format(DateTimeFormatter.ofPattern(UTC_MS));
    }

    /**
     * 北京时间转为UTC时间格式
     *
     * @param shanghaiTime 北京时间
     * @return OffsetDateTime
     */
    public static OffsetDateTime shanghaiToOffsetDateTime(@Nullable LocalDateTime shanghaiTime) {
        if (shanghaiTime == null) {
            return null;
        }
        return OffsetDateTime.of(shanghaiTime, ZoneOffset.of("+8"));
    }

    /**
     * OffsetDateTime转北京时间
     *
     * @param offsetDateTime OffsetDateTime
     * @return 北京时间
     */
    public static LocalDateTime offsetDateTimeToShanghaiTime(@Nullable OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime
                .atZoneSameInstant(ZoneId.of(TIME_ZONE_SHANGHAI))
                .toLocalDateTime();
    }

    /**
     * 北京时间转为UTC时间格式
     *
     * @param shanghaiTime 北京时间
     * @return UTC时间格式(字符串)
     */
    public static String shanghaiToUtcSecond(@Nullable LocalDateTime shanghaiTime) {
        if (shanghaiTime == null) {
            return null;
        }
        OffsetDateTime offsetDateTime = OffsetDateTime.of(shanghaiTime, ZoneOffset.of("+8"));
        return offsetDateTime.format(DateTimeFormatter.ofPattern(UTC_SECOND));
    }

    /**
     * 获取开始时间到结束时间之间，每个周期的时间范围
     *
     * @param beginTime   开始时间
     * @param endTime     结束时间
     * @param intervalDay 每个周期间隔多少天
     * @return 切割后的时间周期
     */
    public static List<RequestDateRange> getEveryPeriod(LocalDateTime beginTime, LocalDateTime endTime, int intervalDay) {

        if (intervalDay <= 0) {
            return Collections.singletonList(new RequestDateRange(beginTime, endTime));
        }

        List<RequestDateRange> periodList = new ArrayList<>();
        while (true) {
            LocalDateTime partBeginTime = beginTime;
            LocalDateTime partEndTime = partBeginTime.plusDays(intervalDay);

            if (partEndTime.compareTo(endTime) <= 0) {
                periodList.add(new RequestDateRange(partBeginTime, partEndTime));

                beginTime = partEndTime;
            } else {
                periodList.add(new RequestDateRange(partBeginTime, endTime));
                break;
            }
        }

        return periodList;
    }

    /**
     * 时间戳转化为零时区的LocalDateTime
     * 例如:
     * 时间戳:1665457449(北京时间2022-10-11 11:04:09)
     * 输出:2022-10-11 03:04:09
     *
     * @param timestampSecond 时间戳(秒)
     * @return 零时区的LocalDateTime
     */
    public static LocalDateTime timestampSecondToUtc(@Nullable Integer timestampSecond) {
        if (timestampSecond == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestampSecond).atZone(ZoneId.of("UTC")).toLocalDateTime();
    }


    /**
     * 时间戳转化为零时区的LocalDateTime
     * 例如:
     * 时间戳:1665457449123(北京时间2022-10-11 11:04:09)
     * 输出:2022-10-11 03:04:09
     *
     * @param timestampMilli 时间戳(毫秒)
     * @return 零时区的LocalDateTime
     */
    public static LocalDateTime timestampMillToUtc(@Nullable Long timestampMilli) {
        if (timestampMilli == null) {
            return null;
        }
        return Instant.ofEpochMilli(timestampMilli).atZone(ZoneId.of("UTC")).toLocalDateTime();
    }


    /**
     * LocalDateTime转秒级时间戳
     *
     * @param dateTime LocalDateTime
     * @return 时间戳(秒)
     */
    public static int toTimestampSecond(@NotNull LocalDateTime dateTime) {
        return (int) dateTime.toEpochSecond(ZoneOffset.of("+8"));
    }

    /**
     * LocalDateTime转毫秒级时间戳
     *
     * @param dateTime LocalDateTime
     * @return 时间戳(毫秒)
     */
    public static long toTimestampMilli(@NotNull LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 转换时区
     *
     * @param time           time
     * @param sourceTimeZone 转换前的时区 如：Asia/Shanghai
     * @param targetTimeZone 转换后的时区 如：US/Pacific
     * @return 转化后的时间
     */
    public static LocalDateTime timeZoneConvert(@Nullable LocalDateTime time,
                                                @NotNull String sourceTimeZone,
                                                @NotNull String targetTimeZone) {
        if (Objects.isNull(time)) {
            return null;
        }
        return time.atZone(ZoneId.of(sourceTimeZone)).withZoneSameInstant(ZoneId.of(targetTimeZone)).toLocalDateTime();
    }

    /**
     * 转换时区
     *
     * @param time           time
     * @param sourceTimeZone 转换前的时区 如：Asia/Shanghai
     * @param targetTimeZone 转换后的时区 如：US/Pacific
     * @return 转化后的时间
     */
    public static LocalDateTime timeZoneConvert(@Nullable LocalDateTime time,
                                                @NotNull ZoneId sourceTimeZone,
                                                @NotNull ZoneId targetTimeZone) {
        if (Objects.isNull(time)) {
            return null;
        }
        return time.atZone(sourceTimeZone).withZoneSameInstant(targetTimeZone).toLocalDateTime();
    }


    /**
     * 获取年月
     * 格式yyyy-MM
     *
     * @param time 时间
     * @return yyyy-MM(2022-12)
     */
    public static String getMonthStr(@Nullable LocalDateTime time) {
        if (Objects.isNull(time)) {
            return "";
        }
        return joinMonthStr(time.getYear(), time.getMonthValue());
    }

    /**
     * 获取时间段之间的月份
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return ['2022-01','2022-02']
     */
    public static List<String> getBetweenMonth(LocalDateTime beginTime, LocalDateTime endTime) {
        if (Objects.isNull(beginTime) || Objects.isNull(endTime)) {
            return new ArrayList<>();
        }

        List<String> monthStrList = new ArrayList<>();

        LocalDate beginDate = LocalDate.of(beginTime.getYear(), beginTime.getMonthValue(), 1);
        LocalDate endDate = LocalDate.of(endTime.getYear(), endTime.getMonthValue(), 1);
        do {
            String monthStr = joinMonthStr(beginDate.getYear(), beginDate.getMonthValue());
            if (!monthStrList.contains(monthStr)) {
                monthStrList.add(monthStr);
            }

            beginDate = beginDate.plusMonths(1L);
        } while (beginDate.compareTo(endDate) <= 0);

        return monthStrList;
    }

    /**
     * 获取时间段之间的月份
     *
     * @param beginDate 开始时间
     * @param endDate   结束时间
     * @return ['2022-01','2022-02']
     */
    public static List<LocalDate> getBetweenDay(LocalDate beginDate, LocalDate endDate) {
        if (Objects.isNull(beginDate) || Objects.isNull(endDate)) {
            return new ArrayList<>();
        }

        List<LocalDate> monthStrList = new ArrayList<>();

        do {
            monthStrList.add(beginDate);

            beginDate = beginDate.plusDays(1L);
        } while (!beginDate.isAfter(endDate));

        return monthStrList;
    }

    private static String joinMonthStr(int year, int monthValue) {
        return year + "-" + (monthValue < 10 ? "0" + monthValue : monthValue);
    }


    /**
     * 根据月份获取当月第一天
     *
     * @param monthStr 月份,yyyy-MM(2022-12)
     * @return 时间
     */
    public static LocalDateTime getFirstDayByMonthStr(@Nullable String monthStr) {
        if (Objects.isNull(monthStr)) {
            return null;
        }
        String[] split = monthStr.split("-");
        if (split.length != 2) {
            throw new IllegalArgumentException("参数格式不正确,例如[2022-02],错误参数:" + monthStr);
        }
        return LocalDateTime.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), 1, 0, 0, 0);
    }

    /**
     * 根据月份获取当月最后一天最后一秒
     *
     * @param monthStr 月份,yyyy-MM(2022-12)
     * @return 时间
     */
    public static LocalDateTime getLastDayByMonthStr(@Nullable String monthStr) {
        if (Objects.isNull(monthStr)) {
            return null;
        }
        String[] split = monthStr.split("-");
        if (split.length != 2) {
            throw new IllegalArgumentException("参数格式不正确,例如[2022-02],错误参数:" + monthStr);
        }
        LocalDateTime dateTime = LocalDateTime.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), 1, 23, 59, 59);
        return dateTime.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * <p>Checks if a CharSequence is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the CharSequence.
     * That functionality is available in isBlank().</p>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     * @since 3.0 Changed signature from isEmpty(String) to isEmpty(CharSequence)
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 格式化时间 分钟
     * 09时03分
     * 1月16日 09时03分
     * 2023-02-23
     *
     * @param time 时间
     * @return 格式化后的时间
     */
    public static String formatTimeAsMinute(LocalDateTime time) {
        LocalDateTime todayStartTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0));
        // 今天
        if (!time.isBefore(todayStartTime)) {
            // 展示小时:分钟
            return time.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        // 当年
        if (time.getYear() == todayStartTime.getYear()) {
            // 展示月日 小时:分钟
            return time.format(DateTimeFormatter.ofPattern("M月d日"));
        }
        // 年月日
        return time.format(DateTimeFormatter.ofPattern("yyyy年M月d日"));
    }

    /**
     * 格式化时间 秒
     * 09时03分03秒
     * 1月16日 09时03分03秒
     * 2023-02-23
     *
     * @param time 时间
     * @return 格式化后的时间
     */
    public static String formatTimeAsSeconds(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        LocalDateTime todayStartTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0));
        // 今天
        if (!time.isBefore(todayStartTime)) {
            // 展示小时:分钟
            return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        // 当年
        if (time.getYear() == todayStartTime.getYear()) {
            // 展示月日 小时:分钟
            return time.format(DateTimeFormatter.ofPattern("M月d日 HH:mm:ss"));
        }
        // 年月日
        return time.format(DateTimeFormatter.ofPattern("yyyy年M月d日"));
    }
}
