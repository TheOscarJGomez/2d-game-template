package com.corvstudios.blanktemplate;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11Ext;

/*
 * @author Oscar J. Gomez
 * @version 1.5
 * @modified 2/11/2013
 */
public class Sprite {
	private static int BINDED_TEXTURE = -1;
	
	private int width = 0;
	private int height = 0;
	private int halfWidth = 0;
	private int halfHeight = 0;
	private float widthScaled = 0;
	private float heightScaled = 0;
	
	private int textureID = -1;
	
	private boolean rotatable = false;
	private boolean flipable = false;

	//rotatable sprite
	private FloatBuffer vertexBuffer,flipBuffer;
	private FloatBuffer textureBuffer;
	private ByteBuffer indexBuffer;
	private float[] vertices,flipVertices;
	private float[] texture = {0.0f, 0.0f,
							1.0f, 0.0f,
							0.0f, 1.0f,
							1.0f, 1.0f};
	private byte[] indices = {0, 1, 2, 3, 2, 1};
	
	/**
     * Sprite constructor creates the object.
     *
     * @param id  identifier for the sprite given by ImageLoader.
     * @param w   width of the sprite.
     * @param h   height of the sprite.
     * @param r   if the Sprite can be rotated.
     * @param f   if the Sprite can be flipped.
     * @see ImageLoader
     */
	public Sprite(int id,int w,int h,boolean r,boolean f) {
		BINDED_TEXTURE = -1;
		textureID = id;
		width = w;
		height = h;
		halfWidth = w/2;
		halfHeight = h/2;
		widthScaled = w;
		heightScaled = h;
		rotatable = r;
		flipable = f;
		
		if(r || f) {
			//flipable texture drawing
			if(f) {
				flipVertices = new float[8];
				flipVertices[0] = 0.0f; flipVertices[1] = 0.0f;
				flipVertices[2] = -w; flipVertices[3] = 0.0f;
				flipVertices[4] = 0.0f; flipVertices[5] = h;
				flipVertices[6] = -w; flipVertices[7] = h;
			
				flipBuffer = ByteBuffer.allocateDirect(flipVertices.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
				flipBuffer.put(flipVertices);
				flipBuffer.position(0);
			}
			
			vertices = new float[8];
			vertices[0] = 0.0f; vertices[1] = 0.0f;
			vertices[2] = w; vertices[3] = 0.0f;
			vertices[4] = 0.0f; vertices[5] = h;
			vertices[6] = w; vertices[7] = h;
			
	        //rotatable texture drawing
	        vertexBuffer = ByteBuffer.allocateDirect(vertices.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
			vertexBuffer.put(vertices);
			vertexBuffer.position(0);
			
	        textureBuffer = ByteBuffer.allocateDirect(texture.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
			textureBuffer.put(texture);
			textureBuffer.position(0);
			
	        indexBuffer = ByteBuffer.allocateDirect(indices.length);
			indexBuffer.put(indices);
			indexBuffer.position(0);
		}
	}
	/**
	 * Stretches the sprite ready to draw.
	 * 
	 * @param w			the amount to stretch along the x-axis.
	 * @param h			the amount to stretch along the y-axis.
	 */
	public void stretch(int w,int h) {
		vertices[2] = w;
		vertices[5] = h;
		vertices[6] = w;
		vertices[7] = h;
		
        vertexBuffer.put(vertices);
		vertexBuffer.position(0);
	}
	/**
	 * Draws the sprite.
	 * 
	 * @param gl		graphics library object.
	 * @param x			location to draw on the x-axis.
	 * @param y			location to draw on the y-axis.
	 * @param winWidth	the width of the window.
	 * @param winHeight	the height of the window.
	 * @param winScale	the scale of the window.
	 */
	public void draw(GL10 gl,float x,float y,int winWidth,int winHeight,float winScale) {
        //gl.glEnable(GL10.GL_TEXTURE_2D);
		
		//binds specified texture
		if(BINDED_TEXTURE!=textureID) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);
			BINDED_TEXTURE = textureID;
		}

		//opacity
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		if(rotatable || flipable) {
			//save current matrix
			gl.glPushMatrix();
			
			//edit matrix
			gl.glTranslatef(x*winScale, y*winScale, 1);
			if(winScale!=1)
				gl.glScalef(winScale, winScale, winScale);
			
			//Point to our buffers
			//gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
			//Enable the vertex and texture state
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
			
			//Draw the vertices as triangles, based on the Index Buffer information
			gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
			
			//Disable the client state before leaving
			//gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			
			//load last matrix
			gl.glPopMatrix();
		} else
			//draw_texture
			((GL11Ext)gl).glDrawTexfOES(x*winScale, winHeight-(y*winScale+heightScaled), 0, widthScaled, heightScaled);
        
		//gl.glDisable(GL10.GL_TEXTURE_2D);
	}

	/**
	 * Sets this sprite into memory ready to draw.
	 * 
	 * @param gl		graphics library object.
	 */
	public void bindTexture(GL10 gl) {
		if(BINDED_TEXTURE!=textureID) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);
			BINDED_TEXTURE = textureID;
		}

		//opacity
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
	/**
	 * Draws the texture.
	 * 
	 * @param gl		graphics library object.
	 * @param x			location to draw on the x-axis.
	 * @param y			location to draw on the y-axis.
	 * @param winWidth	the width of the window.
	 * @param winHeight	the height of the window.
	 * @param winScale	the scale of the window.
	 */
	public void drawTexture(GL10 gl,float x,float y,int winWidth,int winHeight,float winScale) {
		((GL11Ext)gl).glDrawTexfOES(x*winScale, winHeight-(y*winScale+heightScaled), 0, widthScaled, heightScaled);
	}
	
	//accessors
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public int getHalfWidth() {
		return halfWidth;
	}
	public int getHalfHeight() {
		return halfHeight;
	}
	public boolean getRotatable() {
		return rotatable;
	}
	public boolean getFlippable() {
		return rotatable;
	}
	
	//mutators
	public void setScale(float scale) {
		widthScaled = width*scale;
		heightScaled = height*scale;
	}
}