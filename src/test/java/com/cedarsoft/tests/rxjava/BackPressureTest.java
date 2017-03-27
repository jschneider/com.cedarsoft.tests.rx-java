package com.cedarsoft.tests.rxjava;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.junit.*;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BackPressureTest {

  @Test
  public void subscriber() throws Exception {

    Flowable<String> observable = Flowable.create(new FlowableOnSubscribe<String>() {
      @Override
      public void subscribe(FlowableEmitter<String> e) throws Exception {
        for (int i = 0; i < 1000; i++) {
          e.onNext(String.valueOf(i));
          Thread.sleep(100);
          System.out.println("publish " + i);
        }
      }
    }, BackpressureStrategy.DROP);


    Subscriber<String> subscriber = new Subscriber<String>() {
      private Subscription subscription;

      @Override
      public void onSubscribe(Subscription s) {
        subscription = s;
        System.out.println("BackPressureTest.onSubscribe");
        s.request(1);
      }

      @Override
      public void onNext(String s) {
        System.out.println("BackPressureTest.onNext: " + s);
        subscription.request(1);
      }

      @Override
      public void onError(Throwable t) {
        System.out.println("BackPressureTest.onError");
      }

      @Override
      public void onComplete() {
        System.out.println("BackPressureTest.onComplete");
      }
    };

    observable
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.single())
      .subscribe(subscriber);

    Thread.sleep(10000);

  }

  @Test
  public void basic() throws Exception {
    Observable<String> flowable = Observable.create(new ObservableOnSubscribe<String>() {
      @Override
      public void subscribe(ObservableEmitter<String> e) throws Exception {
        for (int i = 0; i < 1000; i++) {
          e.onNext(String.valueOf(i));
          Thread.sleep(100);
          System.out.println("publish " + i);
        }
      }
    });


    //Disposable disposable = flowable
    //  .subscribeOn(Schedulers.io())
    //  .observeOn(Schedulers.single())
    //  .onBackpressureDrop(new Consumer<String>() {
    //    @Override
    //    public void accept(String s) throws Exception {
    //      System.out.println("\tdropping " + s);
    //    }
    //  })
    //  .subscribe(new Consumer<String>() {
    //    @Override
    //    public void accept(String s) throws Exception {
    //      System.out.println(s + " in " + Thread.currentThread().getName());
    //      Thread.sleep(1000);
    //    }
    //  });


    flowable
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.single())
      .buffer(500, TimeUnit.MILLISECONDS)
      .subscribe(new Consumer<List<String>>() {
        @Override
        public void accept(List<String> strings) throws Exception {
          System.out.println("--> got " + strings);
        }
      });

    Thread.sleep(100000);
  }
}
