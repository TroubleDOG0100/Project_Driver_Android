package com.bingostudio.project_driver;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;

public class RenderHandler {

	public Rectangle camera;
	private int[] pixels;
	private Bitmap view;

	public RenderHandler() {
		//Create a buffered image, that will represent our view.
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		view = Bitmap.createBitmap(Game.displayMetrics.widthPixels,Game.displayMetrics.heightPixels,conf);

		camera = new Rectangle(0,0,Game.displayMetrics.widthPixels,Game.displayMetrics.heightPixels);

		camera.x = 0;
		camera.y = 0;

		// Create an array for the pixels.
		pixels = new int[view.getWidth() * view.getHeight()];
		view.getPixels(pixels,0,Game.displayMetrics.widthPixels,0,0,Game.displayMetrics.widthPixels,Game.displayMetrics.heightPixels);//((DataBufferInt) view.getRaster().getDataBuffer()).getData();
	}

	public void render(Canvas canvas) {
		//canvas.drawBitmap(view, 0,0,null);
		//g.drawImage(view, 0, 0, Game.WIDTH, Game.HEIGHT, null);
	}

	public void clear() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0;
		}
	}

	public void renderArray(int[] arrayPixels, int renderX, int renderY, int renderWidth, int renderHeight, int xZoom, int yZoom) {
		for (int y = 0; y < renderHeight; y++) {
			for (int x = 0; x < renderWidth; x++) {
				for (int yDot = 0; yDot < yZoom; yDot++){
					for (int xDot = 0; xDot < xZoom; xDot++) {
						setPixel(arrayPixels[x + (y * renderWidth)], ((x * xZoom) + renderX + xDot), ((y * yZoom) + renderY + yDot));
					}
				}
			}
		}
		view.setPixels(pixels,0,view.getWidth(),0,0,view.getWidth(),view.getHeight());
	}

	public void renderImg(Bitmap img, int x, int y, Canvas canvas) {
		/*int[] imgPixels = new int[img.getWidth() * img.getHeight()];

		img.getPixels(imgPixels,0,img.getWidth(),0,0,img.getWidth(),img.getHeight());
		renderArray(imgPixels, x, y, img.getWidth(), img.getHeight(), xZoom, yZoom);*/
		canvas.drawBitmap(img, x, y, null);
	}

	public void renderSprite(Bitmap spriteSheet, int x, int y, int row, int column, int rowSize, int columnSize, Canvas canvas) {
		//long spriteStart = System.currentTimeMillis();

		int spriteXPos = rowSize * row;
		int spriteYPos = columnSize * column;
		canvas.drawBitmap(spriteSheet,new Rect(spriteXPos, spriteYPos,rowSize + spriteXPos, columnSize + spriteYPos), new Rect(x, y, rowSize + x, columnSize + y), null);
		//System.out.println("One sprite took:" + (System.currentTimeMillis()-spriteStart));
	}

	public void renderRect(Rectangle rect, Canvas canvas) {
		canvas.drawRect(new Rect(rect.x,rect.y,(rect.w + rect.x),(rect.h + rect.y)), rect.color);
		//renderArray(rect.returnPixels(), rect.x, rect.y, rect.w, rect.h, 1, 1);
	}
	
	public void setPixel(int objPixel, int x, int y) {
		int pixelIndex = 0;
		//Check if the pixel is in the bounds player visible area.
		if (x >= camera.x && y >= camera.y && (camera.x + camera.w) > x && (camera.y + camera.h) > y) {
			pixelIndex = (x - camera.x) + (y - camera.y) * view.getWidth();
		}
		
		if (pixelIndex < pixels.length && objPixel != Game.ALPHA)
			pixels[pixelIndex] = objPixel;
	}

}
