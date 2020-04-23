package gameMain;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import ai_MachineIntelligence.PathGenerator;
import chapterDesign.ChapterDesigner;
import characters.*;
import cutScene.CutSceneGenerator;
import extras.*;
import gameMain.Game.STATE;
import graphics.PlayerInfoGFX;
import graphics.PopUpMenu;
import graphics.TradeMenu;
import items.CombatItem;
import items.Fists;
import items.Item;
import tiles.Tile;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 12345L;
	//All of these do not pertain to the constructor
	//public final static int WIDTH = 1200;
	//public final static int HEIGHT = WIDTH/5 * 4 + 34;
	public final static int WIDTH = 870;
	public final static int HEIGHT = 4*WIDTH/5 + 40;
	
	public final int[] saveBox = {Game.WIDTH/4, Game.HEIGHT/5, 2*Game.WIDTH/3, Game.HEIGHT/4};
	/**True if yes to go back to menu, false if no */
	public boolean menuYes;
	public static ImageManager IM = new ImageManager();
	public SFXPlayer SFX;
	private Thread thread;
	private boolean running = false;
	public ThreadPool pool;
	public MusicPlayer MP;
	//public static int nRow = 12;
	/** Number of tiles in each row on the screen */
	public static int nRow = 16;
	/** Number of columns visible on the screen */
	public static int nCol = 13;
	public static int scale = WIDTH/nRow; //1200/15 = 80
	//public static int scale = WIDTH/(4*nRow/3); //1200/15 = 80
	//These are for the constructor
	public ChapterOrganizer chapterOrganizer;
	public PlayerInfoGFX playerGFX;
	public PopUpMenu PUM;
	public PathGenerator pathGenerator;
	public AttackManager AttackManager;
	public ChapterDesigner designer;
	public Menu menu;
	public ChapterChooser chapterChooser;
	public LoadScreen loadScreen;
	public TimeKeeper timekeep;
	/** Map of Player names to ID numbers */
	public TreeMap<Integer, String> playerIDMap;
	/** Inverse order of playerIDMap, for easy access */
	public TreeMap<String, Integer> playerIDMapINV;
	/** Responsible for loading the correct game and save-state information */
	public GameLoader gameLoader;
	
	public LevelUpLoader levelUpLoader;
	
	private int loadLevel;
		
	public Player playerForEXP;
	
	public int playerPreviousEXP;
	
	public TradeMenu tradeMenu;
	/** Tracks the dead players to prevent them from being recognized in game */
	public ArrayList<Player> deadPlayers;
	
	public CutSceneGenerator cutScenes;
	
	public enum STATE {
		Game,
		Menu,
		Info,
		AdvInfo,
		PopUpMenu,
		MoveState,
		AttackState,
		EnemyPhase,
		LoseGame,
		ChapterDesign,
		StartScreen,
		ChapterDesignMenu,
		AttackStage,
		ChapterChoose,
		LoadScreen,
		LoadGame,
		LevelUp,
		GainEXP,
		TradeState,
		EnemyChoice,
		weaponSelection,
		outGameCutScene,
		ReturnToMenu
	}
	/** Current State of the game */
	public STATE gameState = STATE.StartScreen;
	/** Creates a new Sacred Stones Game */
	public Game() {
		setPlayerIDMap();
		menu = new Menu(this);
		timekeep = new TimeKeeper();
		deadPlayers = new ArrayList<>();
		chapterOrganizer = new ChapterOrganizer(this, new ChapterMap(24, 30, this, "route"), 0);
		playerGFX = new PlayerInfoGFX(this);
		AttackManager = new AttackManager(this);
		gameLoader = new GameLoader(this);
		this.addKeyListener(new KeyInput(this));
		new Window(WIDTH, HEIGHT, "Sacred Stones", this);
		loadScreen = new LoadScreen();
		levelUpLoader = new LevelUpLoader(this);
		menuYes = true;
		cutScenes = new CutSceneGenerator(this);
		
	}
	
	/** Returns and Creates the player at the given position with the given ID 
	 * @param ID - player ID
	 * @param x - player xPosition
	 * @param y - player yPosition
	 * @param loadLevel - which save state we are loading from
	 * @return Player with matching ID, Raymond if otherwise
	 */
	public Player getPlayerByID(int ID, int x, int y, int loadLevel, int whichChapter) {	
		
		if (playerIDMap.get(ID) == null) return null;
		
		switch (playerIDMap.get(ID)) {
			case "Ike": return new Ike(x, y, this);
			case "Hector": return new Hector(x, y, this);
			case "Raymond": return new Raymond(x, y, this);
			case "Kent": return new Kent(x, y, this);
			case "Nino": return new Nino(x, y, this);
			case "Marcus": return new Marcus(x, y, this);
			case "Wolf": return new Wolf(x, y, this);
			case "Bard": return new Bard(x, y, this);
			case "Florina": return new Florina(x, y, this);
			case "Bandit": return new Brigand(x, y, this, whichChapter);
			case "Cavalier": return new Cavalier(x, y, this, whichChapter);
			case "Mage": return new Mage(x, y, this, whichChapter);
			case "Archer": return new Archer(x, y, this, whichChapter);
			case "ArmorKnight": return new ArmorKnight(x,y,this,whichChapter);
			case "Boss": return new Boss(x, y, this, whichChapter);

			default: return new Raymond(x, y, this);
		}
	}

	public void tick() {
		if (gameState == STATE.EnemyPhase) return;
		if (gameState == STATE.LoadGame) return;
		if (gameState == STATE.outGameCutScene) {
			cutScenes.tick();
			return;
		}
		if (gameState == STATE.ChapterDesign) {
			designer.organizer.tick();
		} else {
			chapterOrganizer.tick();
		}
	}
	public void render(Graphics g) {

		if (gameState == STATE.Game) {
			chapterOrganizer.render(g);
		} else if (gameState == STATE.Menu) {
			menu.render(g);
		} else if (gameState == STATE.Info) {
			chapterOrganizer.render(g);
			playerGFX.render(g);
		} else if (gameState == STATE.AdvInfo) {
			chapterOrganizer.render(g);
			playerGFX.renderAdv(g);
		} else if (gameState == STATE.PopUpMenu) {
			chapterOrganizer.render(g);
			if (PUM != null) PUM.render(g);
		} else if (gameState == STATE.MoveState) {
			chapterOrganizer.render(g);
		} else if (gameState == STATE.AttackState) {
			chapterOrganizer.render(g);
		} else if (gameState == STATE.EnemyPhase) {
			chapterOrganizer.render(g);
		} else if (gameState == STATE.LoseGame) {
			renderLoseGame(g);
		} else if (gameState == STATE.StartScreen) {
			renderStartScreen(g);
		} else if (gameState == STATE.ChapterDesignMenu) {
			if (designer != null) designer.menu.render(g);
		} else if (gameState == STATE.ChapterDesign) {
			if (designer != null) designer.render(g);
		} else if (gameState == STATE.AttackStage) {
			AttackManager.renderAttackAnimation(g);
		} else if (gameState == STATE.ChapterChoose) {
			if (chapterChooser != null) chapterChooser.render(g);
		} else if (gameState == STATE.LoadScreen) {
			loadScreen.render(g);
		} else if (gameState == STATE.ReturnToMenu) {
			chapterOrganizer.render(g);
			renderMenuPopUp(g);
		} else if (gameState == STATE.LoadGame) {
			gameLoader.render(g);
		} else if (gameState == STATE.GainEXP || gameState == STATE.LevelUp) {
			chapterOrganizer.render(g);
			levelUpLoader.render(g);
		} else if (gameState == STATE.TradeState) {
			chapterOrganizer.render(g);
			if (tradeMenu != null) tradeMenu.render(g);
		} else if (gameState == STATE.EnemyChoice) {
			chapterOrganizer.render(g);
		} else if (gameState == STATE.weaponSelection) {
			chapterOrganizer.render(g);
			playerGFX.renderWeaponSelection(g);
		} else if (gameState == STATE.outGameCutScene) {
			cutScenes.render(g);
		}
		
	}
	

	/** Returns the absolute value of x distance + y distance */
	public int getTrueDist(Tile a, Tile b) {
		return (Math.abs(a.x - b.x) + Math.abs(a.y - b.y));
	}
	
	public void setGameState(STATE state) {
		this.gameState = state;
	}
	
	public void run() {
		running = true;
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		// int frames = 0;
		while (running) {
//			long now = System.nanoTime();
//			delta += (now - lastTime) / ns;
//			lastTime = now;
//			while (delta >= 1) {
				tick();
//				delta--;
//			}
//			if (running) {
				renderGame();
//			}
			// frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				// System.out.println("FPS: " + frames);
				// frames = 0;
			}
		}
		stop();
	}
	public void renderGame() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		
		render(g);
		
		g.dispose();
		bs.show();
	}

	public synchronized void start() {
		 pool = new ThreadPool(3);
		 pool.runTask(this);
	//	thread = new Thread(this);
	//	thread.start();
		MP = new MusicPlayer("FireEmblemTheme", "FireEmblemHomeTune", "windsAcrossThePlane");
		pool.runTask(MP);
		MP.playSong(0);
		SFX = new SFXPlayer("Cursor", "LevelUp", "Select", "Experience", "GameOver");
		pool.runTask(SFX);
		running = true;
	}

	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static int clamp(int var, int min, int max) {
		if (var >= max) {
			return var = max;
		} else if (var <= min) {
			return var = min;
		} else {
			return var;
		}
	}
	public static void main(String[] args) {
		new Game();
	}
	public void setPopUpMenu(PopUpMenu PUM) {
		this.PUM = PUM;
	}
	public void setPathGenerator(PathGenerator PG) {
		this.pathGenerator = PG;
	}
	public void addAlly(AllyPlayer ally) {
		chapterOrganizer.addAlly(ally);
	}
	public void addEnemy(EnemyPlayer enemy) {
		chapterOrganizer.addEnemy(enemy);
	}
	public void addPlayer(Player player) {
		chapterOrganizer.addPlayer(player);
	}
	public void loseGame() {
		gameState = STATE.LoseGame;
	}
	/** When Chapter Designer is selected in menu */
	public void setChapterDesigner() {
		designer = new ChapterDesigner(this);
		gameState = STATE.ChapterDesignMenu;
	}
	/** When Choose Chapter is selected in menu */
	public void setChapterChooser() {
		chapterChooser = new ChapterChooser(this);
		gameState = STATE.ChapterChoose;
	}
	public void renderLoseGame(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		g.setColor(Color.red);
		g.setFont(new Font("Times New Roman", Font.BOLD, 55));
		g.drawString("YOU LOSE", Game.HEIGHT/3 + Game.HEIGHT/11, Game.WIDTH/3 + Game.WIDTH/7);
	}
	public void renderStartScreen(Graphics g) {
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		
		g.setColor(Color.RED);
		g.setFont(new Font("Times New Roman", Font.ITALIC, 80));
		g.drawString("Fire Emblem", Game.WIDTH/2 - 220, Game.HEIGHT/2-40);
		g.setFont(new Font("Times New Roman", Font.ITALIC, 50));
		g.drawString("Press Any Button To Continue", Game.WIDTH/2 - 300, Game.HEIGHT/2 + 40);
	}
	/**True if the players are both on the same team, false otherwise*/
	public static boolean sameTeam(Player one, Player two) {
		return (one.teamID.equalsIgnoreCase(two.teamID));
	}
	public void setChapterOrganizer(ChapterOrganizer organizer) {
		this.chapterOrganizer = organizer;
	}
	public void backToMenu() {
		gameState = STATE.LoadScreen;
		designer = null;
		gameLoader.isSaving = false;
		chapterOrganizer = new ChapterOrganizer(this, new ChapterMap(24, 30, this, "route"), 0);
		this.removeKeyListener(this.getKeyListeners()[0]);
		this.addKeyListener(new KeyInput(this));
		gameState = STATE.Menu;
	}
	public void renderMenuPopUp(Graphics g) {
		
		int thickness = 5;
		g.setColor(Color.blue);
		for (int i = 0; i < thickness; i++) g.drawRect(saveBox[0] + i, saveBox[1] + i, saveBox[2], saveBox[3]);
		g.setColor(Color.white);
		g.fillRect(saveBox[0] + thickness, saveBox[1] + thickness, saveBox[2] - thickness, saveBox[3] - thickness);
		g.setColor(Color.black);
		g.setFont(new Font("Times New Roman", Font.BOLD, 22));
		g.drawString("Would you like to return to the main menu without saving?", saveBox[0] + 5, saveBox[1] + 25);
		int xPos = saveBox[0] + saveBox[2]/2 - scale;
		int yesY =  saveBox[1] + 80;
		int noY = yesY + 50;
		g.setFont(new Font("Times New Roman", Font.BOLD, 35));
		g.drawString("Yes", xPos, yesY);
		g.drawString("No", xPos, noY);
		g.setColor(Color.blue);
		if (menuYes) g.drawRect(xPos - 8, yesY - 28, 75, 40);
		else g.drawRect(xPos - 8, noY - 28, 75, 40);
	}
	private void setPlayerIDMap() {
		playerIDMap = new TreeMap<>();
		playerIDMapINV = new TreeMap<>();
		try {
			Scanner reader = new Scanner(new File("res//characters//playerIDs"));
			String[] line;
			while (reader.hasNextLine()) {
				line = reader.nextLine().split(" ");
				playerIDMap.put(Integer.valueOf(line[1]), line[0]);
				playerIDMapINV.put(line[0], Integer.valueOf(line[1]));
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		}
	}
	public int getLoadLevel() {
		return loadLevel;
	}
	public void setLoadLevel(int newLoadLevel) {
		this.loadLevel = newLoadLevel;
	}
	
	public int[] getBaseStatsForEnemy(String Class) {
		int[] stats = new int[10];
		String line;
		try {
			Scanner statsReader = new Scanner(new File("res//characters//enemyBaseStats"));
			while (statsReader.hasNextLine()) {
				line = statsReader.nextLine();
				if (line.equalsIgnoreCase(Class)) {
					for (int i = 0; i < 10; i++) {
						stats[i] = Integer.parseInt(statsReader.nextLine());
					}
					break;
				}
			}
			statsReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return stats;
	}
	
	public void removePlayer(Player player) {
		chapterOrganizer.removePlayer(player);
	}
	/** Starts the EXP scene, isEnemyPhase = false means it is Ally Phase */
	public void startEXPScene(boolean isEnemyPhase) {
		if (playerForEXP == null) {
			throw new RuntimeException("Game 403 - PLAYER IS NULL IN startEXPScene()");
		}
		levelUpLoader.setPlayer(playerForEXP, playerPreviousEXP, isEnemyPhase);
		if (!isEnemyPhase) SFX.playSong(3);
		gameState = STATE.GainEXP;
	}
	public void setPlayerForEXP(Player player, int prevEXP) {
		this.playerForEXP = player;
		this.playerPreviousEXP = prevEXP;
	}
	/** Image, Stats */
	public String[] getBossStats(int whichChapter) {
		try {
			Scanner reader = new Scanner(new File("res//characters//bossStats"));
			String[] linePiece;
			String[] stats = new String[11];
			while (reader.hasNextLine()) {
				linePiece = reader.nextLine().split(":");
				if (linePiece.length == 2) {
					if (whichChapter == Integer.valueOf(linePiece[1])) {
						//we are on the correct part
						reader.nextLine(); //name
						reader.nextLine(); //class
						reader.nextLine(); // weapon
						for (int i = 0; i < 11; i++) {
							stats[i] = reader.nextLine();
						}
						
					}
				}
			}
			reader.close();
			return stats;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new String[13];
	}
	public CombatItem getBossItem(int whichChapter) {
		try {
			Scanner reader = new Scanner(new File("res//characters//bossStats"));
			String[] linePiece;
			while (reader.hasNextLine()) {
				linePiece = reader.nextLine().split(":");
				if (linePiece.length == 2) {
					if (whichChapter == Integer.valueOf(linePiece[1])) {
						//we are on the correct part
						reader.nextLine();
						reader.nextLine();
						CombatItem weapon = (CombatItem)Item.getItemByID(Integer.valueOf(reader.nextLine()));
						reader.close();
						return weapon;
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new Fists();
	}
	public String getBossName(int whichChapter) {
		try {
			Scanner reader = new Scanner(new File("res//characters//bossStats"));
			String[] linePiece;
			String name;
			while (reader.hasNextLine()) {
				linePiece = reader.nextLine().split(":");
				if (linePiece.length == 2) {
					if (whichChapter == Integer.valueOf(linePiece[1])) {
						//we are on the correct part
						name = reader.nextLine();
						reader.close();
						return name;						
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "John Doe";
	}
	public String getBossClass(int whichChapter) {
		try {
			Scanner reader = new Scanner(new File("res//characters//bossStats"));
			String[] linePiece;
			String clas;
			while (reader.hasNextLine()) {
				linePiece = reader.nextLine().split(":");
				if (linePiece.length == 2) {
					if (whichChapter == Integer.valueOf(linePiece[1])) {
						//we are on the correct part
						reader.nextLine(); //name
						clas = reader.nextLine();
						reader.close();
						return clas;						
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "John Doe";
	}
	
	public void setTradeMenu(Player one, Player two) {
		this.tradeMenu = new TradeMenu(one, two);
		gameState = STATE.TradeState;
	}
	
	public void destroyOutGameCutScene(boolean start) {
		if (start) {
			cutScenes.nullAllScenes();
			gameState = STATE.Game;
		} else {
			cutScenes.nullAllScenes();
			gameLoader.isSaving = true;
			gameState = STATE.LoadGame;
		}
	}
	public void endChapter() {
		cutScenes.startScene(chapterOrganizer.currentChapter, false);
		gameState = STATE.outGameCutScene;
	}
}
