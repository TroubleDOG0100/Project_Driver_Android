package com.bingostudio.project_driver;

import android.graphics.Paint;

public class Rectangle {

	public int x,y,w,h;
	public Paint color;
	
	public Rectangle(int x, int y, int w, int h, int colorInt) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;

		color = new Paint();
		color.setColor(colorInt);
		color.setStyle(Paint.Style.FILL);
	}

	public Rectangle(int x, int y, int w, int h) {
		this(x,y,w,h,Game.DEFAULT_COLOR);
	}

	public Rectangle() {
		this(0,0,0,0, Game.DEFAULT_COLOR);
	}
	
	public boolean intersects(Rectangle otherRect) {
		if (x < otherRect.x || x > (otherRect.x + otherRect.w))
			return false;
		
		if (y < otherRect.y || y > (otherRect.y + otherRect.h))
			return false;

		return true;
	}
	
	@Override
	public String toString() {
		return "[" + x + " " + y + " " + w + " " + h + "]";
	}
}
