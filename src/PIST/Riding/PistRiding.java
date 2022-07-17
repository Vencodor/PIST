package PIST.Riding;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import PIST.Pist;
import PIST.ServerManager.PistPlugin;

public class PistRiding extends PistPlugin implements Listener{

	Pist plugin;
	
	public PistRiding(Pist ser) {
		super(ser);
		plugin = ser;
	}
	
	private static HashMap<String,Integer> sneak = new HashMap<String,Integer>();
	
	public static HashMap<String,Horse> horses = new HashMap<String,Horse>();
	
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		if(sneak.get(p.getName())!=null&&sneak.get(p.getName())>=3) {
			if(horses.get(p.getName())==null) {
				sneak.remove(p.getName());
				spawnHorse(p);
			}
		} else {
			sneak.put(p.getName(), sneak.get(p.getName())==null?1:sneak.get(p.getName())+1);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					sneak.remove(p.getName());
				}
			}, 10L);
		}
	}
	
	@EventHandler
	public void onClickSaddle(InventoryClickEvent e) {
		if(e.getCurrentItem()!=null) {
			if(e.getCurrentItem().getType() == Material.SADDLE) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDismount(VehicleExitEvent e) {
		if(e.getExited() instanceof Player) {
			if(e.getVehicle() instanceof Horse) {
				Horse horse = (Horse) e.getVehicle();
				horse.remove();
				horses.remove(e.getExited().getName());
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Horse) {
			Horse horse = (Horse) e.getEntity();
			horse.remove();
			horses.remove(getKey(horses, horse));
		}
	}
	
	@EventHandler
	public void onExit(PlayerQuitEvent e) {
		if(horses.get(e.getPlayer().getName())!=null) {
			horses.get(e.getPlayer().getName()).remove();
			horses.remove(e.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if(horses.get(e.getEntity().getName())!=null) {
			horses.get(e.getEntity().getName()).remove();
			horses.remove(e.getEntity().getName());
		}
	}
	
	public static void removeAllHorse() {
		for(Horse a : horses.values()) {
			a.remove();
		}
	}
	
	public static void removeHorse(Player p) {
		if(horses.get(p.getName())!=null) {
			horses.get(p.getName()).remove();
			horses.remove(p.getName());
		}
	}
	
    public static void spawnHorse(Player p) {
        Horse horse = (Horse) p.getWorld().spawnEntity(p.getLocation(), EntityType.HORSE);
		horses.put(p.getName(),horse);
        
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        
        horse.setOwner(p);
        horse.setTamed(true);
        horse.setRemoveWhenFarAway(false);

        horse.setCustomName(" "+ChatColor.GOLD + p.getName() + ChatColor.GRAY + "님의 말 ");
        horse.setCustomNameVisible(true);
        horse.setAge(100);
        horse.setMaxHealth(20.0D);
        horse.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4, true, false));
        horse.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 100,  true, false));
        horse.setPassenger(p);
        //horse.setColor(Horse.Color.BLACK);
        //horse.setStyle(Horse.Style.NONE);
        //horse.setBreed(false);
        //horse.setCanPickupItems(false);
    }

}
