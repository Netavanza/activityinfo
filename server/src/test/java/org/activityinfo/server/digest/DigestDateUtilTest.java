package org.activityinfo.server.digest;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class DigestDateUtilTest {

    private Date createDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2013, 3, 18, 15, 6, 30);
        return cal.getTime();
    }

    @Test
    public void testIsOnToday() {
        // morning
        Date d = createDate();

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 10);
        Date c = cal.getTime();
        Assert.assertTrue(DigestDateUtil.isOnToday(d, c));

        cal.add(Calendar.MINUTE, -20);
        c = cal.getTime();
        Assert.assertFalse(DigestDateUtil.isOnToday(d, c));
    }

    @Test (expected=IllegalArgumentException.class)
    public void testIsOnTodayTomorrow() {
        // evening
        Date d = createDate();

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        Date c = cal.getTime();
        Assert.assertTrue(DigestDateUtil.isOnToday(d, c));

        cal.add(Calendar.MINUTE, 5);
        c = cal.getTime();
        // should throw
        Assert.assertFalse(DigestDateUtil.isOnToday(d, c));
    }
    
    @Test
    public void testIsOnYesterday() {
        Date d = createDate();

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 10);
        Date c = cal.getTime();
        Assert.assertFalse(DigestDateUtil.isOnYesterday(d, c));

        cal.add(Calendar.MINUTE, -20);
        c = cal.getTime();
        Assert.assertTrue(DigestDateUtil.isOnYesterday(d, c));
    }

    @Test
    public void testDaysBeforeMidnight() {
        Date d = createDate();

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 10);
        Date c = cal.getTime();
        Assert.assertEquals(0, DigestDateUtil.daysBeforeMidnight(d, c));

        cal.add(Calendar.MINUTE, -20);
        c = cal.getTime();
        Assert.assertEquals(1, DigestDateUtil.daysBeforeMidnight(d, c));

        cal.setTime(d);
        cal.add(Calendar.DATE, -1);
        cal.add(Calendar.MINUTE, 5);
        Assert.assertEquals(1, DigestDateUtil.daysBeforeMidnight(d, c));

        cal.add(Calendar.MINUTE, -10);
        Assert.assertEquals(1, DigestDateUtil.daysBeforeMidnight(d, c));
    }

    @Test
    public void testAbsoluteDaysBetween() {
        Date d = createDate();

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 10);
        Date c = cal.getTime();
        Assert.assertEquals(0, DigestDateUtil.absoluteDaysBetween(d, c));

        cal.add(Calendar.MINUTE, -20);
        c = cal.getTime();
        Assert.assertEquals(0, DigestDateUtil.absoluteDaysBetween(d, c));

        cal.setTime(d);
        cal.add(Calendar.DATE, -1);
        cal.add(Calendar.MINUTE, 5);
        c = cal.getTime();
        Assert.assertEquals(0, DigestDateUtil.absoluteDaysBetween(d, c));

        cal.add(Calendar.MINUTE, -10);
        c = cal.getTime();
        Assert.assertEquals(1, DigestDateUtil.absoluteDaysBetween(d, c));
    }
}
