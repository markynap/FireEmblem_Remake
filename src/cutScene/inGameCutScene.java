package cutScene;

import java.awt.Graphics;
import java.io.File;
import java.util.ArrayList;

import characters.Player;
import gameMain.Game;

/** Represents a CutScene performed in-game, using the game background */
public class inGameCutScene extends CutScene{
	
	/** The list of players speaking in this in-game cutscene, in order */
	public ArrayList<Player> playerSpeaking;
	
	
	public inGameCutScene(Game game, String fileName) {
		super(game, fileName);
	}


	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void render(Graphics g) {
		// TODO Auto-generated method stub
		
	}

}
