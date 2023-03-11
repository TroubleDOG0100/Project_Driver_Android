package com.bingostudio.project_driver;

import android.graphics.Canvas;

import java.util.Random;

import com.bingostudio.project_driver.Game.States;

public class Player extends GameObject {
	
	private Game game;
	private RenderHandler renderH;
	private Handler handler;
	
	private int[] speeds = {10,8,5};
 	private Random r = new Random();

 	private int sideDir = 0;
 	private int topBottomDir = 0;
	
	public Player(Game game, Rectangle plrRect) {
		super(plrRect.x, plrRect.y, plrRect.w, plrRect.h, ID.Player);
		this.game = game;
		this.renderH = game.getRenderH();
		this.handler = game.getHandler();

	}

	public void setSideDir(int dir){
		sideDir = dir;
	}

	public void setTopBottom(int dir){
		topBottomDir = dir;
	}

	@Override
	public void tick() {
		// Plr speed is different depending on how close the player is to the oncoming car spawn.
		int plrSpeed = speeds[(int) rect.y/(Game.displayMetrics.heightPixels/speeds.length)];
		
		// Player input registration
		rect.x += plrSpeed * sideDir;
		rect.y += plrSpeed * topBottomDir;
		
		int estimatedTopBarHeight = 0;
		
		rect.x = (int) Game.clamp(rect.x, 100, Game.displayMetrics.widthPixels - 100 - rect.w);
		rect.y = (int) Game.clamp(rect.y , 0, Game.displayMetrics.heightPixels - rect.h - estimatedTopBarHeight);
		
		//Collision check with cars.
		for (int i = 0; i < handler.gameObj.size(); i++) {
			GameObject tempObj = handler.gameObj.get(i);
			
			//Check if the game object is a car
			if ((tempObj.id == ID.Car)) {
				if (rect.intersects(tempObj.rect)) {
					game.setState(States.Death);
					handler.removeObj(this);
				}
			}
		}
	}

	@Override
	public void render(Canvas canvas) {
		//renderH.renderRect(rect, canvas);
		renderH.renderSprite(game.getPropSheet().sheetImg,rect.x,rect.y,0,0,game.getPropSheet().rowSize,game.getPropSheet().columnSize,canvas);
	}

}
