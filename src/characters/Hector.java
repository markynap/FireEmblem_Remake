package characters;

import gameMain.Game;
import items.*;

public class Hector extends AllyPlayer{
	
	public Hector(int xPos, int yPos, Game game) {
		super("Hector", "AxeLord", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/hector.png");
	}

}
