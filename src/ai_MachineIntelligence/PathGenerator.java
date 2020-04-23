package ai_MachineIntelligence;

import java.util.ArrayList;

import characters.Player;
import gameMain.ChapterMap;
import gameMain.Game;
import tiles.Tile;

/**
 * Responsible for generating a path on a grid
 * @author mark
 *
 */
public class PathGenerator {

	public Game game;
	public Player player;
	public ArrayList<Tile> currentPath;
	public ChapterMap map;
	public Tile startTile;
	public int move;
	/**
	 * Creates a new PathGenerator that will highlight every tile in range as Pathable
	 * @param game
	 * @param startTile
	 * @param move
	 */
	public PathGenerator(Game game, Tile startTile, int move) {
		this.game = game;
		this.startTile = startTile;
		this.move = move;
		currentPath = new ArrayList<>();
		currentPath.add(startTile);
		map = game.chapterOrganizer.currentMap;
		player = startTile.carrier;
	}
	/**
	 * Sets every tile within (move) distance of the startTile to Pathable
	 */
	public void setAllPathableTiles(boolean isFlier) {
		
		
		ArrayList<Tile> pathables = new ArrayList<>();
		EnemyPathFinder finder = new EnemyPathFinder(game, game.chapterOrganizer);
		if (!isFlier) {
			for (int i = 0; i < map.tiles.size(); i++) {
				Tile tile = map.tiles.get(i);
				if (game.getTrueDist(startTile, tile) <= move) {
					if (!tile.isCrossable) continue;
					if (tile.carrier == null) {
						pathables.add(tile);				
					} else {
						if (player.teamID.equalsIgnoreCase(tile.carrier.teamID)) {
							pathables.add(tile);
						}
					}
				}
			}
			// loop through pathables and see if a path to it exists
			for (Tile tile : pathables) {
				finder.setStartEnd(startTile, tile);
				ArrayList<Tile> path = finder.findPath();
				if (path.size() <= 1) {
					// if the path has only one tile in it (probably start tile)
					if (game.getTrueDist(startTile, tile) <= 1) {
						tile.setPathable(true); //if the distance between is only one tile anyway
					}
					
				} else {
					
					// found more than one tile for the path
					if (path.size()-1 > player.MOV) {
						continue; //path greater than we can move to, not pathable
					}
					
					// in range and a path exists! It is pathable
					tile.setPathable(true);
				}
			}
			
		} else {
			
			//player is a flier! can traverse over walls
			for (int i = 0; i < map.tiles.size(); i++) {
				Tile tile = map.tiles.get(i);
				if (game.getTrueDist(startTile, tile) <= move) {
					tile.setPathable(true);
				}
			}
		}
	}
	
	public void resetTiles() {
		for (int i = 0; i < map.tiles.size(); i++) {
			Tile t = map.tiles.get(i);
			t.setPathable(false);
			t.setArrow(false);
			t.setArrowHead(false);
		}
		currentPath.clear();
	}
	
	public void setTileArrow(Tile t) {
		if (t == null) return;
		if (currentPath.size() > player.MOV) return;
		if (!t.pathable) return;
		if (currentPath.contains(t)) return;
		
		currentPath.add(t);
		// checks if our path is not longer than terrain allows
		int taxSum = 0;
		if (!player.isFlier) {
			for (Tile a : currentPath) taxSum += a.movementTax();
			if (taxSum-1 > player.MOV) {
				currentPath.remove(t);
				return;
			}
		}
		
		t.setArrow(true);
		game.chapterOrganizer.currentMap.selectedBoxTile = t;
		
		if (!currentPath.isEmpty()) {
			currentPath.get(currentPath.size()-1).arrowHead = true;
			if (currentPath.size() > 1) {
				for (int i = 0; i < currentPath.size() - 1; i++) {
					currentPath.get(i).arrowHead = false;
				}
			}
		}
	}
	
	public Tile getTopTile() {
		if (currentPath.size() == 0) return startTile;
		return currentPath.get(currentPath.size() - 1);
	}
	public void drawTilePath(Tile t) {
		if (Math.abs(t.x - startTile.x) + Math.abs(t.y - startTile.y) > move) return;
		currentPath.add(t);
		game.chapterOrganizer.currentMap.selectedBoxTile = t;
		if (t != startTile) {
			if (t.isOccupied()) {
				if (!player.teamID.equalsIgnoreCase(t.carrier.teamID)) {
					t.carrier.drawScope = true;
				}
			}
		}
	}
	public void eraseScopes() {
		for (int i = 0; i < game.chapterOrganizer.allys.size(); i++) {
			game.chapterOrganizer.allys.get(i).drawScope = false;
		}
		for (int i = 0; i < game.chapterOrganizer.enemys.size(); i++) {
			game.chapterOrganizer.enemys.get(i).drawScope = false;
		}
	}
	
}
