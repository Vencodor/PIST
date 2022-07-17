package PIST.SettingManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import PIST.Pist;
import PIST.DataObject.User.SettingDTO;
import PIST.ServerManager.PistPlugin;

public class SettingManager extends PistPlugin implements Listener{
	public SettingManager(Pist ser) {
		super(ser);
	}
	public static HashMap<String,SettingDTO> userSetting = new HashMap<String,SettingDTO>();
	public Inventory setGUI = Bukkit.createInventory(null, 27, green+"유저 설정창");
	
	public void setUserSetting(Player p,SettingDTO s) {
		userSetting.put(p.getName(), s);
	}

	public SettingDTO getSettingDTO(Player p) {
		if(userSetting.get(p.getName())==null)
			userSetting.put(p.getName(), new SettingDTO());
		
		return userSetting.get(p.getName());
	}
	
	public void settingGUI() {
		List<String> lore = new ArrayList<String>();
		lore.add(gray+"스코어 보드의 "+green+"구성"+gray+"을 "+green+"빼거나"+gray+", "+green+"추가"+gray+"할 수 있습니다.");
		setGUI.setItem(10, createItem(new ItemStack(Material.SIGN),orange+"스코어보드 설정",lore));
	}
	
	public Inventory getScoreBoardGUI(Player p) {
		Inventory setScGUI = Bukkit.createInventory(null, 27, green+"스코어보드 설정창");
		SettingDTO s = getSettingDTO(p);
		
		setScGUI.setItem(9, createSettingItem(yellow+"스코어보드 표시",s.isShowSb()));
		setScGUI.setItem(10, createSettingItem(yellow+"레벨 표시",s.isShowSbLevel()));
		setScGUI.setItem(11, createSettingItem(yellow+"돈 표시",s.isShowSbMoney()));
		setScGUI.setItem(12, createSettingItem(yellow+"등급 표시",s.isShowSbRank()));
		setScGUI.setItem(13, createSettingItem(yellow+"속성 표시",s.isShowSbMyType()));
		setScGUI.setItem(14, createSettingItem(yellow+"현재 영토 표시",s.isShowSbNowFlat()));
		setScGUI.setItem(15, createSettingItem(yellow+"소속 길드 표시",s.isShowSbGuild()));
		setScGUI.setItem(16, createSettingItem(yellow+"상태 표시",s.isShowSbStat()));
		
		return setScGUI;
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		if(e.getInventory().getTitle().contains("설정창")) {
			e.setCancelled(true);
			
			ItemStack item = e.getCurrentItem();
			if(item==null) return;
			if(item.getItemMeta()==null) return;
			if(item.getItemMeta().getDisplayName()==null) return;
			String disName = item.getItemMeta().getDisplayName();

			Player p = (Player) e.getWhoClicked();
			if(e.getInventory().getTitle().contains("스코어보드")) {
				e.getInventory().setItem(e.getSlot(), createSettingItem(disName,changeSetting(p,item.getItemMeta())));
			} else if(e.getInventory().getTitle().contains("유저")) {
				
				if(disName.contains("스코어")) {
					p.openInventory(getScoreBoardGUI(p));
				}
			}
		}
	}
	
	private boolean changeSetting(Player p,ItemMeta item) {
		if(item.getLore().get(0).contains("비활성화")) {
			setSetting(p,item.getDisplayName(),true);
			return true; //활성화로 변경
		} else {
			setSetting(p,item.getDisplayName(),false);
			return false; //비활성화로 변경
		}
	}
	
	private void setSetting(Player p,String disName,boolean setBol) {
		SettingDTO s = getSettingDTO(p);
		if(disName.contains("스코어보드 표시")) {
			s.setShowSb(setBol);
		} else if(disName.contains("레벨 표시")) {
			s.setShowSbLevel(setBol);
		} else if(disName.contains("돈 표시")) {
			s.setShowSbMoney(setBol);
		} else if(disName.contains("등급 표시")) {
			s.setShowSbRank(setBol);
		} else if(disName.contains("속성 표시")) {
			s.setShowSbMyType(setBol);
		} else if(disName.contains("현재 영토 표시")) {
			s.setShowSbNowFlat(setBol);
		} else if(disName.contains("소속 길드 표시")) {
			s.setShowSbGuild(setBol);
		} else if(disName.contains("상태 표시")) {
			s.setShowSbStat(setBol);
		}
		userSetting.put(p.getName(), s);
	}
	
	private ItemStack createSettingItem(String name, boolean isTrue) {
		ItemStack item = new ItemStack(Material.WOOL,1,isTrue?(byte)5:(byte)14);
	    ItemMeta im = item.getItemMeta();
	    im.setDisplayName(name);
	    List<String> lore = new ArrayList<String>();
	    lore.add(isTrue?(gray+"현재  : "+green+"활성화"):(gray+"현재  : "+red+"비활성화"));
	    im.setLore(lore);
	    item.setItemMeta(im);
	    return item;
	}
	
}
