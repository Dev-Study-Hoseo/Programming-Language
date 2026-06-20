package textTrunRPG;

public class GameCharacter {
	String name;
	int hp;
	int maxHp;
	int atk;
	
	public GameCharacter(String name, int hp, int atk) {
		this.name = name;
		this.hp = hp;
		this.maxHp = hp;
		this.atk = atk;
	}
	
	public void attack(GameCharacter target) {
		System.out.println(this.name + "(이)가 " + target.name + "을(를) 공격!");
		target.takeDamage(this.atk);		
	}
	
	public void takeDamage(int damage) {
		this.hp -= damage;
		if(this.hp < 0) {
			this.hp = 0;
		}
		System.out.println(this.name + "의 현재 체력 : " + this.hp + "/" + this.maxHp);
	}
}
