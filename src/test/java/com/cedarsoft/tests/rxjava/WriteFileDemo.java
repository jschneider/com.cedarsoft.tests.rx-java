package com.cedarsoft.tests.rxjava;

import com.cedarsoft.tests.SwingScheduler;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.junit.*;
import org.junit.rules.*;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;

/**
 * Writes a file
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class WriteFileDemo {
  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  @Test
  public void simple() throws Exception {

    AtomicBoolean finished = new AtomicBoolean();

    Observable<WrittenChunk> observable = writeFile(tmp.newFile(), "daContent");

    observable
      .observeOn(SwingScheduler.getInstance())
      .subscribe(new Consumer<WrittenChunk>() {
        @Override
        public void accept(WrittenChunk writtenChunk) throws Exception {
          System.out.println("Chunk written: " + writtenChunk.getLength() + " in " + Thread.currentThread().getName());
        }
      }, new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
          System.out.println("error in " + Thread.currentThread().getName());
          throwable.printStackTrace();
        }
      }, new Action() {
        @Override
        public void run() throws Exception {
          System.out.println("finished in " + Thread.currentThread().getName());
          finished.set(true);
        }
      });

    await().atMost(5, TimeUnit.SECONDS).untilTrue(finished);
  }

  @Nonnull
  private Observable<WrittenChunk> writeFile(@Nonnull File file, @Nonnull String daContent) {
    return Observable.create(new ObservableOnSubscribe<WrittenChunk>() {
      @Override
      public void subscribe(ObservableEmitter<WrittenChunk> e) throws Exception {
        try (FileOutputStream out = new FileOutputStream(file)) {
          for (int i = 0; i < daContent.length(); i++) {
            out.write(daContent.charAt(i));
            e.onNext(new WrittenChunk(file, i));

            //Simulate slow writing
            Thread.sleep(500);
          }
        }

        e.onComplete();
      }
    })
      .subscribeOn(Schedulers.io())
      ;
  }

  /**
   * Contains information about the chunk that has been written to a file
   */
  public static class WrittenChunk {
    @Nonnull
    private final File file;
    private final int length;

    public WrittenChunk(@Nonnull File file, int length) {
      this.file = file;
      this.length = length;
    }

    @Nonnull
    public File getFile() {
      return file;
    }

    public int getLength() {
      return length;
    }
  }

}
