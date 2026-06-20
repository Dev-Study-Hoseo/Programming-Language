package textTrunRPG;

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class Main {
	static Player player;
	static Scanner scanner = new Scanner(System.in);
	static ArrayList itemList =  new ArrayList();
	
	public static void main(String[] args) {
		System.out.println("=== 턴제 RPG ===");
		System.out.println("닉네임을 입력하세요: ");
		String name = scanner.nextLine();
		
		player = new Player(name);
		
		loadItemList();
		loadGame();
		
		while(true) {
			System.out.println("\n[마을] 무엇을 하시곘습니까?");
			System.out.println("1. 사냥터");
			System.out.println("2. 스테이터스/인벤토리");
			System.out.println("3. 잡화 상점");
			System.out.println("4. 게임 저장 및 종료");
			System.out.println("메뉴 번호 선택: ");
			
			int menu = 0;
			try {
				menu = scanner.nextInt();
				scanner.nextLine();
			} catch(InputMismatchException e) {
				System.out.println("숫자만 입력해주세요.");
				scanner.nextLine();
				continue;
			}
			
			switch(menu) {
				case 1:
					startBattle();
					break;
				case 2:
					showInventory();
					break;
				case 3:
					showShop();
					break;
				case 4:
					saveGame();
					System.out.println("게임이 저장되었습니다. 게임을 종료합니다.");
					return;
				default:
					System.out.println("잘못된 번호입니다. 옳은 번호를 입력해주세요.");
			}
		}
	}
	
	public static void startBattle() {
		String monsterName = (Math.random() > 0.5) ? "슬라임" : "고블린";
		GameCharacter monster = new GameCharacter(monsterName, 40, 8);
		
		System.out.println("\n앗! 야생의 [" + monster.name + "](이)가 나타났다!");
		
		while(player.hp > 0 && monster.hp > 0) {
			System.out.println("\n[현재 상황] 내 HP: " + player.hp + " | " + monster.name + " HP: " + monster.hp);
			System.out.println("1. 공격한다 | 2. 도망친다");
			System.out.print("선택: ");
			
			int action;
			try {
				action = scanner.nextInt();
				scanner.nextLine();
			} catch(InputMismatchException e) {
				System.out.println("숫자만 입력해주세요.");
				scanner.nextLine();
				continue;
			}
			
			if(action == 1) {
				player.attack(monster);
				
				if(monster.hp <= 0) {
					System.out.println("\n축하합니다!" + monster.name + "을(를) 처치했습니다!");
					
					int crtKill = 0;
					if(player.killCount.containsKey(monster.name)) {
						crtKill = (int)player.killCount.get(monster.name);
					}
					player.killCount.put(monster.name, crtKill + 1);
					
					int rewardGold = (int)(Math.random() * 21) + 10;
					player.gold += rewardGold;
					System.out.println(rewardGold + "골드를 얻었습니다.(보유 골드: " + player.gold + ")");
					
					if(Math.random() < 0.3) {
						if(!itemList.isEmpty()) {
							int randomNum = (int)(Math.random() * itemList.size());
							Item dropItem = (Item)itemList.get(randomNum);
							player.inventory.add(new Item(dropItem.name, dropItem.effect, 0));
							System.out.println("[아이템 획득] 몬스터가 '" + dropItem.name + "'을 떨어트렸습니다.");
						}
					}
					break;
				}
				
				monster.attack(player);
				
				if(player.hp <= 0) {
					System.out.println("\n무력화되었습니다.");
					System.out.println("마을로 텔레포트합니다.");
					player.deathCount++;
					player.hp = player.maxHp;
					break;
				}
			} else {
				System.out.println("무사히 도망쳤습니다.");
				break;
			}
			
		}
	}
	
	public static void showInventory() {
		System.out.println("\n==== " + player.name + "의 정보 ====");
		System.out.println("체력: " + player.hp + "/" + player.maxHp);
		System.out.println("보유 골드: " + player.gold + "G");
		System.out.println("사망 횟수: " + player.deathCount + "회");
		System.out.println("[몬스터 처치 기록]");
		if(player.killCount.isEmpty()) {
			System.out.println(" - 아직 물리친 몬스터가 없습니다.");
		} else {
			for(Object key : player.killCount.keySet()) {
				String mobName = (String)key;
				int count = (int)player.killCount.get(mobName);
				System.out.println(" - " + mobName + ": " + count + "마리");
			}
		}
		System.out.println("------------------------------");
		System.out.println("[인벤토리 목록]");
		
		if(player.inventory.isEmpty()) {
			System.out.println("(비어 있음)");
		} else {
			for(int i = 0; i < player.inventory.size(); i++) {
				Item item = (Item)player.inventory.get(i);
				System.out.println("[" + i + "]" + item.name);
			}
			
			System.out.print("사용할 아이템 번호를 입력하세요(취소는 -1): ");
			int itemNum = -1;
			try {
				itemNum = scanner.nextInt();
				scanner.nextLine();
			} catch(InputMismatchException e) {
				System.out.println("숫자만 입력해주세요.");
				scanner.nextLine();
			}
			
			if(itemNum >= 0 && itemNum < player.inventory.size()) {
				Item usedItem = (Item)player.inventory.remove(itemNum);
				
				int finalEffect = usedItem.effect;
				
				if(finalEffect == 999) {
					finalEffect = (Math.random() > 0.5) ? 55 : -35;
				}
				
				player.hp += finalEffect;
				
				if(player.hp > player.maxHp) player.hp = player.maxHp;
				if(player.hp < 0) player.hp = 0;
				
				System.out.println("\n" + usedItem.name + "을 사용했습니다.");
				
				if(finalEffect >= 0) {
					System.out.println("체력이 " + finalEffect + " 회복되었습니다.");
				} else {
					System.out.println("윽! 독약이었습니다! 체력이 " + Math.abs(finalEffect) + "감소했습니다.");
				}
				
				if(player.hp <= 0) {
					System.out.println("무력화되었습니다. 회복합니다.");
					player.deathCount++;
					player.hp = player.maxHp;
				}
			} else {
				System.out.println("아이템 사용을 취소했습니다.");
			}
		}
	}
	
	public static void showShop() {
		int potionPrice = 30;
		
		while(true) {
			System.out.println("\n==== 잡화 상점 ====");
			System.out.println("보유 골드: " + player.gold + "G");
			
			for(int i = 0; i < itemList.size(); i++) {
				Item item = (Item)itemList.get(i);
				System.out.println((i + 1) + ". " + item.name + "구매 (" + item.price + "G)");
			}
			System.out.println((itemList.size() + 1) + ". 마을로 돌아가기");
			System.out.println("선택: ");
			
			int choice = 0;
			try {
				choice = scanner.nextInt();
				scanner.nextLine();
			} catch(InputMismatchException e) {
				System.out.println("숫자만 입력해주세요.");
				scanner.nextLine();
				continue;
			}
			
			if(choice == itemList.size() + 1) {
				System.out.println("상점을 나갑니다.");
				return;
			}
			
			if(choice >= 1 && choice <= itemList.size()) {
				Item selectItem = (Item)itemList.get(choice - 1);
				
				if(player.gold >= selectItem.price) {
					player.gold -= selectItem.price;
					player.inventory.add(new Item(selectItem.name, selectItem.effect, 0));
					System.out.println("\n[구매 완료] '" + selectItem.name + "'을 구매했습니다.");
					System.out.println("남은 골드: " + player.gold + "G");
				} else {
					System.out.println("\n[골드 부족] 골드가 부족하여 아이템을 구매할 수 없습니다.");
					System.out.println("부족한 골드: " + (selectItem.price - player.gold) + "G");
				}
			} else {
				System.out.println("잘못된 번호입니다. 옳은 번호를 입력해주세요.");
			}
		}
	}
	
	public static void saveGame() {
		try(BufferedWriter bw = new BufferedWriter(new FileWriter("savefile.txt"))) {
			bw.write(player.name + "\n");
			bw.write(player.hp + "\n");
			bw.write(player.gold + "\n");
			
			for(int i = 0; i < player.inventory.size(); i++) {
				Item item = (Item)player.inventory.get(i);
				bw.write(item.name + ":" + item.effect + ",");
			}
			bw.write("\n");
			
			bw.write(player.deathCount + "\n");
			
			for(Object key : player.killCount.keySet()) {
				String mobName = (String)key;
				int count = (int)player.killCount.get(mobName);
				bw.write(mobName + ":" + count + ",");
			}
			bw.write("\n");
		} catch(IOException e) {
			System.out.println("게임 저장 중 에러가 발생했습니다: " + e.getMessage());
		}
	}
	
	public static void loadGame() {
		File file = new File("savefile.txt");
		if(!file.exists()) return;
		
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			String savedName = br.readLine();
			int savedHp = Integer.parseInt(br.readLine());
			int savedGold = Integer.parseInt(br.readLine());
			String savedItems = br.readLine();
			String savedDeath = br.readLine();
			String savedKill = br.readLine();
			
			if(savedName != null) {
				player.name = savedName;
				player.hp = savedHp;
				player.gold = savedGold;
				
				player.inventory.clear();
				if(savedItems != null && !savedItems.trim().isEmpty()) {
					String[] itemsArray = savedItems.split(",");
					for(String item : itemsArray) {
						if(!item.trim().isEmpty()) {
							String[] itemInfo = item.split(":");
							String name = itemInfo[0];
							int effect = Integer.parseInt(itemInfo[1]);
							
							player.inventory.add(new Item(name, effect, 0));
						}
					}
				}
				
				if(savedDeath != null) {
					player.deathCount = Integer.parseInt(savedDeath);
				}
				
				if(savedKill != null && !savedKill.trim().isEmpty()) {
					player.killCount.clear();
					String[] killArr = savedKill.split(",");
					for(String killStr : killArr) {
						if(!killStr.trim().isEmpty()) {
							String[] killInfo = killStr.split(":");
							String mobName = killInfo[0];
							int count = Integer.parseInt(killInfo[1]);
							player.killCount.put(mobName, count);
						}
					}
				}
				System.out.println("\n[시스템] 이전 저장 데이터를 성공적으로 불러왔습니다.");
			}
		} catch(Exception e) {
			System.out.println("[시스템] 저장 파일을 읽는 중 오류가 발생해 새 게임으로 시작합니다.");
		}
	}
	
	public static void loadItemList() {
		File file = new File("item_list.txt");
		if(!file.exists()) return;
		
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = br.readLine()) != null) {
				if(line.trim().isEmpty()) continue;
			
				String[] info = line.split(":");
				
				String name = info[0];
				int effect = Integer.parseInt(info[1]);
				int price = Integer.parseInt(info[2]);
				
				itemList.add(new Item(name, effect, price));
			}
			System.out.println("[시스템] 아이템 리스트 총 " + itemList.size() + "개를 성공적으로 불러왔습니다.");
		} catch(Exception e) {
			System.out.println("[시스템] 아이템 리스트를 읽는 중 오류가 발생했습니다: " + e.getMessage());
		}
	}
}
