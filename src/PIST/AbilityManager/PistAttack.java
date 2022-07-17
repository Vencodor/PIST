package PIST.AbilityManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.JTable.PrintMode;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import PIST.Pist;
import PIST.DataObject.User.StatDTO;
import PIST.Enum.Attribute;
import PIST.ServerManager.PistPlugin;
import PIST.Stat.Stat;
import PIST.UserManager.PistUser;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.EquationEffect;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PistAttack extends PistPlugin implements Listener {

	Pist plugin;
	private EffectManager em;

	private HashMap<String, Integer> combo = new HashMap<String, Integer>();
	private HashMap<String, Boolean> coolTime = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> jumpTime = new HashMap<String, Boolean>();

	public EquationEffect equ;

	public PistAttack(Pist ser) {
		super(ser);
	}

	public void setPlugin(Pist plugin) {
		this.plugin = plugin;

		em = new EffectManager(plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		e.setCancelled(attackAction(e.getPlayer()));
	}

	@EventHandler
	public void onFlightAttempt(PlayerToggleFlightEvent e) {
		if (e.isFlying() && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			Player p = e.getPlayer();
			p.setFlying(false);
			e.setCancelled(true);
			p.setVelocity(p.getVelocity().add(p.getEyeLocation().getDirection().multiply(1.2)));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAttackInteract(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player))
			return;
		if (e.getDamage() == 0)
			return;
		Player p = (Player) e.getDamager();
		Random rnd = new Random();
		StatDTO s = Stat.getStat(p);
		double dmg = 1 + (PistUser.getInfo(p).getLevel() * PistUser.powerAdd);
		if (s != null && s.getPower() != 0)
			dmg = dmg + s.getPower() * Stat.powerAdd;
		if (rnd.nextInt(100 - (int) (s.getCriticalPer() * Stat.criticalAdd)) == 1D)
			dmg = s.getPower() * (s.getCriticalDamagePer() * Stat.criticalDamageAdd);
		if (isContainsDisplay(p.getInventory().getItemInMainHand(), "속성")) {
			dmg = dmg * PistAttribute.getAtt(p).getDamageAdd();

			if (coolTime.get(p.getName()) == null)
				coolTime.put(p.getName(), true);
			if ((coolTime.get(p.getName()) != null && coolTime.get(p.getName()))) {
				attackAction((Player) e.getDamager());
			}
			e.setDamage(dmg);
		} else {
			e.setDamage(dmg * 0.2);
		}
	}

	private boolean attackAction(Player p) {
		ItemStack item = p.getInventory().getItemInMainHand();
		if (isContainsLore(item, "무기")) {
			if (coolTime.get(p.getName()) != null && coolTime.get(p.getName()))
				return true;
			Attribute att = PistAttribute.getAtt(p);

			int count = 0;
			List<Entity> nearEntites = p.getNearbyEntities(5, 5, 5);
			Location loc = p.getLocation();
			Collections.sort(nearEntites, new Comparator<Entity>() {
				public int compare(Entity o1, Entity o2) {
					if (o1.getLocation().distance(loc) < o2.getLocation().distance(loc)) {
						return -1;
					} else if (o1.getLocation().distance(loc) > o2.getLocation().distance(loc)) {
						return 1;
					} else {
						return 0;
					}
				}
			});

			Random rnd = new Random();
			StatDTO s = Stat.getStat(p);
			/*
			 * double dmg = s.getPower()*Stat.powerAdd+1; if(rnd.nextInt(100 -
			 * (int)(s.getCriticalPer() * Stat.criticalAdd))==1D) dmg = s.getPower() * (
			 * s.getCriticalDamagePer() * Stat.criticalDamageAdd);
			 * lastSkullDamage.put(p.getName(), dmg);
			 */
			boolean hit = false;
			List<Entity> attackList = new ArrayList<Entity>();
			for (Entity a : nearEntites) {
				try {
					LivingEntity entity = (LivingEntity) a;
					if (getLookingAt(p, entity)) {
						// p.sendMessage(a.getName()+"");
						p.setVelocity(p.getEyeLocation().toVector().subtract(entity.getEyeLocation().toVector())
								.normalize().multiply(-0.7D));
						entity.damage(1D, p);
						attackList.add(a);
						count++;
						if (!hit) {
							hit = true;
						}
					}
					if (count > 4)
						break;
				} catch (Exception e1) {
					continue;
				}
			}
			if (hit) {
				int stack = 1;
				if (combo.get(p.getName()) != null)
					stack = combo.get(p.getName());
				if (stack > 5) {
					stack = 0;
					p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.4F, 0.6F);
					// p.setHealth(p.getHealth()+(firstT.getHealth()*0.1));
					EquationEffect equ = new EquationEffect(em);
					equ.setEntity(p);
					equ.xEquation = "0.5(-3.5cos(t)+10cos(-0.7t))";
					equ.yEquation = "2.3+0.75(5sint+5cost)";
					equ.zEquation = "0.5(-3.5sin(t)-10sin(-0.7t))";
					equ.duration = 300;
					equ.particles = 30;
					equ.orient = false;
					equ.color = att.getRGBColor();
					em.start(equ);
					Vector knockBack = p.getEyeLocation().getDirection().multiply(1.7);
					knockBack.setY(0.6);
					for (Entity a : attackList) {
						a.setVelocity(knockBack);
					}
				}
				if (stack <= 3) {
					if (stack == 0 || stack == 2)
						runAttackEffect("3.8*cos(t)^2", "3*cos(8-t)", att.getRGBColor(), p);
					else if (stack == 1 || stack == 3)
						runAttackEffect("3.8*cos(t)^2", "-3*cos(8-t)", att.getRGBColor(), p);
					else
						runAttackEffect("3.8*cos(t)^2", "cos(8-t)", att.getRGBColor(), p);
				} else {
					runAttackEffect("3.8*cos(t)^2", "3*cos(8-t)", att.getRGBColor(), p);
					runAttackEffect("3.8*cos(t)^2", "-3*cos(8-t)", att.getRGBColor(), p);
				}

				combo.put(p.getName(), stack + 1);
				// [ ▮ ▮ ▫ ▫ ▫ ▫ ▫ ▫ ]
				String comboBar = gray + "[ ";
				for (int k = 0; k < stack; k++)
					comboBar = comboBar + att.getColor() + bold + "▮ ";
				for (int k = 0; k < 5 - stack; k++)
					comboBar = comboBar + dgray + bold + "▫ ";
				comboBar = comboBar + gray + "]";

				p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
						TextComponent.fromLegacyText(gray + bold + " --- " + gray + comboBar + gray + bold + " --- "));

				coolTime.put(p.getName(), true);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						coolTime.put(p.getName(), false);
					}
				}, att.getCoolTime());
			}
			return true;
		}
		return false;
	}

	private void runAttackEffect(String x, String z, Color c, Player p) {
		if (x == null)
			x = "3.8*cos(t)^2";
		if (z == null)
			z = "-3*cos(8-t)";
		equ = new EquationEffect(em);
		equ.setEntity(p);
		equ.iterations = 1;
		equ.xEquation = x;
		equ.yEquation = "1.85*sin(t)";
		equ.zEquation = z;
		equ.particles = 70;
		equ.orientPitch = false;
		equ.duration = 0;
		equ.color = c;
		em.start(equ);
	}

	private boolean getLookingAt(Player player, LivingEntity livingEntity) {
		Location eye = player.getEyeLocation();
		Vector toEntity = livingEntity.getLocation().toVector().subtract(eye.toVector());
		double dot = toEntity.normalize().dot(eye.getDirection());
		return dot > 0.59D;
	}

}
