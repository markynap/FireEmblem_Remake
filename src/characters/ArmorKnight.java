package characters;

import gameMain.Game;
import items.BronzeAxe;

public class ArmorKnight extends EnemyPlayer{
	
	protected static int[] growths = {85, 55, 40, 15, 10, 40, 10};

	public ArmorKnight(int xPos, int yPos, Game game, int chaptNum) {
		super("ArmorKnight", "Armor Knight", game.getBaseStatsForEnemy("Armor Knight"), growths, xPos, yPos, game, new BronzeAxe(), chaptNum, false);
		image = Game.IM.getImage("/characterPics/armorknight.png");
		this.sightRange = 8;
	}

}
