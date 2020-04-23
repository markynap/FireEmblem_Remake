package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import characters.*;
import gameMain.*;
import items.*;

public class PlayerInfoGFX {
	public final int rectSize = 15;
	public final int EQrectX = 19*Game.scale/2;
	public final int USErectX = Game.scale * 10;
	public Game game;
	public Player carrier;
	public Wallet wallet;
	public int[] sizes;
	public int weaponIndex;
	public Item currentItem;
	/**is true if a player pressed a on an item and can equip, use, or remove it */
	public boolean inItemOptions;
	public int itemOptionIndex;
	public final static String[] itemOptions = {"EQUIP", "USE", "DROP"};
	
	/** Used in rendering weapon selection before combat */
	private int[] playerbox = {Game.WIDTH/2 + 2*Game.scale, Game.HEIGHT/3, (int)(Game.WIDTH/3.0), Game.HEIGHT/4 - 20};
	/** Used in rendering weapon selection before combat */
	private int[] weaponsBox = { Game.WIDTH/10 + Game.scale/2, Game.HEIGHT/3, (int)(Game.WIDTH/4.0), (int)(Game.HEIGHT/7)};
	/** Index responsible for selecting a weapon to attack with */
	public int weaponChooseIndex;

	public PlayerInfoGFX(Game game) {
		this.game = game;
		sizes = new int[2];

	}
	
	public void setPlayer(Player player) {
		if (player != null) {
			this.carrier = player;
			this.wallet = carrier.wallet;
			sizes[0] = wallet.weapons.size();
			sizes[1] = wallet.utilities.size();
			currentItem = wallet.getFirstWeapon();
			weaponsBox[3] = (int)(Game.HEIGHT/12) * carrier.wallet.weapons.size();
			weaponChooseIndex = 0;
		}
	}
	
