package com.bingostudio.project_driver;

import android.graphics.Bitmap;

import java.net.HttpURLConnection;

public class SpriteSheet {
	
	protected Bitmap sheetImg;

	protected int rowSize = 0;
	protected int columnSize = 0;
	
	private int xSprites, ySprites;

	public SpriteSheet(Bitmap sheetImg, int rowSize, int columnSize) {
		this.sheetImg = sheetImg;
		this.rowSize = rowSize;
		this.columnSize = columnSize;

		//Should return integer if the rowSize and columnSize is accurate to each sprite.
		this.xSprites = sheetImg.getWidth()/rowSize;
		this.ySprites = sheetImg.getHeight()/columnSize;
	}

}
