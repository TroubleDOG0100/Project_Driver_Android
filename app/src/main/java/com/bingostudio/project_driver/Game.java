package com.bingostudio.project_driver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Process;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Vector;

public class Game implements Runnable {

	public final static String TITLE = "Project Driver";
	public final static int DEFAULT_COLOR = 0xFFFFFFFF;
	public final static int ALPHA = 0xFFFF00FF;

	public static final int MAX_VEHICLES_IN_ROW = 3;

	public Rectangle[] rects = new Rectangle[2];
	private RenderHandler renderH;

	public boolean gameRunning = false;

	private Thread gameThread;
	private GameView gameView;
	private MainActivity mainAct;
	private Handler handler;
	private GUI gui;
	private Player player;

	public static Vector2D propSheetScale;

	private static SpriteSheet propSheet;

	public static DisplayMetrics displayMetrics = new DisplayMetrics();

	public static int roadWidth;

	public static class settings{
		public enum TOUCH_CONTROLS {
			FRAME_BASED,
			REL_TO_PLAYER,
			REL_TO_CENTER
		}

		public static TOUCH_CONTROLS currentControls = TOUCH_CONTROLS.FRAME_BASED;
	}

	public enum States {
		Playing,
		Death,
		Start;
	}

	private HashMap<String,Bitmap> loadedPictures = new HashMap<>();

	public Bitmap getBitmapResource(String key) {
		return loadedPictures.get(key);
	}

	private Rectangle[] screenTouchRect;
	private int touchRectSize;

	public States state = States.Start;

	public Game(MainActivity mainAct) {
		this.mainAct = mainAct;

		mainAct.getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		this.handler = new Handler(this);
		this.renderH = new RenderHandler();
		this.gui = new GUI(this);

		rects[0] = new Rectangle(0,0,100,Game.displayMetrics.heightPixels, 0xFF00FF00);
		rects[1] = new Rectangle(Game.displayMetrics.widthPixels - 100,0,100,Game.displayMetrics.heightPixels,0xFF00FF00);

		screenTouchRect = new Rectangle[4];

		//Size of touchable rectangles depends on the size of the display
		touchRectSize = (int) clamp((float) displayMetrics.widthPixels/3,100.0f,10000.0f);

		//Create rectangles at the side of screen, used for detecting player input.
		for (int i = 0; i < screenTouchRect.length; i++){
			if (i < 2){
				screenTouchRect[i] = new Rectangle(0,(displayMetrics.heightPixels-touchRectSize) * i,displayMetrics.widthPixels,touchRectSize);
			}else {
				screenTouchRect[i] = new Rectangle((displayMetrics.widthPixels-touchRectSize) * (i - 2),0,touchRectSize,displayMetrics.heightPixels);
			}
		}

		roadWidth = displayMetrics.widthPixels - 200;

		//Loading resources
		Bitmap roadBitmap = BitmapFactory.decodeResource(mainAct.getResources(), R.drawable.road_vec);
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(roadBitmap, roadWidth, displayMetrics.heightPixels, false);
		loadedPictures.put("road_texture", scaledBitmap);


		//Bitmap gets scaled depending on PIXEL DENSITY
		Bitmap propBitmap = BitmapFactory.decodeResource(mainAct.getResources(), R.drawable.car_spritesalpha);

		propSheetScale = new Vector2D(propBitmap.getWidth()/384.f, propBitmap.getHeight()/384.f);

		// Since our bitmap was scaled up, we need to calculate the new column width/height in px for each tile.
		propSheet = new SpriteSheet(propBitmap, (int) (128 * propSheetScale.x), (int) (128 * propSheetScale.y));

		Car.computeCarDimensions();
		Car.parseCarsFromSprite(propSheet);

		//Starts the app.
		this.gameView = new GameView(this.mainAct,this);

		System.out.println(displayMetrics.widthPixels + " " + displayMetrics.heightPixels);
	}
	static final Vector2D MOVE_VECTOR_SCALE = new Vector2D(500, 1000);
	static final Vector2D MOVE_VECTOR_MAX = new Vector2D(5, 10);

