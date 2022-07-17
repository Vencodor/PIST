package PIST.Land;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import PIST.Pist;
import PIST.PistScoreBoard;
import PIST.DataObject.LandDTO;
import PIST.Enum.LandType;
import PIST.Riding.PistRiding;
import PIST.ServerManager.PistPlugin;
import PIST.UserManager.PistUser;
import PIST.Utility.Utility;

public class PistLandManager extends PistPlugin implements CommandExecutor, Listener {

	Pist plugin;
	
	public PistLandManager(Pist ser) {
		super(ser);
	}
	
	public void setPlugin(Pist pist) {
		this.plugin = pist;
	}

	public static HashMap<String, LandDTO> land = new HashMap<String, LandDTO>();
	
	public static HashMap<String, String> editMode = new HashMap<String, String>();
	public static HashMap<String, String> displayMode = new HashMap<String, String>();
	public static HashMap<String, String> descriptionMode = new HashMap<String, String>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;
		if (!p.isOp())
			return true;
		if (args.length == 0) {
			p.sendMessage(green + "/땅 생성");
			p.sendMessage(green + "/땅 영역 <code>");
			p.sendMessage(green + "/땅 설정 <code> <type>");
			p.sendMessage(green + "/땅 설명 <code> <type>");
			p.sendMessage(green + "/땅 제한 <code> <type>");
			p.sendMessage(green + "/땅 타입 <code> <type>");
			p.sendMessage(green + "/땅 제거 <code>");
			p.sendMessage(green + "/땅 목록");
		} else {
			if (args[0].equalsIgnoreCase("생성")) {
				String key = land.size()+"";
				land.put(key, new LandDTO());
				land.get(key).setCode(key);
				p.sendMessage(gray + "코드 " + key + " 으로 땅이 생성되었습니다.");
			} else if (args[0].equalsIgnoreCase("영역")) {
				if (args.length == 1) {
					p.sendMessage(red + "땅 코드를 추가로 입력하세요.");
				} else {
					if (land.get(args[1]) == null) {
						p.sendMessage(red + "해당 땅 코드를 가진 땅이 존재하지 않습니다.");
						return true;
					}
					if (editMode.get(p.getName()) == null) {
						editMode.put(p.getName(), args[1]);
						p.sendMessage(green + "당신은 영역 편집모드에 진입하였습니다. 금 도끼이로 좌/우클릭을 통해 영역을 설정하세요.");
						p.sendMessage(gray + "명령어를 다시 입력하면 편집모드가 해제됩니다.");
					} else {
						editMode.remove(p.getName());
						p.sendMessage(red + "영역 편집모드에서 나갔습니다.");
					}
				}
			} else if (args[0].equalsIgnoreCase("설정")) {
				if (args.length <= 2) {
					p.sendMessage(red + "/땅 설정 <code> [설치/채팅/파괴/싸움]");
				} else {
					if (land.get(args[1]) == null) {
						p.sendMessage(red + "해당 땅 코드를 가진 땅이 존재하지 않습니다.");
						return true;
					}
					LandDTO l = land.get(args[1]);
					if (args[2].equals("설치")) {
						l.setCanPlace(l.isCanPlace() ? false : true);
					} else if (args[2].equals("채팅")) {
						l.setCanChat(l.isCanChat() ? false : true);
					} else if (args[2].equals("파괴")) {
						l.setCanBreak(l.isCanBreak() ? false : true);
					} else if (args[2].equals("싸움")) {
						l.setCanPvp(l.isCanPvp() ? false : true);
					} else {
						p.sendMessage(red + "[설치/채팅/파괴/싸움] 중 하나를 입력하세요.");
						return true;
					}
					p.sendMessage(green + "설정되었습니다. #" + l.getCode());
					p.sendMessage(gray + "설치 - " + red + l.isCanPlace());
					p.sendMessage(gray + "채팅 - " + red + l.isCanChat());
					p.sendMessage(gray + "파괴 - " + red + l.isCanBreak());
					p.sendMessage(gray + "싸움 - " + red + l.isCanPvp());
				}
			} else if (args[0].equalsIgnoreCase("제한")) {
				if (args.length <= 2) {
					p.sendMessage(red + "/땅 제한 <code> [레벨/아이템/접근금지]");
				} else {
					if (land.get(args[1]) == null) {
						p.sendMessage(red + "해당 땅 코드를 가진 땅이 존재하지 않습니다.");
						return true;
					}
					LandDTO l = land.get(args[1]);
					if (args[2].equals("레벨")) {
						if (args.length <= 3) {
							p.sendMessage(red + "/땅 제한 <code> 레벨 <레벨>");
							return true;
						} else {
							int level = 1;
							try {
								level = Integer.parseInt(args[3]);
							} catch (NumberFormatException e) {
								p.sendMessage(red + "숫자를 입력하세요.");
								return true;
							}
							l.setLevelLock(level);
						}
					} else if (args[2].equals("아이템")) {
						l.setItemLock(p.getInventory().getItemInMainHand());
					} else if (args[2].equals("접근금지")) {
						l.setLock(l.isLock() ? false : true);
					} else {
						p.sendMessage(red + "[레벨/아이템] 중 하나를 입력하세요.");
						return true;
					}
					p.sendMessage(green + "설정되었습니다. #" + l.getCode());
					p.sendMessage(gray + "레벨 - " + red + l.getLevelLock());
					p.sendMessage(gray + "접근금지 - " + red + l.isLock());
					p.sendMessage(gray + "아이템 - " + l.getItemLock());
				}
			} else if (args[0].equalsIgnoreCase("설명")) {
				if (args.length == 1) {
					p.sendMessage(red + "땅 코드를 추가로 입력하세요.");
				} else {
					if (land.get(args[1]) == null) {
						p.sendMessage(red + "해당 땅 코드를 가진 땅이 존재하지 않습니다.");
						return true;
					}
					if (args.length <= 2) {
						p.sendMessage(red + "/땅 설명 <code> [설명/이름]");
					} else {
						if (args[2].equals("설명")) {
							descriptionMode.put(p.getName(), args[1]);
							p.sendMessage(gray + "채팅에 설명을 입력하세요. " + red + "취소" + gray + "를 입력하면 취소됩니다.");
						} else if (args[2].equals("이름")) {
							displayMode.put(p.getName(), args[1]);
							p.sendMessage(gray + "채팅에 표시될 땅이름을 입력하세요. " + red + "취소" + gray + "를 입력하면 취소됩니다.");
						} else {
							p.sendMessage(red + "[설명/이름] 중 하나를 입력하세요.");
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("제거")) {
				if (args.length == 1) {
					p.sendMessage(red + "땅 코드를 추가로 입력하세요.");
				} else {
					if (land.get(args[1]) == null) {
						p.sendMessage(red + "해당 땅 코드를 가진 땅이 존재하지 않습니다.");
						return true;
					}
					land.remove(args[1]);
					p.sendMessage(gray + "코드 " + args[0] + "# 를 삭제했습니다.");
				}
			} else if (args[0].equalsIgnoreCase("목록")) {
				for (LandDTO a : land.values()) {
					p.sendMessage(gray + a.getCode() + "# " + a.getName());
				}
			} else if(args[0].equalsIgnoreCase("타입")) {
				if (args.length == 1) {
					p.sendMessage(red + "땅 코드를 추가로 입력하세요.");
				} else {
					if (land.get(args[1]) == null) {
						p.sendMessage(red + "해당 땅 코드를 가진 땅이 존재하지 않습니다.");
						return true;
					}
					if(args.length<=2) {
						p.sendMessage(red + "타입을 추가로 입력하세요.");
						p.sendMessage(gray+"[던전/이벤트_던전/평지/스폰/성/마을/섬/보스방/기타]");
					} else {
						try {
							land.get(args[1]).setType(LandType.valueOf(args[2]));
							p.sendMessage(green+"타입 설정이 완료되었습니다.");
						} catch(Exception e) {
							p.sendMessage(red+"[던전/이벤트_던전/평지/스폰/성/마을/섬/보스방/기타]");
						}
					}
				}
			}
		}
		return true;
	}
	
	public String getPlayerAreaName(Player p) {
		for (LandDTO a : land.values()) {
			if (a.getLoc1() == null || a.getLoc2() == null)
				continue;
			if (isInArea(p.getLocation(), a.getLoc1(), a.getLoc2())) {
				return a.getDisplay();
			}
		}
		return "없음";
	}
	
	public LandDTO getLand(Location l) {
		for (LandDTO a : land.values()) {
			if (a.getLoc1() == null || a.getLoc2() == null)
				continue;
			if (isInArea(l, a.getLoc1(), a.getLoc2())) {
				return a;
			}
		}
		return null;
	}
	
	public LandDTO getPlayerArea(Player p) {
		for (LandDTO a : land.values()) {
			if (a.getLoc1() == null || a.getLoc2() == null)
				continue;
			if (isInArea(p.getLocation(), a.getLoc1(), a.getLoc2())) {
				return a;
			}
		}
		return null;
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (e.getTo().distance(e.getFrom()) > 0.01) {
			Player p = e.getPlayer();
			for (LandDTO a : land.values()) {
				if (a.getLoc1() == null || a.getLoc2() == null)
					continue;
				if (isInArea(e.getTo(), a.getLoc1(), a.getLoc2())) {
					
					if (a.isLock() || a.getLevelLock() > PistUser.getInfo(p).getLevel()) {
						PistRiding.removeHorse(p);
						Vector v = new Vector();
						Vector vel = e.getTo().toVector().subtract(e.getFrom().toVector());
						v = new Vector(Math.signum(vel.getX())  *-1.2, 0.6, Math.signum(vel.getZ()) *-1.2);
						p.setVelocity(v);
						if (a.isLock())
							p.sendMessage(red + "접근이 불가능합니다.");
						else if (a.getLevelLock() > PistUser.getInfo(p).getLevel())
							p.sendMessage(red + "레벨이 낮아 접근이 불가능합니다.");
					} else if (!isInArea(e.getFrom(), a.getLoc1(), a.getLoc2())) {
						// 영역 입장
						p.sendTitle(gray+"< "+green+a.getDisplay()+gray+" >", 
								orange+a.getDescription());
						
						if(a.getType() == LandType.던전||a.getType() == LandType.이벤트_던전) {
							p.playSound(p.getLocation(), Sound.AMBIENT_CAVE, 1, 1);
							p.sendMessage(red+bold+"* 주의");
							p.sendMessage(gray+"당신은 던전에 입장하였습니다.");
						}
						
						/*
						p.sendMessage(white+" ");
						p.sendMessage(gray+" [ "+green+a.getDisplay()+gray+" ] ");
						p.sendMessage(gray+a.getDescription());
						p.sendMessage(white+" ");
						*/
						
						PistScoreBoard scM = new PistScoreBoard(plugin);
						scM.reloadScoreBoard(p);
					}
				} else if (isInArea(e.getFrom(), a.getLoc1(), a.getLoc2())) {
					if (!isInArea(e.getTo(), a.getLoc1(), a.getLoc2())) {
						p.sendMessage("영역 퇴장 감지");
						return;
					}
				}
			}
			// 이곳은 플레이어가 어떠한 영역에도 속해있지 않은곳
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (p.getInventory().getItemInMainHand().getType() == Material.GOLD_AXE) {
			if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				String key = editMode.get(p.getName());
				if (key == null)
					return;
				LandDTO l = land.get(key);
				l.setLoc1(e.getClickedBlock().getLocation());
				p.sendMessage(gray + "위치 1을 잡았습니다.");
				e.setCancelled(true);
			} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				String key = editMode.get(p.getName());
				if (key == null)
					return;
				LandDTO l = land.get(key);
				l.setLoc2(e.getClickedBlock().getLocation());
				p.sendMessage(gray + "위치 2를 잡았습니다.");
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onChat(PlayerChatEvent e) {
		Player p = e.getPlayer();
		String name = p.getName();
		String message = e.getMessage();
		if (message.equals("취소")) {
			p.sendMessage(red + "이름 편집모드가 취소되었습니다.");
			return;
		} if (displayMode.get(name) != null) {
			LandDTO l = land.get(displayMode.get(name));
			l.setDisplay(message.replace("&", "§"));
			displayMode.remove(name);
			e.setCancelled(true);
			p.sendMessage(green + "성공적으로 display 이 변경되었습니다.");
			p.sendMessage(l.getDisplay());
		} else if (descriptionMode.get(name) != null) {
			LandDTO l = land.get(descriptionMode.get(name));
			l.setDescription(message.replace("&", "§"));
			descriptionMode.remove(name);
			e.setCancelled(true);
			p.sendMessage(green + "성공적으로 description 이 변경되었습니다.");
			p.sendMessage(l.getDescription());
		}
	}
}
