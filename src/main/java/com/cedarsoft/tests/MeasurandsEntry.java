package com.cedarsoft.tests;

import java.util.Arrays;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class MeasurandsEntry {
  private long[] ids;
  private double[] values;

  public MeasurandsEntry(long[] ids, double[] values) {
    this.ids = ids;
    this.values = values;
  }

  public long[] getIds() {
    return ids;
  }

  public double[] getValues() {
    return values;
  }

  @Override
  public String toString() {
    return "Entry{" +
      "ids=" + Arrays.toString(ids) +
      ", values=" + Arrays.toString(values) +
      '}';
  }
}
