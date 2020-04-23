package characters;

import gameMain.Game;

public class Bard extends AllyPlayer{

	public Bard(int xPos, int yPos, Game game) {
		super("Bard", "Dancer", game, xPos, yPos);
		this.image = Game.IM.getImage("/characterPics/bard.png");
	}
}
