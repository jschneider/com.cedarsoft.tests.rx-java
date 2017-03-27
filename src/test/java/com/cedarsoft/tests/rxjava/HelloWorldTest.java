package com.cedarsoft.tests.rxjava;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.junit.*;
import org.reactivestreams.Subscriber;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class HelloWorldTest {

  @Test
  public void asdf() throws Exception {
    Observable<Integer> source = Observable.range(1, 50);

    source.subscribe(new Observer<Integer>() {
      @Override
      public void onSubscribe(Disposable d) {
        System.out.println("HelloWorldTest.onSubscribe");
      }

      @Override
      public void onNext(Integer integer) {
        System.out.println("onNext " + integer);
      }

      @Override
      public void onError(Throwable e) {
        System.out.println("HelloWorldTest.onError");
      }

      @Override
      public void onComplete() {
        System.out.println("HelloWorldTest.onComplete");
      }
    });

    System.out.println("done");

    source.subscribe(new Consumer<Integer>() {
      @Override
      public void accept(Integer integer) throws Exception {
        System.out.println("--> " + integer);
      }
    });
  }

  @Test
  public void completable() throws Exception {
    Completable completable = Completable.fromCallable(new Callable<Object>() {
      @Override
      @Nullable
      public Object call() throws Exception {
        Thread.sleep(500);
        return null;
      }
    });

    Disposable disposable = completable.subscribe(new Action() {
      @Override
      public void run() throws Exception {
        System.out.println("completed");
      }
    });
  }

  @Test
  public void completableFailling() throws Exception {
    Completable completable = Completable.fromCallable(new Callable<Object>() {
      @Override
      @Nullable
      public Object call() throws Exception {
        System.out.println("will wait");
        Thread.sleep(1500);
        System.out.println("waited");
        throw new RuntimeException("Uups");
      }
    });

    Disposable disposable = completable.subscribe(new Action() {
      @Override
      public void run() throws Exception {
        System.out.println("completed");
      }
    }, new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        System.out.println("failed with: " + throwable);
      }
    });


    for (int i = 0; i < 10; i++) {
      System.out.println("disposed " + disposable.isDisposed());
      Thread.sleep(100);
    }
  }

  @Test
  public void Single() throws Exception {
    Single<String> single = Single.just("asdf");

    Disposable disposable = single.subscribe(new Consumer<String>() {
      @Override
      public void accept(String s) throws Exception {
        System.out.println(s);
      }
    });
  }

  @Test
  public void ownFlowable() throws Exception {
    Flowable<String> flowable = new Flowable<String>() {
      @Override
      protected void subscribeActual(Subscriber<? super String> s) {
        System.out.println("HelloWorldTest.subscribeActual");
        s.onNext("Hello");
        s.onNext("world");
        System.out.println("HelloWorldTest.subscribeActual finished");
      }
    };

    System.out.println("Will Subscribe");

    flowable.subscribe(new Consumer<String>() {
      @Override
      public void accept(String s) throws Exception {
        System.out.println(s);
      }
    });
  }

  @Test
  public void testit() {
    Flowable<String> flowable = Flowable.fromArray("Hello", "world");
    flowable.subscribe(new Consumer<String>() {
      @Override
      public void accept(String s) throws Exception {
        System.out.println("s");
      }
    }, new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        System.err.println("ERROR");
        throwable.printStackTrace();
      }
    });
  }

  @Test
  public void observ() throws Exception {
    Observable<String> observable = Observable.fromArray("Hello", "world", "dada");

    observable.subscribe(new Consumer<String>() {
      @Override
      public void accept(String s) throws Exception {
        System.out.println(s);
      }
    });
  }

  @Test
  public void maybe() throws Exception {
    Maybe<String> maybe = Maybe.just("asdf");

    maybe.subscribe(new Consumer<String>() {
      @Override
      public void accept(String s) throws Exception {
        System.out.println(s);
      }
    });


  }
}
