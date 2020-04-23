package tiles;

import java.awt.Graphics;

import gameMain.ChapterMap;

public class MountainTile extends Tile{

	public MountainTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Mountain";
		this.spriteMapImage = Tile.grassyTerrainMap;
		this.spriteMapCoordinates[0] = 8* 18 - 1;
		this.spriteMapCoordinates[1] = 4* 17 - 5;
		this.isGround = false;
		this.isCrossable = true;
		terrainBonuses[0] = 3;
		terrainBonuses[1] = 20;
	}
	
	@Override
	public int movementTax() {
		return 2;
	}

	@Override
	public void setSprite(int spriteIndex) {

		this.spriteMapCoordinates[0] = 8* 18 - 1;
		this.spriteMapCoordinates[1] = 4* 17 - 5;
		
	}

	@Override
	public int getSpriteIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numSprites() {
		// TODO Auto-generated method stub
		return 1;
	}

}
