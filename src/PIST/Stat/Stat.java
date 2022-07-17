package PIST.Stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import PIST.Pist;
import PIST.DataObject.User.StatDTO;
import PIST.ServerManager.PistPlugin;
import net.md_5.bungee.api.ChatColor;

public class Stat extends PistPlugin implements CommandExecutor, Listener {

	public Stat(Pist ser) {
		super(ser);
	}
	
	public Pist plugin;
	
	public Inventory statGUI = Bukkit.createInventory(null, 27, gray+bold+"Statistic");
	public static HashMap<UUID,Integer> point = new HashMap<UUID,Integer>();
	public static HashMap<UUID,StatDTO> stat = new HashMap<UUID,StatDTO>();
	
	public static double powerAdd = 1.1;
	public static double HealthAdd = 4.5;
	public static double speedAdd = 0.0017;
	public static double criticalAdd = 0.2;
	public static double criticalDamageAdd = 1.8;
	public static double missAdd = 0.14;
	public static double coolTimeAdd = 0.27; //나중에 스텟 최대 설정할것
	
	public void setPlugin(Pist pl) {
		plugin = pl;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;

		if (label.equalsIgnoreCase("스텟")) {
			if(args.length>=1) {
				if(args[0].equalsIgnoreCase("관리")) {
					if(!p.isOp())
						openStatInv(p);
					else {
						if(args.length==1) {
							p.sendMessage(gray+"/스텟 관리 "+green+"스텟권"+gray+" 스텟권을 지급 받습니다.");
							p.sendMessage(gray+"/스텟 관리 "+green+"보기 <Name>"+gray+" - 유저의 스텟을 확인합니다.");
							p.sendMessage(gray+"/스텟 관리 "+green+"초기화 <Name>"+gray+" - 유저의 스텟을 초기화합니다.");
							p.sendMessage(gray+"/스텟 관리 "+green+"포인트초기화 <Name>"+gray+" - 유저의 스텟 포인트를 초기화합니다.");
						} else {
							if(args[1].equalsIgnoreCase("스텟권")) {
								List<String> lore = new ArrayList<String>();
								lore.add(white+" ");
								lore.add(gray+"해당 스텟권을 우클릭 하여 "+green+"스텟 포인트"+gray+"를 지급 받으세요.");
								lore.add(white+" ");
								ItemStack item = createItem(new ItemStack(Material.PAPER), 
										white+ChatColor.BOLD+"[ "+aqua+"스텟권"+white+ChatColor.BOLD+" ]", lore);
								p.getInventory().addItem(item);
							} else if(args[1].equalsIgnoreCase("보기")) {
								if(args.length==2) {
									p.sendMessage(red+"인자값이 하나 더 필요합니다. "+gray+"/스텟 관리 보기 <Name>");
								} else {
									if(Bukkit.getOfflinePlayer(args[2])==null) {
										p.sendMessage(red+"해당 유저는 존재하지 않습니다.");
									} else {
										openStatInv(Bukkit.getOfflinePlayer(args[2]).getPlayer());
									}
								}
							} else if(args[1].equalsIgnoreCase("초기화")) {
								if(args.length==2) {
									p.sendMessage(red+"인자값이 하나 더 필요합니다. "+gray+"/스텟 관리 초기화 <Name>");
								} else {
									if(Bukkit.getOfflinePlayer(args[2])==null) {
										p.sendMessage(red+"해당 유저는 존재하지 않습니다.");
									} else {
										stat.remove(Bukkit.getOfflinePlayer(args[2]).getUniqueId());
									}
								}
							} else if(args[1].equalsIgnoreCase("포인트초기화")) {
								if(args.length==2) {
									p.sendMessage(red+"인자값이 하나 더 필요합니다. "+gray+"/스텟 관리 포인트초기화 <Name>");
								} else {
									if(Bukkit.getOfflinePlayer(args[2])==null) {
										p.sendMessage(red+"해당 유저는 존재하지 않습니다.");
									} else {
										point.remove(Bukkit.getOfflinePlayer(args[2]).getUniqueId());
									}
								}
							}
						}
					}
				} else {
					openStatInv(p);
				}
			} else {
				openStatInv(p);
			}
		}
		return true;
	}
	
	public static StatDTO getStat(Player p) {
		return stat.get(p.getUniqueId());
	}
	
	public static HashMap<UUID,StatDTO> getStatHash() {
		return stat;
	}
	
