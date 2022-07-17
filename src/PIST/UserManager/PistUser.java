package PIST.UserManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import PIST.Pist;
import PIST.DataObject.User.UserRecordDTO;
import PIST.ServerManager.PistPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PistUser extends PistPlugin implements CommandExecutor, Listener, TabCompleter {
	public static HashMap<UUID, UserRecordDTO> user = new HashMap<UUID, UserRecordDTO>();

	public static double healthAdd = 6.5;
	public static double speedAdd = 0.0022;
	public static double powerAdd = 1.8;

	public PistUser(Pist ser) {
		super(ser);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		checkHasInfo(e.getPlayer());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;
		UUID uid = p.getUniqueId();

		checkHasInfo(p);
		UserRecordDTO r = user.get(uid);

		if (label.equalsIgnoreCase("피스") || label.equalsIgnoreCase("돈")) {
			if (args.length == 0) {
				TextComponent info = new TextComponent(
						orange + "❘" + gray + " 현재 보유중인 피스 : " + green + showMoney(r.getMoney()));
				info.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("§a" + r.getMoney() + " §7만큼의 피스을 보유중입니다.").create()));
				TextComponent sendM = new TextComponent(
						orange + "❘" + gray + " /피스 " + green + "보내기 " + gray + "[대상] [금액]");
				sendM.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("§a[대상]§7 에게 §a[금액]§7만큼의 피스을 보냅니다.").create()));
				TextComponent rankM = new TextComponent(orange + "❘" + gray + " /피스 " + green + "순위");
				rankM.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("§7클릭시 §a피스순위 §7를 확인합니다.").create()));
				rankM.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/피스 순위"));

				p.sendMessage(white + " ");
				p.spigot().sendMessage(info);
				p.sendMessage(white + " ");
				p.spigot().sendMessage(sendM);
				p.spigot().sendMessage(rankM);
				p.sendMessage(white + " ");
			} else {
				if (args[0].equalsIgnoreCase("보내기")) {
					if (args.length <= 2) {
						p.sendMessage(gray + "/피스 보내기 " + red + "[대상] [금액]");
					} else {
						OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
						if (target == null || user.get(target.getUniqueId()) == null) {
							p.sendMessage(red + "대상을 찾을 수 없습니다!");
							return true;
						} else {
							int money = 0;
							try {
								money = Integer.parseInt(args[2]);
							} catch (NumberFormatException e) {
								p.sendMessage(red + "금액에는 올바른 숫자값을 입력하세요!");
								return true;
							}
							if (r.getMoney() < money) {
								p.sendMessage(red + "피스가 부족합니다! " + gray + (r.getMoney() - money));
							} else {
								UserRecordDTO t = user.get(target.getUniqueId());
								user.get(uid).setMoney(r.getMoney() - money);
								user.get(target.getUniqueId()).setMoney(t.getMoney() + money);
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("순위")) {
					int page = 0;
					if (args.length == 2) {
						try {
							page = Integer.parseInt(args[1]);
						} catch (NumberFormatException e) {
							p.sendMessage(gray + "/피스 순위 " + red + "[페이지]" + gray + ", 페이지가 숫자가 아니기 때문에 기본값 " + green
									+ "0 페이지" + gray + "를 표시합니다.");
							page = 0;
						}
					}
					List<UserRecordDTO> moneyList = user.values().stream().sorted((a, b) -> b.getMoney() - a.getMoney())
							.collect(Collectors.toList());
					if (moneyList == null || moneyList.size() == 0) {
						p.sendMessage(red + "유저 랭킹에 관한 정보가 단 한개도 없습니다..");
						return true;
					}

					p.sendMessage(
							gray + "〓〓〓〓 " + green + page * 10 + " 순위 ~ " + (page * 10 + 10) + "순위 " + gray + "〓〓〓〓");
					for (int i = page * 10; i <= (page * 10) + 10; i++) {
						if (i >= moneyList.size()) {
							p.sendMessage(gray + "〓〓" + red + " 더이상 정보가 없습니다! " + gray + "〓〓");
							return true;
						}
						if (moneyList.get(i) != null) {
							UserRecordDTO u = moneyList.get(i);
							TextComponent rankM = new TextComponent(orange + (i + 1) + "등, " + green + u.getName()
									+ gray + " " + showMoney(u.getMoney()));
							rankM.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder("§7클릭시 해당 유저의 §a정보§7를 봅니다.").create()));
							rankM.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND,
									"/정보 " + u.getName()));
							p.spigot().sendMessage(rankM);
						} else {
							p.sendMessage(gray + "〓〓" + red + " 더이상 정보가 없습니다! " + gray + "〓〓");
							p.sendMessage(white + " ");
							p.sendMessage(gray + "당신의 순위 : " + orange + searchRank(p, moneyList));
							return true;
						}
					}
					TextComponent nextPage = new TextComponent(gray + "〓〓〓" + orange + " 다음페이지 보기 " + gray + "〓〓〓");
					nextPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder("§7다음 페이지를 보려면 클릭하세요.").create()));
					nextPage.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND,
							"/피스 순위 " + (page + 1)));

					p.spigot().sendMessage(nextPage);
				} else if (args[0].equalsIgnoreCase("관리")) {
					if (!p.isOp())
						return true;
					if (args.length == 1) {
						p.sendMessage(gray + "/피스 관리 확인 [이름]");
						p.sendMessage(gray + "/피스 관리 지급 [이름] [갯수]");
						p.sendMessage(gray + "/피스 관리 설정 [이름] [갯수]");
					} else {
						OfflinePlayer tP = Bukkit.getOfflinePlayer(args[2]);
						UUID u = tP.getUniqueId();
						if (user.get(u) == null) {
							p.sendMessage(red + "해당 플레이어는 존재하지 않습니다.");
							return true;
						}
						UserRecordDTO info = user.get(u);
						if (args[1].equalsIgnoreCase("확인")) {
							if (args.length == 3) {
								p.sendMessage(green + tP.getName() + green + "님의 피스 : " + info.getMoney());
							}
						} else if (args[1].equalsIgnoreCase("지급")) {
							if (args.length == 4) {
								try {
									user.get(u).setMoney(info.getMoney() + Integer.parseInt(args[3]));
								} catch (NumberFormatException e) {
									p.sendMessage(red + "숫자를 입력하세요.");
									return true;
								}
								p.sendMessage(gray + info.getMoney() + " -> " + user.get(u).getMoney());
							}
						} else if (args[1].equalsIgnoreCase("설정")) {
							if (args.length == 4) {
								try {
									user.get(u).setMoney(Integer.parseInt(args[3]));
								} catch (NumberFormatException e) {
									p.sendMessage(red + "숫자를 입력하세요.");
									return true;
								}
								p.sendMessage(gray + info.getMoney() + " -> " + user.get(u).getMoney());
							}
						}
					}
				}
			}

		} else if (label.equalsIgnoreCase("레벨")) {
			if (args.length == 0) {
				TextComponent showAb = new TextComponent(
						dgray + "  [" + gray + "이곳을 클릭하여 레벨 보정 능력치를 확인하세요" + dgray + "]");
				showAb.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("§7당신의 현재 보정 능력치를 확인합니다.").create()));
				showAb.setClickEvent(
						new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/레벨 보정능력치"));

				TextComponent per = new TextComponent(dpurple + "❘ " + gray + "        [ " + green + r.getLevelUp()
						+ gray + " / " + dpurple + getMaxLevel(r.getLevel()) + gray + " ]        ");
				per.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("§7[ 현재 레벨 달성치 / 필요 레벨 달성치 ] 입니다. 현재 달성도는 §a"
								+ getLevelPer(r.getLevel(), r.getLevelUp()) + "% §7입니다.").create()));

				p.sendMessage(white + " ");
				p.sendMessage(dpurple + "❘ " + gray + "당신의 레벨 : " + green + r.getLevel() + "Lv.");
				p.spigot().sendMessage(showAb);
				p.sendMessage(white + " ");
				p.spigot().sendMessage(per);
				p.sendMessage(white + " ");
				/*
				 * String levelBar = ""; for(int i=0; i<10-(getLevelPer(r.getLevel())); i++) {
				 * levelBar = levelBar + "⬛"; } for(int i=0; i<10-levelBar.length(); i++) {
				 * levelBar = levelBar + "⬜"; }
				 * p.sendMessage(levelBar+(10-(getLevelPer(r.getLevel()))));
				 */
			} else {
				if (args[0].equalsIgnoreCase("보정능력치")) {
					p.sendMessage(white + " ");
					p.sendMessage(purple + "❘ " + gray + "추가된 레벨 비례 보정 능력치");
					p.sendMessage(white + " ");
					p.sendMessage(red + "❘ " + gray + "공격력 : " + purple + r.getLevel() * powerAdd);
					p.sendMessage(dred + "❘ " + gray + "체력 : " + purple + r.getLevel() * healthAdd);
					p.sendMessage(aqua + "❘ " + gray + "신속 : " + purple
							+ Double.parseDouble(String.format("%.2f", r.getLevel() * speedAdd)));
					p.sendMessage(white + " ");
				} else if (args[0].equalsIgnoreCase("설정")) {
					if (!p.isOp())
						return true;
					if (args.length < 3) {
						p.sendMessage(red + "/레벨 설정 [대상] [레벨]");
					} else {
						OfflinePlayer tP = Bukkit.getOfflinePlayer(args[1]);
						UUID uuid = tP.getUniqueId();

						if (user.get(uuid) == null) {
							p.sendMessage(red + "해당 플레이어는 존재하지 않습니다.");
							return true;
						} else {
							int level = 0;
							try {
								level = Integer.parseInt(args[2]);
							} catch (NumberFormatException e) {
								p.sendMessage(red + "숫자만 입력 가능합니다.");
								return true;
							}
							user.get(uuid).setLevel(level);
							p.sendMessage(green + tP.getName() + "님의 레벨이 " + level + "로 설정 되었습니다.");
						}
					}
				}
			}
		}

		return true;
	}

	private int searchRank(Player p, List<UserRecordDTO> ranking) {
		for (UserRecordDTO a : ranking) {
			if (a.getName().equals(p.getName())) {
				return ranking.indexOf(a);
			}
		}
		return -1;
	}

	public static UserRecordDTO getInfo(Player p) {
		UUID uid = p.getUniqueId();
		checkHasInfo(p);
		return user.get(uid);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("피스")) {
			if (args.length == 1) {
				ArrayList<String> cmdList = new ArrayList<String>();

				cmdList.add("보내기");
				cmdList.add("순위");

				return cmdList;
			}
		}
		return null;
	}

	private static void checkHasInfo(Player p) {
		UUID uid = p.getUniqueId();
		if (user.get(uid) == null) {
			TextComponent sendM = new TextComponent(ChatColor.GREEN + "당신의 정보가 새롭게 생성되었습니다!");
			sendM.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("§7당신의 §a유저 데이터§7(피스, 랭크, 등)이 §a새롭게 생성§7 되었음을 의미합니다.").create()));

			UserRecordDTO u = new UserRecordDTO();
			u.setName(p.getName());
			user.put(uid, u);
			p.spigot().sendMessage(sendM);
		}
	}

}
