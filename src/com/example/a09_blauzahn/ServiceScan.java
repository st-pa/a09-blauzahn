package com.example.a09_blauzahn;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class ServiceScan
extends Service {

	private static Timer timer = new Timer();
	private Context context;
	private AppBlauzahn app;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.context = this;
		this.app = (AppBlauzahn) getApplication();
	}

	/**
	 * start scanning.
	 */
	protected void start() {
		if (app.settings.isBtOn()) {
			if (app.settings.isBtAuto()) {
				timer.scheduleAtFixedRate(
					new TaskScanBluetooth(),
					new Date(),
					app.settings.getBtInterval()
				);
			} else {
				app.scan();
			}
		}
	}

	private class TaskScanBluetooth extends TimerTask {
		@Override
		public void run() {
			app.scan();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
/*
public class LocalService extends Service {
    public void onCreate() {
          super.onCreate();
          ctx = this; 
          startService();
    }
    private void startService()   {           
        timer.scheduleAtFixedRate(new mainTask(), 0, 5000);
    }
    private class mainTask extends TimerTask   { 
        public void run()      {
            toastHandler.sendEmptyMessage(0);
        }
    }    
    private final Handler toastHandler = new Handler()    {
        @Override
        public void handleMessage(Message msg)      {
            Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
        }
    };    
}
*/
}
