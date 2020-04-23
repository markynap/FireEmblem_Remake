package cutScene;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;

import gameMain.Game;

/** A cutscene performed either before or after a chapter begins/ends */
public class outGameCutScene extends CutScene{
	/** Responsible for drawing players to the screen */
	private int playerOneX = Game.WIDTH/8, playerTwoX = 7*Game.WIDTH/10, playerY = Game.HEIGHT/3 + 20;
	/** Height and width of displayed Player's images */
	private int playerH = 300, playerW = 150, thickness = 5;
	/** Position of the TextBox */
	private int textBoxY = 4*Game.HEIGHT/5;
	/** The String to grow toward our final dialogue */
	private String completeString;
	/** Helps the text-flow come at a slower rate */
	private int timeKeepTracker;
	/** The current character position in the string we are writing */
	private int posInString;
	/** Which dialogue string we are processing */
	private int posInDialogue;
	/** True if there is more dialogue left to process */
	private boolean continueTyping;
	/** True if this is the beginning scene of a chapter */
	private boolean startOfScene;
	
	public outGameCutScene(Game game, String fileName, boolean startScene) {
		super(game, fileName);
		completeString = "" + dialogue.get(0).charAt(0);
		posInString = 1;
		continueTyping = true;
		this.startOfScene = startScene;
	}

	@Override
	public void tick() {
		if (continueTyping) {
			timeKeepTracker++;
			if (timeKeepTracker > 20) {
				timeKeepTracker = 0;
				completeString += dialogue.get(posInDialogue).charAt(posInString);
				posInString++;
				if (posInString >= dialogue.get(posInDialogue).length()) {
					continueTyping = false;
				}
			}
		}
	}
	
	public void fillLine() {
		if (completeString.equalsIgnoreCase(dialogue.get(posInDialogue))) {
			setNextLine();
			return;
		}
		completeString = dialogue.get(posInDialogue);
		posInString = completeString.length();
		continueTyping = false;
	}
	
	public void setPreviousLine() {
		posInString = 0;
		posInDialogue--;
		completeString = "";
		timeKeepTracker = 0;
		if (posInDialogue < 0) posInDialogue = 0;
		continueTyping = true;
	}
	
	public void setNextLine() {
		
		posInString = 0;
		posInDialogue++;
		completeString = "";
		if (posInDialogue >= dialogue.size()) {
			posInDialogue = 0;
			timeKeepTracker = 0;
			posInString = 0;
			continueTyping = false;
			game.destroyOutGameCutScene(startOfScene);
		} else continueTyping = true;
	}

	@Override
	public void render(Graphics g) {
	
		g.drawImage(bgImage, 0, 0, Game.WIDTH, Game.HEIGHT, null);
		g.drawImage(dialoguePlayerMap.get(dialogue.get(posInDialogue)), playerOneX, playerY, playerW, playerH, null);
		g.drawImage(playersSpokenTo.get(posInDialogue), playerTwoX, playerY, playerW, playerH, null);
		
		drawTextBox(g);
		g.setFont(new Font("Times New Roman", Font.BOLD, 32));
		g.setColor(Color.black);
		g.drawString(dialogueNames.get(posInDialogue) + ": " + completeString, 20, textBoxY + Game.HEIGHT/10);
		
	}

	private void drawTextBox(Graphics g) {
		g.setColor(Color.black);
		for (int i = 0; i < thickness; i++)
			g.drawRect(i, textBoxY + i, Game.WIDTH - 2*i - 5, Game.HEIGHT - textBoxY - 2*i);
		g.setColor(Color.white);
		g.fillRect(thickness, textBoxY + thickness, Game.WIDTH - 3*thickness, Game.HEIGHT - textBoxY - 9*thickness);
	}
	
}
