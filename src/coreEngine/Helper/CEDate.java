package coreEngine.Helper;

import coreEngine.reliabilityAnalysis.DataStruct.DemandData;
import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * This class is used to store and modify seed and RRP dates.
 *
 * @author Shu Liu
 * @author Lake Trask
 */
public class CEDate implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2435357335649L;

    /**
     * Year of the date
     */
    public int year;

    /**
     * Month of the date
     */
    public int month;

    /**
     * Day of the date
     */
    public int day;

    /**
     * Default constructor
     */
    public CEDate() {
        this(2000, 1, 1);
    }

    /**
     * Constructor of CEDate class
     *
     * @param month month of the date
     * @param day day of the date
     */
    public CEDate(int month, int day) {
        this(2000, month, day);
    }

    /**
     * Constructor of CEDate class
     *
     * @param year year of the date
     * @param month month of the date
     * @param day day of the date
     */
    public CEDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    /**
     * Constructor of CEDate class
     *
     * @param date date in String (YYYY-MM-DD)
     */
    public CEDate(String date) {
        String[] item = date.split("-");
        this.year = Integer.parseInt(item[0]);
        this.month = Integer.parseInt(item[1]);
        this.day = Integer.parseInt(item[2]);
    }

    /**
     * Return day of week of the date
     *
     * @return index of the day of the week 0 - Monday, 1 -Tuesday, 2 -
     * Wednesday 3 - Thursday, 4 - Friday, 5 - Saturday, 6 - Sunday.
     */
    public int dayOfWeek() {
        return CEDate.dayOfWeek(this);
    }

    /**
     * Return day of week of the date
     *
     * @param date CEDate instance
     * @return index of the day of the week 0 - Monday, 1 -Tuesday, 2 -
     * Wednesday 3 - Thursday, 4 - Friday, 5 - Saturday, 6 - Sunday.
     */
    public static int dayOfWeek(CEDate date) {
        //Zeller's algorithm
        int w = date.year - (14 - date.month) / 12;
        int x = w + w / 4 - w / 100 + w / 400;
        int z = date.month + 12 * ((14 - date.month) / 12) - 2;
        return (date.day + x + (31 * z) / 12 - 1) % 7;
    }

    /**
     * Return day of week of the date
     *
     * @param day day of the date (1,...,31)
     * @param month month of the date (1,...,12)
     * @param year year of the date (...,2010,2011,...)
     *
     * @return index of the day of the week 0 - Monday, 1 -Tuesday, 2 -
     * Wednesday 3 - Thursday, 4 - Friday, 5 - Saturday, 6 - Sunday.
     */
    public static int dayOfWeek(int day, int month, int year) {
        //Zeller's algorithm
        int w = year - (14 - month) / 12;
        int x = w + w / 4 - w / 100 + w / 400;
        int z = month + 12 * ((14 - month) / 12) - 2;
        return (day + x + (31 * z) / 12 - 1) % 7;
    }

    /**
     * Return number of days in the specified month
     *
     * @param month month of the date
     * @param year year of the date
     * @return Number of days in the specified month
     */
    public static int daysInMonth(int month, int year) {
        int numDaysInMonth = 0;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                numDaysInMonth = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                numDaysInMonth = 30;
                break;
            case 2:
                if (((year % 4 == 0) && !(year % 100 == 0)) || (year % 400 == 0)) {
                    numDaysInMonth = 29;
                } else {
                    numDaysInMonth = 28;
                }
                break;
            default:
                System.out.println("Invalid Month.");
                break;
        }
        return numDaysInMonth;
    }

    /**
     * Return number of days of week in a period
     *
     * @param analysisYear year of the analysis date
     * @param activeDays active days in the period
     * @return number of days of week in a period
     */
    public static int[] numDayOfWeekInMonthAP(int analysisYear, boolean[] activeDays) {
        int[] numDayOfWeekPerMonthInYear = new int[84];
        int numDaysInMonth = 0;
        int currDayType = 0;
        for (int monthIdx = 1; monthIdx < 13; monthIdx++) {
            numDaysInMonth = daysInMonth(monthIdx, analysisYear);
            for (int day = 1; day <= numDaysInMonth; day++) {
                currDayType = dayOfWeek(day, monthIdx, analysisYear);
                if (activeDays[currDayType]) {
                    numDayOfWeekPerMonthInYear[(monthIdx - 1) * 7 + currDayType]++;
                }
            }
        }
        return numDayOfWeekPerMonthInYear;
    }

    /**
     * Return number of days of week in a period
     *
     * @param analysisYear year of the analysis period
     * @param startMonth month of the start date
     * @param startDay day of the start date
     * @param endMonth month of the end date
     * @param endDay month of the end date
     * @param activeDays active days in the period
     * @return number of days of week in a period
     */
    public static int[] numDayOfWeekInMonthAP(int analysisYear, int startMonth, int startDay, int endMonth, int endDay, boolean[] activeDays) {
        int[] numDayOfWeekPerMonthInYear = new int[84];
        int numDaysInMonth = 0;
        int currDayType = 0;

        if (endMonth != startMonth) {
            // Getting number days in startMonth (partial month)
            numDaysInMonth = daysInMonth(startMonth, analysisYear);
            for (int day = startDay; day <= numDaysInMonth; day++) {
                currDayType = dayOfWeek(day, startMonth, analysisYear);
                if (activeDays[currDayType]) {
                    numDayOfWeekPerMonthInYear[(startMonth - 1) * 7 + currDayType]++;
                }
            }

            // Getting number of days in full months
            for (int monthIdx = startMonth + 1; monthIdx < endMonth; monthIdx++) {
                numDaysInMonth = daysInMonth(monthIdx, analysisYear);
                for (int day = 1; day <= numDaysInMonth; day++) {
                    currDayType = dayOfWeek(day, monthIdx, analysisYear);
                    if (activeDays[currDayType]) {
                        numDayOfWeekPerMonthInYear[(monthIdx - 1) * 7 + currDayType]++;
                    }
                }
            }

            // Getting number days in endMonth (partial month)
            for (int day = 1; day <= endDay; day++) {
                currDayType = dayOfWeek(day, endMonth, analysisYear);
                if (activeDays[currDayType]) {
                    numDayOfWeekPerMonthInYear[(endMonth - 1) * 7 + currDayType]++;
                }
            }
        } else {
            for (int day = startDay; day <= endDay; day++) {
                currDayType = dayOfWeek(day, startMonth, analysisYear);
                if (activeDays[currDayType]) {
                    numDayOfWeekPerMonthInYear[(startMonth - 1) * 7 + currDayType]++;
                }
            }
        }
        return numDayOfWeekPerMonthInYear;
    }

    /**
     * Return number of days of week per month in the total year.
     *
     * @param demandData demand data
     * @return number of days of week per month in the total year (84 length
     * array)
     */
    public static int[] numDayOfWeekInMonthAP(DemandData demandData) {
        int analysisYear = demandData.getYear();
        int startMonth = demandData.getStartMonth();
        int startDay = demandData.getStartDay();
        int endMonth = demandData.getEndMonth();
        int endDay = demandData.getEndDay();
        boolean[] activeDays = demandData.getActiveDays();

        int[] numDayOfWeekPerMonthInYear = numDayOfWeekInMonthAP(analysisYear, startMonth, startDay, endMonth, endDay, activeDays);
        return numDayOfWeekPerMonthInYear;
    }

    /**
     * Returns a integer array of length 7. Each value is the number of that day
     * type in the given portion of a month. For example, numDayInMonth[0] = 4
     * indicates that there are 4 Mondays in the specified portion of the month.
     * Note: whether or not a day is active in the study period is not
     * considered.
     *
     * @param year year
     * @param month month
     * @param startDay start day
     * @param endDay end day
     * @return a integer array of length 7
     */
    public static int[] numEachDayOfWeekInPeriod(int year, int month, int startDay, int endDay) {

        int currDayType;
        int[] numDayOfWeek = new int[7];

        int numDaysInMonth = daysInMonth(month, year);
        if (endDay > numDaysInMonth) {
            endDay = numDaysInMonth;
        }
        for (int day = startDay; day <= endDay; day++) {
            currDayType = dayOfWeek(day, month, year);
            numDayOfWeek[currDayType]++;
        }

        return numDayOfWeek;
    }

    /**
     * Method that checks if this CEDate is before another. Return true if
     * before, returns false if they are the same date or this date is after the
     * input date.
     *
     * @param date date to compare
     * @return whether this CEDate is before another
     */
    public boolean isBefore(CEDate date) {
        boolean isBefore = false;
        if (this.year < date.year) {
            isBefore = true;
        } else if (this.year == date.year) {
            if (this.month < date.month) {
                isBefore = true;
            } else if (this.month == date.month) {
                if (this.day < date.day) {
                    isBefore = true;
                }
            }
        }
        return isBefore;
    }

    /**
     * Method that checks if this CEDate is after another. Return true if after,
     * returns false if they are the same date or this date is before the input
     * date.
     *
     * @param date date to compare
     * @return whether this CEDate is after another
     */
    public boolean isAfter(CEDate date) {
        boolean isAfter = false;
        if (this.year > date.year) {
            isAfter = true;
        } else if (this.year == date.year) {
            if (this.month > date.month) {
                isAfter = true;
            } else if (this.month == date.month) {
                if (this.day > date.day) {
                    isAfter = true;
                }
            }
        }
        return isAfter;
    }

    /**
     * Method to check if two dates are the same.
     *
     * @param date date to compare
     * @return whether two dates are the same
     */
    public boolean isSameDateAs(CEDate date) {
        return (this.month == date.month && this.year == date.year && this.day == date.day);
    }

    /**
     * Compare two dates
     *
     * @param date date to compare
     * @return whether this date is before or same as the other
     */
    public boolean isBeforeOrSameAs(CEDate date) {
        return (this.isBefore(date) || this.isSameDateAs(date));
    }

    /**
     * Compare two dates
     *
     * @param date date to compare
     * @return whether this date is after or same as the other
     */
    public boolean isAfterOrSameAs(CEDate date) {
        return (this.isAfter(date) || this.isSameDateAs(date));
    }

    /**
     * Return a String to represent the month and day of week of a date
     *
     * @param month month of the date
     * @param day day of the date
     * @return a String to represent the month and day of week of a date
     */
    public static String getMonthDayString(int month, int day) {
        String monthString = "";
        String dayString = "";
        switch (month) {
            case 1:
                monthString = "Jan";
                break;
            case 2:
                monthString = "Feb";
                break;
            case 3:
                monthString = "Mar";
                break;
            case 4:
                monthString = "Apr";
                break;
            case 5:
                monthString = "May";
                break;
            case 6:
                monthString = "Jun";
                break;
            case 7:
                monthString = "Jul";
                break;
            case 8:
                monthString = "Aug";
                break;
            case 9:
                monthString = "Sep";
                break;
            case 10:
                monthString = "Oct";
                break;
            case 11:
                monthString = "Nov";
                break;
            case 12:
                monthString = "Dec";
                break;
        }

        switch (day) {
            case 0:
                dayString = "Mon";
                break;
            case 1:
                dayString = "Tue";
                break;
            case 2:
                dayString = "Wed";
                break;
            case 3:
                dayString = "Thur";
                break;
            case 4:
                dayString = "Fri";
                break;
            case 5:
                dayString = "Sat";
                break;
            case 6:
                dayString = "Sun";
                break;
        }
        return monthString + "-" + dayString;
    }

    /**
     * Return a String to represent the month
     *
     * @param month month
     * @return String of full name of month
     */
    public static String getMonthString(int month) {
        String monthString = "";
        switch (month) {
            case 1:
                monthString = "January";
                break;
            case 2:
                monthString = "February";
                break;
            case 3:
                monthString = "March";
                break;
            case 4:
                monthString = "April";
                break;
            case 5:
                monthString = "May";
                break;
            case 6:
                monthString = "June";
                break;
            case 7:
                monthString = "July";
                break;
            case 8:
                monthString = "August";
                break;
            case 9:
                monthString = "September";
                break;
            case 10:
                monthString = "October";
                break;
            case 11:
                monthString = "November";
                break;
            case 12:
                monthString = "December";
                break;
        }
        return monthString;
    }

    /**
     * Getter for month index (start with 1)
     *
     * @param monthName month name
     * @return month index (start with 1)
     */
    public static int getMonthNumber(String monthName) {
        switch (monthName) {
            case "January":
                return 1;
            case "February":
                return 2;
            case "March":
                return 3;
            case "April":
                return 4;
            case "May":
                return 5;
            case "June":
                return 6;
            case "July":
                return 7;
            case "August":
                return 8;
            case "September":
                return 9;
            case "October":
                return 10;
            case "November":
                return 11;
            case "December":
                return 12;
            default:
                System.err.println("Not a valid month");
                return 0;
        }
    }

    /**
     * Return a String to represent day of week of a date
     *
     * @param dayOfWeek day of week of the date
     * @return a String to represent day of week of a date
     */
    public static String getDayName(int dayOfWeek) {
        String dayString = "";
        switch (dayOfWeek) {
            case 0:
                dayString = "Mon";
                break;
            case 1:
                dayString = "Tue";
                break;
            case 2:
                dayString = "Wed";
                break;
            case 3:
                dayString = "Thur";
                break;
            case 4:
                dayString = "Fri";
                break;
            case 5:
                dayString = "Sat";
                break;
            case 6:
                dayString = "Sun";
                break;
        }
        return dayString;
    }

    @Override
    public String toString() {
        DecimalFormat formatter = new DecimalFormat("00");
        return year + "-" + formatter.format(month) + "-" + formatter.format(day);
    }

    /**
     * Return a String to represent the work zone
     *
     * @return a String to represent the work zone
     */
    public String toWorkZoneString() {
        String monthString = "";
        String dayString = "";
        switch (month) {
            case 1:
                monthString = "Jan";
                break;
            case 2:
                monthString = "Feb";
                break;
            case 3:
                monthString = "Mar";
                break;
            case 4:
                monthString = "Apr";
                break;
            case 5:
                monthString = "May";
                break;
            case 6:
                monthString = "Jun";
                break;
            case 7:
                monthString = "Jul";
                break;
            case 8:
                monthString = "Aug";
                break;
            case 9:
                monthString = "Sep";
                break;
            case 10:
                monthString = "Oct";
                break;
            case 11:
                monthString = "Nov";
                break;
            case 12:
                monthString = "Dec";
                break;
        }

        switch (dayOfWeek()) {
            case 0:
                dayString = "Mon";
                break;
            case 1:
                dayString = "Tue";
                break;
            case 2:
                dayString = "Wed";
                break;
            case 3:
                dayString = "Thur";
                break;
            case 4:
                dayString = "Fri";
                break;
            case 5:
                dayString = "Sat";
                break;
            case 6:
                dayString = "Sun";
                break;
        }
        return monthString + " " + day + "  " + dayString;
    }
}