	public void render(Graphics g) {
		int walletHeight = Game.HEIGHT*2/5;
		int walletWidth = Game.WIDTH;
		
		int HACx = 10; //hit avoid crit x location
		int statsX = 9*Game.scale/4;
		int expX = 4*Game.scale + Game.scale/3;
		int walletTop = Game.HEIGHT - walletHeight;
		int statSpacing = 23;
		int statStart = walletTop + 45;
		
		int itemBoxX = 7*Game.scale;
		
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, walletTop , walletWidth, walletHeight);
		Image playerImage = carrier.getImage();
		g.drawImage(playerImage, 0, walletTop + Game.scale/2, Game.scale*2, Game.scale*3, null);
		
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 18));
		g.drawString("Hit: " + carrier.hit, HACx, walletTop+Game.scale*4+(5*Game.scale/12) - 20);
		g.drawString("Avoid: " + carrier.avoid, HACx, walletTop+Game.scale*4+(5*Game.scale/12)-2);
		g.drawString("Crit: " + carrier.crit, HACx, walletTop+Game.scale*4+(5*Game.scale/12)+16);
		
		g.setFont(new Font("Times New Roman", Font.BOLD, 21));
		g.drawString("Name: " + carrier.name, statsX, walletTop+20);
		g.drawString("HP: " + carrier.currentHP + "/" + carrier.HP, statsX, statStart);
		g.drawString("EXP: " + carrier.EXP, expX, walletTop+50);
		g.drawString("Class: " + carrier.Class, expX, walletTop + 70);
		for (int i = 1; i < carrier.stats.length; i++) {
			if (carrier.equiptItem.getDamageName().equalsIgnoreCase("DMG")) {
				g.drawString(Player.StatNames[i] + ": " + carrier.stats[i], statsX, statStart + (statSpacing * i));
			} else {
				g.drawString(Player.MagStatNames[i] + ": " + carrier.stats[i], statsX, statStart + (statSpacing * i));
			}
		}
		
	//	g.drawString(carrier.skill.skillString, Game.scale*4, Game.HEIGHT-walletHeight+130);
	//	g.setFont(new Font("Times New Roman", Font.ITALIC, 13));
	//	g.drawString(carrier.skill.description, Game.scale*3 + Game.scale/2, Game.HEIGHT-walletHeight+150);
		
		g.drawRect(itemBoxX,  walletTop +10, walletWidth/2, walletHeight-40);
		g.drawString("Items", itemBoxX + walletWidth/5, Game.HEIGHT-walletHeight+25);
		g.setFont(new Font("Times New Roman", Font.BOLD, 12));
		g.drawString("Name:                                    DMG      HIT      CRIT      WGT      RNG        DUR", itemBoxX + 10, walletTop +50);
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		drawEquipped(g);
		if (inItemOptions) drawOptions(g);
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		for (int i = 0; i < wallet.weapons.size(); i++) {
			CombatItem it = wallet.weapons.get(i);
			g.drawString(it.name, itemBoxX + 10, walletTop + 70 + 20*i);
			g.drawString("" + it.damage, itemBoxX + 160, walletTop + 70 + 20*i);
			g.drawString("" + it.hit, itemBoxX + 200, walletTop + 70 + 20*i);
			g.drawString("" + it.crit, itemBoxX + 245, walletTop + 70 + 20*i);
			g.drawString("" + it.weight, itemBoxX + 295, walletTop + 70 + 20*i);
			g.drawString("" + it.range, itemBoxX + 340, walletTop + 70 + 20*i);
			g.drawString("" + it.duration, itemBoxX + 385, walletTop + 70 + 20*i);
		}

		for (int j = 0; j < wallet.utilities.size(); j++) {
			UtilityItem it = wallet.utilities.get(j);
			g.setColor(Color.white);
			g.drawString(it.name + "  (" + it.duration + ")" + "             " + it.description, itemBoxX + 10, walletTop +75+(20*wallet.weapons.size()));
		}
	}
	public void drawOptions(Graphics g) {
		int walletHeight = Game.HEIGHT*2/5;
		int walletTop = Game.HEIGHT - walletHeight;
		int itemBoxX = 11*Game.scale/2;
		int beginX = itemBoxX + 40 + Game.WIDTH/3;
		int beginY = walletTop + 30 + (20*weaponIndex);
		int height = 20;
		int width = 65;
		int thickness = 3;
		g.setFont(new Font("Times New Roman", Font.BOLD, 18));
		for (int i = 0; i < 3; i++) {
		g.setColor(Color.black);
		g.drawRect(beginX, beginY + (i * height), width, height);
		g.setColor(Color.white);
		g.fillRect(beginX+1, beginY + (i * height) + 1, width - 2, height - 2);
		g.setColor(Color.RED);
		g.drawString(itemOptions[i], beginX + 2, beginY + (i * height) + height - 4);
		}
		g.setColor(Color.blue);
		for (int i = 0; i < thickness; i++)
		g.drawRect(beginX + i, beginY + (itemOptionIndex * height) + i, width - (2*i), height - (2*i));
	}
	
	public void drawEquipped(Graphics g) {
		int walletHeight = Game.HEIGHT*2/5;
		int walletTop = Game.HEIGHT - walletHeight;
		int itemBoxX = 11*Game.scale/2;
		int width = Game.WIDTH/3 + 115;
		int height = 20;
		
		g.setColor(Color.blue);
		g.fillRect(itemBoxX + width/4 - 15, walletTop + 50 + (20*weaponIndex) + 4, width, height);
	}
	
	public void renderAdv(Graphics g) {
		
		int walletHeight = Game.HEIGHT*2/5;
		int walletWidth = Game.WIDTH;
		int weaponBoxWidth = 150;
		int walletTop = Game.HEIGHT - walletHeight;
		int statsX = 9*Game.scale/4;
		int classnLvY = walletTop + Game.scale*2 + 25;
		int weaponBoxX = 11*Game.scale/2;
		int masteryBoxX = 8*Game.scale + 10;
		int charX = 21*Game.scale/2 + 32;
		
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, walletTop , walletWidth, walletHeight);
		Image playerImage = carrier.getImage();
		g.drawImage(playerImage, 0, walletTop, Game.scale*2, Game.scale*3, null);
		
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 22));
		g.drawString(carrier.Class, 10, classnLvY);
		g.setFont(new Font("Times New Roman", Font.BOLD, 21));
		g.drawString("Level: " + carrier.level, 10, classnLvY + 25);
		g.drawString("Name: " + carrier.name, Game.scale*2, walletTop+20);
		for (int i = 0; i < carrier.growths.length; i++) {
			if (carrier.equiptItem.getDamageName().equalsIgnoreCase("DMG")) {
				g.drawString(Player.StatNames[i] + ": " + carrier.growths[i] + "%", statsX, walletTop + 45 + (25 * i));
			} else {
				g.drawString(Player.MagStatNames[i] + ": " + carrier.growths[i] + "%", statsX, walletTop + 45 + (25 * i));
			}
		}
		
		g.drawRect(weaponBoxX, walletTop+5, walletWidth/2, walletHeight-40);
		g.drawString("Weapon Skill Tree", Game.scale*5 + walletWidth/4 - 50, Game.HEIGHT-walletHeight+24);
		g.setFont(new Font("Times New Roman", Font.BOLD, 18));
		int strTop = walletTop + 60;
		int boxTop = strTop - 16;
		int distBetween = 50;
		for (int i = 0; i < Item.weaponTypes.length; i++) {
			if (this.carrier.equiptItem.getDamageName().equalsIgnoreCase("DMG"))
				g.drawString(Item.weaponTypes[i], Game.scale*7, strTop + (distBetween*i));
			else 
				g.drawString(Item.magWeaponTypes[i], Game.scale*7, strTop + (distBetween*i));
			g.drawRect(masteryBoxX, boxTop +(distBetween*i), weaponBoxWidth, 25);
			g.drawString(String.valueOf(carrier.weaponMasteriesGrade[i]), charX, strTop + (distBetween * i));
		}
		
		g.setColor(Color.cyan);
		for (int i = 0; i < 4; i++) {
			g.fillRect(masteryBoxX, boxTop + (distBetween*i), 1 + weaponBoxWidth*(carrier.weaponMasteries[i]+1)/carrier.weaponUpgrade, 25);
		}
		
		
	}
	/** Shows the screen where attacker chooses their item before attacking */
	public void renderWeaponSelection(Graphics g) {
		
		int thickness = 4;
		g.drawImage(carrier.image, playerbox[0] + playerbox[2]/6, playerbox[1] - 150, 2*playerbox[2]/3, 300, null);
		g.setColor(new Color(169,169,169));
		for (int i = 0; i < thickness; i++) {
		g.drawRect(playerbox[0] + i, playerbox[1] + i, playerbox[2] - i/2, playerbox[3] - i/2);
		g.drawRect(weaponsBox[0] + i, weaponsBox[1] + i, weaponsBox[2] - i/2, weaponsBox[3] - i/2);
		}
		g.setColor(new Color(123, 108, 227));
		g.fillRect(playerbox[0] + thickness, playerbox[1] + thickness, playerbox[2] - thickness, playerbox[3] - thickness);
		g.fillRect(weaponsBox[0] + thickness, weaponsBox[1] + thickness, weaponsBox[2] - thickness, weaponsBox[3] - thickness);
		CombatItem playerItem = (CombatItem)currentItem;
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 26));
		g.drawString(carrier.name, playerbox[0] + playerbox[2]/2 - 10, playerbox[1] + 30);
		g.drawString("Attack: " + (carrier.damage - carrier.equiptItem.damage + playerItem.damage), playerbox[0] + Game.scale/2, playerbox[1] + playerbox[3]/3 + Game.scale/2);
		g.drawString("Hit: " + (carrier.hit - carrier.equiptItem.hit + playerItem.hit), playerbox[0] + Game.scale/2, playerbox[1] + playerbox[3]/3 + Game.scale);
		g.drawString("Crit: " + (carrier.crit - carrier.equiptItem.crit + playerItem.crit), playerbox[0] + 3*Game.scale, playerbox[1] + playerbox[3]/3 + Game.scale/2);
		g.drawString("Avoid: " + carrier.avoid,  playerbox[0] + 3*Game.scale, playerbox[1] + playerbox[3]/3 + Game.scale);
		
		//where we draw the items
		int height = (int)(weaponsBox[3]/(double)carrier.wallet.weapons.size());
		for (int i = 0; i < carrier.wallet.weapons.size(); i++) {
			CombatItem it = carrier.wallet.weapons.get(i);
			if (i == weaponChooseIndex) {
				g.setColor(Color.BLUE);
				g.fillRect(weaponsBox[0], weaponsBox[1] + i *height, 
						weaponsBox[2], height);
			}
			g.setColor(Color.white);
			g.drawString(it.name, weaponsBox[0] + 20, weaponsBox[1] + i*height + height/2);
		}
		
	}
	/** Index responsible for selecting an attack weapon */
	public void incWeaponChooseIndex(int amount) {
		weaponChooseIndex += amount;
		if (weaponChooseIndex < 0) weaponChooseIndex = carrier.wallet.weapons.size()-1;
		else if (weaponChooseIndex >= carrier.wallet.weapons.size()) weaponChooseIndex = 0;
	}
	
	public void incWeaponIndex() {
		sizes[0] = wallet.weapons.size();
		sizes[1] = wallet.utilities.size();
		if (sizes[0] == 0 && sizes[1] == 0) return;
		weaponIndex++;
		if (weaponIndex >= (sizes[0] + sizes[1])) {
			weaponIndex = 0;
		}
		if (weaponIndex > sizes[0] - 1) {
			currentItem = wallet.utilities.get((weaponIndex-sizes[0]));
		} else {
			currentItem = wallet.weapons.get(weaponIndex);
		}
	}
	public void decWeaponIndex() {
		sizes[0] = wallet.weapons.size();
		sizes[1] = wallet.utilities.size();
		if (sizes[0] == 0 && sizes[1] == 0) return;
		weaponIndex--;
		if (weaponIndex < 0) {
			weaponIndex = (sizes[0] + sizes[1]) -1;
		}
		if (weaponIndex > sizes[0] - 1) {
			currentItem = wallet.utilities.get((weaponIndex-sizes[0]));
		} else {
			currentItem = wallet.weapons.get(weaponIndex);
		}
	}
	public void setItemOptions(boolean tf) {
		inItemOptions = tf;
	}
	public void incItemOptionIndex(int val) {
		itemOptionIndex += val;
		if (itemOptionIndex < 0) itemOptionIndex = 2;
		if (itemOptionIndex > 2) itemOptionIndex = 0;
	}
	/** Returns and equipts the selected item our unit will use to attack */
	public CombatItem getSelectedItem() {
		CombatItem item = carrier.wallet.weapons.get(weaponChooseIndex);
		carrier.wallet.equipt(item);
		return item;
	}
	
}
