package textTrunRPG;

import java.util.ArrayList;
import java.util.HashMap;

public class Player extends GameCharacter {
	int gold;
	ArrayList inventory;
	int deathCount;
	HashMap killCount;
	
	public Player(String name) {
		super(name,  100,  15);
		this.gold = 100;
		this.inventory = new ArrayList();
		this.deathCount = 0;
		this.killCount = new HashMap();
	}
}
