package characters;

import gameMain.Game;
import items.*;

public class Nino extends AllyPlayer{

	public Nino(int xPos, int yPos, Game game) {
		super("Nino", "Mage", game, xPos, yPos);
		this.image = Game.IM.getImage("/characterPics/Nino.png");
		this.wallet.addItem(new Vulnery());
	}

}