	public void screenPressed(int tX, int tY) {
		if (player != null) {
			Vector2D vec2 = new Vector2D();
			switch (settings.currentControls) {
				case FRAME_BASED: {
					Rectangle touchRectangle = new Rectangle(tX,tY,1,1);

					if (touchRectangle.intersects(screenTouchRect[0]))
						//Top screen rectangle
						vec2.y += -1;
					else if (touchRectangle.intersects(screenTouchRect[1]))
						//Bottom screen rectangle
						vec2.y += 1;
					else if (touchRectangle.intersects(screenTouchRect[2]))
						//Left screen rectangle
						vec2.x += -1;
					else if (touchRectangle.intersects(screenTouchRect[3]))
						//Right screen rectangle
						vec2.x += 1;

					break;
				}
				case REL_TO_PLAYER:
				case REL_TO_CENTER:{
					Vector2D center = null;
					if (settings.currentControls == settings.TOUCH_CONTROLS.REL_TO_PLAYER){
						center = player.rect.getCenter();
					}else{
						center = new Vector2D(displayMetrics.widthPixels/2, displayMetrics.heightPixels/2);
					}

					// MOVE_VECTOR_SCALE determines the sensitivity of player motion from touch.
					float xDeltaScaled = (center.x - tX) / MOVE_VECTOR_SCALE.x;
					float yDeltaScaled = (center.y - tY) / MOVE_VECTOR_SCALE.y;

					vec2.x = -clamp(xDeltaScaled, Integer.MIN_VALUE, MOVE_VECTOR_MAX.x);
					vec2.y = -clamp(yDeltaScaled, Integer.MIN_VALUE, MOVE_VECTOR_MAX.y);
				}
			}
			System.out.println(vec2);

			player.setMoveVector(vec2);
		}

		gui.onScreenPressed(tX,tY);
	}

	public void screenUnPressed() {
		if (player != null){
			player.setMoveVector(new Vector2D());
		}
	}

	public boolean touchOverRect(Rectangle rect ,int tX, int tY){
		if (tX >= rect.x && tX <= (rect.x + rect.w) && tY >= rect.y && tY <= (rect.y + rect.h))
			return true;
		return false;
	}

	public void setState(States newState) {
		switch (newState){
			case Playing: {
				handler.clearCars();
				game:startPlayer();
				break;
			}case Death: {
				System.out.println("Death");
				break;
			}case Start: {
				System.out.println("Start");
				break;
			}
		}

		state = newState;
	}

	public synchronized void start() {
		setGameRunning(true);

		gameThread = new Thread(this);
		gameThread.start();
	}

	public void startPlayer(){
		//Reinitialize score to 0, as a new game has started.
		gui.setScore(0);

		player = new Player(this, new Rectangle((int) (100 + roadWidth/2 - Car.car_size_px.x/2), (int) (displayMetrics.heightPixels - Car.car_size_px.y), (int) Car.car_size_px.x, (int) Car.car_size_px.y));
		handler.addObj(player);
	}

	public void setGameRunning(boolean isRunning){
		gameRunning = isRunning;
	}
	
	public synchronized void stop() {
		boolean retry = true;
		while (retry) {
			try {
				setGameRunning(false);
				gameThread.join();
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			retry = false;
		}
	}

	public void sendScreenShot(Bitmap screenShoot){
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		screenShoot.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(mainAct.getContentResolver(), screenShoot, "Screeny", "This is a test :)");
		Uri imageUri = Uri.parse(path);

		Intent sendImg = new Intent();
		sendImg.setAction(Intent.ACTION_SEND);
		sendImg.putExtra(Intent.EXTRA_TEXT,"Hey! I managed to score big in "+ TITLE +". Do you think you can beat my high score of " + gui.getScore() + " points?");
		sendImg.putExtra(Intent.EXTRA_STREAM, imageUri);
		sendImg.setType("img/*");

		mainAct.startActivity(sendImg);
	}

	public void renderOnCanvas(Canvas canvas) {
		renderH.renderImg(getBitmapResource("road_texture"), 100, 0, canvas);

		renderH.renderRect(rects[0],canvas);
		renderH.renderRect(rects[1],canvas);

		for (int i = 0; i < handler.gameObj.size(); i++){
			GameObject tempObj = handler.gameObj.get(i);
			tempObj.render(canvas);
		}

		gui.render(canvas);
	}

	@Override
	public void run() {
		// Create buffer strategy, start game loop etc.
		long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;

        while(gameRunning){
            long now = System.nanoTime();
			Canvas canvas = null;
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				// Each new frame.
				handler.tick();
				gui.tick();
				delta--;
			}
			if(gameRunning) {
				canvas = gameView.getSurfaceHolder().lockCanvas();
				if (canvas != null){
					gameView.draw(canvas);
					gameView.getSurfaceHolder().unlockCanvasAndPost(canvas);
				}

				frames++;
			}
            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println("FPS: "+ frames);
                frames = 0;
            }
        }
        // Thread ends execution, gets terminated ;).
	}

	public static float clamp(float var, float min, float max) {
		if (var >= max) {
			return max;
		}else if (var <= min) {
			return min;
		}else {
			return var;
		}
	}

	public RenderHandler getRenderH() {
		return renderH;
	}

	public Handler getHandler(){
		return handler;
	}

	public GameView getView(){
		return gameView;
	}

	public GUI getGui() {
		return gui;
	}

}
