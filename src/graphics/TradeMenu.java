package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import characters.Player;
import gameMain.Game;
import items.CombatItem;
import items.Item;
import items.UtilityItem;

public class TradeMenu {
	/** Game coordinates of Menu */
	public int xPos, yPos;
	
	private int scale = Game.scale;
	
	public Game game;
	
	public Player trader1, trader2;
	//height, width
	private int boxH = 3*Game.scale;
	
	private int boxW = 5*Game.scale;
	
	private int thickness = 4;
	
	private int spacing = 20;
	
	private int midLineY, centerLineX;
	
	private int selectedItem;
	
	private boolean onTraderOne;
		
	public TradeMenu(Player one, Player two) {
		this.trader1 = one;
		this.trader2 = two;
		this.xPos = trader1.xPos;
		this.yPos = trader1.yPos + 1;
		midLineY = yPos * scale + scale/3 + 5;
		centerLineX = xPos * scale + boxW/2;
		onTraderOne = true;
	}
	
	public void render(Graphics g) {
		
		g.setColor(Color.white);
		for (int i = 0; i < thickness; i++)
			g.drawRect(xPos * scale + i, yPos * scale + i, boxW - (2*i), boxH - (2*i));
		
		g.setColor(Color.gray);
		g.fillRect(xPos * scale + thickness, yPos * scale + thickness, boxW - 2*thickness, boxH - 2*thickness);

		//draws the selected item we are hovering over
		g.setColor(Color.blue);
		if (onTraderOne) g.fillRect(xPos * scale + thickness/2, midLineY + scale/9 + (spacing * selectedItem), boxW/2 - thickness/2, (int)(2*(spacing/3.0)));	
		else g.fillRect(xPos * scale + thickness/2 + boxW/2, midLineY + scale/9 + (spacing * selectedItem), boxW/2, (int)(2*(spacing/3.0)));

		
		g.setFont(new Font("Times New Roman", Font.BOLD, 15));
		g.setColor(Color.white);
		g.drawLine(centerLineX, yPos * scale + thickness, centerLineX, yPos * scale + boxH - thickness);
		
		g.drawString(trader1.name, xPos * scale + boxW/7, yPos * scale + thickness + 15);
		g.drawString(trader2.name, xPos * scale + 3*boxW/5, yPos * scale + thickness + 15);
		
		g.drawLine(xPos * scale, midLineY, xPos * scale + boxW + thickness, midLineY);
		
		
		for (int i = 0; i < trader1.wallet.weapons.size(); i++) {
			CombatItem item = trader1.wallet.weapons.get(i);
			g.drawString(item.name + " (" + item.duration + ")", xPos * scale + 5, midLineY + scale/3 + (spacing * i));
		}
		
		for (int i = 0; i < trader1.wallet.utilities.size(); i++) {
			UtilityItem item = trader1.wallet.utilities.get(i);
			g.drawString(item.name + " (" + item.duration + ")", xPos * scale + 5, midLineY + scale/3 + (spacing * i) + (spacing * trader1.wallet.weapons.size()));
		}
		
		for (int i = 0; i < trader2.wallet.weapons.size(); i++) {
			CombatItem item = trader2.wallet.weapons.get(i);
			g.drawString(item.name + " (" + item.duration + ")", centerLineX + 5, midLineY + scale/3 + (spacing * i));
		}
		
		for (int i = 0; i < trader2.wallet.utilities.size(); i++) {
			UtilityItem item = trader2.wallet.utilities.get(i);
			g.drawString(item.name + " (" + item.duration + ")", centerLineX + 5, midLineY + scale/3 + (spacing * i) + (spacing * trader2.wallet.weapons.size()));
		}
		
		
	}
	
	public void updateSelectedItem(int amount) {
		
		int max = 0;
		if (onTraderOne) max = Math.max(trader1.wallet.weapons.size()+ trader1.wallet.utilities.size()-1, 0);
		else max = Math.max(trader2.wallet.weapons.size()+ trader2.wallet.utilities.size()-1, 0);
		
		selectedItem += amount;
		
		if (selectedItem > max) selectedItem = 0;
		else if (selectedItem < 0) selectedItem = max;
		
	}
	/** Swaps who's items we are selecting over */
	public void swapTrader() {
		if (onTraderOne) onTraderOne = false;
		else onTraderOne = true;
		selectedItem = 0;
	}
	/** True if the selected item belongs to player one */
	public boolean isOnPlayerOne() {
		return onTraderOne;
	}
	
	public Item itemToTrade() {
		
		if (onTraderOne) {
			
			if (selectedItem >= trader1.wallet.weapons.size()) return trader1.wallet.utilities.get(selectedItem - trader1.wallet.weapons.size()); 
			else return trader1.wallet.weapons.get(selectedItem);
			
		} else {
			
			if (selectedItem >= trader2.wallet.weapons.size()) return trader2.wallet.utilities.get(selectedItem - trader2.wallet.weapons.size()); 
			else return trader2.wallet.weapons.get(selectedItem);
			
		}
		
	}
}
