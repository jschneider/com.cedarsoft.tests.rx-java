package com.cedarsoft.tests;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposables;

import javax.swing.AbstractButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public enum AbstractButtonSource {
  ; // no instances

  /**
   * @see rx.observables.SwingObservable#fromButtonAction
   */
  public static Observable<ActionEvent> fromActionOf(final AbstractButton button) {

    return Observable.create(new ObservableOnSubscribe<ActionEvent>() {
      @Override
      public void subscribe(final ObservableEmitter<ActionEvent> subscriber) throws Exception {
        final ActionListener listener = new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            subscriber.onNext(e);
          }
        };
        button.addActionListener(listener);
        subscriber.setDisposable(Disposables.fromAction(() -> {
          button.removeActionListener(listener);
        }));


      }
    }).subscribeOn(SwingScheduler.getInstance())
      .unsubscribeOn(SwingScheduler.getInstance());
  }
}