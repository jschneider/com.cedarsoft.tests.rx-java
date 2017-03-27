package com.cedarsoft.tests.rxjava;

import com.cedarsoft.test.utils.ThreadRule;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.junit.*;

import java.util.concurrent.TimeUnit;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class ThreadingTest {
  @Rule
  public ThreadRule threadRule = new ThreadRule();

  @After
  public void tearDown() throws Exception {
    Schedulers.shutdown();
  }

  @Test
  public void single() throws Exception {
    Observable<Long> source = Observable.interval(100, TimeUnit.MILLISECONDS);

    source
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.single())
      .subscribe(new Consumer<Long>() {
        @Override
        public void accept(Long aLong) throws Exception {
          System.out.println(aLong + " on " + Thread.currentThread().getName());
        }
      });


    Thread.sleep(1000);
  }

  @Test
  public void interval() throws Exception {
    Observable<Long> interval = Observable.interval(100, TimeUnit.MILLISECONDS);

    interval.subscribe(new Consumer<Long>() {
      @Override
      public void accept(Long aLong) throws Exception {
        System.out.println(aLong);
      }
    });

    Thread.sleep(1000);
  }

  @Test
  public void asdf() throws Exception {
    Disposable disposable = Flowable.fromCallable(() -> {
      Thread.sleep(1000); //  imitate expensive computation
      return "Done";
    })
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.single())
      .subscribe(new Consumer<String>() {
        @Override
        public void accept(String x) throws Exception {
          System.out.println("called accept for <" + x + "> from Thread " + Thread.currentThread());
        }
      }, Throwable::printStackTrace);

    System.out.println("will wait");
    Thread.sleep(2000); // <--- wait for the flow to finish
    System.out.println("waited");

    System.out.println("Dispose " + disposable.isDisposed());
  }
}
