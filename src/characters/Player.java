package characters;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import gameMain.Game;
import gameMain.Game.STATE;
import items.CombatItem;
import items.Fists;
import items.Item;
import items.Wallet;
import tiles.Tile;
/**
 * A player in this game
 * @author mark
 *
 */
public class Player {
	/** how much each mastery field increases each use */
	public final int masteryIncrease = 5;
	/**List of stat names*/
	public static String[] StatNames = {"HP", "STR", "SK", "SP", "LCK", "DEF", "RES", "MOV", "CON", "Level"};
	/** List of Magic Stat Names */
	public static String[] MagStatNames = {"HP", "MAG", "SK", "SP", "LCK", "DEF", "RES", "MOV", "CON", "Level"};
	/**An instance of this game*/
	public Game game;
	/**The name of this player*/
	public String name;
	/**The class of this player*/
	public String Class;
	/** The tile this player is standing on */
	public Tile currentTile;
	/** This player's wallet to hold their items */
	public Wallet wallet;
	/** HP, STR, SK, SP, LUCK, DEF, RES, MOV, CON, LEVEL*/
	public int[] stats;
	/** HP, STR, SK, SP, LUCK, DEF, RES */
	public int[] growths;
	/**The stats broken up*/
	public int HP, STR, SK, SP, LCK, DEF, RES, MOV, CON, level;
	/**The picture representing this player*/
	public Image image;
	/**Current position of this player on the grid*/
	public int xPos, yPos;
	/**The current HP of this player*/
	public int currentHP;
	/**HP, STR, DEF, HIT, AVOID, CRIT*/
	public int[] classBonuses;
	/**Item this player has equipped, null if none in wallet*/
	public CombatItem equiptItem;
	/** This player's EXP, player levels up if it surpasses 100 */
	public int EXP;
	/** Factors that help with fighting */
	public int hit, avoid, crit;
	/** Swords, Lances, Axes, Bows  --> the letter grade*/
	public char[] weaponMasteriesGrade;
	/** Swords, Lances, Axes, Bows --> how many uses */
	public int[] weaponMasteries;
	/** The integer that tracks how long till next upgrade for each type*/
	public final int weaponUpgrade = 120;
	/** Responsible for moving, attacking, and using items*/
	public boolean canMove, canAttack, canUse;
	/**This is true when this player is being looked at for attack by another player*/
	public boolean drawScope = false;
	/**String that separates an Ally from Enemy from NPC */
	public String teamID;
	/**Damage this player can deal*/
	public int damage;
	/**How much damage a player mitigates*/
	public int defense;
	public Color teamColor;
	/** tracks the most recent level ups */
	public boolean[] levelUps;
	/** RNG */
	public Random r;
	/** Number of lines taken up in Player File per save */
	public final int NUM_LINES = 25;
	/** helps for ID sake */
	public boolean isBoss;
	/** True if this unit can fly over impassable terrain */
	public boolean isFlier;
	/** Indicates whether an ally unit is dead or not */
	public boolean dead;
	/** The previous tile this unit has stood on */
	public Tile previousTile;
	
	/** Constructor for making an ally player */
	public Player(String name, String Class, int xPos, int yPos, Game game) {
		this.game = game;
		this.name = name;
		this.Class = Class;
		this.xPos = xPos;
		this.yPos = yPos;
		setClassBonuses();
		this.teamID = "Ally";
		this.currentTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(xPos, yPos);
		this.previousTile = currentTile;
		wallet = new Wallet(this);
		//equiptItem = new Fists();
		//wallet.weapons.add(equiptItem);
		canMove = true;
		canAttack = true;
		canUse = true;
		levelUps = new boolean[7];
		weaponMasteriesGrade = new char[4];
		weaponMasteries = new int[4];
		r = new Random();
		setAllyStats();
		this.currentHP = HP;
		if (dead) {
			game.deadPlayers.add(this);
		}
		repOk();
	}
	/** Constructor for making an enemy player */
	public Player(String name, String Class, String teamID, int[] stats, int[] growths, Game game, int xPos, int yPos, CombatItem equiptItem, boolean isBoss) {
		this.game = game;
		this.name = name;
		this.isBoss = isBoss;
		this.Class = Class;
		this.xPos = xPos;
		this.yPos = yPos;
		setClassBonuses();
		this.teamID = teamID;
		this.currentTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(xPos, yPos);
		this.previousTile = currentTile;
		wallet = new Wallet(this);
		this.equiptItem = equiptItem;
		setMasteryBonuses();
		setEnemyStats(stats, growths);
		if (equiptItem != null) {
			wallet.weapons.add(equiptItem);
		} else {
			wallet.weapons.add(new Fists());
		}
		EXP = 0;
		currentHP = stats[0];
		repOk();
		canMove = true;
		canAttack = true;
		canUse = true;
		levelUps = new boolean[growths.length];
		r = new Random();
	}
	
