package com.corvstudios.blanktemplate;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    //context
    private SurfaceHolder mSurfaceHolder;
    private Context mContext;
    private Handler mHandler;
    private Vibrator mVibrator;
	//saving/loading
	private SharedPreferences savedGame;
	
	//saved game strings
	private static final String PREFS_NAME = "BlankTemplate";
	private static final String PREFS_COINS = "Saved Coins";

	//game loop flag
	private boolean running = true;

    //elapsed time
    private long lastTime = 0;
    private int elapsedTime = 0;
    private float percent = 0;
    private final int PRECISION_MILLIS = 1000000;
    private final int DELTA_TIME_LIMIT = 20;
    private final float MILLIS = 1000;
    //private int framesPerSecond = 0;
    //private int framesOneSecond = 0;

	//screen size
	private int win_width = 0;
	private int win_height = 0;
	private float scale = 0;
	private int winWidthHalf = 0;
	private int winHeightHalf = 0;
	
	//scaled screen size
	private float winScale = 0;
	private float winWidthScaled = 0;
	private float winCentX = 0;
	private float winHeightScaled = 0;
	private float winCentY = 0;

	//game state
	public static int game_state = -1;
	public static int last_state = 0;
	public static final int SPLASH_SCREEN = 0;
	public static final int MAIN_MENU = 1;
	public static final int PLAYING = 2;
	
	//create an image loader
	private ImageLoader iL = new ImageLoader();
	
	//image
	private Sprite sprite;
	private float xPos = 0;
	private float yPos = 0;
	private float xVel = 500;
	private float yVel = 500;
	
	//touch
	private boolean touching = false;
	private float touchX = 0;
	private float touchY = 0;

	public GameThread(SurfaceHolder sH,Context c,Handler h,Activity act,Vibrator vb)
	{
		super();
		mSurfaceHolder = sH;
		mContext = c;
		mHandler = h;
		mVibrator = vb;
		
		game_state = SPLASH_SCREEN;
	}

	public void init(GL10 gl) {
		//load art
		sprite = iL.loadBitmap(mContext, gl, R.raw.star, false, false);
		
		loadGame();
		
		//start game loop
		lastTime = System.nanoTime();
		
		//start thread
		start();
	}

	@Override
	public void run()
	{
		while(running) {
			if(lastTime!=0) {
					
				elapsedTime();
				
				switch(game_state) {
				case SPLASH_SCREEN:
					break;
				case MAIN_MENU:
					break;
				case PLAYING:
					//game logic goes here
					
					//example:
					//move sprite
					xPos += xVel * percent;
					yPos += yVel * percent;
					
					//check x edge collision
					if(xPos < 0) {
						xPos = 0;
						xVel *= -1;
					} else if( xPos > win_width * scale) {
						xPos = win_width * scale;
						xVel *= -1;
					}

					//check y edge collision
					if(yPos < 0) {
						yPos = 0;
						yVel *= -1;
					} else if( yPos > win_height * scale) {
						yPos = win_height * scale;
						yVel *= -1;
					}
					
					//touch star
					if(touching && 
						touchX >= xPos && touchX <= xPos+sprite.getWidth()*scale &&
						touchY >= yPos && touchY <= yPos+sprite.getHeight()*scale)
					{
						xVel *= -1;
						yVel *= -1;
						xPos = (float)Math.random()* win_width *scale;
						yPos = (float)Math.random()* win_height *scale;
						mVibrator.vibrate(22);
					}
					break;
				}
				elapsedTime();
			}
				
			try {
				Thread.sleep(10);
			} catch(InterruptedException ex) {
				//do nothing
			}
		}
	}
	
	//draw
	public void draw(GL10 gl)
	{
		if(lastTime==0)
			return;
		
		switch(game_state)
		{
		case SPLASH_SCREEN:
			//gray
			gl.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			
			sprite.draw(gl, xPos, yPos, win_width, win_height, winScale);
			break;
		case MAIN_MENU:
			//red
			gl.glClearColor(1f, 0.4f, 0.4f, 1.0f);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

			sprite.draw(gl, xPos, yPos, win_width, win_height, winScale);
			break;
		case PLAYING:
			//green
			if(touching)
				gl.glClearColor(0.4f, 1f, 1f, 1.0f);
			else
				gl.glClearColor(0.4f, 1f, 0.4f, 1.0f);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

			sprite.draw(gl, xPos, yPos, win_width, win_height, winScale);
			break;
		}
	}

	/**
	 * Calculates the time difference between game ticks.
	 * 
	 * @modified 1/10/2012
	 */
	private void elapsedTime()
	{
		long curTime = System.nanoTime();//System.currentTimeMillis();
		elapsedTime = (int)((curTime - lastTime)/PRECISION_MILLIS);
		lastTime = curTime;
		
		if(elapsedTime>DELTA_TIME_LIMIT)
			percent = DELTA_TIME_LIMIT/MILLIS;
		else
			percent = elapsedTime/MILLIS;
		
		/*framesOneSecond += elapsedTime;
		framesPerSecond ++;
		if(framesOneSecond>1000) {
			framesOneSecond -= 1000;
			//Log.v("GameThread", "ID:"+getId()+" FPS:"+framesPerSecond+" ET:"+elapsedTime);
			framesPerSecond = 0;
		}*/
	}

	//saved game
	public void saveGame()
	{
		synchronized (mSurfaceHolder)
		{
			if(savedGame!=null) {
				//saving
				SharedPreferences.Editor editor = savedGame.edit();
				//editor.putInt(PREFS_COINS, coins);
				editor.commit();
			}
		}
	}
	public void loadGame()
	{
		//loading
		savedGame = mContext.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
		
		//coins
		//coins = savedGame.getInt(PREFS_COINS, 0);
		
		game_state = SPLASH_SCREEN;
	}
	
	//Touch event
	public void setTouchEvent(boolean value,float x,float y)
	{
		synchronized(mSurfaceHolder)
		{
			switch(game_state)
			{
			case SPLASH_SCREEN:
				game_state = MAIN_MENU;
				break;
			case MAIN_MENU:
				game_state = PLAYING;
				break;
			case PLAYING:
				touching = value;
				touchX = x*scale;
				touchY = y*scale;
				break;
			}
			//topActivity.showDialog(1);
		}
	}
	
	//mutators
	public void setWindowSize(int wW,int wH,float s)
	{
		synchronized (mSurfaceHolder)
		{
			win_width = wW;
			win_height = wH;
			scale = s;

			winWidthHalf = wW/2;
			winHeightHalf = wH/2;
			
			winScale = 1/scale;
			winWidthScaled = wW*winScale;
			winCentX = winWidthScaled/2;
			winHeightScaled = wH*winScale;
			winCentY = winHeightScaled/2;
			
			//scale according to device
			sprite.setScale(scale);
		}
	}
	public void setRunning(boolean r)
	{
		synchronized(mSurfaceHolder)
		{
			running = r;
		}
	}
}
