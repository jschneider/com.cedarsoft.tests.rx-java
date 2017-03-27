package com.cedarsoft.tests.rxjava;

import com.cedarsoft.test.utils.ThreadRule;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.junit.*;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SchedulersTest {
  @Rule
  public ThreadRule threadRule = new ThreadRule();

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
    Schedulers.shutdown();
  }

  @Test
  public void other() throws Exception {
    Scheduler scheduler = Schedulers.computation();
    assertThat(scheduler).isNotNull();
  }

  @Test
  public void asdf() throws Exception {
    Scheduler scheduler = Schedulers.io();
    assertThat(scheduler).isNotNull();


    Scheduler.Worker worker = scheduler.createWorker();
    Scheduler.Worker worker2 = scheduler.createWorker();

    scheduler.scheduleDirect(new Runnable() {
      @Override
      public void run() {
        try {
          System.out.println("Direct. Wait 600");
          Thread.sleep(600);
          System.out.println("Direct done");
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });
    scheduler.scheduleDirect(new Runnable() {
      @Override
      public void run() {
        try {
          System.out.println("Direct2. Wait 600");
          Thread.sleep(600);
          System.out.println("Direct2 done");
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });

    worker.schedule(new Runnable() {
      @Override
      public void run() {
        try {
          System.out.println("will wait 400");
          Thread.sleep(400);
          System.out.println("waited 400");
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });

    Disposable disposablePeriodically = worker2.schedulePeriodically(new Runnable() {
      @Override
      public void run() {
        System.out.println("Period...");
      }
    }, 50, 50, TimeUnit.MILLISECONDS);

    System.out.println("Scheduled periodically: " + disposablePeriodically.isDisposed());


    worker.schedule(new Runnable() {
      @Override
      public void run() {
        try {
          System.out.println("will wait 100");
          Thread.sleep(100);
          System.out.println("waited 100");
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });

    //worker.schedule(new Runnable() {
    //  @Override
    //  public void run() {
    //    System.out.println("disposing");
    //    worker.dispose();
    //    System.out.println("disposed: " + worker.isDisposed());
    //  }
    //});

    System.out.println("Done scheduling. Is disposed: " + worker.isDisposed());
    Thread.sleep(3000);
    System.out.println("dispose period");
    disposablePeriodically.dispose();
    worker.dispose();
    worker2.dispose();
    System.out.println("############");
  }
}
