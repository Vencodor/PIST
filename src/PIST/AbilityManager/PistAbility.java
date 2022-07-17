package PIST.AbilityManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import PIST.Pist;
import PIST.DataObject.User.StatDTO;
import PIST.DataObject.User.UserRecordDTO;
import PIST.ServerManager.PistPlugin;
import PIST.Stat.Stat;
import PIST.UserManager.PistUser;
import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.EquationEffect;

public class PistAbility extends PistPlugin implements Listener{

	public PistAbility(Pist ser) {
		super(ser);
		plugin = ser;
	}
	public Pist plugin;
	
	public void setPlugin(Pist pl) {
		plugin = pl;
	}
	
	//public static HashMap<String,Boolean> setSlow = new HashMap<String,Boolean>();
	//private static HashMap<String,Integer> scID = new HashMap<String,Integer>();
	
	public void startSetAb() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers()) {
					StatDTO s = Stat.getStat(p);
					if(s==null) {
						continue;
					}
					else {
						UserRecordDTO user = PistUser.getInfo(p);
						double health = s.getHealth()*Stat.HealthAdd;
						health = health + user.getLevel()*PistUser.healthAdd;
						p.setMaxHealth(20.0+health);
						p.setHealthScale(20.0);
						
						float speed = (float) (0.2*(s.getSpeed()*Stat.speedAdd));
						speed = speed + (float) (user.getLevel()*PistUser.speedAdd);
						speed = speed + 0.2F;
						//if(setSlow.get(p.getName())!=null&&setSlow.get(p.getName()))
						//	speed = speed * 0.2F;
						speed = speed>1F?1F:speed;
						p.setWalkSpeed(speed);
					}
				}
			}
		}, 20L, 20L * 4);
	}
	/*
	public void reloadSpeedAb(Player p) {
		StatDTO s = Stat.getStat(p);
		if(s==null) {
			return;
		}
		else {
			UserRecordDTO user = PistUser.getInfo(p);
			
			float speed = (float) (0.2*(s.getSpeed()*Stat.speedAdd));
			speed += user.getLevel()*PistUser.speedAdd;
			speed += 0.2F;
			if(setSlow.get(p.getName())!=null&&setSlow.get(p.getName()))
				speed = speed * 0.2F;
			speed = speed>1F?1F:speed;
			
			if(p.getWalkSpeed()!=speed) { //너무 갑작스럽게 속도를 변경하면 화면 일렁거림
				float nowSpeed = p.getWalkSpeed();
				float disSpeed = Math.abs(speed - nowSpeed);//얼마나 현재 속도와 차이나는지
				if(disSpeed<=0.2F) {
					p.setWalkSpeed(speed);
				} else {
					float smallSpeed = speed>=nowSpeed ? nowSpeed : speed;
					
					scID.put(p.getName(),
					Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
						@Override
						public void run() {
							if(p.getWalkSpeed() >= smallSpeed+disSpeed) {
								Bukkit.getScheduler().cancelTask(scID.get(p.getName()));
							} else
								p.setWalkSpeed((float) (smallSpeed+(disSpeed*0.1)));
						}
					}, 1L, 2L)
							);
					//p.setWalkSpeed(smallSpeed+disSpeed);
				}
			}
		}
	}
	*/
}
