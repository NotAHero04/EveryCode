package com.tests.solarlunarday;

public class Main {
  public static double PI = 3.1415926535898;

  static int dayToJulius(int day, int month, int year) {
    int a = (int) Math.floor((14 - month) / 12);
    int y = year + 4800 - a;
    int m = month + 12 * a - 3;
    int d =
        (int)
            (day
                + Math.floor((153 * m + 2) / 5)
                + 365 * y
                + Math.floor(y / 4)
                - Math.floor(y / 100)
                + Math.floor(y / 400)
                - 32045);
    return d;
  }

  static int[] juliusToDay(int julius) {
    int a = julius + 32044;
    int b = (int) Math.floor((4 * a + 3) / 146097);
    int c = (int) (a - Math.floor((b * 146097) / 4));
    int d = (int) Math.floor((4 * c + 3) / 1461);
    int e = (int) (c - Math.floor((1461 * d) / 4));
    int m = (int) Math.floor(5 * e + 2) / 153;
    int day = (int) (e - Math.floor((153 * m + 2) / 5) + 1);
    int month = (int) (m + 3 - 12 * Math.floor(m / 10));
    int year = (int) (b * 100 + d - 4800 + Math.floor(m / 10));
    int[] fullDay = {day, month, year};
    return fullDay;
  }

  static int getNewMoonDay(int k, int timeZone) {
    double T = k / 1236.85; // Time in Julian centuries from 1900 January 0.5
    double T2 = T * T;
    double T3 = T2 * T;
    double dr = PI / 180;
    double Jd1 = 2415020.75933 + 29.53058868 * k + 0.0001178 * T2 - 0.000000155 * T3;
    Jd1 = Jd1 + 0.00033 * Math.sin((166.56 + 132.87 * T - 0.009173 * T2) * dr); // Mean new moon
    double M = 359.2242 + 29.10535608 * k - 0.0000333 * T2 - 0.00000347 * T3; // Sun's mean anomaly
    double Mpr =
        306.0253 + 385.81691806 * k + 0.0107306 * T2 + 0.00001236 * T3; // Moon's mean anomaly
    double F =
        21.2964
            + 390.67050646 * k
            - 0.0016528 * T2
            - 0.00000239 * T3; // Moon's argument of latitude
    double C1 = (0.1734 - 0.000393 * T) * Math.sin(M * dr) + 0.0021 * Math.sin(2 * dr * M);
    C1 = C1 - 0.4068 * Math.sin(Mpr * dr) + 0.0161 * Math.sin(dr * 2 * Mpr);
    C1 = C1 - 0.0004 * Math.sin(dr * 3 * Mpr);
    C1 = C1 + 0.0104 * Math.sin(dr * 2 * F) - 0.0051 * Math.sin(dr * (M + Mpr));
    C1 = C1 - 0.0074 * Math.sin(dr * (M - Mpr)) + 0.0004 * Math.sin(dr * (2 * F + M));
    C1 = C1 - 0.0004 * Math.sin(dr * (2 * F - M)) - 0.0006 * Math.sin(dr * (2 * F + Mpr));
    C1 = C1 + 0.0010 * Math.sin(dr * (2 * F - Mpr)) + 0.0005 * Math.sin(dr * (2 * Mpr + M));
    double deltat;
    if (T < -11) {
      deltat = 0.001 + 0.000839 * T + 0.0002261 * T2 - 0.00000845 * T3 - 0.000000081 * T * T3;
    } else {
      deltat = -0.000278 + 0.000265 * T + 0.000262 * T2;
    }
    ;
    double JdNew = Jd1 + C1 - deltat;
    return (int) Math.floor(JdNew + 0.5 + timeZone / 24);
  }

  static double getSunLongitude(int julius, int timeZone) {
    double T =
        (julius - 2451545.5 - timeZone / 24)
            / 36525; // Time in Julian centuries from 2000-01-01 12:00:00 GMT
    double T2 = T * T;
    double dr = PI / 180; // degree to radian
    double M =
        357.52910 + 35999.05030 * T - 0.0001559 * T2 - 0.00000048 * T * T2; // mean anomaly, degree
    double L0 = 280.46645 + 36000.76983 * T + 0.0003032 * T2; // mean longitude, degree
    double DL = (1.914600 - 0.004817 * T - 0.000014 * T2) * Math.sin(dr * M);
    DL = DL + (0.019993 - 0.000101 * T) * Math.sin(dr * 2 * M) + 0.000290 * Math.sin(dr * 3 * M);
    double L = L0 + DL; // true longitude, degree
    L = L * dr;
    L = L - PI * 2 * (Math.floor(L / (PI * 2))); // Normalize to (0, 2*PI)
    return L / PI * 6;
  }

  static int getLunarMonth11(int year, int timeZone) {
    int off = dayToJulius(31, 12, year) - 2415021;
    int k = (int) Math.floor(off / 29.530588853);
    int nm = getNewMoonDay(k, timeZone);
    double sunLong = getSunLongitude(nm, timeZone); // sun longitude at local midnight
    if (sunLong >= 9) {
      nm = getNewMoonDay(k - 1, timeZone); // can be further optimized
    }
    return nm;
  }

  static int getLeapMonthOffset(int a11, int timeZone) {
    int k = (int) Math.floor((a11 - 2415021.076998695) / 29.530588853 + 0.5);
    double last = 0;
    int i = 1; // We start with the month following lunar month 11
    double arc = getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone);
    do {
      last = arc;
      i++;
      arc = getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone);
    } while (arc != last && i < 14);
    return i - 1;
  }

  static int[] solarToLunar(int day, int month, int year, int timeZone) {
    int dayNumber = dayToJulius(day, month, year);
    int k = (int) Math.floor((dayNumber - 2415021.076998695) / 29.530588853);
    int monthStart = getNewMoonDay(k+1, timeZone);
    if (monthStart > dayNumber) {
      monthStart = getNewMoonDay(k, timeZone);
    }
    int a11 = getLunarMonth11(year, timeZone);
    int b11 = a11;
    int lunarYear;
    if (a11 >= monthStart) {
      lunarYear = year;
      a11 = getLunarMonth11(year - 1, timeZone);
    } else {
      lunarYear = year+1;
      b11 = getLunarMonth11(year + 1, timeZone);
    }
    int lunarDay = dayNumber - monthStart + 1;
    int diff = (int) Math.floor((monthStart - a11) / 29);
    int lunarLeap = 0;
    int lunarMonth = diff + 11;
    if (b11 - a11 > 365) {
      int leapMonthDiff = getLeapMonthOffset(a11, timeZone);
      if (diff >= leapMonthDiff) {
        lunarMonth = diff + 10;
        if (diff == leapMonthDiff) {
          lunarLeap = 1;
        }
      }
    }
    if (lunarMonth > 12) {
      lunarMonth = lunarMonth - 12;
    }
    if (lunarMonth >= 11 && diff < 4) {
      lunarYear -= 1;
    }
    int[] ret = {lunarDay, lunarMonth, lunarYear, lunarLeap};
    return ret;
  }
  public static void main(String[] args) {
    int[] lunar = solarToLunar(31, 1, 2022, 7);
    for (int i = 0; i < lunar.length - 1; i++) {
      System.out.print(lunar[i] + " ");
      if (lunar[lunar.length - 1] == 1) {
        System.out.println("(leap)");
      }
    }
    
  }
}
