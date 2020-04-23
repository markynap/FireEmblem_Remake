package gameMain;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import ai_MachineIntelligence.EnemyPathFinder;
import characters.AllyPlayer;
import characters.EnemyPlayer;
import characters.Player;
import extras.TimeKeeper;
import gameMain.Game.STATE;
import tiles.Tile;

public class EnemyPhaseProcessor {

	public Game game;
	public ChapterOrganizer chapterOrganizer;
	public ChapterMap currentMap;
	public TimeKeeper timekeep;
	public EnemyPathFinder pathFinder;
	public ArrayList<Tile> tempPath;
	public Map<EnemyPlayer, Tile> destTileMap;
	public Map<EnemyPlayer, Stack<Tile>> enemyMoveMap;
	
	public AllyPlayer choice;
	
	public int choiceTime;
	
	public String searchFactor;
	
	public EnemyPlayer nextPlayer;
	
	public EnemyPhaseProcessor(Game game, ChapterOrganizer chapterOrganizer) {
		this.game = game;
		this.chapterOrganizer = chapterOrganizer;
		this.currentMap = chapterOrganizer.currentMap;
		this.pathFinder = new EnemyPathFinder(game, chapterOrganizer);
		timekeep = new TimeKeeper();
		tempPath = new ArrayList<>();
		destTileMap = new HashMap<>();
		enemyMoveMap = new HashMap<>();
		searchFactor = "Distance";
	}
	
	public void setEnemyChoice(AllyPlayer ally) {
		this.choice = ally;
		game.setGameState(STATE.EnemyChoice);
		choice.drawScope = true;
	}
	
	public void renderEnemyChoice(Graphics g) {
		
		choiceTime++;
		if (choiceTime > 60) {
			choice.drawScope = false;
			choiceTime = 0;
			game.AttackManager.Attack(nextPlayer, choice);
			game.setGameState(STATE.EnemyPhase);
		}
		
		
	}
	
