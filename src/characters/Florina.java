package characters;

import gameMain.Game;

public class Florina extends AllyPlayer{

	public Florina(int xPos, int yPos, Game game) {
		super("Florina", "Pegasus", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Florina.png");
		this.isFlier = true;
	}

}
