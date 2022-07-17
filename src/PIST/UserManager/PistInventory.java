package PIST.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import PIST.Pist;
import PIST.AbilityManager.PistAttribute;
import PIST.DataObject.User.EquipDTO;
import PIST.DataObject.User.UserRecordDTO;
import PIST.Enum.Attribute;
import PIST.Enum.EquipSlot;
import PIST.Guild.PistGuild;
import PIST.ItemManager.PistEquip;
import PIST.ServerManager.PistPlugin;

public class PistInventory extends PistPlugin implements Listener{

	public PistInventory(Pist ser) {
		super(ser);
	}
	private Inventory defultGUI = Bukkit.createInventory(null, 54,"DefultGUI");
	
	public void settingDefult() {
		ItemStack empty = createItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)7)," ",new ArrayList<String>());
		for(int i=0; i<54; i++)
			defultGUI.setItem(i, empty);
		
		List<EquipSlot> list = EquipSlot.getList();
		for(EquipSlot a : list) {
			defultGUI.setItem(a.getSlot(), empty);
		}
		//new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)13),orange+a.getName(),new ArrayList<String>())
	}
	
	public Inventory getInv(Player p) {
		String name = p.getName();
		UUID uid = p.getUniqueId();
		UserRecordDTO user = PistUser.getInfo(p);
		
		Inventory i = Bukkit.createInventory(null, 54,green+name+gray+"님의 장비창");
		i.setContents(defultGUI.getContents());
		
		List<String> lore = new ArrayList<String>();
		
		List<Attribute> attList = PistAttribute.getList(p);
		for(int k=0; k<attList.size(); k++) {
			List<String> attLore = new ArrayList<String>();
			attLore.add(white+" ");
			attLore.add(orange + "❘"+attList.get(k).getColor()+" "+attList.get(k).getName());
			attLore.add(white+" ");
			attLore.add(gray+attList.get(k).getDescription());
			i.setItem(k+4, createItem(getAttItem(p,attList.get(k))," "+orange+bold+(k+1)+gray+" 번째 속성 ",attLore));
		}
		EquipDTO eq = PistEquip.getEquip(p);
		i.setItem(EquipSlot.RING_1.getSlot(), eq.getRing_1());
		i.setItem(EquipSlot.RING_2.getSlot(), eq.getRing_2());
		i.setItem(EquipSlot.PENDANT_1.getSlot(), eq.getPendant_1());
		i.setItem(EquipSlot.PENDANT_2.getSlot(), eq.getPendant_2());
		i.setItem(EquipSlot.EARRING_1.getSlot(), eq.getEarring_1());
		i.setItem(EquipSlot.EARRING_2.getSlot(), eq.getEarring_2());
		i.setItem(EquipSlot.ARTIFACT_1.getSlot(), eq.getArtifact_1());
		i.setItem(EquipSlot.ARTIFACT_2.getSlot(), eq.getArtifact_2());
		i.setItem(EquipSlot.PET.getSlot(), eq.getBaseWeapon());
		i.setItem(EquipSlot.RIDING.getSlot(), eq.getRiding());
		i.setItem(EquipSlot.RUNE.getSlot(), eq.getRune());
		i.setItem(EquipSlot.CREATURE.getSlot(), eq.getCreature());
		
		i.setItem(38, createItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)14)," ",new ArrayList<String>()));
		i.setItem(41, createItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)14)," ",new ArrayList<String>()));
		
		List<EquipSlot> list = EquipSlot.getList();
		for(EquipSlot a : list) {
			if(i.getItem(a.getSlot())==null)
				i.setItem(a.getSlot(),createItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)13),orange+a.getName(),new ArrayList<String>()));
		}
		
		return i;
	}
	
	@EventHandler
	public void onCloseInv(InventoryCloseEvent e) {
		if(e.getInventory().getTitle().contains("장비창")) {
			Inventory i = e.getInventory();
			EquipDTO eq = PistEquip.getEquip((Player)e.getPlayer());
			
			eq.setRing_1(getSave(i,EquipSlot.RING_1,eq.getRing_1()));
			eq.setRing_2(getSave(i,EquipSlot.RING_2,eq.getRing_2()));
			eq.setPendant_1(getSave(i,EquipSlot.PENDANT_1,eq.getPendant_1()));
			eq.setPendant_2(getSave(i,EquipSlot.PENDANT_2,eq.getPendant_2()));
			eq.setEarring_1(getSave(i,EquipSlot.EARRING_1,eq.getEarring_1()));
			eq.setEarring_2(getSave(i,EquipSlot.EARRING_2,eq.getEarring_2()));
			eq.setArtifact_1(getSave(i,EquipSlot.ARTIFACT_1,eq.getArtifact_1()));
			eq.setArtifact_2(getSave(i,EquipSlot.ARTIFACT_2,eq.getArtifact_2()));
			eq.setBaseWeapon(getSave(i,EquipSlot.PET,eq.getBaseWeapon()));
			eq.setRiding(getSave(i,EquipSlot.RIDING,eq.getRiding()));
			eq.setCreature(getSave(i,EquipSlot.CREATURE,eq.getCreature()));
			eq.setRune(getSave(i,EquipSlot.RUNE,eq.getRune()));
			
			PistEquip.equip.put(e.getPlayer().getUniqueId(), eq);
		}
	}
	
	private ItemStack getSave(Inventory i,EquipSlot equip,ItemStack beforeItem) {
		ItemStack item = i.getItem(equip.getSlot());
		if(item==null)
			return null;
		if(item.getType() == Material.STAINED_GLASS_PANE)
			return null;
		return item;
	}
	private void addItemInv(Player p,ItemStack item) {
		if(item.getType() == Material.STAINED_GLASS_PANE)
			return;
		p.getInventory().addItem(item);
	}
	@EventHandler
	public void onClickInv(InventoryClickEvent e) {
		if(e.getInventory().getTitle().contains("님의 정보"))
			e.setCancelled(true);
		if(e.getInventory().getTitle().contains("장비창")) {
			e.setCancelled(true);
			
			ItemStack item = e.getCurrentItem();
			int slot = e.getSlot();
			Inventory inv = e.getInventory();
			Player p = (Player)e.getWhoClicked();
			
			if(item == null)
				return;
			if(item.getType() == Material.STAINED_GLASS_PANE)
				return;
			
			if(isContainsDisplay(item, "반지")) {
				if(EquipSlot.RING_1.getSlot() == slot&&e.getClickedInventory().getType() == InventoryType.CHEST) { //첫번째 링을 클릭시
					addItemInv(p,inv.getItem(EquipSlot.RING_1.getSlot()));
					setBeforeItem(inv,EquipSlot.RING_1);
				} else if(EquipSlot.RING_2.getSlot() == slot&&e.getClickedInventory().getType() == InventoryType.CHEST) { //두번째 링을 클릭시
					addItemInv(p,inv.getItem(EquipSlot.RING_2.getSlot()));
					setBeforeItem(inv, EquipSlot.RING_2);
				} else { //착용
					if(inv.getItem(EquipSlot.RING_1.getSlot()).getType() == Material.STAINED_GLASS_PANE) { //첫번째칸 빔
						inv.setItem(EquipSlot.RING_1.getSlot(), item);
						p.getInventory().setItem(slot, null);
					} else if(inv.getItem(EquipSlot.RING_2.getSlot()).getType()==Material.STAINED_GLASS_PANE) { //두번째칸 빔
						inv.setItem(EquipSlot.RING_2.getSlot(), item);
						p.getInventory().setItem(slot, null);
					} else { //공간 없음
						p.sendMessage(red+"먼저 장비를 해제해주세요.");
					}
				}
			} else if(isContainsDisplay(item, "목걸이")) {
				if(EquipSlot.PENDANT_1.getSlot() == slot&&e.getClickedInventory().getType() == InventoryType.CHEST) { //첫번째 링을 클릭시
					addItemInv(p,inv.getItem(EquipSlot.PENDANT_1.getSlot()));
					setBeforeItem(inv,EquipSlot.PENDANT_1);
				} else if(EquipSlot.PENDANT_2.getSlot() == slot&&e.getClickedInventory().getType() == InventoryType.CHEST) { //두번째 링을 클릭시
					addItemInv(p,inv.getItem(EquipSlot.PENDANT_2.getSlot()));
					setBeforeItem(inv, EquipSlot.PENDANT_2);
				} else { //착용
					if(inv.getItem(EquipSlot.PENDANT_1.getSlot()).getType() == Material.STAINED_GLASS_PANE) { //첫번째칸 빔
						inv.setItem(EquipSlot.PENDANT_1.getSlot(), item);
						p.getInventory().setItem(slot, null);
					} else if(inv.getItem(EquipSlot.PENDANT_2.getSlot()).getType()==Material.STAINED_GLASS_PANE) { //두번째칸 빔
						inv.setItem(EquipSlot.PENDANT_2.getSlot(), item);
						p.getInventory().setItem(slot, null);
					} else { //공간 없음
						p.sendMessage(red+"먼저 장비를 해제해주세요.");
					}
				}
			} else if(isContainsDisplay(item, "귀걸이")) {
				if(EquipSlot.EARRING_1.getSlot() == slot&&e.getClickedInventory().getType() == InventoryType.CHEST) { //첫번째 링을 클릭시
					addItemInv(p,inv.getItem(EquipSlot.EARRING_1.getSlot()));
					setBeforeItem(inv,EquipSlot.EARRING_1);
				} else if(EquipSlot.EARRING_2.getSlot() == slot&&e.getClickedInventory().getType() == InventoryType.CHEST) { //두번째 링을 클릭시
					addItemInv(p,inv.getItem(EquipSlot.EARRING_2.getSlot()));
					setBeforeItem(inv, EquipSlot.EARRING_2);
				} else { //착용
					if(inv.getItem(EquipSlot.EARRING_1.getSlot()).getType() == Material.STAINED_GLASS_PANE) { //첫번째칸 빔
						inv.setItem(EquipSlot.EARRING_1.getSlot(), item);
						p.getInventory().setItem(slot, null);
					} else if(inv.getItem(EquipSlot.EARRING_2.getSlot()).getType()==Material.STAINED_GLASS_PANE) { //두번째칸 빔
						inv.setItem(EquipSlot.EARRING_2.getSlot(), item);
						p.getInventory().setItem(slot, null);
					} else { //공간 없음
						p.sendMessage(red+"먼저 장비를 해제해주세요.");
					}
				}
			} else if(isContainsDisplay(item, "아티펙트")) {
				if(EquipSlot.ARTIFACT_1.getSlot() == slot&&e.getClickedInventory().getType() == InventoryType.CHEST) { //첫번째 링을 클릭시
					addItemInv(p,inv.getItem(EquipSlot.ARTIFACT_1.getSlot()));
					setBeforeItem(inv,EquipSlot.ARTIFACT_1);
				} else if(EquipSlot.ARTIFACT_2.getSlot() == slot&&e.getClickedInventory().getType() == InventoryType.CHEST) { //두번째 링을 클릭시
					addItemInv(p,inv.getItem(EquipSlot.ARTIFACT_2.getSlot()));
					setBeforeItem(inv, EquipSlot.ARTIFACT_2);
				} else { //착용
					if(inv.getItem(EquipSlot.ARTIFACT_1.getSlot()).getType() == Material.STAINED_GLASS_PANE) { //첫번째칸 빔
						inv.setItem(EquipSlot.ARTIFACT_1.getSlot(), item);
						p.getInventory().setItem(slot, null);
					} else if(inv.getItem(EquipSlot.ARTIFACT_2.getSlot()).getType()==Material.STAINED_GLASS_PANE) { //두번째칸 빔
						inv.setItem(EquipSlot.ARTIFACT_2.getSlot(), item);
						p.getInventory().setItem(slot, null);
					} else { //공간 없음
						p.sendMessage(red+"먼저 장비를 해제해주세요.");
					}
				}
			} else if(isContainsDisplay(item, "라이딩")) {
				if(EquipSlot.RIDING.getSlot() == slot&&e.getClickedInventory().getType() == InventoryType.CHEST) { //첫번째 링을 클릭시
					addItemInv(p,inv.getItem(EquipSlot.RIDING.getSlot()));
					setBeforeItem(inv,EquipSlot.RIDING);
				} else { //착용
					if(inv.getItem(EquipSlot.RIDING.getSlot()).getType()==Material.STAINED_GLASS_PANE) { //첫번째칸 빔
						inv.setItem(EquipSlot.RIDING.getSlot(), item);
						p.getInventory().setItem(slot, null);
					} else { //공간 없음
						p.sendMessage(red+"먼저 장비를 해제해주세요.");
					}
				}
			} else if(isContainsDisplay(item, "펫")) {
				if(EquipSlot.PET.getSlot() == slot&&e.getClickedInventory().getType() == InventoryType.CHEST) { //첫번째 링을 클릭시
					addItemInv(p,inv.getItem(EquipSlot.PET.getSlot()));
					setBeforeItem(inv,EquipSlot.PET);
				} else { //착용
					if(inv.getItem(EquipSlot.PET.getSlot()).getType()==Material.STAINED_GLASS_PANE) { //첫번째칸 빔
						inv.setItem(EquipSlot.PET.getSlot(), item);
						p.getInventory().setItem(slot, null);
					} else { //공간 없음
						p.sendMessage(red+"먼저 장비를 해제해주세요.");
					}
				}
			} else if(isContainsDisplay(item, "크리쳐")) {
				if(EquipSlot.CREATURE.getSlot() == slot&&e.getClickedInventory().getType() == InventoryType.CHEST) { //첫번째 링을 클릭시
					addItemInv(p,inv.getItem(EquipSlot.CREATURE.getSlot()));
					setBeforeItem(inv,EquipSlot.CREATURE);
				} else { //착용
					if(inv.getItem(EquipSlot.CREATURE.getSlot()).getType()==Material.STAINED_GLASS_PANE) { //첫번째칸 빔
						inv.setItem(EquipSlot.CREATURE.getSlot(), item);
						p.getInventory().setItem(slot, null);
					} else { //공간 없음
						p.sendMessage(red+"먼저 장비를 해제해주세요.");
					}
				}
			} else if(isContainsDisplay(item, "룬")) {
				if(EquipSlot.RUNE.getSlot() == slot&&e.getClickedInventory().getType() == InventoryType.CHEST) { //첫번째 링을 클릭시
					addItemInv(p,inv.getItem(EquipSlot.RUNE.getSlot()));
					setBeforeItem(inv,EquipSlot.RUNE);
				} else { //착용
					if(inv.getItem(EquipSlot.RUNE.getSlot()).getType()==Material.STAINED_GLASS_PANE) { //첫번째칸 빔
						inv.setItem(EquipSlot.RUNE.getSlot(), item);
						p.getInventory().setItem(slot, null);
					} else { //공간 없음
						p.sendMessage(red+"먼저 장비를 해제해주세요.");
					}
				}
			}
		}
	}
	
	public Inventory getInfoInv(Player p) {
		String name = p.getName();
		UUID uid = p.getUniqueId();
		UserRecordDTO user = PistUser.getInfo(p);
		
		Inventory i = Bukkit.createInventory(null, 54,green+name+gray+"님의 정보");
		i.setContents(defultGUI.getContents());
		
		List<String> lore = new ArrayList<String>();
		
		List<Attribute> attList = PistAttribute.getList(p);
		for(int k=0; k<attList.size(); k++) {
			List<String> attLore = new ArrayList<String>();
			attLore.add(white+" ");
			attLore.add(orange + "❘"+attList.get(k).getColor()+" "+attList.get(k).getName());
			attLore.add(white+" ");
			attLore.add(gray+attList.get(k).getDescription());
			i.setItem(k+4, createItem(getAttItem(p,attList.get(k))," "+orange+bold+(k+1)+gray+" 번째 속성 ",attLore));
		}
		EquipDTO eq = PistEquip.getEquip(p);
		i.setItem(EquipSlot.RING_1.getSlot(), eq.getRing_1());
		i.setItem(EquipSlot.RING_2.getSlot(), eq.getRing_2());
		i.setItem(EquipSlot.PENDANT_1.getSlot(), eq.getPendant_1());
		i.setItem(EquipSlot.PENDANT_2.getSlot(), eq.getPendant_2());
		i.setItem(EquipSlot.EARRING_1.getSlot(), eq.getEarring_1());
		i.setItem(EquipSlot.EARRING_2.getSlot(), eq.getEarring_2());
		i.setItem(EquipSlot.ARTIFACT_1.getSlot(), eq.getArtifact_1());
		i.setItem(EquipSlot.ARTIFACT_2.getSlot(), eq.getArtifact_2());
		i.setItem(EquipSlot.PET.getSlot(), eq.getBaseWeapon());
		i.setItem(EquipSlot.RIDING.getSlot(), eq.getRiding());
		i.setItem(EquipSlot.RUNE.getSlot(), eq.getRune());
		i.setItem(EquipSlot.CREATURE.getSlot(), eq.getCreature());
		
		i.setItem(38, createItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)14)," ",new ArrayList<String>()));
		i.setItem(41, createItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)14)," ",new ArrayList<String>()));
		
		UserRecordDTO r = PistUser.getInfo(p);
		
		List<String> info = new ArrayList<String>();
		info.add(white+" ");
		info.add(gray+" ");
		info.add(gray+" 레벨 : "+orange+r.getLevel());
		info.add(gray+" 등급 : "+orange+r.getRank().getName());
		info.add(gray+" 돈 : "+orange+r.getMoney());
		info.add(white+" ");
		if(PistGuild.members.get(p.getUniqueId())==null) {
			info.add(gray + " 소속 길드 : "+yellow+"없음 ");
		} else {
			info.add(gray + " 소속 길드 : "+yellow+PistGuild.getGuildUseCode(
			PistGuild.members.get(p.getUniqueId()).getGuildCode() ).getName()+" ");
		}
		info.add(white+" ");
		i.setItem(3, createItem(getHead(p),red+p.getName(),info));
		
		List<EquipSlot> list = EquipSlot.getList();
		for(EquipSlot a : list) {
			if(i.getItem(a.getSlot())==null)
				i.setItem(a.getSlot(),createItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)13),orange+a.getName(),new ArrayList<String>()));
		}
		
		return i;
	}
	
	private void setBeforeItem(Inventory inv,EquipSlot eq) {
		inv.setItem(eq.getSlot(), 
				createItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)13),orange+eq.getName(),new ArrayList<String>()));
	}
	
	private ItemStack getAttItem(Player p,Attribute att) {
		ItemStack is = new ItemStack(Material.BARRIER);
		if(att == Attribute.FIRE)
			is = new ItemStack(Material.INK_SACK,1,(byte)1);
		else if(att == Attribute.WATER)
			is = new ItemStack(Material.INK_SACK,1,(byte)6);
		else if(att == Attribute.EARTH)
			is = new ItemStack(Material.INK_SACK,1,(byte)3);
		else if(att == Attribute.ELECTRIC)
			is = new ItemStack(Material.INK_SACK,1,(byte)11);
		else if(att == Attribute.ICE)
			is = new ItemStack(Material.INK_SACK,1,(byte)12);
		else if(att == Attribute.METAL)
			is = new ItemStack(Material.INK_SACK,1,(byte)8);
		else if(att == Attribute.WIND)
			is = new ItemStack(Material.INK_SACK,1,(byte)7);
		
		return is;
	}
	
}
