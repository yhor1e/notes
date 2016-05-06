package jp.yhorie.notes;

import android.app.Application;
import android.os.Handler;
import android.os.StrictMode;

import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;

public class NotesApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    setDebugTools();
    FlowManager.init(this);
  }

  private void setDebugTools() {
    // https://code.google.com/p/android/issues/detail?id=35298
    new Handler().postAtFrontOfQueue(
      new Runnable() {
        @Override
        public void run() {
//          StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//              .detectDiskReads()
//              .detectDiskWrites()
//              .detectNetwork()   // or .detectAll() for all detectable problems
//              .penaltyLog()
//              .build());
//          StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//              .detectLeakedSqlLiteObjects()
//              .detectLeakedClosableObjects()
//              .penaltyLog()
//              .penaltyDeath()
//              .build());
        }
      }
    );

    Stetho.initializeWithDefaults(this);
    LeakCanary.install(this);
  }
}
