package PIST.MobManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarFlag;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;

import PIST.Pist;
import PIST.Entity.CustomPet;
import PIST.Entity.CustomPig;
import PIST.Entity.Test;
import PIST.Enum.Attribute;
import PIST.Enum.CustomEntities;
import PIST.Enum.Mob;
import PIST.Mob.DataObject.MobDTO;
import PIST.Mob.DataObject.MobLocationDTO;
import PIST.Quest.DataObject.MyQuestDTO;
import PIST.Quest.DataObject.QuestDTO;
import PIST.ServerManager.PistPlugin;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.GenericAttributes;

public class PistMobManager extends PistPlugin implements Listener, CommandExecutor {

	public PistMobManager(Pist ser) {
		super(ser);
	}

	public static HashMap<Entity, MobDTO> mobs = new HashMap<Entity, MobDTO>();
	public static HashMap<Integer, MobLocationDTO> mobLoc = new HashMap<Integer, MobLocationDTO>();
	
	private static HashMap<String,UUID> playerPet = new HashMap<String,UUID>();

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (e.getTo().distance(e.getFrom()) > 0.01) {
			Player p = e.getPlayer();
			Location pLoc = p.getLocation();
			for(MobLocationDTO a : mobLoc.values()) {
				if(e.getFrom().distance(a.getLoc()) > 8&&e.getTo().distance(a.getLoc()) <= 8) { //만약 해당 던전에 진입하면
					int count = 0;
					for(Entity entity : p.getNearbyEntities(8, 8, 8)) { //만약 플레이어 주위에 플레이어를 제외한 몬스터가 6마리 이상 있다면 생성하지 않음
						try {
							if(entity instanceof Player) continue;
							LivingEntity living = (LivingEntity) entity;
							if(living.isDead()) continue;
							count++;
						} catch(Exception e1) {
							continue;
						}
					}
					if(count>=6) return;
					
					for(MobDTO b : mobs.values()) {
						if(b.getParentSpawner().equals(a)) //이미 소환된 몹이 있다면 취소
							return;
					}
					//((CraftWorld)pLoc.getWorld()).getHandle()
					//.addEntity(new CustomPet(a.getLoc(),p),SpawnReason.CUSTOM);
					Entity entity = spawnMob(a.getMob(),a.getLoc());
					if(entity==null) return;
					mobs.put(entity, new MobDTO(entity,a.getMobAtt(),false,false,
							a.getDropItem(),a.getSpecialDropItem(),a.getMob().getExp(),a.getMob().getMoney(),a.getMobDuration(),a));
				}
			}
		}
		for(Entity a : mobs.keySet()) { //위의것과 별개로 몹이 생성 위치에서 15블럭 이상 떨어지면 사라지게
			if(a.getLocation().distance(mobs.get(a).getParentSpawner().getLoc())>=15) {
				a.teleport(mobs.get(a).getParentSpawner().getLoc());
			}
		}
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		if(e.getEntity() instanceof Player) return;
		Entity entity = e.getEntity();
		if(mobs.get(entity)!=null) {
			mobs.remove(entity);
		}
	}
	
	public Entity spawnMob(Mob m,Location loc) {
		Entity e = loc.getWorld().spawnEntity(loc, m.getType());
		LivingEntity le = (LivingEntity) e;
		e.setCustomName(m.getName());
		e.setCustomNameVisible(true);
		
		le.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(m.getHealth());
		le.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(m.getSpeed());
		if(m.getType() != EntityType.PIG)
			le.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(m.getDamage());
		le.setCanPickupItems(false);
		return e;
	}
	/*
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		net.minecraft.server.v1_12_R1.Entity entity = new CustomPet(e.getPlayer().getLocation(),e.getPlayer());
		((CraftWorld)e.getPlayer().getLocation().getWorld()).getHandle().addEntity(entity,SpawnReason.CUSTOM);
		playerPet.put(e.getPlayer().getName(), entity.getUniqueID());
	}
	
	@EventHandler
	public void onExit(PlayerQuitEvent e) {
		if(playerPet.get(e.getPlayer().getName())!=null) {
			net.minecraft.server.v1_12_R1.Entity entity = 
					((CraftWorld)e.getPlayer().getLocation().getWorld()).getHandle().getEntity(playerPet.get(e.getPlayer().getName()));
			entity.die();
		}
	}
	*/
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;
		if (!p.isOp())
			return true;
		if (args.length == 0) {
			p.sendMessage(gray + "/던전 " + green + "[목록/추가/삭제/복사]");
			p.sendMessage(gray + "/던전 " + green + "설정 <코드> [몹/지속시간/몹타입/드랍템/특별드랍템"+dgray+"/돈/경험치/속성"+green+"]");
			p.sendMessage(gray + "/던전 " + green + "소환 <몹>");
			p.sendMessage(gray+"/던전 "+green+"검색 <반경>");
		} else {
			if (args[0].equalsIgnoreCase("목록")) {
				for (MobLocationDTO a : mobLoc.values()) {
					p.sendMessage(gray + a.getCode() + " | " + a.getMob());
				}
			} else if (args[0].equalsIgnoreCase("추가")) {
				mobLoc.put(mobLoc.size(), new MobLocationDTO());
				mobLoc.get(mobLoc.size() - 1).setCode(mobLoc.size() - 1);
				mobLoc.get(mobLoc.size() - 1).setLoc(p.getLocation());
				p.sendMessage(green + "현재 위치에 몹 생성 구역이 추가되었습니다.");
			} else if (args[0].equalsIgnoreCase("삭제")) {
				if (args.length <= 1) {
					p.sendMessage(red + "/던전 삭제 <코드>");
				} else {
					int code = 0;
					try {
						code = Integer.parseInt(args[1]);
					} catch (NumberFormatException e1) {
						p.sendMessage(red + "숫자만 입력하세요.");
						return true;
					}
					if(mobLoc.get(code)==null) {
						p.sendMessage(red+"해당 코드를 가지는 던전이 없습니다.");
						return true;
					}
					mobLoc.remove(code);
					p.sendMessage(gray + "성공적으로 " + green + code + gray + "던전이 삭제되었습니다.");
				}
			} else if(args[0].equals("복사")) {
				if (args.length <= 1) {
					p.sendMessage(red + "/던전 복사 <코드>");
				} else {
					int code = 0;
					try {
						code = Integer.parseInt(args[1]);
					} catch (NumberFormatException e1) {
						p.sendMessage(red + "숫자만 입력하세요.");
						return true;
					}
					if(mobLoc.get(code)==null) {
						p.sendMessage(red+"해당 코드를 가지는 던전이 없습니다.");
						return true;
					}
					mobLoc.put(mobLoc.size(), mobLoc.get(code));
					mobLoc.get(mobLoc.size()-1).setLoc(p.getLocation());
					p.sendMessage(gray + "성공적으로 " + green + code + gray + "던전이 당신의 위치로 복사되었습니다.");
				}
			} else if (args[0].equals("설정")) {
				if (args.length <= 1) {
					p.sendMessage(red + "/던전 설정 <코드> [몹/지속시간/속성/드랍/특별드랍/돈/경험치]");
				} else {
					int code = 0;
					try {
						code = Integer.parseInt(args[1]);
					} catch (NumberFormatException e1) {
						p.sendMessage(red + "숫자만 입력하세요.");
						return true;
					}
					if(mobLoc.get(code)==null) {
						p.sendMessage(red+"해당 코드를 가지는 던전이 없습니다.");
						return true;
					}
					MobLocationDTO ml = mobLoc.get(code);
					if(args[2].equals("몹")) {
						if(args.length<=3) {
							p.sendMessage(red+"/던전 설정 <코드> 몹 <몹 이름>");
							return true;
						}
						try {
							ml.setMob(Mob.valueOf(args[3]));
							p.sendMessage(green+"몹이 설정되었습니다. "+gray+Mob.valueOf(args[3]).getName());
						} catch(Exception e) {
							p.sendMessage(red+"해당하는 몹이 없습니다!");
							p.sendMessage(gray+Mob.values());
						}
					} else if(args[2].equals("지속시간")) {
						if(args.length<=3) {
							p.sendMessage(red+"/던전 설정 <코드> 지속시간 <초>");
							return true;
						}
						int duration = 0;
						try {
							duration = Integer.parseInt(args[3]);
						} catch(NumberFormatException e) {
							p.sendMessage(red+"숫자만 입력하세요.");
							return true;
						}
						ml.setMobDuration(duration);
						p.sendMessage(green+"성공적으로 몹 지속시간이 설정되었습니다. "+gray+duration);
					} else if(args[2].equals("드랍템")) {
						ItemStack item = p.getInventory().getItemInMainHand();
						if(item == null||item.getType() == Material.AIR) {
							p.sendMessage(red+"손에 아이템을 들고 있어야합니다.");
						} else {
							ml.setDropItem(item);
							p.sendMessage(gray+"드랍 아이템이 설정되었습니다.");
						}
					} else if(args[2].equals("특별드랍")) {
						ItemStack item = p.getInventory().getItemInMainHand();
						if(item == null||item.getType() == Material.AIR) {
							p.sendMessage(red+"손에 아이템을 들고 있어야합니다.");
						} else {
							ml.setSpecialDropItem(item);
							p.sendMessage(gray+"특별드랍 아이템이 설정되었습니다.");
						}
					} 
					/*
					else if(args[2].equals("돈")) {
						if(args.length<=3) {
							p.sendMessage(red+"/던전 설정 <코드> 돈 <액수>");
							return true;
						}
						int money = 0;
						try {
							money = Integer.parseInt(args[3]);
						} catch(NumberFormatException e) {
							p.sendMessage(red+"숫자만 입력하세요.");
							return true;
						}
						ml.setMoney(money);
						p.sendMessage(green+"성공적으로 돈이 설정되었습니다. "+gray+money);
					} else if(args[2].equals("경험치")) {
						if(args.length<=3) {
							p.sendMessage(red+"/던전 설정 <코드> 경험치 <경험치>");
							return true;
						}
						int exp = 0;
						try {
							exp = Integer.parseInt(args[3]);
						} catch(NumberFormatException e) {
							p.sendMessage(red+"숫자만 입력하세요.");
							return true;
						}
						ml.setExp(exp);
						p.sendMessage(green+"성공적으로 경험치가 설정되었습니다. "+gray+exp);
					} else if(args[2].equals("속성")) {
						if(args.length<=3) {
							p.sendMessage(red+"/던전 설정 <코드> 속성 <속성>");
							return true;
						}
						try {
							ml.setMobAtt(Attribute.valueOf(args[3]));
							p.sendMessage(green+"몹이 설정되었습니다. "+gray+Attribute.valueOf(args[3]).getName());
						} catch(Exception e) {
							p.sendMessage(red+"해당하는 속성이 없습니다!");
							p.sendMessage(gray+Attribute.values());
						}
					} 
					*/
				}
			} else if(args[0].equals("검색")) {
				if(args.length<=1) {
					p.sendMessage(red+"/던전 검색 <반경>");
					return true;
				}
				int r = 0;
				try {
					r = Integer.parseInt(args[1]);
				} catch (NumberFormatException e1) {
					p.sendMessage(red + "숫자만 입력하세요.");
					return true;
				}
				Location pLoc = p.getLocation();
				p.sendMessage(gray+"반경 "+green+r+"블럭 내의 던전 목록입니다.");
				for(MobLocationDTO a : mobLoc.values()) {
					if(a.getLoc().distance(pLoc)<=r) {
						p.sendMessage(gray+a.getCode()+" | "+a.getMob());
					}
				}
			} else if (args[0].equals("소환")) {

			}
		}
		return true;
	}

}
