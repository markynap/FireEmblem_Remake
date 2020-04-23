package chapterDesign;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import characters.*;
import gameMain.*;
import gameMain.Game;
import gameMain.Game.STATE;
import items.*;
import tiles.*;

public class ChapterDesignKeyInput extends KeyAdapter {
	
	public Game game;
	public char key;
	public int keyCode;
	public Tile currentTile;
	public ChapterDesigner designer;
	public ChapterMap currentMap;
	
	public ChapterDesignKeyInput(Game game, ChapterDesigner designer) {
		this.game = game;
		this.designer = designer;
	}
	
	public void keyPressed(KeyEvent e) {
		key = e.getKeyChar();
		keyCode = e.getExtendedKeyCode();
		if (designer.organizer != null) {
			currentMap = designer.organizer.currentMap;
			currentTile = currentMap.currentTile;
		}
		
		
		if (game.gameState == STATE.ChapterDesign) {
			if (keyCode == KeyEvent.VK_ESCAPE) {
				designer.flipSaveMode();
				designer.inCommandViewMode = false;
			}
			if (designer.inSaveMode) {
				if (key != 'a' && keyCode != KeyEvent.VK_ENTER) {
					designer.flipYesNoBox();
					return;
				} else {
					//this is where we save our chapter info
					if (designer.boxIndex == 0) {
						//save changes
						designer.editor.saveContents();
					} else {
						//do not save changes
						designer.flipSaveMode();
					}
					game.backToMenu();
				}
			}
			moveCurrentTile();
			if (keyCode == KeyEvent.VK_BACK_SPACE) {
				if (currentTile.isOccupied()) {
					designer.organizer.removePlayer(currentTile.carrier);
					currentTile.carrier = null;
				}
				currentMap.setTile(currentMap.currentTile, new GrassTile(currentTile.x, currentTile.y, currentMap), 0);
			} else if (keyCode == KeyEvent.VK_SPACE) {
				currentMap.setTile(currentMap.currentTile, new WallTile(currentTile.x, currentTile.y, currentMap), 0);
			}
			
			if (key == 'a') {
				currentMap.currentTile.nextSprite();
			}
			
			if (key == '`') {
				if (designer.inCommandViewMode) designer.inCommandViewMode = false;
				else designer.inCommandViewMode = true;
			}
			
			if (key == 't') {
				currentMap.setTile(currentTile, new TreeTile(currentTile.x, currentTile.y, currentMap), 0);
			} else if (key == 'i') {
				designer.organizer.addAlly(new Ike(currentTile.x, currentTile.y, game));
			} else if (key == 'h') {
				designer.organizer.addAlly(new Hector(currentTile.x, currentTile.y, game));
			} else if (key == 'r') {
				designer.organizer.addAlly(new Raymond(currentTile.x, currentTile.y, game));
			} else if (key == 'e') {
				designer.organizer.addEnemy(new Brigand(currentTile.x, currentTile.y, game, 1));
			} else if (key == 'l') {
				designer.organizer.addAlly(new Florina(currentTile.x, currentTile.y, game));
			}
				
			else if (key == 'm') {
				currentMap.setTile(currentMap.currentTile, new MountainTile(currentTile.x, currentTile.y, currentMap), 0);
			} else if (key == 'v') {
				currentMap.setTile(currentMap.currentTile, new Village(currentTile.x, currentTile.y, currentMap, new Vulnery()), 0);
			} else if (key == 'w') {
				currentMap.setTile(currentMap.currentTile, new WaterTile(currentTile.x, currentTile.y, currentMap), 0);
			} else if (key == 'n') {
				currentMap.setTile(currentMap.currentTile, new Throne(currentTile.x, currentTile.y, currentMap), 0);				
			} else if (key == 'f') {
				currentMap.setTile(currentMap.currentTile, new FloorTile(currentTile.x, currentTile.y, currentMap), 0);				
			} else if (key == 'p') {
				currentMap.setTile(currentMap.currentTile, new PillarTile(currentTile.x, currentTile.y, currentMap), 0);
			} else if (key == 'S') {
				currentMap.setTile(currentMap.currentTile, new StairsTile(currentTile.x, currentTile.y, currentMap), 0);
			} else if (key == 's') {
				currentMap.setTile(currentMap.currentTile, new StoneWallTile(currentTile.x, currentTile.y, currentMap), 0);
			} else if (key == 'F') {
				currentMap.setTile(currentMap.currentTile, new FortTile(currentTile.x, currentTile.y, currentMap), 0);
			}
			else if (key == 'd') {
				currentMap.setTile(currentMap.currentTile, new MudTile(currentTile.x, currentTile.y, currentMap), 0);
			} else if (key == 'D') {
				currentMap.setTile(currentMap.currentTile, new BridgeTile(currentTile.x, currentTile.y, currentMap), 0);
			} else if (key == 'o') {
				currentMap.setTile(currentMap.currentTile, new DoorTile(currentTile.x, currentTile.y, currentMap), 0);
			}
			
			else if (key == 'k') {
				designer.organizer.addAlly(new Kent(currentTile.x, currentTile.y, game));
			} else if (key == 'r') {
				designer.organizer.addAlly(new Raymond(currentTile.x, currentTile.y, game));
			} else if (key == 'N') {
				designer.organizer.addAlly(new Nino(currentTile.x, currentTile.y, game));
			} else if (key == 'M') {
				designer.organizer.addEnemy(new Mage(currentTile.x, currentTile.y, game, 1));

			} else if (key == 'C') {
				designer.organizer.addEnemy(new Cavalier(currentTile.x, currentTile.y, game, 1));

			} else if (key == 'A') {
				designer.organizer.addEnemy(new Archer(currentTile.x, currentTile.y, game, 1));

			} else if (key == 'K') {
				designer.organizer.addEnemy(new ArmorKnight(currentTile.x, currentTile.y, game, 1));

			} else if (key == 'B') {
				designer.organizer.addEnemy(new Boss(currentTile.x, currentTile.y, game, 1));
			} else if (key == 'c') {
				designer.organizer.addAlly(new Marcus(currentTile.x, currentTile.y, game));

			} else if (key == 'W') {
				designer.organizer.addAlly(new Wolf(currentTile.x, currentTile.y, game));
			} else if (key == 'b') {
				designer.organizer.addAlly(new Bard(currentTile.x, currentTile.y, game));

			}
			
			
		} else if (game.gameState == STATE.ChapterDesignMenu) {
			
			if (keyCode == KeyEvent.VK_RIGHT) {
				designer.menu.incBoxIndex();
			} else if (keyCode == KeyEvent.VK_LEFT) {
				designer.menu.decBoxIndex();
			} else if (keyCode == KeyEvent.VK_DOWN) {
				designer.menu.boxIndex = 6;
			} else if (keyCode == KeyEvent.VK_UP) {
				designer.menu.boxIndex = 7;
			}
			if (key == 'a') {
				if (designer.menu.boxIndex == 7) { //clicks on left arrow
					designer.menu.decScreenNum();
				} else if (designer.menu.boxIndex == 6) { //clicks on right arrow
					designer.menu.incScreenNum();
				} else {
					//clicks on any of the chapter boxes
					designer.menu.chooseChapter();
				}
			}
		}
		
	}

	/** responsible for moving the cursor around the map*/
	public void moveCurrentTile() {
		if (keyCode == KeyEvent.VK_RIGHT) {
			designer.organizer.currentMap.setCurrentTile(
					designer.organizer.currentMap.getTileAtAbsolutePos(currentTile.x + 1, currentTile.y));
		} else if (keyCode == KeyEvent.VK_LEFT) {
			designer.organizer.currentMap.setCurrentTile(
					designer.organizer.currentMap.getTileAtAbsolutePos(currentTile.x - 1, currentTile.y));
		} else if (keyCode == KeyEvent.VK_UP) {
			designer.organizer.currentMap.setCurrentTile(
					designer.organizer.currentMap.getTileAtAbsolutePos(currentTile.x, currentTile.y - 1));
		} else if (keyCode == KeyEvent.VK_DOWN) {
			designer.organizer.currentMap.setCurrentTile(
					designer.organizer.currentMap.getTileAtAbsolutePos(currentTile.x, currentTile.y + 1));
		}
	}
}