	public static HashMap<UUID,Integer> getPointHash() {
		return point;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			//p.sendBlockChange(pL, Material.LAVA, (byte) 0);
			
			ItemStack item = p.getItemInHand();
			if(item.getItemMeta()==null)
				return;
			if(item.getItemMeta().getDisplayName()==null)
				return;
			if(item.getItemMeta().getDisplayName().contains("스텟권")) {
				item.setAmount(item.getAmount()-1);
				if(point.get(p.getUniqueId())==null)
					point.put(p.getUniqueId(), 0);
				point.put(p.getUniqueId(), point.get(p.getUniqueId())+1);
				p.sendMessage(gray+"당신은 "+green+"스텟 포인트"+gray+"등록을 하였습니다. ( 포인트 "+green+point.get(p.getUniqueId())+gray+"개 있음. )");
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		if(e.getInventory()==null)
			return;
		
		if(e.getInventory().getTitle().contains("Statistic")) {
			e.setCancelled(true);
			ItemStack item = e.getCurrentItem();
			if(item==null)
				return;
			if(item.getItemMeta()==null)
				return;
			if(item.getItemMeta().getDisplayName()==null)
				return;
			
			UUID uid = p.getUniqueId();
			int poi = point.get(uid);
			String display = item.getItemMeta().getDisplayName();
			if(display.contains("공격력")||display.contains("체력")||display.contains("속도")
					||display.contains("치명타 확률")||display.contains("치명타 데미지")||display.contains("회피율")||
					display.contains("스킬 쿨타임 감소")) {
				if(e.getClick() == ClickType.SHIFT_LEFT) {
					if(poi>=10) {
						if(addStat(uid, display,10)) {
							point.put(uid, poi-10);
							updateStatItem(e.getInventory(),p);
						} else
							p.sendMessage(dred+"작업을 처리하던중 오류가 발생하였습니다. 관리자에게 문의하세요");
					} else {
						setDisableMoment(e.getInventory(),e.getSlot());
					}
						
				} else if(e.getClick() == ClickType.LEFT) {
					if(poi>=1) {
						if(addStat(uid, display,1)) {
							point.put(uid, poi-1);
							updateStatItem(e.getInventory(),p);
						} else
							p.sendMessage(dred+"작업을 처리하던중 오류가 발생하였습니다. 관리자에게 문의하세요");
					} else {
						setDisableMoment(e.getInventory(),e.getSlot());
					}
					
				}
			}
		}
	}
	
	public void setDisableMoment(Inventory i, int slot) {
		ItemStack item = i.getItem(slot);
		
		List<String> lore = new ArrayList<String>();
		lore.add(gray+"스텟 포인트는 "+green+"레벨업"+gray+" 을 통해서 획득하실 수 있습니다.");
		i.setItem(slot, createItem(new ItemStack(Material.BARRIER),red+"포인트가 부족합니다.",lore));
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() { // 시작
				try {
					i.setItem(slot,item);
				} catch(Exception e) {
					return;
				}
			}
		}, 30L);
	}
	
	public boolean addStat(UUID uid, String display,int amount) {
		if(display.contains("공격력")) {
			stat.get(uid).setPower(stat.get(uid).getPower()+amount);
		} else if(display.contains("체력")) {
			stat.get(uid).setHealth(stat.get(uid).getHealth()+amount);
		} else if(display.contains("속도")) {
			stat.get(uid).setSpeed(stat.get(uid).getSpeed()+amount);
		} else if(display.contains("치명타 확률")) {
			stat.get(uid).setCriticalPer(stat.get(uid).getCriticalPer()+amount);
		} else if(display.contains("치명타 데미지")) {
			stat.get(uid).setCriticalDamagePer(stat.get(uid).getCriticalDamagePer()+amount);
		} else if(display.contains("회피율")) {
			stat.get(uid).setMiss(stat.get(uid).getMiss()+amount);
		} else if(display.contains("스킬 쿨타임 감소")) {
			stat.get(uid).setCoolTimePer(stat.get(uid).getCoolTimePer()+amount);
		}else {
			return false;
		}
		return true;
	}
	
	public void updateStatItem(Inventory i,Player p) {
		StatDTO s = stat.get(p.getUniqueId());
		int poi = point.get(p.getUniqueId());
		
		ItemStack power = createItem(new ItemStack(Material.IRON_SWORD),red+bold+"["+white+" 공격력 "+red+bold+"]",
				settingLores(s.getPower(),powerAdd+"",poi));
		i.setItem(10, power);
		
		ItemStack health = createItem(new ItemStack(Material.GOLD_CHESTPLATE),dred+bold+"["+white+" 체력 "+dred+bold+"]",
				settingLores(s.getHealth(),HealthAdd+"",poi));
		i.setItem(11, health);
		
		ItemStack speed = createItem(new ItemStack(Material.IRON_BOOTS),aqua+bold+"["+white+" 속도 "+aqua+bold+"]",
				settingLores(s.getSpeed(),speedAdd+"%",poi));
		i.setItem(12, speed);
		
		ItemStack critical = createItem(new ItemStack(Material.IRON_AXE),yellow+bold+"["+white+" 치명타 확률 "+yellow+bold+"]",
				settingLores(s.getCriticalPer(),criticalAdd+"%",point.get(p.getUniqueId())));
		i.setItem(13, critical);
		
		ItemStack criticalDmg = createItem(new ItemStack(Material.GOLD_AXE),red+bold+"["+white+" 치명타 데미지 "+red+bold+"]",
				settingLores(s.getCriticalPer(),criticalDamageAdd+"%",point.get(p.getUniqueId())));
		i.setItem(14, criticalDmg);
		
		ItemStack miss = createItem(new ItemStack(Material.CHAINMAIL_BOOTS),gray+bold+"["+white+" 회피율 "+gray+bold+"]",
				settingLores(s.getCriticalPer(),missAdd+"%",point.get(p.getUniqueId())));
		i.setItem(15, miss);
		
		ItemStack cool = createItem(new ItemStack(Material.DIAMOND_HELMET),blue+bold+"["+white+" 스킬 쿨타임 감소 "+blue+bold+"]",
				settingLores(s.getCriticalPer(),coolTimeAdd+"%",point.get(p.getUniqueId())));
		i.setItem(16, cool);
		
		ItemStack playerStat = createItem(new ItemStack(Material.APPLE),"  "+green+p.getName()+"  ",setProfileLore(p));
		i.setItem(22, playerStat);
		
		List<String> signLore = new ArrayList<String>();
		signLore.add(white+" ");
		signLore.add(gray+"플레이어의 능력치를 올려주는 스텟 시스템입니다.");
		signLore.add(green+"SHIFT + CLICK"+gray+" 를 통해 한번에 "+green+"10개"+gray+"씩 업그레이드 가능합니다.");
		signLore.add(white+" ");
		
		i.setItem(4,createItem(new ItemStack(Material.SIGN),gray+bold+"PIST",signLore));
		ItemStack empty = createItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)7), " ", new ArrayList<String>());
		for(int k=0; k<i.getSize(); k++) {
			if(i.getItem(k)==null)
				i.setItem(k, empty);
		}
		
	}
	
	public void openStatInv(Player p) {
		Inventory i = Bukkit.createInventory(null, 27, gray+bold+"Statistic");
		i.setContents(statGUI.getContents());
		
		UUID uid = p.getUniqueId();
		
		if(stat.get(uid)==null)
			stat.put(uid, new StatDTO());
		if(point.get(uid)==null)
			point.put(uid, 0);
		
		updateStatItem(i, p);
		
		p.openInventory(i);
	}
	
	public List<String> settingLores(int addedStat,String upGage,int beingStat) {
		List<String> defultLore = new ArrayList<String>();
		
		//defultLore.add(gray+"▁▁▁▁▁▁▁▁▁▁");
		defultLore.add(white+" "); 
		defultLore.add(green+"❘"+gray+" 추가된 총 스텟 "+green+addedStat+" ");
		defultLore.add(white+" ");
		defultLore.add(green+"❘"+gray+" 1 포인트 스텟 증가량 ");
		defultLore.add(gray+bold+"  [ "+red+upGage+gray+bold+" ]  ");
		defultLore.add(white+" ");
		defultLore.add(green+"❘"+gray+" 남은 스텟 포인트 - "+green+beingStat+" ");
		defultLore.add(white+" ");
		//defultLore.add(gray+"▁▁▁▁▁▁▁▁▁▁");
		
		return defultLore;
	}
	
	public List<String> setProfileLore(Player p) {
		StatDTO s = stat.get(p.getUniqueId());
		List<String> l = new ArrayList<String>();
		
		//l.add(gray+"▁▁▁▁▁▁▁▁▁▁");
		l.add(white+" "); 
		l.add(green+"❘"+gray+" 공격력 "+green+"+"+(s.getPower()*powerAdd));
		l.add(green+"❘"+gray+" 체력 "+green+"+"+(s.getHealth()*HealthAdd));
		l.add(green+"❘"+gray+" 속도 "+green+"+"+(s.getSpeed()*speedAdd)+"%");
		l.add(green+"❘"+gray+" 치명타 확률 "+green+"+"+(s.getCriticalPer()*criticalAdd)+"%");
		l.add(green+"❘"+gray+" 치명타 데미지 "+green+"+"+(s.getCriticalDamagePer()*criticalDamageAdd)+"%");
		l.add(green+"❘"+gray+" 회피율 "+green+"+"+(s.getMiss()*missAdd)+"%");
		l.add(green+"❘"+gray+" 스킬 쿨타임 감소 "+green+"+"+(s.getCoolTimePer()*coolTimeAdd)+"%");
		l.add(white+" ");
		l.add(green+"❘"+gray+" 총 추가된 스텟 "+green+(s.getPower()+s.getHealth()+s.getSpeed()+
				s.getCriticalPer()+s.getCriticalDamagePer()+s.getMiss()+s.getCoolTimePer()));
		l.add(white+"  ");
		//l.add(gray+"▁▁▁▁▁▁▁▁▁▁");
		
		return l;
	}
	
}
