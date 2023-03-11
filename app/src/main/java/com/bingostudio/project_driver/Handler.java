package com.bingostudio.project_driver;

import java.util.LinkedList;
import java.util.Random;

public class Handler {
	
	private Random r = new Random();
	public LinkedList<GameObject> gameObj = new LinkedList<>();

	private Game game;
	
	private int frameCounter = 0;
	private int carTimeout = 160; // amount of frames between new car spawning
	private int bushTimeout = 320;
	
	private static final int MAX_VEHICLES = 3;
	
	public Handler(Game game) {
		this.game = game;
	}
	
	public void tick() {
		frameCounter++;
		
		if (frameCounter % carTimeout == 0)
			createOncomingTraffic();

		if (frameCounter >= bushTimeout) {
			frameCounter = 0;
			//createBushes();
		}
		
		for (int i = 0; i < gameObj.size(); i++) {
			gameObj.get(i).tick();
		}
	}
	
	/*private void createBushes() {
		int bushCount = r.nextInt(2);
		
		for (int i = 0; i <= bushCount; i++) {
			int bushX;
			
			if (i % 2 == 0)
				bushX = 15;
			else
				bushX = (Game.WIDTH - (15 + 34));	
			
			addObj(new Bush(game, bushX, -60, 34, 60, ID.Bush));	
		}
	}*/

	public void addObj(GameObject obj) {
		gameObj.add(obj);
	}
	
	public void removeObj(GameObject obj) {
		gameObj.remove(obj);
	}
	
	public void createOncomingTraffic() {
		int roadSize = (Game.displayMetrics.widthPixels - 200);
		
		int carCount = 0;
		
		for (int i = 0; i < MAX_VEHICLES; i++) {
			boolean isCar = r.nextBoolean();
			if (isCar && carCount < (MAX_VEHICLES-1)) {
				carCount++;
				int columnSize = (roadSize/MAX_VEHICLES);
				int carWDiff = (columnSize - Car.CAR_SIZE.w);
				
				int carXPos = 100 + (columnSize * i) + (carWDiff/2);
				
				Car car = new Car(game, carXPos, -Car.CAR_SIZE.h);
				addObj(car);
			}
		}
	}

	public void clearCars() {
		for (int i = 0; i < gameObj.size(); i++) {
			GameObject tempObj = gameObj.get(i);
			
			if (tempObj.id == ID.Car) {
				gameObj.remove(tempObj);
				i--;
			}
		}
	}
	
}
