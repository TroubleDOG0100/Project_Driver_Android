package com.bingostudio.project_driver;

import android.graphics.Canvas;
import android.graphics.Rect;

public abstract class GameObject {
	
	protected Rectangle rect;
	protected ID id;
	
	public GameObject(int x, int y, int width, int height, ID id) {
		this.rect = new Rectangle(x, y, width, height);
		this.id = id;
	}
	
	public abstract void tick();
	public abstract void render(Canvas canvas);
	
}
