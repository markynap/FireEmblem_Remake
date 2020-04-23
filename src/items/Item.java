package items;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import extras.*;
import gameMain.*;

public abstract class Item {
	/** Sword, Lances, Axes, Bows */
	public final static String[] weaponTypes = {"Swords", "Lances", "Axes", "Bows"};
	/** Fire, Ice, Earth, Dark */
	public final static String[] magWeaponTypes = {"Fire", "Ice", "Earth", "Dark"};
	/**What this item is called*/
	public String name;
	/**Physical, Magical, Healing, Utility*/
	public String category;
	/**stat used for effectiveness -- either dmg or healing amount or utility amount, 0 if no amount*/
	public int damage;
	/**all items have weight, players CON effects how many heavy items they can carry*/
	public int weight;
	/**how many times the item can be used before it is gone, negative for infinite times*/
	public int duration;
	/**The image manager that will handle rendering item images*/
	public ImageManager IM = Game.IM;
	/** The image path to the item */
	public String imagePath;
		
	public Image getImage(String path) {
		return IM.getImage(path);
	}
	public Image getImage() {
		return IM.getImage(imagePath);
	}
	public abstract String getDamageName();
	
	public int getWeaponID() {
		try {
			Scanner reader = new Scanner(new File("res//designInfo//weaponIDs"));
			String[] line;
			while (reader.hasNextLine()) {
				line = reader.nextLine().split(":");
				if (line[0].equalsIgnoreCase(name)) return Integer.valueOf(line[1]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("WEAPON ID NOT SPECIFIED FOR: " + name);
	}
	
	public static Item getItemByID(int ID) {
		try {
			Scanner reader = new Scanner(new File("res//designInfo//weaponIDs"));
			String[] line;
			while (reader.hasNextLine()) {
				line = reader.nextLine().split(":");
				if (Integer.valueOf(line[1]) == ID) return getItemByName(line[0]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new Fists();
	}
	
	public static Item getItemByName(String name) {
		
		switch (name) {
		case "Fists of Fury  ": return new Fists();
		
		case "Bronze Axe": return new BronzeAxe();
		case "Iron Axe": return new IronAxe();
		case "Steel Axe": return new SteelAxe();
		
		case "Bronze Sword": return new BronzeSword();
		case "Iron Sword": return new IronSword();
		case "Steel Sword": return new SteelSword();
		
		case "Bronze Lance": return new BronzeLance();
		case "Iron Lance": return new IronLance();
		case "Steel Lance": return new SteelLance();
		case "Javelin": return new Javelin();
		
		case "Bronze Bow": return new BronzeBow();
		case "Iron Bow": return new IronBow();
		case "Steel Bow": return new SteelBow();
		
		case "FireTome": return new FireTome();
		
		case "Staff": return new Staff();
		case "Vulnery": return new Vulnery();
		
		default:
			System.out.println("ITEM NAME NOT SPECIFIED! NAME: " + name);
			return new Fists();
		}
	}
	
}
