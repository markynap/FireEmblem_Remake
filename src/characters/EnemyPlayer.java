package characters;

import java.awt.Color;

import gameMain.Game;
import items.*;

public class EnemyPlayer extends Player {
	
	public final static int[] growths = {60, 40, 30, 35, 20, 25, 25};
	
	private int chaptNum;
	/** The maximum range at which each enemy can detect ally players */
	public int sightRange = 15;
	
	public EnemyPlayer(String name, String Class, int[] stats, int xPos, int yPos, Game game, CombatItem equiptItem, int chaptNum, boolean isBoss) {
		super(name, Class, "Enemy", stats, growths, game, xPos, yPos, equiptItem, isBoss);
		teamColor = Color.RED;
		this.chaptNum = chaptNum;
		buffStats();
		this.currentHP = this.HP;
		repOk();
	}
	
	public EnemyPlayer(String name, String Class, int[] stats, int[] growths, int xPos, int yPos, Game game, CombatItem equiptItem, int chaptNum, boolean isBoss) {
		super(name, Class, "Enemy", stats, growths, game, xPos, yPos, equiptItem, isBoss);
		teamColor = Color.RED;
		this.chaptNum = chaptNum;
		buffStats();
		this.currentHP = this.HP;
		repOk();
	}
	
	private void buffStats() {
		
		for (int i = 0; i < chaptNum; i++) {
			levelUp();
		}
		
	}

}
