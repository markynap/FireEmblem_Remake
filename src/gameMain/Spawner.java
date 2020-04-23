package gameMain;

import java.util.Random;

import characters.*;
import tiles.Tile;

/** Spawns Ally or Enemy units in the game depending on the current turn */
public class Spawner {
	
	private Game game;
	
	private ChapterMap map;
	
	private Random random;
	
	
	public Spawner(Game game, ChapterMap map) {
		this.game = game;
		this.map = map;
		random = new Random();
	}
	
	public Spawner(Game game) {
		this.game = game;
		this.map = game.chapterOrganizer.currentMap;
	}
	/** Spawn the appropriate set of units for our current Chapter and Turn */
	public void spawnUnits() {
		
		int chapt = game.chapterOrganizer.currentChapter;
		
		if (chapt == 2) {
			
			switch (map.turnCount) {
			
			case 2: spawnUnit(new Mage(0, 0, game, chapt));
					spawnUnit(new Cavalier(0, 0, game, chapt));
					break;
			
			case 3: spawnUnit(new Archer(0, 0, game, chapt));
					spawnUnit(new Cavalier(0, 0, game, chapt));
					break;
			
			case 4: spawnUnit(new Nino(1, 1, game));
					spawnUnit(new Bard(1, 2, game));
					spawnUnit(new Cavalier(14, 14, game, chapt));
					break;
			
			}
			
		}
		
	}
	
	private void spawnUnit(Player unit) {
		if (unit.teamID.equalsIgnoreCase("Ally")) {
			if (!unit.currentTile.isOccupied()) {
				game.addPlayer(unit);
			}
			else {
				Tile copy;
				for (int i = 0; i < 25; i++) {
					copy = map.getTileAtAbsolutePos(random.nextInt(map.cols), random.nextInt(map.rows));
					if (!copy.isOccupied()) {
						unit.setCurrentTile(copy);
						unit.xPos = copy.x;
						unit.yPos = copy.y;
						game.addPlayer(unit);
						return;
					}
				}
			}
		} else {
			
			Tile fort = game.chapterOrganizer.currentMap.getUnoccupiedFort();
			if (fort != null) {
				unit.xPos = fort.x;
				unit.yPos = fort.y;
				unit.setCurrentTile(fort);
				game.addPlayer(unit);
			}
			
		}
	}
	

}
