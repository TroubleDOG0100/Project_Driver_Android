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
	private static final int TITLE_MAX_MARGIN_TOP = 300;
	private static final float TITLE_Y_SCALE_FROM_TOP = 0.1f;

	private static final int BUTTON_XSize = 450;
	private static final int BUTTON_YSize = 220;
	
	private static final String[] DS_BUTTONS = {"Continue", "Share score"};

	private static final int BUTTON_FILL_COLOR = 0xFF555555;

	private static final int BUTTON_TEXT_FILL_COLOR = 0xFFFFFFFF;

	private static final int TITLE_MARGIN_X = 80;
	private static final float BUTTON_PADDING_SCALE_X = 0.15f;
	private static final int BUTTON_MAX_PADDING_X = 250;

	private static final float BUTTON_PADDING_SCALE_Y = 0.02f;
	private static final int BUTTON_MAX_PADDING_Y = 40;

	private static final float BUTTON_Y_SCALE_OFFSET_FROM_TITLE = 0.04f;
	private static final int BUTTON_MAX_Y_OFFSET_FROM_TITLE = 40;

	private static final float BUTTON_Y_SCALE_OFFSET_FROM_EACH = 0.04f;
	private static final int BUTTON_MAX_Y_OFFSET_FROM_EACH = 100;

	private static final float PLAY_SCORE_Y_SCALE_FROM_TOP = 0.08f;
	private static final int PLAY_SCORE_MAX_MARGIN_TOP = 130;

	private static final float SCORE_Y_SCALE_FROM_TITLE = 0.04f;
	private static final int SCORE_MAX_OFFSET_FROM_TITLE = 40;

	private static final float BUTTON_OFFSET_FROM_BOTTOM_SCALE = 0.07f;
	private static final int MAX_BUTTON_OFFSET_FROM_BOTTOM = 70;


	private States stateKeeper;
	private int score = 0;
	private ArrayList<Rectangle> currentButtons = new ArrayList<>();

	public static int titleOffsetFromTop;
	public static int buttonOffsetFromTitle;
	public static int buttonOffsetFromEach;
	public static int buttonPaddingX;
	public static int buttonPaddingY;
	public static int playingScoreOffsetY;
	public static int scoreOffsetFromTitleY;
	public static int buttonOffsetFromBottomY;
	public static Vector2D smButtonDimensions;
	public static Vector2D dmButtonDimensions;

	public static float global_text_scale = 1.f;
	private static Paint scorePaint;
	private static Paint titlePaint;
	private static Paint buttonPaint;
	private static Paint boxPaint;
	
	public GUI(Game game) {
		this.game = game;
		this.stateKeeper = game.state;

		// Current method of setting textSize relative to available screen width seems janky.
		titlePaint = new Paint();
		titlePaint.setColor(0xFFFFFFFF);
		titlePaint.setTextSize(160);
		titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
		titlePaint.setAntiAlias(true);

		// Checks by what factor does the title text must be scaled down to fit within the screen + margin.
		float textWidth = titlePaint.measureText(Game.TITLE);
		global_text_scale = textWidth/(Game.displayMetrics.widthPixels - TITLE_MARGIN_X);
		titlePaint.setTextSize(160/global_text_scale);

		scorePaint = new Paint();
		scorePaint.setColor(0xFFFFFFFF);
		scorePaint.setTextSize(100/global_text_scale);
		scorePaint.setStyle(Paint.Style.FILL);
		scorePaint.setTypeface(Typeface.DEFAULT);

		buttonPaint = new Paint();
		buttonPaint.setColor(BUTTON_TEXT_FILL_COLOR);
		buttonPaint.setTextSize(120/global_text_scale);
		buttonPaint.setTypeface(Typeface.DEFAULT);

		boxPaint = new Paint();
		boxPaint.setColor(BUTTON_FILL_COLOR);

		// Compute the sizes and offsets of GUI elements.
		playingScoreOffsetY = (int) Game.clamp(PLAY_SCORE_Y_SCALE_FROM_TOP*Game.displayMetrics.heightPixels, 30, PLAY_SCORE_MAX_MARGIN_TOP);
		titleOffsetFromTop = (int) Game.clamp(TITLE_Y_SCALE_FROM_TOP*Game.displayMetrics.heightPixels, 5, TITLE_MAX_MARGIN_TOP);
		buttonPaddingX = (int) Game.clamp(BUTTON_PADDING_SCALE_X*Game.displayMetrics.widthPixels, 10, BUTTON_MAX_PADDING_X);
		buttonPaddingY = (int) Game.clamp(BUTTON_PADDING_SCALE_Y*Game.displayMetrics.heightPixels, 5, BUTTON_MAX_PADDING_Y);
		buttonOffsetFromTitle = (int) Game.clamp(BUTTON_Y_SCALE_OFFSET_FROM_TITLE*Game.displayMetrics.heightPixels, 20, BUTTON_MAX_Y_OFFSET_FROM_TITLE);
		buttonOffsetFromEach = (int) Game.clamp(BUTTON_Y_SCALE_OFFSET_FROM_EACH*Game.displayMetrics.heightPixels, 15, BUTTON_MAX_Y_OFFSET_FROM_EACH);
		scoreOffsetFromTitleY = (int) Game.clamp(SCORE_Y_SCALE_FROM_TITLE*Game.displayMetrics.heightPixels, 10, SCORE_MAX_OFFSET_FROM_TITLE);
		buttonOffsetFromBottomY = (int) Game.clamp(BUTTON_OFFSET_FROM_BOTTOM_SCALE*Game.displayMetrics.heightPixels, 10, MAX_BUTTON_OFFSET_FROM_BOTTOM);

		// Calculate start menu button width (based on greatest of button widths)
	 	smButtonDimensions = new Vector2D(GUI.getGreatestButtonWidth(SM_BUTTONS) + buttonPaddingX*2, GUI.getGreatestButtonHeight(SM_BUTTONS) + buttonPaddingY*2);

		// Calculate death screen button width (based on greatest of button widths)
		dmButtonDimensions = new Vector2D(GUI.getGreatestButtonWidth(DS_BUTTONS) + buttonPaddingX*2, GUI.getGreatestButtonHeight(DS_BUTTONS) + buttonPaddingY*2);
	}

	public static int getGreatestButtonWidth(String[] buttonContents){
		if (buttonContents.length == 0)
			throw new Error("Cannot compute width with no button contents.");

		int greatest = 0, contentIndex = 0;
		for (int i = 0; i < buttonContents.length; i++){
			if (buttonContents[i].length() > greatest) {
				greatest = buttonContents[i].length();
				contentIndex = i;
			}
		}

		return (int) Math.ceil(buttonPaint.measureText(buttonContents[contentIndex]));
	}

	public static int getGreatestButtonHeight(String[] buttonContents){
		if (buttonContents.length == 0)
			throw new Error("Cannot compute height with no button contents.");

		Rect temp = new Rect();
		int greatest = 0;
		for (int i = 0; i < buttonContents.length; i++){
			buttonPaint.getTextBounds(buttonContents[i], 0, buttonContents[i].length(), temp);
			if (temp.height() > greatest)
				greatest = temp.height();
		}

		return (int) Math.ceil(greatest);
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

					scorePaint.getTextBounds(displayText, 0,displayText.length(), boundRect);
					int textWidth = (int) scorePaint.measureText(displayText);
					
					canvas.drawText("Score: " + score, Game.displayMetrics.widthPixels/2 - textWidth/2, playingScoreOffsetY, scorePaint);
				}
				break;
			case Start: {
					Rect boundRect = new Rect();

					titlePaint.getTextBounds(Game.TITLE, 0,Game.TITLE.length(), boundRect);
					int textWidth = (int) titlePaint.measureText(Game.TITLE);

					canvas.drawText(Game.TITLE, Game.displayMetrics.widthPixels/2 - textWidth/2, titleOffsetFromTop, titlePaint);

					int titleSizeY = boundRect.height();

					for (int i = 0; i < SM_BUTTONS.length; i++) {
						int boxXPos = Game.displayMetrics.widthPixels/2 - (int) smButtonDimensions.x/2;
						int boxYPos = (titleOffsetFromTop + titleSizeY + buttonOffsetFromTitle) + ((int) (smButtonDimensions.y + buttonOffsetFromEach) * i);

						canvas.drawRect(new Rect(boxXPos, boxYPos,  (int) (boxXPos + smButtonDimensions.x), (int) (boxYPos + smButtonDimensions.y)),	boxPaint);
						
						//Add text
						drawRectCenter(canvas, SM_BUTTONS[i], boxXPos, boxYPos, (int) smButtonDimensions.x, (int) smButtonDimensions.y);
						
						if (addButtons)
							currentButtons.add(new Rectangle(boxXPos, boxYPos, (int) smButtonDimensions.x, (int) smButtonDimensions.y));
					}
				}
				break;
			case Death:{
					String displayText = "Game Over";

					Rect titleBoundRect = new Rect();

					titlePaint.getTextBounds(displayText, 0, displayText.length(), titleBoundRect);
					int textWidth = (int) titlePaint.measureText(displayText);

					canvas.drawText(displayText,Game.displayMetrics.widthPixels/2 - textWidth/2, titleOffsetFromTop, titlePaint);

					String scoreText = "Your score was: " + score;

					Rect boundRect = new Rect();

					scorePaint.getTextBounds(scoreText, 0, scoreText.length(), boundRect);
					int scoreWidth = (int) scorePaint.measureText(scoreText);

					canvas.drawText(scoreText,Game.displayMetrics.widthPixels/2 - scoreWidth/2, titleOffsetFromTop + titleBoundRect.height() + scoreOffsetFromTitleY, scorePaint);
					
					//Create buttons
					for (int i = 0; i < DS_BUTTONS.length; i++) {
						int posRel = DS_BUTTONS.length - i;

						int boxXPos = (Game.displayMetrics.widthPixels/2) - (int) dmButtonDimensions.x/2;
						int boxYPos = (Game.displayMetrics.heightPixels - buttonOffsetFromBottomY) - ((int) dmButtonDimensions.y * posRel + buttonOffsetFromEach * (posRel - 1));
						
						//Add text
						drawRectCenter(canvas, DS_BUTTONS[i], boxXPos, boxYPos, (int) dmButtonDimensions.x, (int) dmButtonDimensions.y);
						
						if (addButtons)
							currentButtons.add(new Rectangle(boxXPos, boxYPos, (int) dmButtonDimensions.x, (int) dmButtonDimensions.y));
					}
				}
				break;
		}
	}
	
	//Method for drawing text into a center of a rectangle.
	public void drawRectCenter(Canvas canvas, String text, int rectX, int rectY, int rectW, int rectH) {
		Rect boundRect = new Rect();

		buttonPaint.getTextBounds(text, 0,text.length(), boundRect);
		Paint.FontMetrics metrics = buttonPaint.getFontMetrics();
		int textWidth = (int) buttonPaint.measureText(text);

		canvas.drawText(text, rectX + (rectW/2) - (textWidth/2), rectY + (rectH - buttonPaddingY) - metrics.bottom, buttonPaint);
	}
	
	public void onScreenPressed(int mX, int mY) {
		for (int buttonIndex = 0; buttonIndex < currentButtons.size(); buttonIndex++) {
			Rectangle button = currentButtons.get(buttonIndex);
			
			if (game.touchOverRect(button, mX, mY)) {
				switch(buttonIndex) {
					//Play/Continue Button
					case 0:{
							if (game.state == States.Start) {
								game.setState(States.Playing);
							}else if (game.state == States.Death) {
								game.setState(States.Start);
							}
						}
						break;
					case 1:{
							if (game.state == States.Start) {
								//About
							} else if (game.state == States.Death) {
								Bitmap.Config config = Bitmap.Config.ARGB_8888;
								//TODO:  Hide temporarily buttons while taking the screen shoot. (For some other day...)
								Bitmap screenShoot = Bitmap.createBitmap(Game.displayMetrics.widthPixels,Game.displayMetrics.heightPixels,config);

								Canvas shootCanvas = new Canvas(screenShoot);
								game.renderOnCanvas(shootCanvas);

								game.sendScreenShot(screenShoot);
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