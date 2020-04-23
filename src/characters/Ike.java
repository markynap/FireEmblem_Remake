package characters;

import gameMain.Game;
import items.*;

public class Ike extends AllyPlayer{
	
	public Ike(int xPos, int yPos, Game game) {
		super("Ike", "Lord", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Ike.png");
	}
	
}
