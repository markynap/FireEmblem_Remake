package tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import characters.Player;
import extras.ImageManager;
import gameMain.ChapterMap;
import gameMain.Game;

public abstract class Tile {
	/** Where the tile is in terms of the current grid of tiles */
	public int xPos, yPos;
	/** The permanent position of this tile when the grid is created (top left 0, 0)*/
	public int x,y;
	/**The size of each tile*/
	public int scale = Game.scale;
	/** Ground, Mud, Water, Grass, */
	public String category;
	/** The imageManager for drawing tiles to the screen */
	public ImageManager IM = Game.IM;
	/**The player who is standing upon this tile */
	public Player carrier;
	/**Whether or not this tile is being viewed by the player*/
	public boolean isVisible;
	/**Instance of the map holding these tiles*/
	public ChapterMap map;
	/**indicates whether this tile can be pathed to or not */
	public boolean pathable;
	/**Indicats whether or not this tile is part of a path*/
	public boolean arrow, arrowHead;
	/** a Tile isCrossable if it is not a wall or water*/
	public boolean isCrossable;
	/** a Tile isGround if it has no specific terrain effects and isCrossable*/
	public boolean isGround;
	/**needed for path finding algorithm*/
	public int f, g, h;
	public ArrayList<Tile> neighbors;
	public Tile previous;
	/** DEF, AVOID */
	public int[] terrainBonuses;
	
	public boolean enemySelected;
	
	/** A large image containing all of our sprites */
	public static Image spriteMap = Game.IM.getImage("/tilePics/spriteMap.png");
	/** Grassy Terrain Tiles */
	public static Image grassyTerrainMap = Game.IM.getImage("/tilePics/grassyTerrainMap.png");
	/** For pillars and indoor castle stones */
	public static Image pillarStonesMap = Game.IM.getImage("/tilePics/pillarStonesMap.png");
	/** For water, armory, vendors */
	public static Image waterTerrainMap = Game.IM.getImage("/tilePics/waterTerrainMap.png");
	/** Has the thrown */
	public static Image throneMap = Game.IM.getImage("/tilePics/throneMap.png");
	/** has armory and stone tiles */
	public static Image armoryMap = Game.IM.getImage("/tilePics/armoryStoneMap.png");
	/** The sprite map whose image we are using to find this tile */
	public Image spriteMapImage;

	/** The coordinates to the sprite Map this object is drawn from */
	public int[] spriteMapCoordinates;
	/** grassyTerrainMap, pillarStonesMap, waterTerrainMap*/
	protected int[] lengthWidthsOfSpriteMaps;
	
	public Tile(int x, int y, ChapterMap map) {
		this.x = x;
		this.y = y;
		this.xPos = x;
		this.yPos = y;
		this.map = map;
		neighbors = new ArrayList<>();
		previous = null;
		terrainBonuses = new int[2];
		spriteMapCoordinates = new int[2];
		lengthWidthsOfSpriteMaps = new int[2];
		lengthWidthsOfSpriteMaps[0] = 18;
		lengthWidthsOfSpriteMaps[1] = 18;
	}
	/** Sets the sprite */
	public abstract void setSprite(int spriteIndex);
	/** Current Sprite we are viewing */
	public abstract int getSpriteIndex();
	/** Returns the number of different sprites this tile has */
	public abstract int numSprites();
	/** Advances the tile to the next Sprite state, if one exists */
	public void nextSprite() {
		if (getSpriteIndex()+1 >= numSprites()) {
			setSprite(0);
		} else {
			setSprite(getSpriteIndex()+1);
		}
	}
	
