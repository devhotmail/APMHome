package webapp.framework.util;

import java.util.Date;

public class DateUtil {

    /**
     * Compare 2 dates using seconds precision.
     * Useful for JPA equality concern when comparing Date from Java world (with millisec precision) with date
     * retrieved from Oracle (second precision only).
     */
    public static boolean equalsWithSecondPrecision(Date date1, Date date2) {
        return removeMilliseconds(date1).equals(removeMilliseconds(date2));
    }

    public static int hashCodeWithSecondPrecision(Date date) {
        return removeMilliseconds(date).hashCode();
    }

    private static String removeMilliseconds(Date date) {
        String asString = Long.toString(date.getTime());
        return asString.substring(0, asString.length() - 3);
    }
}