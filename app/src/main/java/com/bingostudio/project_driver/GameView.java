package com.bingostudio.project_driver;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	private Game game;
	private RenderHandler renderH;
	private Handler handler;
	private SurfaceHolder surfaceHolder;

	public GameView(Context context, Game game) {
		super(context);

		this.game = game;
		this.renderH = game.getRenderH();
		this.handler = game.getHandler();

		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);

		setFocusable(true);
	}

	public SurfaceHolder getSurfaceHolder(){
		return surfaceHolder;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		System.out.println("Created surface");
		game.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO: In case we implement Dynamic UI text scaling, this would become useful.
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		System.out.println("Surface destroyed");
		game.stop();
	}

	@Override
	public void draw(Canvas canvas){
		//Clears canvas
		super.draw(canvas);
		game.renderOnCanvas(canvas);
	}

}
	