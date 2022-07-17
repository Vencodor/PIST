package PIST.Interact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import PIST.Pist;
import PIST.DataObject.GatherInfoDTO;
import PIST.ServerManager.PistPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Gathering extends PistPlugin implements Listener, CommandExecutor {

	public Gathering(Pist ser) {
		super(ser);
	}

	public static HashMap<Integer, GatherInfoDTO> gathers = new HashMap<Integer, GatherInfoDTO>();

	private static HashMap<String, Integer> editMode = new HashMap<>();

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (e.getTo().distance(e.getFrom()) > 0.01) {
			Player p = e.getPlayer();
			Location pLoc = p.getLocation();
			ArrayList<GatherInfoDTO> gList = new ArrayList<GatherInfoDTO>();
			for (GatherInfoDTO a : gathers.values()) {
				if (a.getLoc().distance(e.getTo()) <= 3.5) {
					if (!(a.getLoc().distance(e.getFrom()) <= 3.5)) { // 반경 3.5블럭 내에 진입했을때
						if (isClear(p, a.getCode())) // 만약 이미 수락 했다면
							continue;
						gList.add(a);
					}
				}
			}
			if (gList.size() == 0)
				return;
			else {
				//BLOCK_NOTE_PLING 높이 2 하면 띵
				p.playSound(pLoc, Sound.ENTITY_PLAYER_LEVELUP, 1, 1.6F);
				p.sendMessage(white + " ");
				for (GatherInfoDTO a : gList) {
					TextComponent mainComponent = new TextComponent(orange + a.getName() + gray + "(이)가 당신에게 관심을 보입니다. ");
					TextComponent agreeComponent = new TextComponent("§a§l[ 상호작용 ]");
					agreeComponent.setClickEvent(
							new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/상호 상호작용수락하기 " + a.getCode()));
					mainComponent.addExtra(agreeComponent);
					p.spigot().sendMessage(mainComponent);
				}
				p.sendMessage(white + " ");
			}
		}
	}

	public boolean isClear(Player p, int code) {
		ArrayList<UUID> list = gathers.get(code).getClearUser();
		for (UUID a : list) {
			if (p.getUniqueId().equals(a))
				return true;
		}
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { // 접두사 상호
		Player p = (Player) sender;
		if (args.length == 0) {
			p.sendMessage(gray + "/상호 " + green + "목록");
			p.sendMessage(gray + "/상호 " + green + "추가 <이름>");
			p.sendMessage(gray + "/상호 " + green + "제거 <코드>");
			p.sendMessage(gray + "/상호 " + green + "복사 <코드>");
			p.sendMessage(gray + "/상호 " + green + "설명 <코드>");
			p.sendMessage(gray + "/상호 " + green + "필요 <코드> <돈/레벨/등급>");
		} else if (args[0].equals("상호작용수락하기")) {
			if (args.length > 1) {
				int code = 0;
				try {
					code = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					p.sendMessage(red + "숫자만 입력하세요.");
					return true;
				}
				if (gathers.get(code) == null) {
					p.sendMessage(red + "해당 코드를 가진 상호작용이 없습니다.");
					return true;
				}
				if (isClear(p, code)) {
					p.sendMessage(red + "당신은 이미 이 대상과의 상호작용을 한 적이 있습니다.");
					return true;
				}
				GatherInfoDTO g = gathers.get(code);
				g.getClearUser().add(p.getUniqueId());
				p.getInventory().addItem(g.getItem());
				p.sendMessage(orange + "당신은 " + orange + g.getName() + orange + "과의 상호작용을 진행하였습니다.");
				p.sendMessage(red+"어딘가 이상한 느낌이 듭니다.");
			}
		} else {
			if(!p.isOp()) {
				return true; 
			}
			if (args[0].equals("목록")) {
				for (GatherInfoDTO a : gathers.values()) {
					p.sendMessage(gray + a.getCode() + "# " + a.getName());
				}
			} else if (args[0].equals("추가")) {
				if (args.length < 2) {
					p.sendMessage(red + "/상호 추가 <이름>");
				} else {
					GatherInfoDTO g = new GatherInfoDTO();
					g.setCode(gathers.size());
					g.setName(args[1]);
					g.setLoc(p.getLocation());
					gathers.put(gathers.size(), g);
					p.sendMessage(green + "당신의 위치에 상호작용이 추가되었습니다. " + gray + "#" + (gathers.size() - 1));
				}
			} else if (args[0].equals("제거")) {
				if (args.length < 2) {
					p.sendMessage(red + "/상호 제거 <코드>");
				} else {
					int code = 0;
					try {
						code = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						p.sendMessage(red + "숫자만 입력하세요.");
						return true;
					}
					if (gathers.get(code) == null) {
						p.sendMessage(red + "해당 코드를 가진 상호작용이 없습니다.");
						return true;
					}
					gathers.remove(code);
					p.sendMessage(red + code + "번이 삭제되었습니다.");
				}
			} else if (args[0].equals("복사")) {
				if (args.length < 2) {
					p.sendMessage(red + "/상호 복사 <코드>");
				} else {
					int code = 0;
					try {
						code = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						p.sendMessage(red + "숫자만 입력하세요.");
						return true;
					}
					if (gathers.get(code) == null) {
						p.sendMessage(red + "해당 코드를 가진 상호작용이 없습니다.");
						return true;
					}
					gathers.put(gathers.size(), gathers.get(code));
					gathers.get(gathers.size() - 1).setCode(gathers.size() - 1);
					p.sendMessage(green + code + "번의 복사본을 당신의 위치에 추가하였습니다.");
				}
			} else if (args[0].equals("설명")) {
				if (args.length < 2) {
					p.sendMessage(red + "/상호 설명 <코드>");
				} else {
					int code = 0;
					try {
						code = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						p.sendMessage(red + "숫자만 입력하세요.");
						return true;
					}
					if (gathers.get(code) == null) {
						p.sendMessage(red + "해당 코드를 가진 상호작용이 없습니다.");
						return true;
					}
					editMode.put(p.getName(), code);
					p.sendMessage(gray + "채팅창에 설명을 입력하세요. " + red + "취소" + gray + "를 입력하면 취소됩니다.");
				}
			} else if (args[0].equals("보상")) { // 여기 만들것
				if (args.length < 2) {
					p.sendMessage(red + "/상호 보상 <코드>");
				} else {
					int code = 0;
					try {
						code = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						p.sendMessage(red + "숫자만 입력하세요.");
						return true;
					}
					if (gathers.get(code) == null) {
						p.sendMessage(red + "해당 코드를 가진 상호작용이 없습니다.");
						return true;
					}
					ItemStack item = p.getInventory().getItemInHand();
					if (item == null || item.getType() == Material.AIR) {
						p.sendMessage(red + "보상 아이템을 비웠습니다.");
						gathers.get(code).setItem(new ItemStack(Material.AIR));
					} else {
						gathers.get(code).setItem(item);
						p.sendMessage(green + "성공적으로 아이템을 설정하였습니다.");
					}
				}
	
			} else if (args[0].equals("필요")) {
				if (args.length < 2) {
					p.sendMessage(red + "/상호 보상 <코드>");
				} else {
					int code = 0;
					try {
						code = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						p.sendMessage(red + "숫자만 입력하세요.");
						return true;
					}
					if (gathers.get(code) == null) {
						p.sendMessage(red + "해당 코드를 가진 상호작용이 없습니다.");
						return true;
					}
					// 여기 이제 개발필요
				}
			}
		}
		return true;
	}

	@EventHandler
	public void onChat(PlayerChatEvent e) {
		Player p = e.getPlayer();
		if (editMode.get(p.getName()) != null) {
			int code = editMode.get(p.getName());
			editMode.remove(p.getName());
			if (e.getMessage().equals("취소")) {
				p.sendMessage(red + "설명 편집모드가 취소되었습니다.");
				return;
			}
			gathers.get(code).setDescription(e.getMessage().replace("&", "§"));
			p.sendMessage(green + "성공적으로 설명이 변경되었습니다.");
		}
	}

}