	/** Starts the Enemy's turn, they will move to their designated tiles as they please */
	public void startTurn() {
		try {
			Thread.sleep(110);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (chapterOrganizer.getEnemiesWithMove().isEmpty()) {
			game.chapterOrganizer.currentMap.findAllyWithMoves();
			game.setGameState(STATE.Game);
			return;
		}
		this.nextPlayer = chapterOrganizer.getNextEnemyWithMove();
		Stack<Tile> path = enemyMoveMap.get(nextPlayer);
		game.chapterOrganizer.currentMap.setCurrentTile(nextPlayer.currentTile);
//		currentMap.setCurrentTile(nextPlayer.currentTile);
		if (nextPlayer.currentTile.placeEquals(destTileMap.get(nextPlayer))) {
			chapterOrganizer.lookForKills(nextPlayer);
			nextPlayer.setMAU(false);
			return;
		} else {
			if (path == null) {
				chapterOrganizer.lookForKills(nextPlayer);
				nextPlayer.setMAU(false);
				return;
			} else if (path.isEmpty()) {
				chapterOrganizer.lookForKills(nextPlayer);
				nextPlayer.setMAU(false);
				return;
			}

			currentMap.move(nextPlayer, path.firstElement()); // move to the next tile in our path
			enemyMoveMap.get(nextPlayer).remove(0); // remove the tile we just moved to from our pat
			nextPlayer.setCanMove(true); //let them move again since they are not at their destination yet
			chapterOrganizer.lookForKills(nextPlayer); //look to see if anyone is in range on their way
		}
	}
	public void setDestTileMap() {
		destTileMap.clear();
		enemyMoveMap.clear();
		for (int i = 0; i < chapterOrganizer.enemys.size(); i++) {
			EnemyPlayer en = chapterOrganizer.enemys.get(i);
			destTileMap.put(en, getFinalDestinationForEnemy(en, searchFactor));
			enemyMoveMap.put(en, getPathForEnemy(en));
		}
	}
	
	/** Sets the start and end tiles for the pathFinder, then finds the path*/
	private ArrayList<Tile> findPath(Tile startTile, Tile endTile) {
		pathFinder.setStartEnd(startTile, endTile);
		//FIX THIS
		return pathFinder.findPath();
	}
	/** finds a target location for the enemy then the path to that location*/
	private ArrayList<Tile> findPathForEnemy(EnemyPlayer enemy, String searchFactor) {
		AllyPlayer target = findOptimalAlly(enemy, searchFactor);
		if (target == null) {
			ArrayList<Tile> cur = new ArrayList<>();
			cur.add(enemy.currentTile);
			return cur;
		}
		if (enemy.MOV + enemy.equiptItem.range < game.getTrueDist(enemy.currentTile, target.currentTile)) {
			return findPath(enemy.currentTile, getTileBeforeTarget(enemy, target.currentTile)); // if out of range of target
		}
		// if in range of target we find spot surrounding him to move to
		Tile targetpos = getBestTileInRange(target.currentTile, enemy.currentTile, enemy.equiptItem.range);
		ArrayList<Tile> path = findPath(enemy.currentTile, targetpos); //this is where it all goes wrong
		//need to fix the findPath method!
		return path;
	}
	/** Returns the tile inbetween the target and enemy */
	private Tile getTileBeforeTarget(EnemyPlayer enemy, Tile targetTile) {
		
		int xDist = Math.abs(enemy.currentTile.x - targetTile.x);
		int yDist = Math.abs(enemy.currentTile.y - targetTile.y);
		
		if (xDist > yDist) {
			if (enemy.currentTile.x < targetTile.x) return chapterOrganizer.currentMap.getTileAtAbsolutePos(targetTile.x -1 , targetTile.y);
			else if (enemy.currentTile.x > targetTile.x) return chapterOrganizer.currentMap.getTileAtAbsolutePos(targetTile.x + 1 , targetTile.y);
		} else {
			if (enemy.currentTile.x < targetTile.y) return chapterOrganizer.currentMap.getTileAtAbsolutePos(targetTile.x, targetTile.y - 1);
			else if (enemy.currentTile.x > targetTile.y) return chapterOrganizer.currentMap.getTileAtAbsolutePos(targetTile.x, targetTile.y+1);
		}
		return targetTile;
	}
	/** If the target is too far away for our enemy to see he will not move */
	private Tile checkOutOfSight(Tile startTile, Player target) {
		if (game.getTrueDist(startTile, target.currentTile) <= nextPlayer.sightRange) return target.currentTile;
		else return startTile;
	}
	
	/**
	 * finds the best tile surrounding the desired unit within a certain range
	 * @param startTile the tile belonging to our target
	 * @param observer the unit attacking the target
	 * @param range the attacking unit (observer's) range
	 * @return
	 */
	private Tile getBestTileInRange(Tile startTile, Tile observer, int range) {
		Tile tempTile;
		int shortestDist = 500;
		Tile bestTile = null;
		for (int i = 1; i <= range; i++) {
		
			tempTile = currentMap.getTileAtAbsolutePos(startTile.x+i, startTile.y);
			if (tempTile != null) {
				if (tempTile.isCrossable && tempTile.carrier == null) {
					if (game.getTrueDist(tempTile, observer) < shortestDist) {
						shortestDist = game.getTrueDist(tempTile, observer);
						bestTile = tempTile;
					}
				}
			}
		
			tempTile = currentMap.getTileAtAbsolutePos(startTile.x, startTile.y+i);
			if (tempTile != null) {
				if (tempTile.isCrossable && tempTile.carrier == null) {
					if (game.getTrueDist(tempTile, observer) < shortestDist) {
						shortestDist = game.getTrueDist(tempTile, observer);
						bestTile = tempTile;
					}
				}
			}
		
			tempTile = currentMap.getTileAtAbsolutePos(startTile.x-i, startTile.y);
			if (tempTile != null) {
				if (tempTile.isCrossable && tempTile.carrier == null) {
					if (game.getTrueDist(tempTile, observer) < shortestDist) {
						shortestDist = game.getTrueDist(tempTile, observer);
						bestTile = tempTile;
					}
				}
			}			
			
			tempTile = currentMap.getTileAtAbsolutePos(startTile.x, startTile.y-i);
			if (tempTile != null) {
				if (tempTile.isCrossable && tempTile.carrier == null) {
					if (game.getTrueDist(tempTile, observer) < shortestDist) {
						shortestDist = game.getTrueDist(tempTile, observer);
						bestTile = tempTile;
					}
				}
			}
			
		}
		if (bestTile == null) {
			System.out.println("There is no path surrounding our desired unit");
			return startTile;
		} else return bestTile;
	}
	
	/**
	 * The final Tile that this unit can move to as part of it's path
	 * @param enemy the enemy who's path will be assessed
	 * @param searchFactor - what enemies are searching for - Distance or HP
	 * @return the tile this unit shall move to
	 */
	public Tile getFinalDestinationForEnemy(EnemyPlayer enemy, String searchFactor) {
		ArrayList<Tile> path = findPathForEnemy(enemy, searchFactor);
		
		if (path.size() == 0) {
			System.out.println("path size is zero EPP L128");
			return enemy.currentTile;
		}
		if (path.size() <= enemy.MOV) {
			return path.get(path.size()-1);
		} else {
			return path.get(enemy.MOV);
		}
	}
	/**
	 * The entire path that this enemy can move to
	 * @param enemy
	 * @return
	 */
	public ArrayList<Tile> getPathWithinMoveForEnemy(EnemyPlayer enemy) {
		ArrayList<Tile> path = findPathForEnemy(enemy, searchFactor);
		ArrayList<Tile> newPath = new ArrayList<>();
		if (path.size() == 0) return null;
		if (path.size() <= enemy.MOV) {
			return path;
		} else {
			for (int i = 0; i <= enemy.MOV; i++) {
				newPath.add(path.get(i));
			}
			return newPath;
		}
	}
	
	private Stack<Tile> getPathForEnemy(EnemyPlayer enemy) {
		Stack<Tile> stack = new Stack<>();
		ArrayList<Tile> path = findPathForEnemy(enemy, searchFactor);
		if (path.size() == 0) return null;
		if (path.size() < enemy.MOV) {
			for (Tile a : path) {
				stack.add(a);
			}
			return stack;
		} else {
			for (int i = 0; i < enemy.MOV; i++) {
				stack.add(path.get(i));
			}
			return stack;
		}
	}
	
	public boolean tileInRange(Tile start, Tile end, int range) {
		return ((Math.abs(start.x - end.x) + Math.abs(start.y - end.y)) <= range);
	}
	/**
	 * This algorithm will search through every ally player in the area
	 * if the enemy is next to an ally or in attack range, they are the priorty
	 * otherwise, the ally with the lowest currentHP will be the target
	 * @return the AllyPlayer that fits this criteria 
	 */
	private AllyPlayer findOptimalAlly(EnemyPlayer enemy, String searchFactor) {
		if (chapterOrganizer.allys.size() == 0) {
			game.loseGame();
			return null;
		}
		
		//return findClosestAndLowestAlly(enemy);
		
		if (searchFactor.equalsIgnoreCase("HP")) {
			return findLowestHealthAlly(enemy);
		} else if (searchFactor.equalsIgnoreCase("Distance")) {
			return findClosestAlly(enemy);
		} else {
			System.out.println("EnemyPhaseProcessor - Search Factor case undetermined - factor = " + searchFactor);
			return findClosestAlly(enemy);
		}
		
	}
	/** Returns the closest and lowest health enemy available and in sight range */
	private AllyPlayer findClosestAndLowestAlly(EnemyPlayer enemy) {
		
		int attRange = enemy.equiptItem.range;
		ArrayList<AllyPlayer> inAttackRangeEnemies = new ArrayList<>();
		ArrayList<AllyPlayer> inMOVRangeEnemies = new ArrayList<>();
		AllyPlayer closest = chapterOrganizer.allys.get(0);
		int distance = 0;
		int prevDistance = 100000;
		int finalDistance = 0;
		// Loop Through Allies
		for (int i = 0; i < chapterOrganizer.allys.size(); i++) {
			AllyPlayer ally = chapterOrganizer.allys.get(i);
			
			//if an ally is in range of this enemy
			//distance = game.getTrueDist(ally.currentTile, enemy.currentTile); 
			distance = game.getTrueDist(enemy.currentTile, ally.currentTile);
			
			if (distance <= attRange) {
				inAttackRangeEnemies.add(ally);
			} else if (distance <= enemy.MOV) {
				inMOVRangeEnemies.add(ally);
			}
			if (distance < prevDistance) {
				closest = ally;
				finalDistance = distance;
			}
			prevDistance = distance;
		}
		
		if (inAttackRangeEnemies.isEmpty()) {
			
			if (inMOVRangeEnemies.isEmpty()) {
				
				if (finalDistance > enemy.sightRange) return null;
				else return closest;
				
			} else {
				if (inMOVRangeEnemies.size() == 1) return inMOVRangeEnemies.get(0);
				else {
					int lowestHP = 100000;
					AllyPlayer target = null;
					for (AllyPlayer a : inMOVRangeEnemies) {
						if (lowestHP < a.currentHP) {
							lowestHP = a.currentHP;
							target = a;
						}
					}
					return target;
				}
				
			}
			
		} else {
			// attack range enemies list is not empty
			if (inAttackRangeEnemies.size() == 1) return inAttackRangeEnemies.get(0);
			else {
				int lowestHP = 100000;
				AllyPlayer target = null;
				for (AllyPlayer a : inAttackRangeEnemies) {
					if (lowestHP < a.currentHP) {
						lowestHP = a.currentHP;
						target = a;
					}
				}
				return target;
			}
			
		}
	}
	
	
	
	
	
	private AllyPlayer findClosestAlly(EnemyPlayer enemy) {
		
		int attRange = enemy.equiptItem.range;
		AllyPlayer closest = chapterOrganizer.allys.get(0);
		int distance = 0;
		int finalDistance = 1110;
		// Loop Through Allies
		for (int i = 0; i < chapterOrganizer.allys.size(); i++) {
			AllyPlayer ally = chapterOrganizer.allys.get(i);
			//if an ally is in range of this enemy
			if (game.getTrueDist(ally.currentTile, enemy.currentTile) <= attRange) return ally;
			distance = game.getTrueDist(ally.currentTile, enemy.currentTile);
			if (distance < game.getTrueDist(enemy.currentTile, closest.currentTile)) {
				closest = ally;
				finalDistance = distance;
			}
		}
		if (finalDistance > enemy.sightRange) {
			//enemies should not be able to see this unit
			return null;
		}
		return closest;
	}
	
	private AllyPlayer findLowestHealthAlly(EnemyPlayer enemy) {
		
		int attRange = enemy.equiptItem.range;
		int min = chapterOrganizer.allys.get(0).currentHP;
		AllyPlayer target = chapterOrganizer.allys.get(0);

		//Loop through Allys
		for (int i = 0; i < chapterOrganizer.allys.size(); i++) {
			AllyPlayer ally = chapterOrganizer.allys.get(i);
			//if an ally is in range of this enemy
			if (game.getTrueDist(ally.currentTile, enemy.currentTile) <= attRange) return ally;

			min = Math.min(min, ally.currentHP);
			if (min == ally.currentHP) {
				target = ally;
			}
		}
		return target;
	}
	/** Sets the map the enemies can see to our current game map */
	public void updateMap() {
		this.currentMap = chapterOrganizer.currentMap;
	}
}
