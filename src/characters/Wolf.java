package characters;

import gameMain.Game;

public class Wolf extends AllyPlayer{

	public Wolf(int xPos, int yPos, Game game) {
		super("Wolf", "Archer", game, xPos, yPos);
		this.image = Game.IM.getImage("/characterPics/archer.png");
	}
}
