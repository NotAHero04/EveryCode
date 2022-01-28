package com.tests.polynomial.wip;

public class Utility {
  private static double fetch(int i0, int i1, double[][] series) {
    // return the requested value
    return series[i0][i1];
  }

  static double[][] normalize(int a, int b, int i, double[][] series) {
    // multiply an equation (index a) with a number to get a new equation which has the same
    // index with another equation (index b) at index i
    // we should also have a check to prevent multiplying with zero
    if (series[b][i] == 0 | series[a][i] == 0) {
      // System.out.println(
      //  "WARNING: Detected multiplying with zero. Temporarily ignored; it WILL lead to unintended
      // results!");
      return series;
    } else {
      double var1 = fetch(a, i, series);
      double var2 = fetch(b, i, series);
      double[][] ret = series;
      for (int j = 0; j < ret[a].length; j++) {
        ret[a][j] = ret[a][j] * var2 / var1;
      }
      return ret;
    }
  }

  static double[][] subtract(int a, int b, double[][] series) {
    // simply subtract the equation with index b from the one with index a
    double[][] ret = series;
    for (int j = 0; j < ret[a].length; j++) {
      ret[a][j] = ret[a][j] - ret[b][j];
    }
    return ret;
  }

  static double[][] process(int a, int b, int i, double[][] series) {
    return subtract(a, b, normalize(a, b, i, series));
  }

  static double[][] revProcess(int a, int b, int i, double[][] series) {
    return subtract(b, a, normalize(a, b, i, series));
  }

  static double[][] reduce(int a, double[][] series) {
    // reduce the coefficients to 1
    double[][] ret = series;
    double[][] temp = new double[2][series[a].length];
    for (int i = 0; i < ret[a].length; i++) {
      temp[0][i] = series[a][i];
      temp[1][i] = 1;
    }
    for (int i = 0; i < temp[0].length; i++) {
      if (temp[0][i] != 0) {
        temp = normalize(0, 1, i, temp);
        break;
      }
    }
    ret[a] = temp[0];
    return ret;
  }

  static double[][] reduce(double[][] series) {
    // reduce the entire series
    double[][] ret = series;
    for (int i = 0; i < ret.length; i++) {
      reduce(i, ret);
    }
    return ret;
  }

  static void print(int a, double[][] series) {
    // print an equation
    for (int i = 0; i < series[a].length; i++) {
      System.out.print(series[a][i] + " ");
    }
    System.out.println();
  }

  static void print(double[][] series) {
    // print the entire series
    for (int i = 0; i < series.length; i++) {
      print(i, series);
    }
  }

  static double[][] process(double[][] series) {
    // process the entire series
    double[][] ret = series;
    for (int i = series.length - 1; i > 0; i--) {
      for (int j = i; j > 0; j--) {
        for (int k = 0; k < j; k++) {
          ret = process(k, j, i, ret);
          // System.out.println(k + " " + j + " " + i);
        }
      }
    }
    return ret;
  }

  static double[][] revProcess(double[][] series) {
    // reverse-process the entire series
    double[][] ret = series;
    for (int i = 0; i < series.length; i++) {
      for (int j = i + 1; j < series.length; j++) {
        ret = revProcess(i, j, i, ret);
      }
    }
    return ret;
  }

  static double[][] fullProcess(double[][] series) {
    return reduce(revProcess(process(series)));
  }

  // public static void main(String[] args) {
  //  double[][] test = {{0, 1, 2}, {3, 1, 5}};
  //  print(test);
  //  System.out.println();
  //  test = revProcess(process(test));
  //  test = reduce(test);
  //  print(test);
  // }
  // you can re-enable this test if you want
}
