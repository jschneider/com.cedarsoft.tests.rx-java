package com.cedarsoft.tests;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Provides measurand values every 100 ms
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class MeasurandValuesProvider implements ObservableOnSubscribe<MeasurandsEntry> {
  @Nonnull
  private final Scheduler scheduler;

  private Disposable disposable;

  public MeasurandValuesProvider(@Nonnull Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public void subscribe(ObservableEmitter<MeasurandsEntry> e) throws Exception {
    disposable = scheduler.schedulePeriodicallyDirect(new Runnable() {
      @Override
      public void run() {
        try {
          e.onNext(getNextEntry());
        } catch (IOException e) {
          throw new RuntimeException(e);
        } catch (InterruptedException ignore) {
          System.out.println("##### InterruptedException");
          disposable.dispose();
        }
      }
    }, 0, 500, TimeUnit.MILLISECONDS);
  }


  private int index;

  /**
   * Loads the next entry over the network
   */
  @Nonnull
  private MeasurandsEntry getNextEntry() throws IOException, InterruptedException {
    Thread.sleep((long) (Math.random() * 100));
    index++;
    return new MeasurandsEntry(new long[]{index, index + 1, index + 2}, new double[]{Math.random(), Math.random(), Math.random()});
  }

  @Nonnull
  public static Observable<MeasurandsEntry> create(@Nonnull Scheduler scheduler) {
    return Observable.create(new MeasurandValuesProvider(scheduler));
  }
}
