package com.corvstudios.blanktemplate;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;

public class OpenGLRenderer extends Thread implements GLSurfaceView.Renderer
{
	private SurfaceHolder mSurfaceHolder;
	private Context mContext;
	private Handler mHandler;
	
	private Activity topActivity;
	private Vibrator vibrator;
	
	private GameThread gameThread;
	
	private float scale = 1;
	
	private boolean gluOrthoFlag = false;
    
	public OpenGLRenderer(SurfaceHolder surfaceHolder,Context context,Handler handler,Activity act,Vibrator vb)
	{
		mSurfaceHolder = surfaceHolder;
		mContext = context;
		mHandler = handler;
		topActivity = act;
		vibrator = vb;
	}
	@Override
	public void onDrawFrame(GL10 gl)
	{
		gl.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		gameThread.draw(gl);
	}
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		if(gameThread!=null)
			gameThread.setRunning(false);
		gameThread = new GameThread(mSurfaceHolder,mContext,mHandler,topActivity,vibrator);
		
		gluOrthoFlag = false;
		
		/*
         * Some one-time OpenGL initialization can be made here probably based
         * on features of this particular context
         */
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        gl.glShadeModel(GL10.GL_FLAT);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	    gl.glLineWidth(2.0f);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL10.GL_TEXTURE_2D);

        /*
         * By default, OpenGL enables features that improve quality but reduce
         * performance. One might want to tweak that especially on software
         * renderer.
         */
        gl.glDisable(GL10.GL_DITHER);
        gl.glDisable(GL10.GL_LIGHTING);
	}
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		gl.glViewport(0, 0, width, height);

		if(!gluOrthoFlag) {
			//Set Aspect Ratio Of The Window
			gl.glMatrixMode(GL10.GL_PROJECTION);
			GLU.gluOrtho2D(gl, 0, width, height, 0);

			gameThread.init(gl);
			gluOrthoFlag = true;
		}
		
		//Select The Modelview Matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	
		scale = height/15f/32f;
		gameThread.setWindowSize(width,height,scale);
	}
	
	//access to the game thread
	public GameThread getGameThread()
	{
		return gameThread;
	}
}
