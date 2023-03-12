package com.bingostudio.project_driver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.bingostudio.project_driver.Game.States;

public class GUI {
	
	private Game game;
	
	private static final String[] SM_BUTTONS = {"Play", "About"};
	private static final int TITLE_YPOS = 170;

	private static final int BUTTON_XSize = 450;
	private static final int BUTTON_YSize = 220;
	
	private static final String[] DS_BUTTONS = {"Continue", "Share score"};

	private static final int BUTTON_FILL_COLOR = 0xFF555555;

	private static final int BUTTON_TEXT_FILL_COLOR = 0xFFFFFFFF;

	private States stateKeeper;
	private int score = 0;
	private ArrayList<Rectangle> currentButtons = new ArrayList<>();

	private Paint defaultPaint;
	private Paint titlePaint;
	private Paint buttonPaint;
	private Paint boxPaint;
	
	public GUI(Game game) {
		this.game = game;
		this.stateKeeper = game.state;

		// TODO: The text size should scale based on screen size.
		defaultPaint = new Paint();
		defaultPaint.setColor(0xFFFFFFFF);
		defaultPaint.setTextSize(100);
		defaultPaint.setStyle(Paint.Style.FILL);
		defaultPaint.setTypeface(Typeface.DEFAULT);

		titlePaint = new Paint();
		titlePaint.setColor(0xFFFFFFFF);
		titlePaint.setTextSize(160);
		titlePaint.setTypeface(Typeface.DEFAULT_BOLD);

		buttonPaint = new Paint();
		buttonPaint.setColor(BUTTON_TEXT_FILL_COLOR);
		buttonPaint.setTextSize(120);
		buttonPaint.setTypeface(Typeface.DEFAULT);

		boxPaint = new Paint();
		boxPaint.setColor(BUTTON_FILL_COLOR);
	}

	public void tick() {
		if (game.state == States.Playing)
			score++;
	}

	public void render(Canvas canvas) {
		
		boolean addButtons = false;
		
		//Reset current buttons if state has been changed.
		if (stateKeeper != game.state || currentButtons.size() == 0) {
			currentButtons.clear();
			stateKeeper = game.state;
			addButtons = true;
		}
		
		switch(game.state) {
			case Playing: {
					String displayText = "Score: " + score;

					Rect boundRect = new Rect();

					defaultPaint.getTextBounds(displayText, 0,displayText.length(), boundRect);
					int textWidth = (int) defaultPaint.measureText(displayText);
					
					canvas.drawText("Score: " + score, Game.displayMetrics.widthPixels/2 - textWidth/2, boundRect.height()+50, defaultPaint);
				}
				break;
			case Start: {
					Rect boundRect = new Rect();

					titlePaint.getTextBounds(Game.TITLE, 0,Game.TITLE.length(), boundRect);
					int textWidth = (int) titlePaint.measureText(Game.TITLE);

					canvas.drawText(Game.TITLE, Game.displayMetrics.widthPixels/2 - textWidth/2, TITLE_YPOS + boundRect.height(),titlePaint);

					int titleSizeY = boundRect.height();

					for (int i = 0; i < SM_BUTTONS.length; i++) {
						int boxXPos = Game.displayMetrics.widthPixels/2 - BUTTON_XSize/2;
						int boxYPos = (TITLE_YPOS + titleSizeY + 260) + (350 * i);

						canvas.drawRect(new Rect(boxXPos, boxYPos,  boxXPos + BUTTON_XSize, boxYPos + BUTTON_YSize),	boxPaint);
						//g.drawRect(boxXPos, boxYPos, 160, 60);
						
						//Add text
						drawRectCenter(canvas, SM_BUTTONS[i], boxXPos, boxYPos, BUTTON_XSize, BUTTON_YSize);
						
						if (addButtons)
							currentButtons.add(new Rectangle(boxXPos, boxYPos, BUTTON_XSize, BUTTON_YSize));
					}
				}
				break;
			case Death:{
					String displayText = "Game Over";

					Rect titleBoundRect = new Rect();

					titlePaint.getTextBounds(displayText, 0, displayText.length(), titleBoundRect);
					int textWidth = (int) titlePaint.measureText(displayText);

					canvas.drawText(displayText,Game.displayMetrics.widthPixels/2 - textWidth/2, TITLE_YPOS, titlePaint);

					String scoreText = "Your score was: " + score;

					Rect boundRect = new Rect();

					defaultPaint.getTextBounds(scoreText, 0, scoreText.length(), boundRect);
					int scoreWidth = (int) defaultPaint.measureText(scoreText);

					canvas.drawText(scoreText,Game.displayMetrics.widthPixels/2 - scoreWidth/2, TITLE_YPOS + titleBoundRect.height() + 10, defaultPaint);
					
					//Create buttons
					for (int i = 0; i < DS_BUTTONS.length; i++) {
						int boxXPos = (Game.displayMetrics.widthPixels/2) - BUTTON_XSize/2;
						int boxYPos = (Game.displayMetrics.heightPixels - 700) + (350 * i);
						
						//g.drawRect(boxXPos, boxYPos, 160, 60);
						
						//Add text
						drawRectCenter(canvas, DS_BUTTONS[i], boxXPos, boxYPos, BUTTON_XSize, BUTTON_YSize);
						
						if (addButtons)
							currentButtons.add(new Rectangle(boxXPos, boxYPos, BUTTON_XSize, BUTTON_YSize));
					}
				}
				break;
		}
	}
	
	//Method for drawing text into a center of a rectangle.

	public void drawRectCenter(Canvas canvas, String text, int rectX, int rectY, int rectW, int rectH) {
		Rect boundRect = new Rect();

		buttonPaint.getTextBounds(text, 0,text.length(), boundRect);
		int textWidth = (int) buttonPaint.measureText(text);

		canvas.drawText(text, rectX + (rectW/2) - (textWidth/2), rectY + (rectH/2) + 40,buttonPaint);
		//g.drawString(text, rectX + (rectW/2) - (textWidth/2), rectY + (rectH/2) + 8);
	}
	
	public void onScreenPressed(int mX, int mY) {
		for (int buttonIndex = 0; buttonIndex < currentButtons.size(); buttonIndex++) {
			Rectangle button = currentButtons.get(buttonIndex);
			
			if (game.touchOverRect(button, mX, mY)) {
				switch(buttonIndex) {
					//Play Button
					case 0:{
							if (game.state == States.Start) {
								game.setState(States.Playing);
								game.startPlayer();
							}else if (game.state == States.Death) {
								game.setState(States.Start);
							}
						}
						break;
					case 1:{
							if (game.state == States.Start) {
								//About
							} else if (game.state == States.Death) {
								// Has to save the score to the text file.

								Bitmap.Config config = Bitmap.Config.ARGB_8888;
								Bitmap screenShoot = Bitmap.createBitmap(Game.displayMetrics.widthPixels,Game.displayMetrics.heightPixels,config);

								Canvas shootCanvas = new Canvas(screenShoot);
								game.renderOnCanvas(shootCanvas);

								game.sendScreenShot(screenShoot);

								/*Intent sendScore = new Intent();
								sendScore.setAction(Intent.ACTION_SEND);

								sendScore.setType("text/plain");
								sendScore.putExtra(Intent.EXTRA_TEXT, "Hey! I just scored " + score + " in Bingo Driver, isn't that awesome!");

								game.startActivityFromIntent(sendScore);*/
							}
						}
						break;
				}
			}
		}
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}