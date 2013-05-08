package com.corvstudios.blanktemplate;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class OpenGLView extends GLSurfaceView
{
	private Context mContext;
	private SurfaceHolder mHolder;
	private OpenGLRenderer renderer;

	private Touch touch;
	private Activity topActivity;
	
	public OpenGLView(Context context, AttributeSet attrs)
	{
		super(context,attrs);
		mContext = context;
	}
	public void init(Activity act,Handler mHandler,Vibrator vib)
	{
		mHolder = getHolder();
		mHolder.addCallback(this);
		topActivity = act;
		
		//opengl renderer
		renderer = new OpenGLRenderer(mHolder,mContext,mHandler,topActivity,vib);
		setRenderer(renderer);

		try {
			if(Float.parseFloat(android.os.Build.VERSION.SDK)>=5)
				touch = new MultiTouch(renderer);
			else 
				touch = new Touch(renderer);
		} catch(VerifyError e) {
			touch = new Touch(renderer);
		}
		
		//allows for keyboard presses
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event)
	{
		if(renderer!=null && renderer.getGameThread()!=null)
			touch.useTouch(event);
		
		return true;
	}
	@Override
	public boolean onKeyDown(final int keyCode, KeyEvent event)
	{
		queueEvent(new Runnable()
		{
			public void run()
			{
				/*GBGameLoop gameLoop;
				if(gbRenderer!=null && (gameLoop = gbRenderer.getPBGameLoop())!=null)
				{
					if(GBGameLoop.GAME_STATE==GBGameLoop.NAME_INPUT)
					{
						if(keyCode>=29 && keyCode<55)
						{
							gameLoop.setUsername(gameLoop.getUsername()+(char)(keyCode+36));
						}
						else if(keyCode==KeyEvent.KEYCODE_DEL)
						{
							String user = gameLoop.getUsername();
							if(user.length()>0)
								gameLoop.setUsername(user.substring(0, user.length()-1));
						}
					}
					else
					{
						switch(keyCode)
						{
							case KeyEvent.KEYCODE_DPAD_UP:
							case KeyEvent.KEYCODE_W:
								gameLoop.setAccelerometer(0,-1.5f,0);
								break;
							case KeyEvent.KEYCODE_DPAD_DOWN:
							case KeyEvent.KEYCODE_S:
								gameLoop.setAccelerometer(0,1.5f,0);
								break;
							case KeyEvent.KEYCODE_DPAD_LEFT:
							case KeyEvent.KEYCODE_A:
								gameLoop.setAccelerometer(-1.5f,0,0);
								break;
							case KeyEvent.KEYCODE_DPAD_RIGHT:
							case KeyEvent.KEYCODE_D:
								gameLoop.setAccelerometer(1.5f,0,0);
								break;
						}
					}
				}*/
			}
		});
		return  super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onKeyUp(final int keyCode, KeyEvent event)
	{
		return super.onKeyUp(keyCode, event);
	}
	
	//when game goes off screen stop the game, and save info
	@Override
	public void onPause()
	{
		if(renderer!=null)
		{
			GameThread gameThread = renderer.getGameThread();
			if(gameThread!=null)
			{
				gameThread.setRunning(false);
				gameThread.saveGame();
			}
		}
		super.onPause();
	}
}
class Touch
{
	protected OpenGLRenderer renderer;
	public Touch(OpenGLRenderer rend)
	{
		renderer = rend;
	}
	public void useTouch(MotionEvent event)
	{
		GameThread gameThread = renderer.getGameThread();
		
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			gameThread.setTouchEvent(true,event.getX(),event.getY());
			break;
		case MotionEvent.ACTION_UP:
			gameThread.setTouchEvent(false,0,0);
			break;
		}
	}
}
class MultiTouch extends Touch
{
	public MultiTouch(OpenGLRenderer rend)
	{
		super(rend);
	}
	
	@Override
	public void useTouch(MotionEvent event)
	{
		GameThread gameThread = renderer.getGameThread();
		
		/*if(event.getPointerCount()>1)
			for(int i=0; i<event.getPointerCount(); i++)
				Log.v("MotionEvent", "X"+i+":"+event.getX(i)+" Y"+i+":"+event.getY(i));
		else
			Log.v("MotionEvent", "X0:"+event.getX(0)+" Y0:"+event.getY(0));*/
		
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			//gameThread.setTouchEvent(true,event.getX(),event.getY());
			for(int i=0; i<event.getPointerCount(); i++)
				gameThread.setTouchEvent(true,event.getX(i),event.getY(i));
			break;
		case MotionEvent.ACTION_UP:
			gameThread.setTouchEvent(false,0,0);
			break;
		}
	}
}
