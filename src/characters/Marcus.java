package characters;

import gameMain.Game;

public class Marcus extends AllyPlayer{

	public Marcus(int xPos, int yPos, Game game) {
		super("Marcus", "Paladin", game, xPos, yPos);
		this.image = Game.IM.getImage("/characterPics/Marcus.png");
	}

}
