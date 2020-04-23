package characters;

import gameMain.Game;
import items.BronzeSword;
import items.FireTome;

public class Mage extends EnemyPlayer{
	
	protected static int[] growths = {85, 40, 45, 35, 40, 10, 40};

	public Mage(int xPos, int yPos, Game game, int chaptNum) {
		super("Mage", "Mage", game.getBaseStatsForEnemy("Mage"), growths, xPos, yPos, game, new FireTome(), chaptNum, false);
		image = Game.IM.getImage("/characterPics/enemymage.png");
		this.sightRange = 12;
	}
	
}
