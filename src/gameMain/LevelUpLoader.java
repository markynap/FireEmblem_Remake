package gameMain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import characters.Player;
import gameMain.Game.STATE;

public class LevelUpLoader {

	public Game game;
	/** Player who is gaining exp or leveling up */
	private Player player;
	/** Amount of EXP the player gained from the last fight */
	public int expGained;
	/** another helper count variable */
	private int expTracker;
	/** Stats for coordinate of the experience box */
	private int[] box = {Game.WIDTH/2 - 200, Game.HEIGHT/2 - 80, 400, 160};
	/** Stats for coordinate of the level up box */
	private int[] Lbox = {Game.WIDTH/2 - 200, Game.HEIGHT/2 - 200, 400, 400};
	/** The player's previous experience */
	private int prevEXP;
	/** If the EXP goes over 100 */
	private boolean goesOver;
	/** If the player gains 100 EXP */
	private boolean fullLevelUp;
	/** Keepss a well-paced experience bar */
	private int ticker;
	
	public boolean isEnemyPhase;
	
	/** Responsible for displaying all level ups as well as the
	 *  increase in EXP bar after each battle 
	 * @param game
	 */
	public LevelUpLoader(Game game) {
		this.game = game;
	}
	
	public void tick() {
		
		if (game.gameState == STATE.LevelUp) {
			
			//remember to reset after the end of the level up
			
		} else { //STATE.GainEXP

			if (goesOver) {
				
				if (prevEXP < 100) {
					ticker++;
					if (ticker % 3 == 0) prevEXP++;
				}
				else {
					prevEXP = 0;
					goesOver = false;
				}
					
			} else {
					
				if (prevEXP < player.EXP) {
					ticker++;
					if (ticker % 4 == 0) prevEXP++;
				}
				else {
						
					if (fullLevelUp) {
						
						game.SFX.playSong(1);
						game.setGameState(STATE.LevelUp);
							
					} else {
						// no level up occurred
						// hold frame for a second so they can see frame
						if (isEnemyPhase) {
							reset();
							game.setGameState(STATE.EnemyPhase);
							return;
						}

						expTracker++;
						if (expTracker >= 70) {
							reset();
							game.setGameState(STATE.Game);
						}
						
					}
				}
			}			
		}
	}

	public void render(Graphics g) {
		
		if (game.gameState == STATE.LevelUp) {
			
			g.setFont(new Font("Times New Roman", Font.BOLD, 22));
			g.setColor(Color.DARK_GRAY);
			g.fillRect(Lbox[0], Lbox[1], Lbox[2], Lbox[3]);
			
			g.setColor(Color.white);
			for (int i = 0; i < 5; i++) {
				g.drawRect(Lbox[0] - 5 + i, Lbox[1] - 5 + i, Lbox[2] + 10 - 2*i, Lbox[3] + 10 - 2*i);
			}
			g.drawString(player.name, Lbox[0] + 10, Lbox[1] + 50);
			for (int i = 0; i < 7; i++) {
				if (player.equiptItem.getDamageName().equalsIgnoreCase("DMG")) {
					if (player.levelUps[i]) g.drawString(Player.StatNames[i] + ":  " + (player.stats[i]-1) + " +1", Lbox[0] + 10, Lbox[1] + 80 + (25 * i));
					else g.drawString(Player.StatNames[i] + ":  " + player.stats[i], Lbox[0] + 10, Lbox[1] + 80 + (25 * i));
				} else {
					if (player.levelUps[i]) g.drawString(Player.MagStatNames[i] + ":  " + (player.stats[i]-1) + " +1", Lbox[0] + 10, Lbox[1] + 80 + (25 * i));
					else g.drawString(Player.MagStatNames[i] + ":  " + player.stats[i], Lbox[0] + 10, Lbox[1] + 80 + (25 * i));
				}
			}
			g.drawString("Level: " + (player.level-1) + " +1", Lbox[0] + 10, Lbox[1] + 80 + (25 * 8));
			g.drawImage(player.image, Lbox[0] + Lbox[2]/2 - 20, Lbox[1] + 15, Lbox[2]/2, Lbox[3] - 30, null);
			
			
		} else { // STATE.GainEXP
		
			
			g.setColor(Color.DARK_GRAY);
			g.fillRect(box[0], box[1], box[2], box[3]);
			
			g.setColor(Color.white);
			for (int i = 0; i < 5; i++) {
				g.drawRect(box[0] - 5 + i, box[1] - 5 + i, box[2] + 10 - 2*i, box[3] + 10 - 2*i);
			}
			g.drawRect(box[0] + box[2]/12, box[1] + box[3]/2, 5*box[2]/6, box[3]/4);
			
			g.setColor(Color.green);
			g.setFont(new Font("Times New Roman", Font.ITALIC, 35));
			
			g.drawString("EXP", box[0] + box[2]/2 - 25, box[1] + 30);
			
			g.fillRect(box[0] + box[2]/12, box[1] + box[3]/2, (int)((prevEXP/100.0)*(5*box[2]/6)), box[3]/4);
			
			
		}
		
	}
	
	public void setPlayer(Player player, int prevEXP, boolean isEnemyPhase) {
		reset();
		this.isEnemyPhase = isEnemyPhase;
		this.player = player;
		this.prevEXP = prevEXP;
		if (player.EXP <= prevEXP) {
			fullLevelUp = true;
			goesOver = true;
		}
	}
	
	public void reset() {
		game.setPlayerForEXP(null, 0);
		goesOver = false;
		prevEXP = 0;
		player = null;
		fullLevelUp = false;
		ticker = 0;
		expTracker = 0;
		isEnemyPhase = false;
	}
}
