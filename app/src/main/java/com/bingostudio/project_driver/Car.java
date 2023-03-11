package com.bingostudio.project_driver;

import android.graphics.Canvas;

import java.util.Random;

public class Car extends GameObject {

	private Game game;
	private RenderHandler renderH;
	private Handler handler;
	
	public static final Rectangle CAR_SIZE = new Rectangle(0,0,230,360);
	
	private int spriteRow;
	private int spriteColumn;
	private int carSpeed;
	
	private Random r = new Random();
	
	public Car(Game game, int x, int y) {
		super(x, y, CAR_SIZE.w, CAR_SIZE.h, ID.Car);
		this.game = game;
		this.renderH = game.getRenderH();
		this.handler = game.getHandler();
		
		spriteColumn = (int) Game.clamp(r.nextInt(2), 0, 1);
		spriteRow = r.nextInt(3);
		
		// Bush in that sprite!
		if (spriteRow == 2 && spriteColumn == 1)
			spriteColumn = 0;
		
		//Check if the traffic doesn't have the same car sprite as the player.
		if (spriteColumn == 0 && spriteRow == 0)
			spriteColumn = 1;
		
		this.carSpeed = 10;//(int) Game.clamp(game.getGui().getScore()/100, 7, 30);
		
		//this.carSprite = game.getSpriteSheet().getSprite(spriteRow, spriteColumn);
	}

	@Override
	public void tick() {
		rect.y += carSpeed;
		
		if (rect.y > Game.displayMetrics.heightPixels) {
			handler.removeObj(this);
		}
	}

	@Override
	public void render(Canvas canvas) {
		renderH.renderSprite(game.getPropSheet().sheetImg, rect.x, rect.y, spriteRow, spriteColumn, game.getPropSheet().rowSize, game.getPropSheet().columnSize,canvas);
	}
	
}