	public void setxPos(int xPos) {
		this.xPos = xPos;
		if (carrier != null) carrier.currentTile = this;
	}
	public void setyPos(int yPos) {
		this.yPos = yPos;
		if (carrier != null) carrier.currentTile = this;
	}
	public void setCarrier(Player p) {
		carrier = p;
		if (p != null) p.currentTile = this;
	}
	public boolean isOccupied() {
		return carrier != null;
	}
	public void render(Graphics g) {
		if (xPos < 0 || xPos > Game.nRow) return;
		if (yPos < 0 || yPos > Game.nCol) return;
		if (arrowHead) drawArrowHead(g);
		else if (arrow) drawArrowTile(g);
		else {
			//not an arrow or arrow head, either pathable or not pathable
			if (pathable) {
				// part of some displayed path
				drawTile(g);
				if (carrier == null) drawPathableTile(g);
				else carrier.render(g);
			
			} else { 
				// not part of any path
				drawTile(g);
				if (carrier != null) carrier.render(g);
			
			}
		}
	}
	
	public void drawTile(Graphics g) {
		g.drawImage(this.spriteMapImage, this.xPos * scale, this.yPos * scale, (this.xPos+1)*scale -1, (this.yPos+1)*scale -1,
				spriteMapCoordinates[0], spriteMapCoordinates[1], 
				spriteMapCoordinates[0] + lengthWidthsOfSpriteMaps[0], spriteMapCoordinates[1] + lengthWidthsOfSpriteMaps[1], null);
	}
	
	public void setPathable(boolean tf) {
		pathable = tf;
	}
	public void setArrow(boolean tf) {
		this.arrow = tf;
	}
	public void setArrowHead(boolean tf) {
		this.arrowHead = tf;
	}
	public void findNeighbors() {
		neighbors.clear();
		if (x < map.cols-1) {
			neighbors.add(map.getTileAtAbsolutePos(x + 1, y));
		}
		if (x > 0) {
			neighbors.add(map.getTileAtAbsolutePos(x - 1, y));
		}
		if (y < map.rows-1) {
			neighbors.add(map.getTileAtAbsolutePos(x, y + 1));
		}
		if (y > 0) {
			neighbors.add(map.getTileAtAbsolutePos(x, y - 1));
		}
	}
	public String toSring() {
		return x + "," + y;
	}
	
	public String getCarrierID() {
		if (carrier == null) return "0";
		else return String.valueOf(carrier.getID());
	}
	/** If the tiles are in the exact same place (x and y) on the grid */
	public boolean placeEquals(Tile otherTile) {
		if (otherTile == null) return false;
		return (x == otherTile.x && y == otherTile.y);
	}
	/** The amount of movement it takes to traverse this tile */
	public int movementTax() {
		return 1;
	}
	
	private void drawArrowTile(Graphics g) {
		drawPathableTile(g);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(xPos * scale + (int)(scale/7.0), yPos * scale + (int)(scale/7.0), 
				(int)(5*scale/7.0) + 1, (int)(5*scale/7.0) + 1);
		if (carrier != null) carrier.render(g);
	}
	
	private void drawArrowHead(Graphics g) {
		drawArrowTile(g);
		g.setColor(Color.RED);
		g.drawLine(xPos*scale + (int)(scale/3.0), yPos*scale + scale/2, xPos*scale + scale/2 , yPos*scale + (int)(scale/3.0));
		g.drawLine(xPos*scale + scale/2, yPos*scale + (int)(scale/3.0), xPos*scale + (int)(3*scale/4.0) -4, yPos*scale + scale/2);
		g.drawLine(xPos * scale + (int)(scale/3.0), yPos*scale + scale/2,xPos*scale + scale/2 , yPos*scale + (int)(3*scale/4.0) - 4);
		g.drawLine(xPos*scale + scale/2 , yPos*scale + (int)(3*scale/4.0) - 4, xPos*scale + (int)(3*scale/4.0) -4, yPos*scale + scale/2);
	}

	private void drawPathableTile(Graphics g) {
		g.setColor(Color.blue);
		g.fillRect(xPos * scale, yPos * scale, scale-1, (int)(scale/7.0));
		g.fillRect(xPos * scale, yPos * scale + (int)(6*scale/7.0), scale-1, (int)(scale/7.0));
		g.fillRect(xPos * scale, yPos * scale, (int)(scale/7.0), scale-1);
		g.fillRect(xPos * scale + (int)(6*scale/7.0), yPos * scale, (int)(scale/7.0), scale-1);
	}
}
