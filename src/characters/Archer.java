package characters;

import gameMain.Game;
import items.BronzeBow;

public class Archer extends EnemyPlayer{

	public Archer(int xPos, int yPos, Game game, int chaptNum) {
		super("Archer", "Archer", game.getBaseStatsForEnemy("Archer"), xPos, yPos, game, new BronzeBow(), chaptNum, false);
		image = Game.IM.getImage("/characterPics/enemyarcher.png");
		this.sightRange = 18;
	}
	
}
