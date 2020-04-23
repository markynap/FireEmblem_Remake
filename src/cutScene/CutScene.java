package cutScene;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

import characters.Player;
import gameMain.Game;
/** A standard CutScene has all of these elements */
public abstract class CutScene {

	public Game game;
	
	/** Which Chapter we are currently on */
	public int chapterNumber;
	/** File we will use to read the cutscene */
	public File readFile;
	/** Contains a list of the lists of words spoken in order*/
	public ArrayList<String> dialogue;
	/** A mapping between specific dialogue and the players who speak it */
	public TreeMap<String, Image> dialoguePlayerMap;
	/** Image behind our speaking Players */
	public Image bgImage;
	/** a list of the players in order of dialogue that are receiving the words spoken */
	public ArrayList<Image> playersSpokenTo;
	/** Names of the people speaking the dialogue */
	public ArrayList<String> dialogueNames;
	
	/** Creates a new standard CutScene filling in important information */
	public CutScene(Game game, String fileName) {
		this.game = game;
		readFile = new File(fileName);
		dialogue = new ArrayList<>();
		dialoguePlayerMap = new TreeMap<>();
		playersSpokenTo = new ArrayList<>();
		dialogueNames = new ArrayList<>();
		setDialogue();
	}
	
	public void setDialogue() {
		try {
			Scanner reader = new Scanner(readFile);
			bgImage = Game.IM.getImage(reader.nextLine());
			String line = "";
			String[] dialogueChop;
			String[] playerChop;
			while (reader.hasNextLine()) {
				line = reader.nextLine();
				dialogueChop = line.split(":");
				playerChop = dialogueChop[0].split("-");
				dialogue.add(dialogueChop[1]);
				dialoguePlayerMap.put(dialogueChop[1], Game.IM.getImage("/characterPics/" + playerChop[0] + ".png"));
				playersSpokenTo.add(Game.IM.getImage("/characterPics/" + playerChop[1] + ".png"));
				dialogueNames.add(playerChop[0]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	      
	public abstract void tick();
	
	public abstract void render(Graphics g);
}
