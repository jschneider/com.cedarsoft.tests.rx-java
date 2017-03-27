package com.cedarsoft.tests.rxjava;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import org.junit.*;

import java.util.concurrent.Callable;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class AsdfTest {
  @Test
  public void amb() throws Exception {
    Observable.fromArray(1,2,3)
      .scan()
  }

  @Test
  public void asdf() throws Exception {
    Observable<Object> observable = Observable.defer(new Callable<ObservableSource<?>>() {
      @Override
      public ObservableSource<?> call() throws Exception {
        System.out.println("Creating observable");

        return Observable.just("asdf");
      }
    });


    System.out.println("1");
    observable.subscribe();
    System.out.println("2");
  }

  @Test
  public void callbale() throws Exception {
    Observable<String> observable = Observable.fromCallable(new Callable<String>() {
      @Override
      public String call() throws Exception {
        System.out.println("Creating observable");
        return "asdf";
      }
    });


    System.out.println("1");
    observable.subscribe();
    System.out.println("2");

  }
}