	/** Sets Ally's states based on the vales obtained in their res//characters// file */
	private void setAllyStats() {
		stats = new int[10];
		growths = new int[7];
		String filename = "res//characters//" + name;
		try {
			Scanner reader = new Scanner(new File(filename));
			String[] line;
			int inc = 0;
			int currentLoadLevel = -1;
			while (reader.hasNextLine()) {
				reader.nextLine();
				inc++;
				if (inc % NUM_LINES == 1) currentLoadLevel++;
				
				if (game.getLoadLevel() == currentLoadLevel) { // if we are at the correct LOAD LEVEL
					
					this.dead = (Integer.parseInt(reader.nextLine()) == 0);
					
					for (int i = 0; i < 7; i++) {
						line = reader.nextLine().split(":"); //stats : growths
						stats[i] = Integer.valueOf(line[0]);
						growths[i] = Integer.valueOf(line[1]);
					}
					stats[7] = Integer.valueOf(reader.nextLine()); //MOV
					stats[8] = Integer.valueOf(reader.nextLine()); //CON
					stats[9] = Integer.valueOf(reader.nextLine()); //LEVEL
					EXP = Integer.valueOf(reader.nextLine());
					for (int i = 0; i < 4; i++) { // MOV CON LV EXP
						line = reader.nextLine().split(":");
						weaponMasteriesGrade[i] = line[0].charAt(0);
						weaponMasteries[i] = Integer.valueOf(line[1]);
					}
					//get items and durations
					for (int i = 0; i < 8; i++) {
						String newLine = reader.nextLine();
						if (newLine.isEmpty() || newLine.equalsIgnoreCase("none")) continue;
						line = newLine.split(":");
						Item item = Item.getItemByID(Integer.valueOf(line[0]));
						if (line.length > 1) item.duration = Integer.valueOf(line[1]);
						wallet.addItem(item);
						if (i == 0) { //first item specified is equipt Item
							if (item.getClass().asSubclass(CombatItem.class) != null) {
								this.equiptItem = (CombatItem)item;
							}
						}
					}
					
					setEnemyStats(stats, growths);
					reader.close();
					break;
				}
				
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/** Sets stats and growths according to inputs */
	protected void setEnemyStats(int[] stats, int[] growths) {
		this.stats = stats;
		this.HP = stats[0];
		this.STR = stats[1];
		this.SK = stats[2];
		this.SP = stats[3];
		this.LCK = stats[4];
		this.DEF = stats[5];
		this.RES = stats[6];
		this.MOV = stats[7];
		this.CON = stats[8];
		this.level = stats[9];
		this.growths = growths;
	}
	
	public void tick() {
		currentHP = Game.clamp(currentHP, 0, HP);
		equiptItem = wallet.getFirstWeapon();
		if (equiptItem == null) wallet.weapons.add(equiptItem = new Fists());
			
		hit = (4 * SK) + (LCK/2) + classBonuses[3] + equiptItem.hit + weaponTierBonus("hit");
		avoid = (3 * SP) + LCK + classBonuses[4] + currentTile.terrainBonuses[1] + (2*(CON - equiptItem.weight)) + weaponTierBonus("avoid");
		crit = SK/2 + LCK/3 + classBonuses[5] + equiptItem.crit + weaponTierBonus("crit");
		damage = STR + equiptItem.damage + classBonuses[1] + weaponTierBonus("damage");
		defense = DEF + classBonuses[2] + currentTile.terrainBonuses[0];
		if (isHealer() || isDancer()) damage = 0; crit = 0;
		if (currentHP <= 0) {
			die();
		}
		for (int i = 0; i < wallet.weapons.size(); i++) {
			if (wallet.weapons.get(i).duration <= 0) wallet.weapons.remove(i);
		}
		for (int i = 0; i < wallet.utilities.size(); i++) {
			if (wallet.utilities.get(i).duration <= 0) wallet.utilities.remove(i);
		}
	}
	/** Returns the ID of this Player, Bosses always have the same ID */
	public int getID() {
		if (isBoss) return game.playerIDMapINV.get("Boss");
		if (game.playerIDMapINV.get(name) == null) return 0;
		return game.playerIDMapINV.get(name);
	}

	
	/**Sets stat bonuses depending on Class */
	public void setClassBonuses() {
		classBonuses = new int[6];
	}
	
	/**Sets the mastery bonuses for each Class */
	public void setMasteryBonuses() {
		weaponMasteriesGrade = new char[4];
		weaponMasteries = new int[4];
		weaponMasteriesGrade[0] = 'F';
		weaponMasteriesGrade[1] = 'F';
		weaponMasteriesGrade[2] = 'F';
		weaponMasteriesGrade[3] = 'F';
		weaponMasteries[0] = 0;
		weaponMasteries[1] = 0;
		weaponMasteries[2] = 0;
		weaponMasteries[3] = 0;
		
		if (Class.equalsIgnoreCase("Lord")) {
			weaponMasteriesGrade[0] = 'C';
			weaponMasteries[0] = 20;
		} else if (Class.equalsIgnoreCase("AxeLord")) {
			weaponMasteriesGrade[2] = 'C';
			weaponMasteries[2] = 20;
		} else if (Class.equalsIgnoreCase("Brigand")) {
			weaponMasteriesGrade[2] = 'C';
			weaponMasteries[2] = 20;
		}
	}
	/** Assigns the tile that this player is standing on */
	public void setCurrentTile(Tile t) {
		if (t != null)
			currentTile = t;
		else
			throw new IllegalArgumentException("The tile assigned to player " + name + " is null!");
	}
	
	public void render(Graphics g) {
		if (canMove && canAttack) {
			if (teamID.equalsIgnoreCase("Ally")) {
				g.setColor(Color.cyan);
			} else {
				g.setColor(Color.red);
			}
			g.fillRect(currentTile.xPos * Game.scale, currentTile.yPos * Game.scale, Game.scale-1, Game.scale-1);
		} else if (canAttack) {
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(currentTile.xPos * Game.scale, currentTile.yPos * Game.scale, Game.scale-1, Game.scale-1);
		} else {
			g.setColor(Color.black);
			g.fillRect(currentTile.xPos * Game.scale, currentTile.yPos * Game.scale, Game.scale-1, Game.scale-1);
		}
		g.drawImage(image, currentTile.xPos * Game.scale, currentTile.yPos * Game.scale, Game.scale-1, Game.scale-1, null);
		
		if (drawScope) drawScopeImage(g);
		drawHealthBar(g);
		
	}
	/** True if this player has Health Points remaining */
	public boolean isAlive() {
		return currentHP > 0;
	}
	
	/** Adds an item to this Player's wallet */
	public void addItem(Item it) {
		if (it == null) throw new NullPointerException("Cannot add a null item to a Player");
		else wallet.addItem(it);
	}
	/** Removes an item from this Player's wallet */
	public void removeItem(Item it) {
		if (it == null) throw new NullPointerException("Cannot remove a null item from a Player");
		else wallet.removeItem(it);
	}
	
	public void repOk() {
		if (stats.length != 10) throw new IllegalArgumentException("Player must have 10 stats!");
		for (int a : stats) if (a < 0) throw new IllegalArgumentException("Player cannot have negative stats");
		if (growths.length != 7) throw new IllegalArgumentException("Player must have 7 growth stats!");
	}
	
	public Image getImage() {
		return image;
	}
	
	/**Populates canMove, canAttack, and canUse*/
	public void populateMAU() {
		canMove = true;
		canAttack = true;
		canUse = true;
	}
	
	public void setCanMove(boolean tf) {
		canMove = tf;
	}
	public void setCanAttack(boolean tf) {
		canAttack = tf;
	}
	public void setCanUse(boolean tf) {
		canUse = tf;
	}
	
	/** Draws a golden scope indicating this unit is being targetted */
	public void drawScopeImage(Graphics g) {
		g.setColor(new Color(255,215,0));
		int offSet = Game.scale/8;
		int thickness = 2;
		int width = Game.scale-(2*offSet);
		int xPosition = (currentTile.xPos * Game.scale) + offSet;
		int yPosition = (currentTile.yPos * Game.scale) + offSet;
		for (int i = 0; i < thickness; i++) {
			g.drawArc(xPosition, yPosition + i, width, width, 0, 360);
		}
		int x1 = xPosition + width/2;
		g.drawLine(x1, yPosition, x1, yPosition + thickness + width);
		g.drawLine(xPosition, yPosition + width/2, xPosition + width, yPosition + width/2);
	}
	
	/** Draws the health bar of this unit */
	public void drawHealthBar(Graphics g) {
		g.setColor(Color.black);
		int x = currentTile.xPos * Game.scale;
		int height = Game.scale/5;
		int y = (currentTile.yPos * Game.scale) + (Game.scale - height);
		
		g.drawRect(x, y, Game.scale, height);
		g.setColor(Color.white);
		g.fillRect(x+1, y+1, Game.scale-1, height-1);
		g.setColor(Color.green);
		g.fillRect(x+1, y+1,(int)((Game.scale-1) * ((double)currentHP/HP)), height);
	}
	
	/** This player is removed from this chapter, becoming unplayable, untargetable, and invisible */
	public void die() {
		currentHP = 0;
		HP = 0;
		if (teamID.equalsIgnoreCase("Ally")) {
			game.chapterOrganizer.allys.remove(this);
		} else if (teamID.equalsIgnoreCase("Enemy")) {
			game.chapterOrganizer.enemys.remove(this);
		} else {
			System.out.println("Did not account for other types of Player's dying");
		}
		currentTile.setCarrier(null);
	}
	
	/** Only works if this player is a healer */
	public void healPlayer(Player other) {
		if (Class.equalsIgnoreCase("Healer")) {
			other.currentHP += this.equiptItem.damage + STR;
			addEXP(15);
			setMAU(false);
			this.equiptItem.duration--;
			game.startEXPScene(false);
		}
	}
	
	/** Only works if this player is a dancer */
	public void danceForPlayer(Player other) {
		if (Class.equalsIgnoreCase("Dancer")) {
			other.setMAU(true);
			addEXP(10);
			setMAU(false);
		}
	}
	
	/** Inflicts damage on a player's health, applying negative damage will heal the player
	 *  however this player cannot be healed above his maximum HP threshold
	 * @param damage
	 */
	public void takeDamage(int damage) {
		currentHP -= damage;
		if (currentHP > HP) currentHP = HP;
	}
	
	/**sets canUse, canAttack, canMove all together */
	public void setMAU(boolean tf) {
		canUse = tf;
		canAttack = tf;
		canMove = tf;
	}
	
	/** Adds EXP to unit and if it is ally starts EXP Scene 
	 *  If the unit's EXP goes over 100, unit levels up and EXP flows back over to zero*/
	public void addEXP(int exp) {
		if (exp < 0) throw new RuntimeException("Cannot award a player negative experience!");
		int prevEXP = EXP;
		if (EXP + exp >= 100) {
			EXP = (EXP + exp) % 100;
			levelUp();
		} else {
			EXP += exp;
		}
		if (teamID.equalsIgnoreCase("Ally")) {
			game.setPlayerForEXP(this, prevEXP);
		}
	}
	/** This player will have a chance to increase each of his first 7 stats according
	 *  to their respective growth rates */
	public void levelUp() {
		
		for (int i = 0; i < levelUps.length; i++) {
			levelUps[i] = false;
		}
		int RNG = 0;
		for (int i = 0; i < growths.length; i++) {
			RNG = r.nextInt(101);
			if (RNG <= growths[i]) {
				stats[i]++;
				levelUps[i] = true;
			}
		}
		this.HP = stats[0];
		this.STR = stats[1];
		this.SK = stats[2];
		this.SP = stats[3];
		this.LCK = stats[4];
		this.DEF = stats[5];
		this.RES = stats[6];
		level++;
		stats[9]++;
	}
	/** Returns the color of the opposite team:
	 * 	if color = red, output = blue and vice versa
	 *  if neither red nor blue, returns orange
	 */
	public Color otherTeamColor(Color input) {
		if (input == Color.RED) {
			return Color.BLUE;
		} else if (input == Color.BLUE) {
			return Color.RED;
		} else return Color.orange;
	}
	/** can Move or Attack*/
	public boolean canMA() {
		if (canAttack || canMove) return true;
		else return false;
	}
	/** Returns the name of this unit */
	public String toString() {
		return name;
	}
	/** Increments the mastery of the weapon used, upgrading them if necessary */
	public void incWeaponGrade(Item weapon) {
		if (weapon == null) return;
		if (weapon.category.equalsIgnoreCase("Physical") || weapon.category.equalsIgnoreCase("Magical")) {
			CombatItem item = (CombatItem) weapon;
			switch (item.weaponType) {
			case "Sword": 
				weaponMasteries[0] += masteryIncrease;
				break;
			case "Lance": 
				weaponMasteries[1] += masteryIncrease;
				break;
			case "Axe": 
				weaponMasteries[2] += masteryIncrease;
				break;
			case "Bow": 
				weaponMasteries[3] += masteryIncrease;
				break;
			case "Fire": 
				weaponMasteries[0] += masteryIncrease;
				break;
			case "Ice": 
				weaponMasteries[1] += masteryIncrease;
				break;
			case "Earth": 
				weaponMasteries[2] += masteryIncrease;
				break;
			case "Dark": 
				weaponMasteries[3] += masteryIncrease;
				break;
			}
			checkMasteryGrades();
		}
	}
	private void checkMasteryGrades() {
		for (int i = 0; i < 4; i++) {
			if (weaponMasteries[i] >= weaponUpgrade) {
				weaponMasteriesGrade[i] = upGrade(weaponMasteriesGrade[i]);
				weaponMasteries[i] = 0;
			}
		}
	}
	private char upGrade(char prev) {
		if (prev == 'F') return 'D';
		else if (prev == 'D') return 'C';
		else if (prev == 'C') return 'B';
		else if (prev == 'B') return 'A';
		else if (prev == 'A') return 'S';
		else return 'S';
	}
	
	private int weaponTierBonus(String whichBonus) {
		
		if (equiptItem == null || equiptItem.getClass().equals(Fists.class)) return 0;
		
		if (equiptItem.weaponType.equalsIgnoreCase("Sword")) return getBonusForMastery(weaponMasteriesGrade[0], whichBonus);
		else if (equiptItem.weaponType.equalsIgnoreCase("Lance")) return getBonusForMastery(weaponMasteriesGrade[1], whichBonus);
		else if (equiptItem.weaponType.equalsIgnoreCase("Axe")) return getBonusForMastery(weaponMasteriesGrade[2], whichBonus);
		else if (equiptItem.weaponType.equalsIgnoreCase("Bow")) return getBonusForMastery(weaponMasteriesGrade[3], whichBonus);
		else if (equiptItem.weaponType.equalsIgnoreCase("Fire")) return getBonusForMastery(weaponMasteriesGrade[0], whichBonus);
		else if (equiptItem.weaponType.equalsIgnoreCase("Ice")) return getBonusForMastery(weaponMasteriesGrade[1], whichBonus);
		else if (equiptItem.weaponType.equalsIgnoreCase("Earth")) return getBonusForMastery(weaponMasteriesGrade[2], whichBonus);
		else if (equiptItem.weaponType.equalsIgnoreCase("Dark")) return getBonusForMastery(weaponMasteriesGrade[3], whichBonus);
		else return 0;
		
	}
	/** Gives the oppropriate bonuses for the given masteryIndex*/
	private int getBonusForMastery(char grade, String bonus) {
		
		if (grade == 'F') return 0;
		if (grade == 'D') {
			switch (bonus) {
			case "hit": return 5;
			case "avoid": return 0;
			case "crit": return 1;
			case "damage": return 0;
			}
		} else if (grade == 'C') {
			switch (bonus) {
			case "hit": return 5;
			case "avoid": return 5;
			case "crit": return 3;
			case "damage": return 1;
			}
		} else if (grade == 'B') {
			switch (bonus) {
			case "hit": return 10;
			case "avoid": return 5;
			case "crit": return 5;
			case "damage": return 2;
			}
		} else if (grade == 'A') {
			switch (bonus) {
			case "hit": return 15;
			case "avoid": return 10;
			case "crit": return 8;
			case "damage": return 2;
			}
		} else if (grade == 'S') {
			switch (bonus) {
			case "hit": return 25;
			case "avoid": return 20;
			case "crit": return 15;
			case "damage": return 5;
			}
		}
		System.out.println("Player - getBonusOnMastery() un-accounted for input: " + bonus + " at grade: " + grade);
		return 0;
		
	}
	/** Trades the specified item with the player */
	public void giveItem(Player other, Item item) {
		if (item.imagePath != null) if (item.imagePath.equalsIgnoreCase("/fists.png")) return;
		if (wallet.weapons.contains(item) || wallet.utilities.contains(item)) {
			other.wallet.addItem(item);
			wallet.removeItem(item);
		}
	}
	/** Determines whether the two players are on the same team */
	public boolean sameTeam(Player other) {
		return teamID.equalsIgnoreCase(other.teamID);
	}
	public boolean isHealer() {
		return Class.equalsIgnoreCase("Healer");
	}
	public boolean isDancer() {
		return Class.equalsIgnoreCase("Dancer");
	}
}
