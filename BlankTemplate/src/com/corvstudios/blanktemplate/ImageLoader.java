package com.corvstudios.blanktemplate;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

/*
 * @author Oscar J. Gomez
 * @version 1.0
 * @modified 1/10/2012
 */
public class ImageLoader
{
	// Pre-allocated arrays to use at runtime so that allocation during the
	// test can be avoided.
	private int[] mTextureNameWorkspace = new int[1];
    private int[] mCropWorkspace = new int[4];

	// Specifies the format our textures should be converted to upon load.
	private static BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
	
    public ImageLoader()
    {
        // Set our bitmaps to 16-bit, 565 format.
        //sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
    }
    
	/** 
	 * Loads a bitmap into OpenGL and sets up the common parameters for 
	 * 2D texture maps. 
	 */
	public Sprite loadBitmap(Context context, GL10 gl, int resourceId, boolean rotatable, boolean flipable)
	{
	    if(context != null && gl != null)
	    {
	    	//Generate one texture pointer...
	        gl.glGenTextures(1, mTextureNameWorkspace, 0);
	        
	        //grab location index
	        int textureID = mTextureNameWorkspace[0];
	        
			//...and bind it to our array
	        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);
	
			//Create Nearest Filtered Texture
	        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	
			//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
	        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
	        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
			
	        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, /*GL10.GL_REPLACE*/ GL10.GL_MODULATE);
	
	        InputStream is = context.getResources().openRawResource(resourceId);
	        Bitmap bitmap;
	        try
	        {
	            bitmap = BitmapFactory.decodeStream(is, null, sBitmapOptions);
	        }
	        finally
	        {
	            try
	            {
	                is.close();
	            }
	            catch(IOException e)
	            {
	                // Ignore.
	            }
	        }
	
	        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	
	        //whether image can rotate or not
	        if(!rotatable)
	        {
	            mCropWorkspace[0] = 0;
	            mCropWorkspace[1] = bitmap.getHeight();
	            mCropWorkspace[2] = bitmap.getWidth();
	            mCropWorkspace[3] = -bitmap.getHeight();
	            
	            bitmap.recycle();
	
	            ((GL11)gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0);
	
	            int error = gl.glGetError();
	            if(error != GL10.GL_NO_ERROR)
	            {
	                //Log.e("SpriteMethodTest", "Texture Load GLError: " + error);
	            }
	        }
	        return new Sprite(textureID,bitmap.getWidth(),bitmap.getHeight(),rotatable,flipable);
	    }
	    return null;
	}
}
