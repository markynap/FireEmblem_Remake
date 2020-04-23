package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import characters.Player;
import gameMain.Game;

public class AttackMenu {

	public int xPos, yPos, menuH, menuW;
	public Player attacker, defender;
	public boolean attMagic, defMagic;
	
	public AttackMenu(Player attacker, Player defender) {
		int x;
		int y;
		if (defender.xPos - attacker.xPos == 1) {
			x = attacker.currentTile.xPos+1;
			y = attacker.currentTile.yPos+1;
		} else {
			x = attacker.currentTile.xPos+1;	
			y = attacker.currentTile.yPos;
		}
		int distFromRight = Game.nRow - x;
		if (distFromRight < 5) {
			x -= 3;
			if (attacker.currentTile.yPos > Game.nCol - 2) y -= 4;
			else y += 2;
		}
		
		this.xPos = x*Game.scale;
		this.yPos = y*Game.scale;
		this.attacker = attacker;
		this.defender = defender;
		this.menuH = 135;
		this.menuW = 290;
		if (attacker.equiptItem.category.equalsIgnoreCase("Magical")) attMagic = true;
		if (defender.equiptItem.category.equalsIgnoreCase("Magical")) defMagic = true;
	}
	
	public void render(Graphics g) {
		g.setColor(Color.white);
		g.drawRect(xPos, yPos, menuW, menuH);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(xPos + 1, yPos + 1, menuW-2, menuH-2);
		g.setColor(Color.black);
		g.setFont(new Font("Times New Roman", Font.BOLD, 24));
		g.drawLine(xPos + menuW/2, yPos, xPos + menuW/2, yPos + menuH);
		g.setColor(Color.white);
		int spacing = 30;
		int attackerDMG;
		int defenderDMG;
		if (attMagic) attackerDMG = attacker.damage - defender.RES;
		else attackerDMG = attacker.damage - defender.defense;
		
		if (defMagic) defenderDMG = defender.damage - attacker.RES;
		else defenderDMG = defender.damage - attacker.defense;
		
		g.drawString("HP: " + attacker.currentHP + "/" + attacker.HP, xPos + 1, yPos + 1 + spacing);
		g.drawString("Hit: " + Math.max(0,(attacker.hit - defender.avoid)), xPos + 1, yPos + (spacing*2));
		if (attacker.SP - defender.SP >= 4) {
			g.drawString("Damage: " + Math.max(0,attackerDMG) + "x2", xPos + 1, yPos + (spacing*3));
		} else {
			g.drawString("Damage: " + Math.max(0,attackerDMG), xPos + 1, yPos + (spacing*3));
		}
		g.drawString("Crit: " + Math.max(0,(attacker.crit - defender.LCK)), xPos + 1, yPos + (spacing*4));
		
		g.drawString("HP: " + defender.currentHP + "/" + defender.HP, xPos + 3 + menuW/2, yPos + 1 + spacing);
		g.drawString("Hit: " + Math.max(0,(defender.hit - attacker.avoid)), xPos + 3 + menuW/2, yPos+ (spacing*2));
		if (defender.SP - attacker.SP >= 4) {
			g.drawString("Damage: " + Math.max(0,defenderDMG) + "x2", xPos + 3 + menuW/2, yPos + (spacing*3));
		} else {
			g.drawString("Damage: " + Math.max(0,defenderDMG), xPos + 3 + menuW/2, yPos + (spacing*3));
		}
		g.drawString("Crit: " + Math.max(0,(defender.crit - attacker.LCK)), xPos + 3 + menuW/2, yPos + (spacing*4));
		
	}
}
