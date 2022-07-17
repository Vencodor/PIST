package PIST.Quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.xml.crypto.dsig.keyinfo.PGPData;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import PIST.Pist;
import PIST.DataObject.User.UserRecordDTO;
import PIST.Land.PistLandManager;
import PIST.Quest.DataObject.MyQuestDTO;
import PIST.Quest.DataObject.QuestDTO;
import PIST.ServerManager.PistPlugin;
import PIST.UserManager.PistUser;
import PIST.Utility.RepeatingTask;

public class PistQuest extends PistPlugin implements CommandExecutor, Listener {

	public static HashMap<Integer, QuestDTO> q = new HashMap<Integer, QuestDTO>();
	public static HashMap<UUID, ArrayList<MyQuestDTO>> userQ = new HashMap<UUID, ArrayList<MyQuestDTO>>();
	private static Inventory baseInv = Bukkit.createInventory(null, 27, "");
	
	private static HashMap<String, HashMap<String, Integer>> editMode = new HashMap<String, HashMap<String, Integer>>();

	int id=0;

	Pist plugin;

	public PistQuest(Pist ser) {
		super(ser);
		plugin = ser;
	}

	public void settingBase() {
		ItemStack empty = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7), "",
				new ArrayList<String>());
		for (int i = 0; i < 9; i++)
			baseInv.setItem(i, empty);
		baseInv.setItem(17, empty);
		for (int i = 18; i < 27; i++)
			baseInv.setItem(i, empty);

		List<String> lore = new ArrayList<String>();
		lore.add(white + " ");
		lore.add(gray + "퀘스트를 클릭하면 퀘스트" + green + "정보" + gray + " 창을 열 수 있습니다.");
		lore.add(gray + "퀘스트 " + red + "포기" + gray + "는 정보 창에서 가능합니다.");
		lore.add(gray + "퀘스트를 완수하시면 퀘스트를 받은 NPC에게 가서 " + purple + "보상" + gray + "을 획득하세요.");
		lore.add(white + " ");
		baseInv.setItem(9, createItem(new ItemStack(Material.SIGN), " ", lore));
	}

	public Inventory getInv(Player p) {
		Inventory i = Bukkit.createInventory(null, 27,
				gray + "[" + red + bold + " Q " + gray + "] " + green + p.getName() + gray + " 님의 퀘스트 목록");
		i.setContents(baseInv.getContents());
		ArrayList<MyQuestDTO> myQ = userQ.get(p.getUniqueId());
		if (myQ != null && myQ.size() > 0) {
			int count = 0;
			for (MyQuestDTO a : myQ) {
				if(a.isReward()) continue; //보상받은 퀘스트는 건너뛰기
				QuestDTO quest = getQuest(a.getQuestCode()); // 받은 퀘스트 확인
				if (!a.isClear())
					i.setItem(count + 10, createItem(new ItemStack(Material.BOOK_AND_QUILL),
							gray + quest.getTitle() + dgray + "#" + quest.getCode(), getLore(p, quest, a, true))); //클리어 하지 않았다면
				else
					i.setItem(count + 10, createItem(new ItemStack(Material.WRITTEN_BOOK), quest.getTitle() + dgray + "#" + quest.getCode(),
							getLore(p, quest, a, true))); // 클리어 했다면
				count++;
			}
		}
		return i;
	}

	public Inventory getInfoInv(Player p, MyQuestDTO myQ,boolean npcClick) {
		QuestDTO quest = getQuest(myQ.getQuestCode());
		Inventory i = Bukkit.createInventory(null, 27,
				gray + "[" + red + bold + " Q " + gray + "] " + white + quest.getTitle()+dgray+"#"+quest.getCode());
		
		if(!myQ.isReward()&&myQ.isClear()&&hasFullItem(p,myQ)&&npcClick) { //보상은 못받았는데 클리어 했다면 && 필요 아이템을 다 가지고 있다면
			//아이템 다 가지고 있는지 체크 , 회수하기
			p.updateInventory();
			List<String> lore1 = new ArrayList<String>();
			lore1.add(white+" ");
			lore1.add(orange+" [ 보상을 받으려면 클릭하세요 ] ");
			lore1.add(white+" ");
			i.setItem(13, createItem(new ItemStack(Material.CHEST), yellow+"보상 수령하기", lore1));
		} else { //정보
			ItemStack empty = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7), "",
					new ArrayList<String>());
			for (int k = 0; k < 27; k++)
				i.setItem(k, empty);
			i.setItem(4, createItem(new ItemStack(Material.BOOK), gray + quest.getTitle(), getLore(p, quest, myQ, false)));
		
			List<String> lore1 = new ArrayList<String>();
			lore1.add(white + " ");
			lore1.add(dgray + bold + "  수령 장소  " + orange + myQ.getLand());
			lore1.add(dgray + bold + "  수령일  " + orange + showDate(myQ.getDate()));
			lore1.add(dgray + bold + "  의뢰인  " + orange + myQ.getNpcName());
			lore1.add(white + " ");
			i.setItem(11, createItem(new ItemStack(Material.SIGN), gray + "정보", lore1));
	
			List<String> lore5 = new ArrayList<String>();
			// lore5.add(white+" ");
			lore5.add(gray + "현재 진행중인 퀘스트를 포기합니다.");
			lore5.add(red + "해당 퀘스트의 진행도를 잃게 됩니다.");
			lore5.add(white + " ");
			lore5.add(gray + " [ 진행하려면 SHIFT + CLICK 을 하세요 ]  ");
			i.setItem(22, createItem(new ItemStack(Material.BARRIER), red + bold + "포기하기" + dgray + "#" + quest.getCode(),
					lore5));
		}
		return i;
	}
	
	public boolean hasFullItem(Player p,MyQuestDTO a) {
		QuestDTO quest = getQuest(a.getQuestCode());
		for (ItemStack n : quest.getNeedItems()) {
			int needCount = n.getAmount();
			//String needName = "";
			int haveItem = getItemCount(p, n);
			/*
			try {
				needName = n.getItemMeta().getDisplayName();
			} catch (NullPointerException e) {
				e.printStackTrace();
				needName = red + bold + "ERROR!";
				haveItem = 0;
			}
			*/
			if (haveItem >= needCount) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	private List<String> getLore(Player p, QuestDTO quest, MyQuestDTO a, boolean notInfo) {
		List<String> lore = new ArrayList<String>();
		int count = 0;
		lore.add(white + " ");
		lore.add(gray + " 의뢰인 : " + a.getNpcName());
		lore.add(gray + " 유형 : " + quest.getType());
		lore.add(white + " ");
		lore.add(orange + bold + "  달성조건");
		for (String m : quest.getNeedMobs().keySet()) {
			HashMap<String, Integer> needMob = quest.getNeedMobs();
			int needCount = needMob.get(m);
			if ((a.getMobKill().get(m)==null?0:a.getMobKill().get(m))>=(quest.getNeedMobs().get(m)==null?0:quest.getNeedMobs().get(m))) {
				lore.add(dgray + "   - " + line + "'" + gray + bold + line + m + dgray + "' " + green + line + needCount
						+ gray + line + "마리 잡기 " + dgray + line + "(" + needCount + "/" + needCount + ")");
				count++;
			} else {
				int killed = a.getMobKill().get(m) == null ? 0 : a.getMobKill().get(m);
				lore.add(dgray + "   - " + "'" + gray + bold + m + dgray + "' " + green + needCount + gray + "마리 잡기 "
						+ dgray + "(" + killed + "/" + needCount + ")");
			}
		}
		for (ItemStack n : quest.getNeedItems()) {
			int needCount = n.getAmount();
			String needName = "";
			int haveItem = getItemCount(p, n);
			try {
				needName = n.getItemMeta().getDisplayName();
			} catch (NullPointerException e) {
				e.printStackTrace();
				needName = red + bold + "ERROR!";
				haveItem = 0;
			}
			if (haveItem >= needCount) {
				lore.add(dgray + "   - " + line + "'" + gray + bold + line + needName + dgray + line + "' " + green
						+ line + needCount + gray + line + "개 가져오기 " + dgray + line + "(" + haveItem + "/" + needCount
						+ ")");
				count++;
			} else {
				lore.add(dgray + "   - " + "'" + gray + bold + needName + dgray + "' " + green + needCount + gray
						+ "개 가져오기 " + dgray + "(" + haveItem + "/" + needCount + ")");
			}
		}
		for (Location l : quest.getNeedLoc()) {
			if (a.getLocation().get(l) != null && a.getLocation().get(l)) { // 해당 좌표 퀘스트를 깨면
				lore.add(dgray + "   - " + line + "'" + gray + bold + line + "x " + l.getBlockX() + ", y "
						+ l.getBlockY() + ", z " + l.getBlockZ() + dgray + line + "' " + gray + line + "도착하기 " + green
						+ bold + line + "성공");
				count++;
			} else {
				lore.add(dgray + "   - " + "'" + gray + bold + "x " + l.getBlockX() + ", y " + l.getBlockY() + ", z "
						+ l.getBlockZ() + dgray + "' " + gray + "도착하기");
			}
		}
		if (notInfo) {
			lore.add(white + " ");
			lore.add(red + " [ 추가로 정보를 보려면 클릭하세요 ]  ");
		}
		
		int needCount = (quest.getNeedItems()==null?0:quest.getNeedItems().size())+
				(quest.getNeedLoc()==null?0:quest.getNeedLoc().size())+
				(quest.getNeedMobs()==null?0:quest.getNeedMobs().size());
		
		if(count>=needCount&&!a.isClear()) { //아직 클리어 하지 않았고 클리어 조건이 충족되면
			p.sendMessage(green+"퀘스트 "+orange+quest.getTitle()+gray+"를 클리어 하였습니다!");
			a.setClear(true);
		}
		return lore;
	}

	public int getItemCount(Player p, ItemStack item) {
		if (item == null)
			return 0;
		int count = 0;
		ItemStack itemClone = item.clone();
		itemClone.setAmount(1);
		for (ItemStack a : p.getInventory().getContents()) {
			if(a==null) continue;
			try {
				ItemStack aClone = a.clone();
				aClone.setAmount(1);
				if (itemClone.equals(aClone)) {
					count = count + a.getAmount();
				}
			} catch(NullPointerException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	public QuestDTO getQuest(int code) {
		return q.get(code);
	}

	public MyQuestDTO getMyQuest(Player p, int code) {
		ArrayList<MyQuestDTO> list = userQ.get(p.getUniqueId());
		if (list == null)
			return null;
		if (list.size() == 0)
			return null;
		for (MyQuestDTO a : list) {
			if (a.getQuestCode() == code) {
				return a;
			}
		}
		return null;
	}

	public void addQuest(Player p, QuestDTO quest) {
		UUID uid = p.getUniqueId();
		if (userQ.get(uid) == null)
			userQ.put(uid, new ArrayList<MyQuestDTO>());
		ArrayList<MyQuestDTO> qList = userQ.get(uid);

		MyQuestDTO m = new MyQuestDTO();
		PistLandManager land = new PistLandManager(plugin);

		m.setQuestCode(quest.getCode());
		m.setLand(land.getPlayerAreaName(p));
		m.setNpcName(quest.getName());
		qList.add(m);
	}
	
	public void sendProgress(Player p,MyQuestDTO a) {
		List<String> lore = new ArrayList<String>();
		QuestDTO quest = getQuest(a.getQuestCode());
		int count = 0;
		
		for (String m : quest.getNeedMobs().keySet()) {
			HashMap<String, Integer> needMob = quest.getNeedMobs();
			int needCount = needMob.get(m);
			if ((a.getMobKill().get(m)==null?0:a.getMobKill().get(m))>=(quest.getNeedMobs().get(m)==null?0:quest.getNeedMobs().get(m))) { //만약 몹 잡기 수치를 넘기면
				lore.add(dgray+" " + line + "'" + gray + bold + line + m + dgray + "' " + green + line + needCount
						+ gray + line + "마리 잡기 " + dgray + line + "(" + needCount + "/" + needCount + ")");
				count++;
			} else {
				int killed = a.getMobKill().get(m) == null ? 0 : a.getMobKill().get(m);
				lore.add(dgray+" " + "'" + gray + bold + m + dgray + "' " + green + needCount + gray + "마리 잡기 "
						+ dgray + "(" + killed + "/" + needCount + ")");
			}
		}
		for (ItemStack n : quest.getNeedItems()) {
			int needCount = n.getAmount();
			String needName = "";
			int haveItem = getItemCount(p, n);
			try {
				needName = n.getItemMeta().getDisplayName();
			} catch (NullPointerException e) {
				e.printStackTrace();
				needName = red + bold + "ERROR!";
				haveItem = 0;
			}
			if (haveItem >= needCount) {
				lore.add(dgray + " " + line + "'" + gray + bold + line + needName + dgray + line + "' " + green
						+ line + needCount + gray + line + "개 가져오기 " + dgray + line + "(" + haveItem + "/" + needCount
						+ ")");
				count++;
			} else {
				lore.add(dgray + " " + "'" + gray + bold + needName + dgray + "' " + green + needCount + gray
						+ "개 가져오기 " + dgray + "(" + haveItem + "/" + needCount + ")");
			}
		}
		for (Location l : quest.getNeedLoc()) {
			if (a.getLocation().get(l) != null && a.getLocation().get(l)) { // 해당 좌표 퀘스트를 깨면
				lore.add(dgray + " " + line + "'" + gray + bold + line + "x " + l.getBlockX() + ", y "
						+ l.getBlockY() + ", z " + l.getBlockZ() + dgray + line + "' " + gray + line + "도착하기 " + green
						+ bold + line + "성공");
				count++;
			} else {
				lore.add(dgray + " " + "'" + gray + bold + "x " + l.getBlockX() + ", y " + l.getBlockY() + ", z "
						+ l.getBlockZ() + dgray + "' " + gray + "도착하기");
			}
		}
		if(lore.size()==0)
			return;
		p.sendMessage(dgray+line+bold+"---- "+dgreen+bold+quest.getTitle()+dgray+line+bold+" ----");
		p.sendMessage(white+" ");
		for(String b : lore) {
			p.sendMessage(b);
		}
		p.sendMessage(white+" ");
		int needCount = (quest.getNeedItems()==null?0:quest.getNeedItems().size())+
				(quest.getNeedLoc()==null?0:quest.getNeedLoc().size())+
				(quest.getNeedMobs()==null?0:quest.getNeedMobs().size());
		
		if(count>=needCount&&!a.isClear()) { //아직 클리어 하지 않았고 클리어 조건이 충족되면
			p.sendMessage(green+"퀘스트 "+orange+quest.getTitle()+gray+"를 클리어 하였습니다!");
			a.setClear(true);
		}
	}
	
	
	@EventHandler
	public void killMob(EntityDeathEvent e) {
		if(!(e.getEntity().getKiller() instanceof Player)) return; 
		//만약 플레이어가 엔티티를 죽인거라면
		Player p = e.getEntity().getKiller();
		String entityN = e.getEntity().getName();
		ArrayList<MyQuestDTO> listQ = userQ.get(p.getUniqueId());
		if(listQ == null) return;
		for(MyQuestDTO a : listQ) {
			QuestDTO quest = getQuest(a.getQuestCode());
			if(quest.getNeedMobs().size()==0) return;
			for(String b : quest.getNeedMobs().keySet()) {
				if(entityN.contains(b)) { //만약 죽인 몹이 퀘스트 - 필요 몬스터에 포함된다면
					HashMap<String,Integer> mob = a.getMobKill();
					mob.put(b, mob.get(b)==null?1:mob.get(b)+1);
					//if(mob.get(entityN)>=quest.getNeedMobs().get(b))
					sendProgress(p,a);
				}
			}
			
		}
	}
	/* 나중에 아이템 안떨구게 할거라서 일단 패스
	@EventHandler
	public void pickUp(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItem().getItemStack();
		ArrayList<MyQuestDTO> listQ = userQ.get(p.getUniqueId());
		if(listQ == null) return;
		for(MyQuestDTO a : listQ) {
			QuestDTO quest = getQuest(a.getQuestCode());
			if(quest.getNeedItems().size()==0) return;
			for(ItemStack b : quest.getNeedItems()) {
				if(b==null) return;
				ItemStack bClone = b.clone();
				bClone.setAmount(1);
				
			}
		}
	}
	*/
	@EventHandler
	public void clickInv(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		Player p = (Player) e.getWhoClicked();
		if (item == null)
			return;
		if (e.getInventory().getTitle().contains(gray + "[" + red + bold + " Q " + gray + "] ")) {
			e.setCancelled(true);
			if (isContainsLore(item, "추가로 정보를 보려면 클릭하세요")) {
				int code = 0;
				try {
					code = Integer.parseInt(item.getItemMeta().getDisplayName().split("#", 2)[1]);
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
					return;
				} catch (Exception e2) {
					e2.printStackTrace();
					return;
				}
				p.openInventory(getInfoInv(p, getMyQuest(p, code),false));
			} else if (isContainsDisplay(item, "포기하기")) {
				if (e.isShiftClick()) {
					int code = 0;
					try {
						code = Integer.parseInt(item.getItemMeta().getDisplayName().split("#", 2)[1]);
					} catch (NumberFormatException e1) {
						e1.printStackTrace();
						return;
					} catch (Exception e2) {
						e2.printStackTrace();
						return;
					}
					ArrayList<MyQuestDTO> listQ = userQ.get(p.getUniqueId());
					if (listQ.size() == 0)
						return;
					for (int k = 0; k < listQ.size(); k++) {
						if (listQ.get(k).getQuestCode() == code) {
							listQ.remove(k);
							p.sendMessage(red + "당신은 퀘스트 " + bold + getQuest(code).getTitle() + red + "을 포기하셨습니다.");
							p.closeInventory();
						}
					}

				}
			} else if (isContainsLore(item, "클릭 시 퀘스트를 수락합니다")) {
				ArrayList<MyQuestDTO> myList = userQ.get(p.getUniqueId());
				if (myList != null && myList.size() >= 7) {
					p.closeInventory();
					p.sendMessage(red + "퀘스트를 7개 이상 받을 수 없습니다.");
					return;
				}
				int code = 0;
				try {
					code = Integer.parseInt(item.getItemMeta().getDisplayName().split("#", 2)[1]);
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
					return;
				} catch (Exception e2) {
					e2.printStackTrace();
					return;
				}
				QuestDTO quest = getQuest(code);
				addQuest(p, quest);
				p.closeInventory();
				String[] thxMsg = { "정말? 고마워!", "고맙다", "잘 부탁해", "고마워", "너만 믿고있을게", "정말 고마워." };
				Random rnd = new Random();
				p.sendTitle(dgray + bold + "[ " + yellow + quest.getName() + dgray + bold + " ]",
						thxMsg[rnd.nextInt(thxMsg.length)], 10, 20, 10);
			} else if(isContainsLore(item, "진행하려면 클릭하세요")) {
				try {
					int code = Integer.parseInt(e.getInventory().getTitle().split("#")[1]);
					int page = Integer.parseInt(e.getInventory().getTitle().split("#")[0].split("@")[1]);
					QuestDTO quest = getQuest(code);
					ArrayList<String> msgs = quest.getMessages();
					if(msgs!=null&&msgs.size()>page+1) { //만약 다음 대사가 있다면
						showMessages(p, msgs.get(page+1), quest.getTitle(), code, page+1);
					} else {
						openAllowPage(p, quest);
					}
				} catch(NumberFormatException e1) {
					p.sendMessage(red+"숫자 변환 오류발생");
				} catch(Exception e2) {
					p.sendMessage(red+"오류 발생");
				}
			} else if(isContainsDisplay(item, "대화 건너뛰기")) {
				try {
					int code = Integer.parseInt(e.getInventory().getTitle().split("#")[1]);
					openAllowPage(p,getQuest(code));
				} catch(NumberFormatException e1) {
					p.sendMessage(red+"숫자 변환 오류발생");
				} catch(Exception e2) {
					p.sendMessage(red+"오류 발생");
				}
			} else if(isContainsLore(item, "보상을 받으려면 클릭하세요")) {
				try {
					int code = Integer.parseInt(e.getInventory().getTitle().split("#")[1]);
					QuestDTO quest = getQuest(code);
					p.closeInventory();
					if(!hasFullItem(p,getMyQuest(p, code))) {
						p.sendMessage(red+"당신은 퀘스트를 완전히 깨지 않았습니다");
						return;
					}
					for(ItemStack n : quest.getNeedItems()) {
						p.getInventory().removeItem(n);
					}
					getMyQuest(p, code).setReward(true);
					for(ItemStack a : quest.getRewardItem()) {
						if(a==null) continue;
						p.getInventory().addItem(a);
					}
					UserRecordDTO user = PistUser.getInfo(p);
					user.setMoney(user.getMoney()+quest.getRewardMoney());
					user.setLevelUp(user.getLevelUp()+quest.getRewardExp());
					p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1F, 1F);
				} catch(NumberFormatException e1) {
					p.sendMessage(red+"숫자 변환 오류발생");
				} catch(Exception e2) {
					p.sendMessage(red+"오류 발생");
					e2.printStackTrace();
				}
				
			}
		}
	}

	@EventHandler
	public void clickQuestNPC(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		String cName = e.getRightClicked().getName();
		for (QuestDTO a : q.values()) {
			if (cName.contains(a.getName())) {
				MyQuestDTO hasQ = getMyQuest(p, a.getCode());
				if (hasQ != null) { // 만약 이미 같은 퀘스트를 진행하고 있다면
					if(!hasQ.isReward()) //만약 보상을 받지 않았다면
						p.openInventory(getInfoInv(p, hasQ,true));
					else { //보상을 받았다면
						p.sendMessage(red + "당신은 이미 이 퀘스트를 진행 하였습니다!");
						p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6F, 0.1F);
						return;
					}
				} else { // 만약 퀘스트를 처음 받는다면
					if (a.getType().equals("메인")) { // 만약 메인 퀘스트를 이미 진행하고 있다면
						if (userQ.get(p.getUniqueId()) != null) {
							for (MyQuestDTO b : userQ.get(p.getUniqueId())) {
								if (getQuest(b.getQuestCode()).getType().equals("메인")) {
									p.sendMessage(red + "메인 퀘스트를 두개 이상 진행할 수 없습니다!");
									p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6F, 0.1F);
									return;
								}
							}
						}
					}
					if(a.getMessages().size()!=0) { //만약 대화가 설정되어 있다면
						showMessages(p, a.getName()+":"+orange+a.getName()+" > "+a.getTitle(), a.getTitle(), a.getCode(), -1);
					} else {
						p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.8F);
						openAllowPage(p, a);
					}
					return;
				}
			}
		}
	}
	
	public void openAllowPage(Player p,QuestDTO a) {
		Inventory i = Bukkit.createInventory(null, 27,
				gray + "[" + red + bold + " Q " + gray + "] " + orange + a.getTitle());
		List<String> lore = new ArrayList<String>();
		lore.add(white + " ");
		lore.add(gray + " 의뢰인 : " + a.getName());
		lore.add(gray + " 유형 : " + a.getType());
		lore.add(white + " ");
		lore.add(orange + bold + "  달성조건");
		for (String m : a.getNeedMobs().keySet()) {
			HashMap<String, Integer> needMob = a.getNeedMobs();
			int needCount = needMob.get(m);
			lore.add(dgray + "   - " + "'" + gray + bold + m + dgray + "' " + green + needCount + gray
					+ "마리 잡기 ");
		}
		for (ItemStack n : a.getNeedItems()) {
			int needCount = n.getAmount();
			String needName = "";
			try {
				needName = n.getItemMeta().getDisplayName();
			} catch (NullPointerException e1) {
				e1.printStackTrace();
				needName = red + bold + "ERROR!";
			}

			lore.add(dgray + "   - " + "'" + gray + bold + needName + dgray + "' " + green + needCount
					+ gray + "개 가져오기 ");
		}
		for (Location l : a.getNeedLoc()) {
			lore.add(dgray + "   - " + "'" + gray + bold + "x " + l.getBlockX() + ", y " + l.getBlockY()
					+ ", z " + l.getBlockZ() + dgray + "' " + gray + "도착하기");
		}
		lore.add(white + " ");
		lore.add(gray + " [ 클릭 시 퀘스트를 수락합니다 ] ");
		i.setItem(13, createItem(new ItemStack(Material.BOOK_AND_QUILL),
				aqua + a.getName() + dgray + "#" + a.getCode(), lore));
		p.openInventory(i);
	}

	public void showMessages(Player p, String msg, String title, int code,int page) {
		Inventory i = Bukkit.createInventory(null, 27, gray + "[" + red + bold + " Q " + gray + "] " + orange + title+dgray+"@"+page+"#"+code);
		List<String> lore = new ArrayList<String>();
		lore.add(white + " ");
		lore.add(white + " ");
		lore.add(white + " ");
		
		ItemStack head = getHead(p);
		i.setItem(13, createItem(head, "", lore));
		
		i.setItem(15, createItem(new ItemStack(Material.SIGN),gray+"대화 건너뛰기",new ArrayList<String>()));
		
		p.openInventory(i);
		
		String name = msg.split(":", 2)[0];
		String message = msg.split(":",2)[1];
		
		new RepeatingTask(plugin,1,1) {
			int count;
			@Override
			public void run() {
				if (count > message.length()) {
					lore.add(orange+" [ 진행하려면 클릭하세요 ] ");
					i.setItem(13, createItem(head, green+name, lore));
					p.updateInventory();
					canncel();
				} else {
					p.playSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 0.2F, 1.6F);
					lore.set(1, gray+" "+message.substring(0,count)+" ");
					i.setItem(13, createItem(head, orange+name, lore));
					p.updateInventory();
					count++;
				}
			}
		};
		
	}
	

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (e.getTo().distance(e.getFrom()) > 0.01) {
			Player p = e.getPlayer();
			Location pLoc = p.getLocation();
			ArrayList<MyQuestDTO> listQ = userQ.get(p.getUniqueId());
			if(listQ==null) return;
			for(MyQuestDTO a : listQ) {
				QuestDTO quest = getQuest(a.getQuestCode());
				ArrayList<Location> loc = quest.getNeedLoc();
				for(Location b : loc) {
					if(a.getLocation().get(b)!=null&&a.getLocation().get(b)) return;
					if(pLoc.distance(b)<2) {
						HashMap<Location,Boolean> clearLoc = a.getLocation();
						clearLoc.put(b, true);
						sendProgress(p, a);
					}
				}
			}
		}
	}

	@EventHandler
	public void onChat(PlayerChatEvent e) {
		Player p = e.getPlayer();
		if (editMode.get(p.getName()) != null) {
			e.setCancelled(true);
			if (e.getMessage().equals("취소")) {
				editMode.remove(p.getName());
			} else {
				String msg = e.getMessage().replace("&", "§").replace("-", " ");
				String type = "";
				int code = 0;
				for (String a : editMode.get(p.getName()).keySet()) {
					type = a;
					code = editMode.get(p.getName()).get(a);
					break;
				}
				QuestDTO quest = getQuest(code);
				if (type.equals("제목")) {
					quest.setTitle(msg);
					p.sendMessage(gray + "제목을 " + msg + gray + "으로 설정하였습니다.");
					editMode.remove(p.getName());
				} else if (type.equals("이름")) {
					quest.setName(msg);
					p.sendMessage(gray + "NPC이름을 " + msg + gray + "으로 설정하였습니다.");
					editMode.remove(p.getName());
				} else if (type.equals("대화")) {
					if (!msg.contains(":")) {
						p.sendMessage(gray + "<대상>:<할말> 의 형식으로 작성하세요.");
						p.sendMessage(red + "편집모드가 취소됩니다.");
						editMode.remove(p.getName());
						return;
					}
					quest.getMessages().add(msg);
					p.sendMessage(gray + msg.split(":")[0] + " >> " + orange + msg.split(":")[1]);
				} else {
					editMode.remove(p.getName());
				}
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		Inventory inv = e.getInventory();
		Player p = (Player) e.getPlayer();
		if (inv.getTitle().contains("필요아이템 설정")) {
			int code = 0;
			try {
				code = Integer.parseInt(inv.getTitle().split("#")[0]);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
				p.sendMessage(red + "저장에 문제가 발생하였습니다.");
				return;
			}
			ArrayList<ItemStack> need = new ArrayList<ItemStack>();
			for (ItemStack a : inv.getContents()) {
				if (a != null) {
					need.add(a);
				}
			}
			getQuest(code).setNeedItems(need);
			p.sendMessage(green + "저장되었습니다.");
		} else if (inv.getTitle().contains("보상아이템 설정")) {
			int code = 0;
			try {
				code = Integer.parseInt(inv.getTitle().split("#")[0]);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
				p.sendMessage(red + "저장에 문제가 발생하였습니다.");
				return;
			}
			ArrayList<ItemStack> reward = new ArrayList<ItemStack>();
			for (ItemStack a : inv.getContents()) {
				reward.add(a);
			}
			getQuest(code).setRewardItem(reward);
			p.sendMessage(green + "저장되었습니다.");
		} else if(inv.getTitle().contains(gray + "[" + aqua + bold + " Q " + gray + "] ")) { //해당 창만 특별하게 색을 바꿔놈
			
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;
		if (label.equalsIgnoreCase("Q") || label.equalsIgnoreCase("퀘스트")) {
			if (!p.isOp() || args.length == 0) {
				p.openInventory(getInv(p));
			} else {
				if (args[0].equals("생성")) {
					q.put(q.size(), new QuestDTO());
					int code = q.size() - 1;
					q.get(code).setCode(code);
					p.sendMessage(gray + "퀘스트가 생성되었습니다. " + green + "코드 " + code);
				} else if (args[0].equals("테스트")) {
					//showMessages(p, "여승기 등드름 만져보고싶다", "등드름", 0);
				} else if (args[0].equals("목록")) {
					for (QuestDTO a : q.values()) {
						p.sendMessage(gray + a.getCode() + "# 이름 : " + a.getName());
					}
				} else {
					if (args.length <= 1) {
						p.sendMessage(red + "편집할 퀘스트의 코드를 입력하세요.");
						p.sendMessage(gray + "/Q " + green + "생성");
						p.sendMessage(gray + "/Q " + green + "[제목/이름/타입/대화/필요/보상] <코드>");
						p.sendMessage(gray + "/Q " + green + "목록");
						p.sendMessage(gray + "/Q " + green + "테스트");
						p.sendMessage(gray + " ( 이름 = NPC이름 )");
						return true;
					}
					int code = -1;
					try {
						code = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						p.sendMessage(red + "코드에는 숫자만 입력 가능합니다.");
						return true;
					}
					if (q.get(code) == null) {
						p.sendMessage(red + "해당 코드와 일치하는 퀘스트가 없습니다.");
						return true;
					}
					QuestDTO quest = q.get(code);
					HashMap<String, Integer> hash = new HashMap<String, Integer>();
					if (args[0].equals("제목")) {
						hash.put("제목", code);
						editMode.put(p.getName(), hash);
						p.sendMessage(gray + "제목 편집모드에 들어오셨습니다.");
						p.sendMessage(gray + "채팅창에 제목을 입력하세요. " + red + "취소" + gray + "를 입력하면 취소됩니다.");
					} else if (args[0].equals("이름")) {
						hash.put("이름", code);
						editMode.put(p.getName(), hash);
						p.sendMessage(gray + "퀘스트 NPC이름 편집모드에 들어오셨습니다.");
						p.sendMessage(gray + "채팅창에 NPC이름을 입력하세요. " + red + "취소" + gray + "를 입력하면 취소됩니다.");
					} else if (args[0].equals("타입")) {
						if (args.length <= 2) {
							p.sendMessage(gray + "/Q 타입 <코드> " + red + "[메인/서브/이벤트]");
						} else {
							if (args[2].equals("메인") || args[2].equals("서브") || args[2].equals("이벤트")) {
								quest.setType(args[2]);
							} else {
								p.sendMessage(gray + "/Q 타입 <코드> " + red + "[메인/서브/이벤트]");
							}
						}
					} else if (args[0].equals("대화")) {
						if (args.length <= 2) {
							p.sendMessage(gray + "/Q 대화 <코드> " + red + "[목록/추가/제거]");
						} else {
							if (args[2].equals("목록")) { // 구분자 .split 로 누가 말하는지 표시하도록
								if (quest.getMessages().size() == 0) {
									p.sendMessage(red + "대화가 설정되지 않았습니다.");
									return true;
								}
								int count = 0;
								for (String a : quest.getMessages()) {
									p.sendMessage(gray + count + "줄 - " + a);
									count++;
								}
							} else if (args[2].equals("추가")) {
								hash.put("대화", code);
								editMode.put(p.getName(), hash);
								p.sendMessage(gray + "대화 추가 편집모드에 들어오셨습니다.");
								p.sendMessage(gray + "채팅창에 대화를 입력하세요. " + red + "취소" + gray + "를 입력하면 취소됩니다.");
								p.sendMessage(gray + " ( 꼭 <대상>:<할 말> 의 형태로 입력하세요 ) ");
							} else if (args[2].equals("제거")) {
								if (args.length <= 3) {
									p.sendMessage(red + "/Q 대화 <코드> 제거 <줄 수>");
								} else {
									int line;
									try {
										line = Integer.parseInt(args[3]);
									} catch (NumberFormatException e) {
										p.sendMessage(red + "숫자만 입력하세요.");
										return true;
									}
									if (quest.getMessages().size() - 1 < line) {
										p.sendMessage(gray + "줄 수는 " + red + (quest.getMessages().size() - 1) + gray
												+ "줄이 넘어가서는 안됩니다.");
									} else {
										ArrayList<String> msg = quest.getMessages();
										p.sendMessage(
												gray + "성공적으로 '" + green + msg.get(line) + gray + "' 대화를 삭제하였습니다.");
										msg.remove(line);
									}
								}
							} else {
								p.sendMessage(gray + "/Q 타입 <코드> " + red + "[메인/서브/이벤트]");
							}
						}
					} else if (args[0].equals("필요")) {
						if (args.length <= 2) {
							p.sendMessage(gray + "/Q 필요 <코드>" + green + " [아이템/위치/몬스터]");
						} else {
							if (args[2].equals("아이템")) { // 이건 GUI로 설정하게 하고
								Inventory setItem = Bukkit.createInventory(null, 54, quest.getCode() + "#필요아이템 설정");
								for (ItemStack a : quest.getNeedItems())
									setItem.addItem(a);
								p.openInventory(setItem);
							} else if (args[2].equals("위치")) { // 이건 자신 위치
								if (args.length <= 3) {
									p.sendMessage(gray + "/Q 필요 <코드> 위치 " + green + "[목록/추가/제거]");
								} else {
									if (args[3].equals("목록")) {
										ArrayList<Location> loc = quest.getNeedLoc();
										if (loc.size() == 0) {
											p.sendMessage(red + "위치 목록이 비어있습니다.");
											return true;
										}
										int count = 0;
										for (Location a : loc) {
											p.sendMessage(gray + count + "번 : " + a.getBlockX() + "," + a.getBlockY()
													+ "," + a.getBlockZ());
										}
									} else if (args[3].equals("추가")) {
										ArrayList<Location> loc = quest.getNeedLoc();
										loc.add(p.getLocation());
										p.sendMessage(gray + "현재 위치로 퀘스트 " + green + quest.getCode() + gray
												+ "번 위치가 추가되었습니다.");
									} else if (args[3].equals("제거")) {
										if (args.length <= 4) {
											p.sendMessage(red + "/Q 필요 <코드> 위치 제거 <줄 수>");
										} else {
											int line;
											try {
												line = Integer.parseInt(args[4]);
											} catch (NumberFormatException e) {
												p.sendMessage(red + "숫자만 입력하세요.");
												return true;
											}
											if (quest.getNeedLoc().size() - 1 < line) {
												p.sendMessage(gray + "줄 수는 " + red + (quest.getMessages().size() - 1)
														+ gray + "줄이 넘어가서는 안됩니다.");
											} else {
												ArrayList<Location> loc = quest.getNeedLoc();
												p.sendMessage(gray + "성공적으로 '" + green + loc.get(line) + gray
														+ "' 위치를 삭제하였습니다.");
												loc.remove(line);
											}
										}
									} else {
										p.sendMessage(gray + "/Q 필요 <코드> 위치 " + green + "[목록/추가/제거]");
									}
								}
							} else if (args[2].equals("몬스터")) { // 이건 이름과 갯수 설정하기
								if (args.length <= 3) {
									p.sendMessage(gray + "/Q 필요 <코드> 몬스터 " + green + "[목록/추가/제거]");
								} else {
									if (args[3].equals("목록")) {
										HashMap<String, Integer> mob = quest.getNeedMobs();
										if (mob.size() == 0) {
											p.sendMessage(red + "설정되지 않았습니다.");
											return true;
										}
										int count = 0;
										for (String a : mob.keySet()) {
											p.sendMessage(gray + count + "줄 - " + a + " | " + mob.get(a) + "마리");
											count++;
										}
									} else if (args[3].equals("추가")) {
										if (args.length <= 5) {
											p.sendMessage(gray + "/Q 필요 <코드> 몬스터 추가 " + red + "<몹 이름> <마리 수>");
										} else { // 필요 args 충족
											int amount;
											try {
												amount = Integer.parseInt(args[5]);
											} catch (NumberFormatException e) {
												p.sendMessage(red + "숫자만 입력 가능합니다.");
												return true;
											}
											quest.getNeedMobs().put(args[4], amount);
											p.sendMessage(gray + "성공적으로 " + quest.getCode() + "번 퀘스트에 몹 " + args[4]
													+ "를 " + amount + "마리 잡기를 추가했습니다.");
										}
									} else if (args[3].equals("제거")) {
										if (args.length <= 4) {
											p.sendMessage(red + "/Q 필요 <코드> 몬스터 제거 <이름>");
										} else {
											if (quest.getNeedMobs().get(args[4]) == null) {
												p.sendMessage(red + "해당하는 몹의 이름이 없습니다.");
											} else {
												HashMap<String, Integer> mob = quest.getNeedMobs();
												p.sendMessage(gray + "성공적으로 " + args[4] + " 을 제거했습니다.");
												mob.remove(args[4]);
											}
										}
									} else {
										p.sendMessage(gray + "/Q 필요 <코드> 몬스터 " + red + "[목록/추가/제거]");
									}
								}
							} else {
								p.sendMessage(gray + "/Q 필요 <코드>" + red + " [아이템/위치/몬스터]");
							}
						}
					} else if (args[0].equals("보상")) {
						if (args.length <= 2) {
							p.sendMessage("/Q 보상 <코드> " + green + "[아이템/경험치/돈/버프]");
						} else {
							if (args[2].equals("아이템")) {
								Inventory setItem = Bukkit.createInventory(null, 54, quest.getCode() + "#보상아이템 설정");
								for (ItemStack a : quest.getRewardItem()) {
									if(a==null) continue;
									setItem.addItem(a);
								}
								p.openInventory(setItem);
							} else if (args[2].equals("경험치")) {
								if (args.length <= 3) {
									p.sendMessage(gray + "/Q 보상 <코드> 경험치 " + green + "<경험치 >");
								} else {
									int exp;
									try {
										exp = Integer.parseInt(args[3]);
									} catch (NumberFormatException e) {
										p.sendMessage(red + "숫자만 입력하세요.");
										return true;
									}
									quest.setRewardExp(exp);
									p.sendMessage(gray + "성공적으로 보상 경험치를 " + exp + "로 설정했습니다.");
								}
							} else if (args[2].equals("돈")) {
								if (args.length <= 3) {
									p.sendMessage(gray + "/Q 보상 <코드> 돈 " + green + "<돈>");
								} else {
									int money;
									try {
										money = Integer.parseInt(args[3]);
									} catch (NumberFormatException e) {
										p.sendMessage(red + "숫자만 입력하세요.");
										return true;
									}
									quest.setRewardMoney(money);
									p.sendMessage(gray + "성공적으로 보상 돈을 " + money + "로 설정했습니다.");
								}
							} else if (args[2].equals("버프")) {
								if (args.length <= 3) {
									p.sendMessage(gray + "/Q 보상 <코드> 버프 " + green + "<버프이름>");
								} else {
									quest.setRewardBuf(args[3]);
									p.sendMessage(gray + "성공적으로 보상 버프를 " + args[3] + "로 설정했습니다.");
								}
							} else {
								p.sendMessage("/Q 보상 <코드> " + red + "[아이템/경험치/돈]");
							}
						}
					} else {
						p.sendMessage(gray + "/Q " + green + "생성");
						p.sendMessage(gray + "/Q " + green + "[제목/이름/타입/대화/필요/보상] <코드>");
						p.sendMessage(gray + "/Q " + green + "목록");
						p.sendMessage(gray + " ( 이름 = NPC이름 ) ");
					}
				}
			}
		}
		return true;
	}
}
