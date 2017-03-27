package com.cedarsoft.tests;

import com.cedarsoft.test.utils.ThreadRule;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.junit.*;

import javax.swing.SwingUtilities;
import java.util.concurrent.TimeUnit;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SwingSchedulerTest {
  @Rule
  public ThreadRule threadRule = new ThreadRule();

  @After
  public void tearDown() throws Exception {
    Schedulers.shutdown();
  }

  @Test
  public void swingName() throws Exception {
    SwingUtilities.invokeAndWait(new Runnable() {
      @Override
      public void run() {
        System.out.println(Thread.currentThread().getName());
      }
    });
  }

  @Test
  public void swingScheduler() throws Exception {
    Observable<Long> observable = Observable.interval(100, TimeUnit.MILLISECONDS);

    SwingScheduler swingScheduler = new SwingScheduler();
    observable
      .observeOn(swingScheduler)
      .subscribe(new Consumer<Long>() {
        @Override
        public void accept(Long aLong) throws Exception {
          System.out.println("--> " + aLong + " on " + Thread.currentThread().getName());
        }
      });


    Thread.sleep(1000);
  }
}