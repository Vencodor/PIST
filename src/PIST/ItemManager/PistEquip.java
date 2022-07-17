package PIST.ItemManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.console.KillRing;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import PIST.Pist;
import PIST.DataObject.User.EquipDTO;
import PIST.ServerManager.PistPlugin;
import PIST.UserManager.PistInventory;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PistEquip extends PistPlugin implements Listener { //주로 능력 사용에 다룸
	Pist plugin;
    PistInventory pi;
	
	public PistEquip(Pist ser) {
		super(ser);
		plugin = ser;
	}

	public static HashMap<UUID,EquipDTO> equip = new HashMap<UUID,EquipDTO>();
	
	public static EquipDTO getEquip(Player p) {
		if(equip.get(p.getUniqueId())==null) {
			equip.put(p.getUniqueId(), new EquipDTO());
			return new EquipDTO();
		}
		return equip.get(p.getUniqueId());
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(!(e.getEntity() instanceof Player))
			return;
		Player p = (Player)e.getEntity();
		int guard = 0;

		guard=guard+ getGuard(p.getInventory().getChestplate());
		guard=guard+ getGuard(p.getInventory().getLeggings());
		guard=guard+ getGuard(p.getInventory().getBoots());
		double damage = e.getDamage()*(1-(guard/2)/e.getDamage());
		if(damage>0) {
			e.setDamage(damage);
			p.sendTitle("", dred+"                     - "+String.format("%.1f", damage),3,13,7);
		} else {
			e.setDamage(0);
			p.sendTitle("",green+"                       Cancel",3,15,7);
		}
	}
	
	private int getGuard(ItemStack item) {
		int guard = 0;
		if(item==null||item.getItemMeta()==null||item.getItemMeta().getLore()==null) return 0;
		for(String a : item.getItemMeta().getLore()) {
			if(a.contains("방어력")) {
				a = a.replace(" ", "");
				try {
					guard = Integer.parseInt(a.split(":")[1]);
				} catch(NumberFormatException e) {
					e.printStackTrace();
					return 0;
				}
				return guard;
			}
		}
		return 0;
	}
	
	
}
