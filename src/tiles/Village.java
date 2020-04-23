package tiles;

import characters.Player;
import gameMain.ChapterMap;
import items.Item;

public class Village extends Tile{

	public boolean visited;
	
	public Item gift;
	
	public Village(int x, int y, ChapterMap map, Item gift) {
		super(x, y, map);
		this.category = "Village";
		this.isGround = false;
		this.isCrossable = false;
		terrainBonuses[0] = 0;
		terrainBonuses[1] = 15;
		visited = false;
		this.gift = gift;
		this.spriteMapImage = Tile.waterTerrainMap;
		this.spriteMapCoordinates[0] = 351;
		this.spriteMapCoordinates[1] = 132;
		this.lengthWidthsOfSpriteMaps[1] = 20;
	}
	
	public void visit(Player player) {
		if (visited) return;
		visited = true;
		//we must set the village sprite to destroyed here!!!
		player.addItem(gift);
	}

	@Override
	public void setSprite(int spriteIndex) {
		
		switch (spriteIndex) {
		
		case 0: // the visit section
				this.spriteMapCoordinates[0] = 351;
				this.spriteMapCoordinates[1] = 132;
				this.isCrossable = true;
				break;
		case 1: //bottom left
				this.spriteMapCoordinates[0] = 335;
				this.spriteMapCoordinates[1] = 132;
				this.isCrossable = false;
				break;
		case 2: //top left
				this.spriteMapCoordinates[0] = 335;
				this.spriteMapCoordinates[1] = 112;
				this.isCrossable = false;
				break;
		case 3: //top middle
				this.spriteMapCoordinates[0] = 351;
				this.spriteMapCoordinates[1] = 112;
				this.isCrossable = false;
				break;
		case 4: //top right
				this.spriteMapCoordinates[0] = 367;
				this.spriteMapCoordinates[1] = 112;
				this.isCrossable = false;
				break;
		case 5: //bottom right
				this.spriteMapCoordinates[0] = 367;
				this.spriteMapCoordinates[1] = 132;
				this.isCrossable = false;
				break;
		}
		
	}

	@Override
	public int getSpriteIndex() {
		
		int x = spriteMapCoordinates[0];
		int y = spriteMapCoordinates[1];
		
		if (x == 351) {
			if (y == 132) return 0;
			else return 3;
		} else if (x == 335) {
			if (y == 132) return 1;
			else return 2;
		} else if (x == 367) {
			if (y == 112) return 4;
			else return 5;
		} else {
			System.out.println("getSpriteIndex() in Village has problems");
			return 0;
		}
		
	}

	@Override
	public int numSprites() {
		// TODO Auto-generated method stub
		return 6;
	}

}
