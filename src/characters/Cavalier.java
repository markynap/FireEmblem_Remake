package characters;

import gameMain.Game;
import items.BronzeSword;
import items.CombatItem;

public class Cavalier extends EnemyPlayer{

	public Cavalier(int xPos, int yPos, Game game, int chaptNum) {
		super("Cavalier", "Cavalier", game.getBaseStatsForEnemy("Cavalier"), xPos, yPos, game, new BronzeSword(), chaptNum, false);
		image = Game.IM.getImage("/characterPics/enemycavalier.png");
		this.sightRange = 20;
	}

}
