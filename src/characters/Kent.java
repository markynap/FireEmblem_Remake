package characters;

import gameMain.Game;
import items.*;

public class Kent extends AllyPlayer{

	public Kent(int xPos, int yPos, Game game) {
		super("Kent", "Cavalier", game, xPos, yPos);
		this.image = Game.IM.getImage("/characterPics/Kent.png");
		this.wallet.addItem(new BronzeLance());
	}

}
