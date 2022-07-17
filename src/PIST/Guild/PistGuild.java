package PIST.Guild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import PIST.Pist;
import PIST.PistChat;
import PIST.DataObject.GuildDTO;
import PIST.DataObject.GuildMemberDTO;
import PIST.Enum.GuildRank;
import PIST.ServerManager.PistPlugin;
import PIST.UserManager.PistUser;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PistGuild extends PistPlugin implements CommandExecutor, Listener {

	public PistGuild(Pist ser) {
		super(ser);
	}

	public static ArrayList<GuildDTO> guilds = new ArrayList<GuildDTO>();
	public static HashMap<UUID, GuildMemberDTO> members = new HashMap<UUID, GuildMemberDTO>();

	private HashMap<String, Boolean> canJoinSort = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> topGuildSort = new HashMap<String, Boolean>();

	private HashMap<String, Boolean> setNameMode = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> setDiscriptionMode = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> setNoticeMode = new HashMap<String, Boolean>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("길드")) {
			Player p = (Player) sender;
			if (args.length == 0) {
				sendHelp(p);
			} else {
				if (args[0].equalsIgnoreCase("생성")) {

					if (args.length == 1) {

						p.sendMessage(white + " ");
						TextComponent tx1 = new TextComponent(
								red + " [ 정말 " + bold + "길드 생성권" + red + " 을 사용해 길드를 생성 하시겠습니까? ]  ");
						tx1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("§c클릭시 길드 생성을 수락합니다.").create()));
						tx1.setClickEvent(
								new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/길드 생성 수락하기1"));
						p.spigot().sendMessage(tx1);
						p.sendMessage(gray + "        계속 진핸하려면 텍스트를 클릭하세요.  ");
						p.sendMessage(white + " ");
					} else if (args[1].equalsIgnoreCase("수락하기1")) {
						for (ItemStack a : p.getInventory().getContents()) {
							if (isContainsDisplay(a, "길드 생성권")) {
								if (members.get(p.getUniqueId()) != null) {
									p.sendMessage(red + "당신은 이미 길드에 속해있습니다.");
									return true;
								}

								a.setAmount(a.getAmount() - 1);
								guilds.add(new GuildDTO(p.getName(), p.getUniqueId()));
								p.sendMessage(white + " ");
								sayMessage(blue + "❘ " + green + p.getName() + gray + "님의 길드가 새로 생성되었습니다!");
								p.sendMessage(gray + "길드를 설정하려면 " + green + "/길드 설정 " + gray + "을 이용하세요.");
								p.sendMessage(white + " ");

								sendAd(green + "길드를 생성하였습니다!", gray + "/길드 설정 을 먼저 해주세요.", Material.SIGN, p);

								members.put(p.getUniqueId(),
										new GuildMemberDTO(getGuildUsePlayer(p).getCode(), GuildRank.LEADER));

								return true;
							}
						}
						p.sendMessage(red + bold + " 길드 생성에 실패하였습니다! " + red + "길드 생성권 을 가지고 있어야 합니다.");

					}

				} else if (args[0].equalsIgnoreCase("설정")) {
					for (GuildDTO a : guilds) {
						if (a.getLeader().equals(p.getName())) {
							p.openInventory(getSettingInv(p, a));
							return true;
						}
					}
					p.sendMessage(red + "당신은 길드 마스터가 아닙니다!");
				} else if (args[0].equalsIgnoreCase("목록")) {
					int page = 0;
					if (args.length >= 2) {
						try {
							page = Integer.parseInt(args[1]);
						} catch (NumberFormatException e) {
							p.sendMessage(red + bold + "ERROR! " + red + "숫자를 입력하세요");
							return true;
						}
					}
					p.openInventory(getListInv(p, page));
				} else if (args[0].equalsIgnoreCase("정보")) {
					if (members.get(p.getUniqueId()) == null) {
						p.sendMessage(red + "당신은 길드에 가입되어 있지 않습니다.");
					} else {
						if(args.length==1) {
							p.openInventory(getInfoInv(getGuildUseDTO(members.get(p.getUniqueId())), p, false));
						} else {
							if(getGuildUseCode(args[1])!=null)
								p.openInventory(getInfoInv(getGuildUseCode(args[1]), p, false));
							else
								p.sendMessage(red+"해당 코드를 가지는 길드가 존재하지 않습니다.");
								
						}
					}
				} else if (args[0].equalsIgnoreCase("탈퇴")) {
					if (members.get(p.getUniqueId()) == null) {
						p.sendMessage(red + "당신은 길드에 가입되어 있지 않습니다.");
					} else {
						if (members.get(p.getUniqueId()).getRank() == GuildRank.LEADER) {
							p.sendMessage(white + " ");
							TextComponent tx1 = new TextComponent(
									red + " [ 정말 길드를 " + bold + "해체" + red + " 하시겠습니까? ]  ");
							tx1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder("§c클릭시 길드를 해체합니다.").create()));
							tx1.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND,
									"/길드 탈퇴수락11"));
							p.spigot().sendMessage(tx1);
							p.sendMessage(gray + "        길드가 해체되면 복구가 안됩니다.  ");
							p.sendMessage(white + " ");
						} else {
							p.sendMessage(white + " ");
							TextComponent tx1 = new TextComponent(red + " [ 정말 길드를 탈퇴 하시겠습니까? ]  ");
							tx1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder("§c클릭시 길드를 탈퇴합니다.").create()));
							tx1.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND,
									"/길드 탈퇴수락11"));
							p.spigot().sendMessage(tx1);
							p.sendMessage(gray + "        계속 진행하려면 텍스트를 클릭하세요.  ");
							p.sendMessage(white + " ");
						}
					}
				} else if (args[0].equalsIgnoreCase("추방")) {
					if (getGuildUsePlayer(p) == null) {
						p.sendMessage(red + "당신은 길드장이 아닙니다!");
						return true;
					}
					if (args.length == 1) {
						p.sendMessage(red + "추방할 대상을 입력하세요.");
						return true;
					} else {
						if (p.getName().equals(args[1])) {
							p.sendMessage(red + "본인을 추방할 수 없습니다.");
							return true;
						}
						GuildDTO g = getGuildUsePlayer(p);
						UUID target = getUUID(args[1]);
						if (target == null) {
							p.sendMessage(red + "대상이 존재하지 않습니다.");
						} else if (members.get(target) == null
								|| !(members.get(target).getGuildCode().equals(g.getCode()))) {
							p.sendMessage(red + "대상은 당신의 길드에 속해있지 않습니다.");
						} else {
							if (members.get(target).getRank() == GuildRank.DEPUTY_LEADER) {
								g.setDeputyLeader(null);
								g.setDeputyLeaderUid(null);
							}
							members.remove(target);
							p.sendMessage(green + args[1] + gray + "님을 길드에서 추방하였습니다.");
						}
					}
				} else if (args[0].equalsIgnoreCase("임명")) {
					if (getGuildUsePlayer(p) == null) {
						p.sendMessage(red + "당신은 길드장이 아닙니다!");
						return true;
					}
					if (args.length == 1) {
						p.sendMessage(red + "임명할 대상을 입력하세요.");
						return true;
					} else {
						if (p.getName().equals(args[1])) {
							p.sendMessage(red + "본인을 임명할 수 없습니다.");
							return true;
						}
						GuildDTO g = getGuildUsePlayer(p);
						UUID target = getUUID(args[1]); //여기 문제있음
						if (target == null) {
							p.sendMessage(red + "대상이 존재하지 않습니다.");
						} else if (members.get(target) == null
								|| !(members.get(target).getGuildCode().equals(g.getCode()))) {
							p.sendMessage(red + "대상은 당신의 길드에 속해있지 않습니다.");
						} else {
							if (members.get(target).getRank() == GuildRank.DEPUTY_LEADER) {
								p.sendMessage(red + "대상은 이미 부 길드장입니다.");
								return true;
							}
							g.setDeputyLeader(args[1]);
							g.setDeputyLeaderUid(target);
							p.sendMessage(green + args[1] + gray + "님을 부 길드장으로 임명하였습니다.");
						}
					}
				} else if (args[0].equalsIgnoreCase("탈퇴수락11")) {
					if (members.get(p.getUniqueId()) == null) {
						p.sendMessage(red + "당신은 길드에 가입되어 있지 않습니다.");
					} else if (members.get(p.getUniqueId()).getRank() == GuildRank.LEADER) {
						GuildDTO g = getGuildUseDTO(members.get(p.getUniqueId()));
						for (UUID a : members.keySet()) {
							if (members.get(a).getGuildCode().equals(g.getCode())) {
								members.remove(a);
							}
						}
						guilds.remove(guilds.indexOf(g));
						sayMessage(blue + "❘ " + green + g.getName() + "#" + g.getCode() + gray + " 길드가 해체되었습니다.");
					} else {
						members.remove(p.getUniqueId());
						p.sendMessage(red + "성공적으로 길드를 탈퇴하였습니다.");
					}
				}
			}
		}
		return true;
	}

	@EventHandler
	public void onListInvClick(InventoryClickEvent e) {
		if (e.getInventory().getTitle().contains("길드목록")) {
			e.setCancelled(true);
			ItemStack item = e.getCurrentItem();
			Player p = (Player) e.getWhoClicked();
			if (item == null)
				return;
			if (item.getItemMeta() == null)
				return;
			if (item.getItemMeta().getDisplayName() == null)
				return;

			int page = 0;
			try {
				page = Integer.parseInt(e.getInventory().getTitle().split("#")[1]);
			} catch (NumberFormatException e1) {
				page = 0;
			}

			if (isContainsDisplay(item, "이전")) {
				p.openInventory(getListInv(p, page));
			} else if (isContainsDisplay(item, "다음")) {
				p.openInventory(getListInv(p, page));
			} else if (item.getType() == Material.QUARTZ_ORE) {
				if (canJoinSort.get(p.getName()) == null)
					canJoinSort.put(p.getName(), true);
				else
					canJoinSort.remove(p.getName());
				p.openInventory(getListInv(p, page));
			} else if (item.getType() == Material.MAGMA) {
				if (topGuildSort.get(p.getName()) == null)
					topGuildSort.put(p.getName(), true);
				else
					topGuildSort.remove(p.getName());
				p.openInventory(getListInv(p, page));
			} else if (isContainsDisplay(item, "#")) {
				String code = item.getItemMeta().getDisplayName().split("#")[1];
				p.openInventory(getInfoInv(getGuildUseCode(code), p, true));
			}

		}
	}

	public Inventory getListInv(Player p, int page) {
		Inventory i = Bukkit.createInventory(null, 54, blue + " 길드목록 - " + orange + "전체#" + page);
		if (guilds.size() == 0) {
			i.setItem(31,
					createItem(new ItemStack(Material.BARRIER), red + "표시할 수 있는 길드가 없습니다!", new ArrayList<String>()));
			return i;
		}
		for (int k = page * 45; k < guilds.size() && k < page * 45 + 45; k++) {
			i.setItem(k, getGuildShowItem(guilds.get(k), true));
		}
		i.setItem(47, createItem(new ItemStack(Material.COMPASS), gray + "이전", new ArrayList<String>()));
		if (canJoinSort.get(p.getName()) != null)
			i.setItem(48, createItem(new ItemStack(Material.QUARTZ_ORE), green + "참가 가능한 길드만 표시하기 ( Comming Soon ) ",
					new ArrayList<String>()));
		else
			i.setItem(48, createItem(new ItemStack(Material.QUARTZ_ORE), red + "모든 길드 표시하기 ( Comming Soon ) ",
					new ArrayList<String>()));

		List<String> signLore = new ArrayList<String>();
		signLore.add(gray + "길드를 " + green + "클릭" + gray + "하면 길드 정보를 볼 수 있습니다.");
		signLore.add(white + " ");
		signLore.add(canJoinSort.get(p.getName()) != null ? gray + " 참가 가능한 길드 표시하기 : " + red + "false"
				: gray + " 참가 가능한 길드 표시하기 : " + green + "true");
		signLore.add(topGuildSort.get(p.getName()) != null ? gray + " 상위 길드 정렬하기 : " + green + "true"
				: gray + " 상위 길드 정렬하기 : " + red + "false");
		i.setItem(49, createItem(new ItemStack(Material.SIGN), gray + "길드 목록", signLore));

		if (topGuildSort.get(p.getName()) != null)
			i.setItem(50, createItem(new ItemStack(Material.MAGMA), green + "상위 길드 정렬 ( Comming Soon ) ",
					new ArrayList<String>()));
		else
			i.setItem(50, createItem(new ItemStack(Material.MAGMA), red + "무작위 정렬 ( Comming Soon ) ",
					new ArrayList<String>()));
		i.setItem(51, createItem(new ItemStack(Material.COMPASS), gray + "다음", new ArrayList<String>()));

		return i;
	}

	@EventHandler
	public void onInfoInvClick(InventoryClickEvent e) {
		if (e.getInventory().getTitle().contains("길드정보")) {
			e.setCancelled(true);
			ItemStack item = e.getCurrentItem();
			Player p = (Player) e.getWhoClicked();
			if (isContainsDisplay(item, "#")) {
				// 플레이어 정보보기
			} else if (isContainsDisplay(item, "가입 신청")) {
				if (members.get(p.getUniqueId()) != null) {
					p.sendMessage(red + "당신은 이미 길드에 속해 있습니다.");
					return;
				}
				String code = e.getInventory().getTitle().split("#")[1];
				members.put(p.getUniqueId(), new GuildMemberDTO(code, GuildRank.MEMBER));
				sendAd(green + "길드 가입이 정상적으로 되었습니다!", gray + "", Material.COMPASS, p);
				p.closeInventory();
			}
		}
	}

	public Inventory getInfoInv(GuildDTO g, Player p, boolean show) {
		if (g == null)
			return null;
		Inventory i = Bukkit.createInventory(null, 54,
				blue + " 길드정보 - " + orange + g.getName() + gray + "#" + g.getCode());

		List<String> lore = new ArrayList<String>();
		lore.add(gray + " [ 클릭 시 플레이어의 정보를 확인합니다 ] ");
		i.setItem(1, createItem(getHead(Bukkit.getOfflinePlayer(g.getLeaderUid()).getPlayer()),
				yellow + "대표 " + aqua + g.getLeader(), lore));
		if (g.getDeputyLeader() != null)
			i.setItem(2, createItem(getHead(Bukkit.getOfflinePlayer(g.getDeputyLeaderUid()).getPlayer()),
					yellow + "부대표 " + aqua + g.getDeputyLeader(), lore));
		i.setItem(4, getGuildShowItem(g, false));
		List<String> infoLore = new ArrayList<String>();
		infoLore.add(white + " ");
		infoLore.add(gray + " 레벨 : " + g.getGuildLevel());
		i.setItem(5, createItem(new ItemStack(Material.BOOK), gray + "레벨", infoLore));

		infoLore.set(1, gray + " 시즌 포인트 : " + g.getGuildPoint());
		i.setItem(6, createItem(new ItemStack(Material.BOOK), gray + "시즌 포인트", infoLore));

		infoLore.set(1, gray + " 최대 인원 : " + g.getMaxPeople());
		i.setItem(7, createItem(new ItemStack(Material.BOOK), gray + "최대 인원", infoLore));

		infoLore.set(1, gray + " 레벨 제한 : " + g.getMemberLevelLock());
		i.setItem(8, createItem(new ItemStack(Material.BOOK), gray + "레벨 제한", infoLore));

		int count = 9;
		for (UUID a : members.keySet()) {
			GuildMemberDTO gm = members.get(a);
			if (gm.getGuildCode().equals(g.getCode())) {
				Player offline = Bukkit.getOfflinePlayer(a).getPlayer();
				i.setItem(count, createItem(getHead(offline),
						yellow + gm.getRank().getName() + gray + " " + offline.getName(), lore));
			}
		}

		for (int k = 9; k < 45; k++) {
			if (i.getItem(k) == null)
				i.setItem(k, createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7), " ",
						new ArrayList<String>()));
		}

		if (show) {
			List<String> lore1 = new ArrayList<String>();
			if (g.getMemberLevelLock() > PistUser.getInfo(p).getLevel()) {
				lore1.add(red + "길드에서 요구하는 레벨이 부족하여 가입할 수 없습니다. 필요레벨 " + g.getMemberLevelLock());
				i.setItem(49, createItem(new ItemStack(Material.BARRIER), aqua + "가입 불가능", lore1));
			} else if (!g.isPublic()) {
				lore1.add(red + "해당 길드는 비공개 상태입니다.");
				i.setItem(49, createItem(new ItemStack(Material.BARRIER), aqua + "가입 불가능", lore1));
			} else if (members.get(p.getUniqueId()) != null) {
				lore1.add(red + "당신은 이미 소속된 길드가 있습니다.");
				i.setItem(49, createItem(new ItemStack(Material.BARRIER), aqua + "가입 불가능", lore1));
			} else {
				lore1.add(green + " [ 클릭 시 길드에 가입을 합니다 ] ");
				i.setItem(49, createItem(new ItemStack(Material.BOOK_AND_QUILL), aqua + "가입 신청", lore1));
			}
		} else {
			List<String> lore1 = new ArrayList<String>();
			lore1.add(green + "길드 내 공지사항 : ");
			lore1.add(white + " ");
			//lore1.add(gray + stringToLore(g.getNotice()));
			List<String> NotictLore = stringToLore(g.getNotice());
			for(String a : NotictLore) {
				lore.add(" "+gray+a);
			}
			lore1.add(white + " ");
			i.setItem(49, createItem(new ItemStack(Material.SIGN), red + "공지사항", lore1));
		}
		return i;
	}

	private int getMemberCount(String code) {
		int count = 0;
		for (GuildMemberDTO a : members.values()) {
			if (a.getGuildCode().equals(code)) {
				count++;
			}
		}
		return count;
	}

	@EventHandler
	public void onSettingInvClick(InventoryClickEvent e) {
		if (e.getInventory().getTitle().contains("길드설정")) {
			e.setCancelled(true);
			ItemStack item = e.getCurrentItem();
			Player p = (Player) e.getWhoClicked();
			if (item.getType() == Material.NAME_TAG && isContainsDisplay(item, "이름")) {
				p.closeInventory();
				p.sendMessage(white + " ");
				p.sendMessage(purple + "  < 길드 이름을 변경합니다. >  ");
				p.sendMessage(gray + "채팅창에 새로운 길드명을 입력하세요. " + red + "취소" + gray + "를 입력하면 취소가 가능합니다.");
				p.sendMessage(gray + "자동으로 채팅이 비활성화 됩니다. / 색 코드 사용 불가능합니다.");
				p.sendMessage(white + " ");

				PistChat.cancelChat.put(p.getName(), true);
				setNameMode.put(p.getName(), true);
			} else if (item.getType() == Material.BOOK && isContainsDisplay(item, "소개")) {
				p.closeInventory();
				p.sendMessage(white + " ");
				p.sendMessage(dpurple + "  < 길드 소개 글을 변경합니다. >  ");
				p.sendMessage(gray + "채팅창에 소개글을 입력하세요. " + red + "취소" + gray + "를 입력하면 취소가 가능합니다.");
				p.sendMessage(gray + "자동으로 채팅이 비활성화 됩니다. / 색 코드 사용 가능합니다.");
				p.sendMessage(white + " ");

				PistChat.cancelChat.put(p.getName(), true);
				setDiscriptionMode.put(p.getName(), true);
			} else if (item.getType() == Material.PAPER && isContainsDisplay(item, "공지사항")) {
				p.closeInventory();
				p.sendMessage(white + " ");
				p.sendMessage(dblue + "  < 길드 내 공지사항 글을 변경합니다. >  ");
				p.sendMessage(gray + "채팅창에 길드 내 공지사항을 적으세요. " + red + "취소" + gray + "를 입력하면 취소가 가능합니다.");
				p.sendMessage(gray + "자동으로 채팅이 비활성화 됩니다. / 색 코드 사용 가능합니다.");
				p.sendMessage(white + " ");

				PistChat.cancelChat.put(p.getName(), true);
				setNoticeMode.put(p.getName(), true);
			} else if (item.getType() == Material.SIGN && isContainsDisplay(item, "최대인원")) {
				GuildDTO g = getGuildUsePlayer(p);
				if (g == null)
					return;
				if (e.getAction() == InventoryAction.DROP_ONE_SLOT) {
					if (getMemberCount(g.getCode()) >= g.getMaxPeople())
						return;
					g.setMaxPeople(g.getMaxPeople() - 1);
				} else {
					if (g.getMaxPeople() >= 40)
						return;
					g.setMaxPeople(g.getMaxPeople() + 1);
				}
				p.openInventory(getSettingInv(p, g));
			} else if (item.getType() == Material.SIGN && isContainsDisplay(item, "최소레벨")) {
				GuildDTO g = getGuildUsePlayer(p);
				if (g == null)
					return;
				if (e.getAction() == InventoryAction.DROP_ONE_SLOT) {
					if (g.getMemberLevelLock() <= 0)
						return;
					g.setMemberLevelLock(g.getMemberLevelLock() - 1);
				} else {
					if (g.getMemberLevelLock() >= 150)
						return;
					g.setMemberLevelLock(g.getMemberLevelLock() + 1);
				}
				p.openInventory(getSettingInv(p, g));
			} else if (item.getType() == Material.SIGN && isContainsDisplay(item, "공개여부")) {
				GuildDTO g = getGuildUsePlayer(p);
				if (g == null)
					return;
				if (g.isPublic()) {
					g.setPublic(false);
				} else {
					g.setPublic(true);
				}
				p.openInventory(getSettingInv(p, g));
			}
		}
	}

	public Inventory getSettingInv(Player p, GuildDTO g) {
		Inventory i = Bukkit.createInventory(null, 36, blue + " 길드설정  - " + orange + g.getName() + " ");

		List<String> lore = new ArrayList<String>();
		lore.add(gray + "[ 클릭하여 설정을 변경하세요 ] ");
		i.setItem(10, createItem(new ItemStack(Material.NAME_TAG), green + "이름 변경", lore));
		i.setItem(12, createItem(new ItemStack(Material.BOOK), orange + "소개글 변경", lore));
		i.setItem(14, createItem(new ItemStack(Material.PAPER), orange + "길드 내 공지사항 변경", lore));

		List<String> lore1 = new ArrayList<String>();
		lore1.add(gray + "[ 좌클릭은 증가, 버리기는 감소로 설정하세요 ] ");
		i.setItem(21,
				createItem(new ItemStack(Material.SIGN), orange + "최대인원 설정 " + gray + "현재 " + g.getMaxPeople(), lore1));
		i.setItem(22, createItem(new ItemStack(Material.SIGN),
				orange + "최소레벨 설정 " + gray + "현재 " + g.getMemberLevelLock(), lore1));
		i.setItem(23, createItem(new ItemStack(Material.SIGN), orange + "공개여부 설정 " + gray + "현재 " + g.isPublic(),
				new ArrayList<String>()));

		i.setItem(31, getGuildShowItem(g, true));
		return i;
	}

	public static GuildDTO getGuildUseCode(String code) {
		if (code == null)
			return null;
		for (GuildDTO a : guilds) {
			if (a.getCode().equals(code))
				return a;
		}
		return null;
	}

	public static GuildDTO getGuildUseDTO(GuildMemberDTO g) {
		String code = g.getGuildCode();
		if (code == null)
			return null;
		for (GuildDTO a : guilds) {
			if (a.getCode().equals(code))
				return a;
		}
		return null;
	}

	private ItemStack getGuildShowItem(GuildDTO g, boolean isHead) {

		List<String> lore = new ArrayList<String>();
		lore.add(white + " ");
		lore.add(gray + "❘ " + red + g.getName() + " 길드");
		lore.add(gray + "  대표 : " + green + g.getLeader());
		lore.add(gray + "  부대표 : " + green + g.getDeputyLeader() == null ? red + "없음" : g.getDeputyLeader());
		lore.add(white + " ");
		lore.add(gray + "  레벨 : " + red + g.getGuildLevel());
		lore.add(gray + "  인원 : (" + red + getMemberCount(g.getCode()) + "/" + g.getMaxPeople() + gray + ")");
		lore.add(white + " ");
		List<String> descriptLore = stringToLore(g.getDescription());
		for(String a : descriptLore) {
			lore.add(" "+gray+a);
		}
		lore.add(white + " ");
		lore.add(gray + "  생성일 : " + showDate(g.getWhenCreated()));
		lore.add(white + " ");
		ItemStack item;
		if (isHead) {
			item = getHead(Bukkit.getOfflinePlayer(g.getLeader()));
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(orange + g.getName() + gray + " #" + g.getCode());
			im.setLore(lore);
			item.setItemMeta(im);
		} else {
			Random rnd = new Random();
			item = new ItemStack(Material.BANNER, 1, (byte) rnd.nextInt(16));
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(orange + g.getName() + gray + " #" + g.getCode());
			im.setLore(lore);
			item.setItemMeta(im);
		}
		return item;
	}

	@EventHandler
	public void onChat(PlayerChatEvent e) {
		Player p = e.getPlayer();
		if (setNameMode.get(p.getName()) != null) {
			if (e.getMessage().equals("취소")) {
				PistChat.cancelChat.remove(p.getName());
				setNameMode.remove(p.getName());
				p.sendMessage(red + "변경이 취소되었습니다.");
				return;
			} else if (e.getMessage().length() > 12) {
				p.sendMessage(red + "이름이 12자를 넘습니다. " + gray + e.getMessage().length() + "자");
				return;
			} else if (e.getMessage().length() < 2) {
				p.sendMessage(red + "이름은 2글자를 넘겨야 합니다. " + gray + e.getMessage().length() + "자");
				return;
			}
			String str = e.getMessage().replaceAll("&", "");
			for (GuildDTO a : guilds) {
				if (a.getName().equalsIgnoreCase(str)) {
					p.sendMessage(red + "중복되는 길드명이 존재합니다.");
					return;
				}
			}
			PistChat.cancelChat.remove(p.getName());
			setNameMode.remove(p.getName());
			GuildDTO g = getGuildUsePlayer(p);
			g.setName(str);
			p.sendMessage(gray + "길드의 이름이 성공적으로 " + green + g.getName() + gray + " 으로 변경되었습니다.");
		} else if (setDiscriptionMode.get(p.getName()) != null) {
			if (e.getMessage().equals("취소")) {
				setDiscriptionMode.remove(p.getName());
				PistChat.cancelChat.remove(p.getName());
				p.sendMessage(gray + "변경이 " + red + "취소" + gray + "되었습니다.");
				return;
			}
			if (e.getMessage().length() > 512) {
				p.sendMessage(red + "이름이 512자를 넘습니다. " + gray + e.getMessage().length() + "자");
				return;
			}
			GuildDTO g = getGuildUsePlayer(p);
			g.setDescription(e.getMessage().replace("&", "§"));
			p.sendMessage(gray + "길드의 소개 글이 " + green + "성공적으로 " + gray + "변경 되었습니다.");
			setDiscriptionMode.remove(p.getName());
			PistChat.cancelChat.remove(p.getName());
		} else if (setNoticeMode.get(p.getName()) != null) {
			setNoticeMode.remove(p.getName());
			PistChat.cancelChat.remove(p.getName());
			if (e.getMessage().equals("취소")) {
				p.sendMessage(gray + "변경이 " + red + "취소" + gray + "되었습니다.");
				return;
			}
			if (e.getMessage().length() > 512) {
				p.sendMessage(red + "글이 512자를 넘습니다. " + gray + e.getMessage().length() + "자");
				return;
			}
			GuildDTO g = getGuildUsePlayer(p);
			g.setNotice(e.getMessage().replace("&", "§"));
			p.sendMessage(gray + "길드의 공지사항이 " + green + "성공적으로 " + gray + "변경 되었습니다.");
			setNoticeMode.remove(p.getName());
			PistChat.cancelChat.remove(p.getName());
		}
	}

	private GuildDTO getGuildUsePlayer(Player p) {
		for (GuildDTO a : guilds) {
			if (a.getLeader().equals(p.getName()))
				return a;
		}
		return null;
	}

	private void sendHelp(Player p) {
		p.sendMessage(white + " ");
		if (members.get(p.getUniqueId()) == null)
			p.sendMessage(blue + "❘    " + gray + "당신은 어떠한 길드에도 속해있지 않습니다.");
		else {
			p.sendMessage(blue + "❘    " + gray + getGuildUseDTO(members.get(p.getUniqueId())).getName());

			TextComponent tx0 = new TextComponent(gray + "  [ 클릭하여 길드 정보를 확인합니다 ]  ");
			tx0.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("§7공지사항, 길드원 목록, 레벨 등을 확인합니다.").create()));
			tx0.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/길드 정보"));
			p.spigot().sendMessage(tx0);
		}
		p.sendMessage(white + " ");
		TextComponent tx1 = new TextComponent(orange + "❘ " + gray + "/길드 " + green + "목록");
		tx1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder("§7클릭시 서버의 §a모든 길드§7를 확인합니다.").create()));
		tx1.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/길드 목록"));
		p.spigot().sendMessage(tx1);

		p.sendMessage(white + " ");
		TextComponent tx3 = new TextComponent(blue + "❘ " + gray + "/길드 " + green + "생성");
		tx3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder("§7해당 명령어를 통해 §a길드 창설§7이 가능합니다.").create()));
		p.spigot().sendMessage(tx3);

		TextComponent tx5 = new TextComponent(red + "❘ " + gray + "/길드 " + red + "탈퇴");
		tx5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder("§7해당 명령어를 통해 §a길드 탈퇴§7가 가능합니다.").create()));
		p.spigot().sendMessage(tx5);
		p.sendMessage(white + " ");

		TextComponent tx4 = new TextComponent(blue + "❘ " + gray + "/길드 " + green + "설정");
		tx4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder("§7해당 명령어를 통해 §a길드 설정§7이 가능합니다.").create()));
		p.spigot().sendMessage(tx4);
		TextComponent tx7 = new TextComponent(red + "❘ " + gray + "/길드 " + red + "추방 <이름>");
		tx7.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder("§7해당 명령어를 통해 §c길드원 추방§7이 가능합니다.").create()));
		p.spigot().sendMessage(tx7);

		TextComponent tx8 = new TextComponent(blue + "❘ " + gray + "/길드 " + green + "임명 <이름>");
		tx8.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder("§7해당 명령어를 통해 §a부 길드장 임명§7이 가능합니다.").create()));
		p.spigot().sendMessage(tx8);

		TextComponent tx6 = new TextComponent(gray + "  [ 유저를 길드에 초대하는 방법? ]  ");
		tx6.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder("§7유저의 정보창에서 길드에 초대할 수 있는 버튼이 있습니다.").create()));
		p.spigot().sendMessage(tx6);
		p.sendMessage(white + " ");
	}

}
