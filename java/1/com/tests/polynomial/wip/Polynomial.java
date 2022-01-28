package com.tests.polynomial.wip;

public class Polynomial {
  static double[][] getSeries(int deg, double[][] points) {
    double[][] seriesOfEquations = new double[deg + 1][deg + 2];
    for (int i = 0; i < deg + 1; i++) {
      for (int j = 0; j < deg + 1; j++) {
        seriesOfEquations[i][j] = Math.pow(points[i][0], (double) deg - j);
      }
      seriesOfEquations[i][deg + 1] = points[i][1];
    }
    return seriesOfEquations;
  }

  public static double[][] processedSeries(double[][] series) {
    return Utility.fullProcess(series);
  }
}
