package com.tests.polynomial.wip;

import java.util.*;

public class Main {

  public static void main(String[] args) throws InputMismatchException {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Degree? ");
    int deg = scanner.nextInt();
    if (deg <= 0) {
      scanner.close();
      throw new InputMismatchException();
    }
    double[][] points = new double[deg + 1][2];
    for (int i = 0; i < deg + 1; i++) {
      System.out.print("Point " + (i + 1) + ": ");
      for (int j = 0; j < 2; j++) {
        points[i][j] = scanner.nextDouble();
      }
    }
    scanner.close();
    double[][] series = Polynomial.getSeries(deg, points);
    Utility.print(series);
    series = Utility.fullProcess(series);
    Utility.print(series);
    for (int i = 0; i < series.length; i++) {
      if (i == 0) {
        System.out.print(series[series.length - 1 - i][series.length]);
      } else {
        System.out.print(Math.abs(series[series.length - 1 - i][series.length]));
      }
      if (i != 0) {
        System.out.print("x");
      }
      if (i > 1) {
        System.out.print("^" + i);
      }
      if (i != series.length - 1) {
        if (series[i][series.length] < 0) {
          System.out.print("-");
        } else {
          System.out.print("+");
        }
      }
    }
  }
}
