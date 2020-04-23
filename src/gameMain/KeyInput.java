package gameMain;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import ai_MachineIntelligence.PathGenerator;
import characters.Brigand;
import characters.Hector;
import characters.Player;
import gameMain.Game.STATE;
import graphics.AttackMenu;
import graphics.PopUpMenu;
import items.UtilityItem;
import tiles.Tile;

public class KeyInput extends KeyAdapter {

	public Game game;
	public char key;
	public int keyCode;
	public ChapterMap currentMap;
	public Tile currentTile;
	public Player currentPlayer;

	public KeyInput(Game game) {
		this.game = game;
	}

	public void keyPressed(KeyEvent e) {
		
		
		if (game.gameState == STATE.StartScreen) {
			game.timekeep.startNewSession();
			game.setGameState(STATE.Menu);
		}
		if (game.gameState == STATE.LoseGame) game.setGameState(STATE.StartScreen);
		
		key = e.getKeyChar();
		keyCode = e.getExtendedKeyCode();
		currentMap = game.chapterOrganizer.currentMap;
		if (currentMap != null) currentTile = currentMap.currentTile;
		
		if (key == 's') {
			if (game.gameState == STATE.EnemyPhase || game.gameState == STATE.EnemyChoice
					|| game.gameState == STATE.outGameCutScene) return;
		if (game.pathGenerator != null)	{
			game.pathGenerator.resetTiles();
			game.pathGenerator.eraseScopes();
			currentMap.nullAttackMenu();
		}
		if (game.gameState == STATE.Game) {
			if (currentTile.carrier != null) {
				Player p = currentTile.carrier;
				if (!p.canMove) {
					if (p.canAttack && p.canUse) {
						currentMap.move(p, p.previousTile);
						p.setMAU(true);
					}
				}
			}
		}
		currentMap.nullAttackMenu();
		game.tradeMenu = null;
		if (game.gameState == STATE.LoadGame) game.setGameState(STATE.Menu); 
		if (game.playerGFX!= null) game.playerGFX.setItemOptions(false);
		if (game.gameState == STATE.Menu) return;
		if (game.gameState == STATE.AttackStage) {
			if (game.AttackManager.attacker.teamID.equalsIgnoreCase("Ally")) {
				game.startEXPScene(false);
				return;
			} else {
				game.startEXPScene(true);
				game.setGameState(STATE.EnemyPhase);
				return;
			}
		} else if (game.gameState == STATE.LevelUp) {
			if (game.levelUpLoader.isEnemyPhase) {
				game.setGameState(STATE.EnemyPhase);
				return;
			}
		}
			game.setGameState(STATE.Game);
		
		}
		
		if (game.gameState == STATE.Game) {
			
			moveCurrentTile();
			
			if (keyCode == KeyEvent.VK_ESCAPE) {
				game.setGameState(STATE.ReturnToMenu);
			}
						
			if (key == 'a') {
				currentPlayer = currentTile.carrier;
				if (currentPlayer == null) {
					
				} else {
					if (!currentPlayer.teamID.equalsIgnoreCase("Ally")) {
						setPopUpMenu();
						return;
					}
					if (!currentPlayer.canMove) {
						setPopUpMenu();
					} else {
					game.setPathGenerator(new PathGenerator(game, currentTile, currentPlayer.MOV));
					game.pathGenerator.setAllPathableTiles(currentPlayer.isFlier);
					game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
					game.setGameState(STATE.MoveState);
					}
				}
			} else if (key == 'q') {
				currentMap.findAllyWithMoves();
			}
		} else if (game.gameState == STATE.Info) {
			
			if (key == 'q') {
				game.setGameState(STATE.AdvInfo);
			} else if (key == 'a') {
				game.SFX.playSelect();
				if (!game.playerGFX.inItemOptions) {
					game.playerGFX.setItemOptions(true);
				} else {
					if (game.playerGFX.itemOptionIndex == 0) {
						game.playerGFX.wallet.equipt(game.playerGFX.currentItem);
						game.playerGFX.setItemOptions(false);
					} else if (game.playerGFX.itemOptionIndex == 1) {
						//put in logic for using item
						if (game.playerGFX.currentItem.category.equalsIgnoreCase("Utility")) {
							UtilityItem it = (UtilityItem) game.playerGFX.currentItem;
							it.use();
						}
						game.playerGFX.setItemOptions(false);
					} else if (game.playerGFX.itemOptionIndex == 2) {
						currentTile.carrier.removeItem(game.playerGFX.currentItem);
						game.playerGFX.decWeaponIndex();
						game.playerGFX.setItemOptions(false);
					}
				}
			
			} else if (keyCode == KeyEvent.VK_DOWN) {
				game.SFX.playCursor();
				if (!game.playerGFX.inItemOptions) game.playerGFX.incWeaponIndex();
				else game.playerGFX.incItemOptionIndex(1);
			} else if (keyCode == KeyEvent.VK_UP) {
				game.SFX.playCursor();
				if (!game.playerGFX.inItemOptions) game.playerGFX.decWeaponIndex();
				else game.playerGFX.incItemOptionIndex(-1);
			} else {
				game.playerGFX.setItemOptions(false);
				game.pathGenerator.resetTiles();
				game.setGameState(STATE.Game);
			}
			
			
		} else if (game.gameState == STATE.AdvInfo) {
			
			if (key == 'q') game.setGameState(STATE.Info);
			else game.setGameState(STATE.Game);
			
		} else if (game.gameState == STATE.PopUpMenu) {
			
			if (key == 's') {
				currentMap.nullAttackMenu();
				game.setGameState(STATE.Game);	
			}
			if (keyCode == KeyEvent.VK_UP) {
				game.SFX.playCursor();
				game.PUM.decSelectedOptions();
			} else if (keyCode == KeyEvent.VK_DOWN) {
				game.SFX.playCursor();
				game.PUM.incSelectedOptions();
			}
			
			if (key == 'a') {
				game.SFX.playSelect();
				
				if (game.PUM.selectedIndex == 0) { //Sieze state
					//NEED TO CHANGE INPUT HERE ONCE WE GET LOAD STATE
					game.endChapter();
					
				} else if (game.PUM.selectedIndex == 1) { //Attack state
					
					if (currentPlayer == null) return;
					if (!currentPlayer.canAttack) return;
					if (currentPlayer.Class.equalsIgnoreCase("Dancer")) {
						game.setPathGenerator(new PathGenerator(game, currentTile, 1));
						game.pathGenerator.resetTiles();
						game.pathGenerator.setAllPathableTiles(false);
						game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
						game.setGameState(STATE.AttackState);
						return;
					}
					game.playerGFX.setPlayer(currentPlayer);
					game.setGameState(STATE.weaponSelection);
					
				} else if (game.PUM.selectedIndex == 2) { //Item state
					
					if (currentPlayer == null) return;
					game.playerGFX.setPlayer(currentTile.carrier);
					game.setGameState(STATE.Info);
					
				} else if (game.PUM.selectedIndex == 3) { //Trade State
					
					if (currentPlayer == null) return;
					if (currentTile.carrier == null) return;
					game.pathGenerator.resetTiles();
					game.setPathGenerator(new PathGenerator(game, currentTile, 1));
					game.pathGenerator.setAllPathableTiles(false);
					game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
					game.setGameState(STATE.TradeState);
					
				} else if (game.PUM.selectedIndex == 4) { //Wait State
					
					if (currentPlayer == null) return;
					currentPlayer.setMAU(false);
					currentMap.nullAttackMenu();
					game.setGameState(STATE.Game);
					game.pathGenerator.resetTiles();
					
				} else if (game.PUM.selectedIndex == 5) { //End State
					
					currentMap.nullAttackMenu();
					if (game.pathGenerator != null) game.pathGenerator.resetTiles();
					game.setGameState(STATE.Game);	
					currentMap.nextPhase();
					
				}
			}
			
		} else if (game.gameState == STATE.weaponSelection) {
		
			if (key == 'a') {
				game.setPathGenerator(new PathGenerator(game, currentTile, game.playerGFX.getSelectedItem().range));
				game.pathGenerator.resetTiles();
				game.pathGenerator.setAllPathableTiles(false);
				game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
				game.setGameState(STATE.AttackState);
			}
			if (keyCode == KeyEvent.VK_UP) {
				game.playerGFX.incWeaponChooseIndex(-1);
			} else if (keyCode == KeyEvent.VK_DOWN) {
				game.playerGFX.incWeaponChooseIndex(1);
			}
			
			
		}
		else if (game.gameState == STATE.TradeState) {
		
			if (game.tradeMenu == null) {
				if (key == 'a') {
					game.setTradeMenu(currentPlayer, game.chapterOrganizer.currentMap.selectedBoxTile.carrier);
					game.pathGenerator.resetTiles();
				}
				moveArrowTile();
			} else {
				
				if (keyCode == KeyEvent.VK_UP) {
					game.tradeMenu.updateSelectedItem(-1);
				} else if (keyCode == KeyEvent.VK_DOWN) {
					game.tradeMenu.updateSelectedItem(1);
				} else if (keyCode == KeyEvent.VK_LEFT) {
					game.tradeMenu.swapTrader();
				} else if (keyCode == KeyEvent.VK_RIGHT) {
					game.tradeMenu.swapTrader();
				}
				if (key == 'a') {
					if (game.tradeMenu.isOnPlayerOne()) {
						game.tradeMenu.trader1.giveItem(game.tradeMenu.trader2, game.tradeMenu.itemToTrade());
						game.tradeMenu.updateSelectedItem(0); // prevents null pointer exception by bringing to top
					}
					else {
						game.tradeMenu.trader2.giveItem(game.tradeMenu.trader1, game.tradeMenu.itemToTrade());
						game.tradeMenu.updateSelectedItem(0); // this brings us back to the top so we dont trade null
					}
				}
				
			}
			
		} else if (game.gameState == STATE.MoveState) {

			moveArrowTile();
			
			if (key == 'a') {
				if (game.pathGenerator.getTopTile() == currentTile) {
					setPopUpMenu();
				} else if (game.pathGenerator.getTopTile().pathable) {
					game.chapterOrganizer.currentMap.move(currentTile.carrier, game.pathGenerator.getTopTile());
					game.chapterOrganizer.currentMap.setCurrentTile(game.pathGenerator.getTopTile());
					game.pathGenerator.resetTiles();
					game.setGameState(STATE.Game);
				}
			}
		} else if (game.gameState == STATE.AttackState) {
			
			currentPlayer = currentTile.carrier;
			if (currentPlayer == null) throw new RuntimeException("idk how this happened, attack state KeyInput");
			game.pathGenerator.eraseScopes();
			moveAttackTile();
			
			if (key == 'a' && game.pathGenerator.getTopTile().isOccupied()) {
					Player opponent = game.pathGenerator.getTopTile().carrier;
					if (currentPlayer.teamID.equalsIgnoreCase(opponent.teamID)) { //if opponent and player are on the same team
						if (currentPlayer.isHealer()) { //if we are a healer
							currentPlayer.healPlayer(opponent);
							game.pathGenerator.resetTiles();
							game.setGameState(STATE.Game);
						} else if (currentPlayer.isDancer()) { //a dancer
							currentPlayer.danceForPlayer(opponent);
							game.pathGenerator.resetTiles();
							game.setGameState(STATE.Game);
						}
					} else { //opponent is on a different team than player
						//this is where we put in the logic for attacking
						if (currentPlayer.isHealer() || currentPlayer.isDancer()) return;
						if (currentMap.inAttackMenu) {
							game.AttackManager.Attack(currentPlayer, opponent);
							game.pathGenerator.resetTiles();
							game.pathGenerator.eraseScopes();
							currentMap.nullAttackMenu();
							
						} else {
						
							currentMap.setAttackMenu(new AttackMenu(currentPlayer, opponent));
							
						}

					}
			}
			
		} else if (game.gameState == STATE.Menu) {
			
			if (keyCode == KeyEvent.VK_DOWN) {
				game.menu.incSelectedOptions();
			} else if (keyCode == KeyEvent.VK_UP) {
				game.menu.decSelectedOptions();
			}
			
			if (key == 'a') {
					if (game.timekeep.newSessionAtDesiredTime(0.6)) {
						game.timekeep.resetSessions();
					if (game.menu.optionIndex == 0) { // new game!
						
						//ChapterOrganizer oldOrg = game.chapterOrganizer;
						game.setChapterOrganizer(new ChapterOrganizer(game, 1, 0));
						game.cutScenes.startScene(1, true);
						game.gameState = STATE.outGameCutScene;
						
					} else if (game.menu.optionIndex == 1) {
						
						game.setGameState(STATE.LoadGame); //load game -- only time load level will not be 0!
				
					} else if (game.menu.optionIndex == 2) {
						//choose chapter state
						game.setChapterChooser();
						
					} else if (game.menu.optionIndex == 3) {
						//this is where we enter our Chapter Design state!!!
						game.setChapterDesigner();
					}
				}
			}
		} else if (game.gameState == STATE.ChapterChoose) {
			if (keyCode == KeyEvent.VK_RIGHT) {
				game.chapterChooser.incBoxIndex();
			} else if (keyCode == KeyEvent.VK_LEFT) {
				game.chapterChooser.decBoxIndex();
			} else if (keyCode == KeyEvent.VK_DOWN) {
				game.chapterChooser.boxIndex = 6;
			} else if (keyCode == KeyEvent.VK_UP) {
				game.chapterChooser.boxIndex = 7;
			}
			if (key == 'a') {
				if (game.chapterChooser.boxIndex == 7) { //clicks on left arrow
					game.chapterChooser.decScreenNum();
				} else if (game.chapterChooser.boxIndex == 6) { //clicks on right arrow
					game.chapterChooser.incScreenNum();
				} else {
					//clicks on any of the chapter boxes
					game.chapterChooser.chooseChapter();
					game.cutScenes.startScene(game.chapterOrganizer.currentChapter, true);
					game.gameState = STATE.outGameCutScene;
				}
			} else if (keyCode == KeyEvent.VK_ESCAPE) {
				game.backToMenu();
			}

		} else if (game.gameState == STATE.ReturnToMenu) {
			if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_UP) {
				if (game.menuYes) game.menuYes = false;
				else game.menuYes = true;
			} else if (key == 'a') {
				if (game.menuYes) {
					game.backToMenu();
				} else {
					game.setGameState(STATE.Game);
				}
			}
		} else if (game.gameState == STATE.LoadGame) {
			
			if (keyCode == KeyEvent.VK_UP) {
				game.gameLoader.changeSelectedIndex(-1);
			} else if (keyCode == KeyEvent.VK_DOWN) {
				game.gameLoader.changeSelectedIndex(1);
			}  
			
			if (key == 'a' || keyCode == KeyEvent.VK_ENTER) {
				if (!game.gameLoader.isSaving) game.gameLoader.loadGame();
				else game.gameLoader.saveGame();
				game.cutScenes.startScene(game.chapterOrganizer.currentChapter, true);
				game.gameState = STATE.outGameCutScene;
			}
			
		} else if (game.gameState == STATE.outGameCutScene) {
			
			if (keyCode == KeyEvent.VK_LEFT) {
				game.cutScenes.cutScene.setPreviousLine();
			} else if (key == 'a') {
				game.cutScenes.cutScene.fillLine();
			} else game.cutScenes.cutScene.setNextLine();
			
		}
		
	}
	
	
	
	
	/** responsible for moving the cursor around the map*/
	public void moveCurrentTile() {
		if (keyCode == KeyEvent.VK_RIGHT) {
			game.chapterOrganizer.currentMap.setCurrentTile(
					game.chapterOrganizer.currentMap.getTileAtAbsolutePos(currentTile.x + 1, currentTile.y));
			game.SFX.playCursor();
		} else if (keyCode == KeyEvent.VK_LEFT) {
			game.chapterOrganizer.currentMap.setCurrentTile(
					game.chapterOrganizer.currentMap.getTileAtAbsolutePos(currentTile.x - 1, currentTile.y));
			game.SFX.playCursor();
		} else if (keyCode == KeyEvent.VK_UP) {
			game.chapterOrganizer.currentMap.setCurrentTile(
					game.chapterOrganizer.currentMap.getTileAtAbsolutePos(currentTile.x, currentTile.y - 1));
			game.SFX.playCursor();
		} else if (keyCode == KeyEvent.VK_DOWN) {
			game.chapterOrganizer.currentMap.setCurrentTile(
					game.chapterOrganizer.currentMap.getTileAtAbsolutePos(currentTile.x, currentTile.y + 1));
			game.SFX.playCursor();
		}
	
	}
	/**for movestates or any state that draws a long trace */
	private void moveArrowTile() {
		Tile arrowTile;
		if (keyCode == KeyEvent.VK_RIGHT) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x + 1, game.pathGenerator.getTopTile().y);
			game.pathGenerator.setTileArrow(arrowTile);
		} else if (keyCode == KeyEvent.VK_LEFT) {			
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x - 1, game.pathGenerator.getTopTile().y);
			game.pathGenerator.setTileArrow(arrowTile);
		} else if (keyCode == KeyEvent.VK_UP) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x, game.pathGenerator.getTopTile().y - 1);
			game.pathGenerator.setTileArrow(arrowTile);
		} else if (keyCode == KeyEvent.VK_DOWN) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x, game.pathGenerator.getTopTile().y + 1);
			game.pathGenerator.setTileArrow(arrowTile);
		}
		game.SFX.playCursor();
	}
	/** For attack states */
	private void moveAttackTile() {
		Tile arrowTile;
		if (keyCode == KeyEvent.VK_RIGHT) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x + 1, game.pathGenerator.getTopTile().y);
			game.pathGenerator.drawTilePath(arrowTile);
		} else if (keyCode == KeyEvent.VK_LEFT) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x - 1, game.pathGenerator.getTopTile().y);
			game.pathGenerator.drawTilePath(arrowTile);
		} else if (keyCode == KeyEvent.VK_UP) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x, game.pathGenerator.getTopTile().y - 1);
			game.pathGenerator.drawTilePath(arrowTile);
		} else if (keyCode == KeyEvent.VK_DOWN) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x, game.pathGenerator.getTopTile().y + 1);
			game.pathGenerator.drawTilePath(arrowTile);
		}
		game.SFX.playCursor();
	}
	
	public void setPopUpMenu() {
		game.setPopUpMenu(new PopUpMenu(game, currentTile));
		game.SFX.playSelect();
		game.setGameState(STATE.PopUpMenu);
	}
}
