package gameMain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;

import chapterDesign.ChapterDesignMenu;
import gameMain.Game.STATE;

public class ChapterChooser extends ChapterDesignMenu{

	public Game game;
	public int maxBoxes, screenNum;
	
	public ChapterChooser(Game game) {
		super(game);
		this.game = game;
		this.maxBoxes = 8;
		this.screenNum = 0;
		this.title = "Chapter Chooser";
	}
	@Override
	public void chooseChapter() {
		game.setGameState(STATE.LoadScreen);
		game.setChapterOrganizer(new ChapterOrganizer(game, ((6*screenNum) + boxIndex+1), 3));
		System.out.println("Automatically loaded new data to save state 3");
		//game.setGameState(STATE.Game);
	}
	
	
}
