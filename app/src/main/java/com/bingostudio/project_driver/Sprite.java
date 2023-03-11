package com.bingostudio.project_driver;

import android.graphics.Bitmap;

public class Sprite {
	
	protected int width,height;
	protected Bitmap spriteImg;
	
	public Sprite(Bitmap image, int x, int y, int width, int height) {
		this.width = width;
		this.height = height;
		
		this.spriteImg = image;
	}
	
	public Sprite(Bitmap image) {
		this(image, 0, 0, image.getWidth(), image.getHeight());
	}

}
