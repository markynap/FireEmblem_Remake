package characters;

import gameMain.Game;
import items.CombatItem;

public class Boss extends EnemyPlayer{

	public Boss(int xPos, int yPos, Game game, int chaptNum) {
		super(game.getBossName(chaptNum),game.getBossClass(chaptNum), new int[10], xPos, yPos, game, game.getBossItem(chaptNum), chaptNum, true);
		String[] s = game.getBossStats(chaptNum);
		int[] stats = new int[10];
		for (int i = 0; i < s.length; i++) {
			if (i == 0) this.image = Game.IM.getImage(s[i]);
			if (i > 0) stats[i - 1] = Integer.valueOf(s[i]);
		}
		this.setEnemyStats(stats, growths);
		this.currentHP = HP;
		this.sightRange = 3;
	}

}
