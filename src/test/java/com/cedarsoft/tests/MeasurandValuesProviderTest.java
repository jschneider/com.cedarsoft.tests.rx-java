package com.cedarsoft.tests;

import com.cedarsoft.test.utils.ThreadRule;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.junit.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class MeasurandValuesProviderTest {
  @Rule
  public ThreadRule threadRule = new ThreadRule();

  @After
  public void tearDown() throws Exception {
    Schedulers.shutdown();
  }

  @Test
  public void name() throws Exception {
    //reads over network
    Observable<MeasurandsEntry> provider = MeasurandValuesProvider.create(Schedulers.io());

    Disposable disposable = provider
      .subscribe(new Consumer<MeasurandsEntry>() {
        @Override
        public void accept(MeasurandsEntry entry) throws Exception {
          System.out.println("New Entry: " + entry + " - called on " + Thread.currentThread().getName());
        }
      }, new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
          System.err.println("----------");
          throwable.printStackTrace();
          System.err.println("----------");
        }
      }, new Action() {
        @Override
        public void run() throws Exception {
          System.out.println("DONE");
        }
      });

    System.out.println("--> Wait 10 secs");
    Thread.sleep(10000);
    System.out.println("Dispose");
    disposable.dispose();
    System.out.println("Disposed");
    Thread.sleep(1000);
    System.out.println("--------------");
  }

  @Test
  public void cache() throws Exception {
    //reads over network
    Observable<MeasurandsEntry> provider = MeasurandValuesProvider.create(Schedulers.io())
      .share()
      .replay(1)
      .autoConnect()
      .observeOn(Schedulers.io())
      ;

    Disposable disposable1 = provider.subscribe(new Consumer<MeasurandsEntry>() {
      @Override
      public void accept(MeasurandsEntry measurandsEntry) throws Exception {
        System.out.println("Consumer 1: " + measurandsEntry + " on " + Thread.currentThread().getName());
      }
    });

    Thread.sleep(1000);

    System.out.println("Register second");
    Disposable disposable2 = provider.subscribe(new Consumer<MeasurandsEntry>() {
      @Override
      public void accept(MeasurandsEntry measurandsEntry) throws Exception {
        System.out.println("Consumer 2: " + measurandsEntry + " on " + Thread.currentThread().getName());
      }
    });


    System.out.println("----------------");
    Thread.sleep(5000);
    System.out.println("Done");
    disposable1.dispose();
    disposable2.dispose();
    Thread.sleep(5000);
    System.out.println("#########");
  }
}