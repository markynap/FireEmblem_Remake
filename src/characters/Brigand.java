package characters;

import extras.IDAssigner;
import gameMain.Game;
import items.*;

public class Brigand extends EnemyPlayer{

	public int[] basicStats = {25, 1, 1, 1, 1, 1, 1, 5, 9, 2};
	
	public Brigand(String name, int[] stats, int xPos, int yPos, Game game, CombatItem equiptItem, int whichChapter) {
		super(name, "Brigand", stats, xPos, yPos, game, equiptItem, whichChapter, false);
		image = Game.IM.getImage("/characterPics/basicenemy.png");
	}
	
	public Brigand(int xPos, int yPos, Game game, int whichChapter) {
		super("Bandit", "Brigand", game.getBaseStatsForEnemy("Brigand"), xPos, yPos, game, new BronzeAxe(), whichChapter, false);
		image = Game.IM.getImage("/characterPics/basicenemy.png");
	}

}
