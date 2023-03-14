package com.bingostudio.project_driver;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

public class Car extends GameObject {

	private Game game;
	private RenderHandler renderH;
	private Handler handler;
	
	public static Vector2D car_size_px = new Vector2D();
	final static int TOTAL_CAR_COUNT_IN_SHEET = 5;
	private static final Vector2D ORIGINAL_CAR_SIZE_PX = new Vector2D(60, 91);
	private static final int CAR_MARGIN_LR = 50;

	public static final Bitmap[] trafficCars = new Bitmap[TOTAL_CAR_COUNT_IN_SHEET];

	private int bitmapID;

	private int carSpeed;
	
	private Random r = new Random();
	
	public Car(Game game, int x, int y) {
		super(x, y, (int) car_size_px.x, (int) car_size_px.y, ID.Car);
		this.game = game;
		this.renderH = game.getRenderH();
		this.handler = game.getHandler();

		bitmapID = 1 + r.nextInt(4);

		// Traffic speed will increase with greater score.
		this.carSpeed = (int) Game.clamp(game.getGui().getScore()/100, 8, 15);
	}

	public static void computeCarDimensions(){
		Car.car_size_px.x = Game.roadWidth/Game.MAX_VEHICLES_IN_ROW - CAR_MARGIN_LR;
		// Now scale on the Y, so that the aspect ratio is not lost.
		Car.car_size_px.y = (Game.propSheetScale.y*ORIGINAL_CAR_SIZE_PX.y*((Car.car_size_px.x)/(Game.propSheetScale.x * ORIGINAL_CAR_SIZE_PX.x)));
	}

	// Extracts from given spriteSheet (which should correspond to resource "car_spritesalpha.png") car bitmaps and stores them in trafficCars array.
	public static void parseCarsFromSprite(SpriteSheet spriteSheet){
		Vector2D rowAndColumn = new Vector2D(0,0);
		Vector2D sheetScale = Game.propSheetScale;

		for (int i = 0; i <= 4; i++){
			rowAndColumn.setTo(i % 3, (float) Math.floor(i/3));

			System.out.println(rowAndColumn.y + " " + rowAndColumn.x);

			Rectangle resRec = spriteSheet.getTileAt((int) rowAndColumn.y, (int) rowAndColumn.x);
			// Take from w un h the bitmap.
			Bitmap snapshot = Bitmap.createBitmap(spriteSheet.sheetImg, resRec.x, resRec.y, (int) (ORIGINAL_CAR_SIZE_PX.x*sheetScale.x), (int) (ORIGINAL_CAR_SIZE_PX.y * sheetScale.y));

			trafficCars[i] = Bitmap.createScaledBitmap(snapshot, (int) Car.car_size_px.x, (int) Car.car_size_px.y, false);
		}
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
		renderH.renderImg(Car.trafficCars[bitmapID], rect.x, rect.y, canvas);
	}
	
}
