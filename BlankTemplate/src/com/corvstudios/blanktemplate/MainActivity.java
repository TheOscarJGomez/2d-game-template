package com.corvstudios.blanktemplate;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Service;

public class MainActivity extends Activity {
	//OpenGL View
	private OpenGLView oglView;

	//handler for actions
	final Handler mHandler = new Handler() {
		@Override
        public void handleMessage(Message msg) {
        	if(msg.getData().getBoolean("do_something")) {
        		//action
        	}
        }
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_main);

        //grab the view, and initiate it
        oglView = (OpenGLView) findViewById(R.id.OpenGLV);
        oglView.init(this,mHandler,(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE));
    }
}
