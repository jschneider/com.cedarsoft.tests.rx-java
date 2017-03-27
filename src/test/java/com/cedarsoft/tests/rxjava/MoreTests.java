package com.cedarsoft.tests.rxjava;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.junit.*;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class MoreTests {
  @Test
  public void emmitter() throws Exception {
    Observable<String> observable = Observable.fromCallable(new Callable<String>() {
      @Override
      public String call() throws Exception {
        Thread.sleep(500);
        return "Hello World";
      }
    });

    observable
      .observeOn(Schedulers.io())
      .subscribe(new Consumer<String>() {
        @Override
        public void accept(String s) throws Exception {
          System.out.println("--> " + s + " (" + Thread.currentThread().getName() + ")");
        }
      }, new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
          throw new RuntimeException(throwable);
        }
      }, new Action() {
        @Override
        public void run() throws Exception {
          System.out.println("--> Done");
        }
      }, new Consumer<Disposable>() {
        @Override
        public void accept(Disposable disposable) throws Exception {
          System.out.println("On Subscribe " + disposable);
        }
      });

    System.out.println("Waiting");
    Thread.sleep(1000);
  }

  @Test
  public void other() throws Exception {
    Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
      @Override
      public void subscribe(ObservableEmitter<String> e) throws Exception {
        Scheduler scheduler = Schedulers.io();
        Disposable disposable = scheduler.schedulePeriodicallyDirect(new Runnable() {
          @Override
          public void run() {
            //check for new files
            e.onNext("one more " + System.currentTimeMillis());
          }
        }, 100, 100, TimeUnit.MILLISECONDS);

        e.setDisposable(disposable);
      }
    });

    Disposable disposable = observable
      //.subscribeOn(Schedulers.io())
      .subscribe(new Consumer<String>() {
        @Override
        public void accept(String s) throws Exception {
          System.out.println("accept <" + s + ">");
        }
      });

    observable
      .subscribe(new Consumer<String>() {
        @Override
        public void accept(String s) throws Exception {
          System.out.println("shared " + s);
        }
      });

    System.out.println("Waiting");
    Thread.sleep(1000);

    System.out.println("Disposing");
    disposable.dispose();
    System.out.println("disposed");

    Thread.sleep(500);

    System.out.println("done");
  }

  @Test
  public void asdf() throws Exception {
    Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
      @Override
      public void subscribe(ObservableEmitter<String> e) throws Exception {
        System.out.println("MoreTests.subscribe");

        while (!e.isDisposed()) {
          try {
            Thread.sleep(100);
          } catch (InterruptedException ignore) {
            continue;
          }
          e.onNext("one more " + System.currentTimeMillis());
        }

        System.out.println("--> finished in subscribe");
      }
    });

    Disposable disposable = observable
      .subscribeOn(Schedulers.io())
      .subscribe(new Consumer<String>() {
        @Override
        public void accept(String s) throws Exception {
          System.out.println("accept <" + s + ">");
        }
      });

    Thread.sleep(1000);

    System.out.println("Disposing");
    disposable.dispose();
    System.out.println("disposed");

    Thread.sleep(500);

    System.out.println("done");

  }
}
