package PIST;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import PIST.DataObject.GuildDTO;
import PIST.DataObject.User.PrefixDTO;
import PIST.Guild.PistGuild;
import PIST.ServerManager.PistPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PistChat extends PistPlugin implements Listener {

	public static HashMap<UUID, ArrayList<PrefixDTO>> prefixs = new HashMap<UUID, ArrayList<PrefixDTO>>();
	public static HashMap<String, ItemStack> prefix = new HashMap<String, ItemStack>();

	public static HashMap<String, Boolean> cancelChat = new HashMap<String, Boolean>();

	public PistChat(Pist ser) {
		super(ser);
	}

	public Inventory getUI(Player p) {
		UUID uid = p.getUniqueId();
		Inventory i = Bukkit.createInventory(null, 54, orange + "칭호");

		if (prefix.get(p.getName()) == null) {
			if (prefixs.get(uid) == null) {
				List<String> lore = new ArrayList<String>();
				i.setItem(22, createItem(new ItemStack(Material.BARRIER), red + "당신은 현재 칭호를 가지고 있지 않습니다.", lore));
				i = setInfo(i, p);
			} else {
				i.setContents(getItems(p));
				i = setInfo(i, p);
			}
		} else {
			i.setContents(getItems(p));
			i = setInfo(i, p);
		}

		return i;
	}

	public ItemStack[] getItems(Player p) {
		if (prefixs.get(p.getUniqueId()) == null) {
			prefixs.put(p.getUniqueId(), new ArrayList<PrefixDTO>());
			return null;
		}

		ItemStack[] items = new ItemStack[44];
		int count = 0;

		for (PrefixDTO pf : prefixs.get(p.getUniqueId())) {
			List<String> lore = new ArrayList<String>(pf.getDescription());
			lore.add(white + " ");
			lore.add(gray + "획득일 : " + orange + showDate(pf.getDate()));
			items[count] = createItem(new ItemStack(Material.ENCHANTED_BOOK), green + pf.getPrefix(), lore);
			count++;
		}

		return items;
	}

	public ItemStack giveItem(String name, List<String> description) {
		ItemStack item = createItem(new ItemStack(Material.ENCHANTED_BOOK), green + name, description);
		return item;
	}

	public Inventory setInfo(Inventory i, Player p) {
		for (int k = 45; k < 54; k++)
			if (k != 49)
				i.setItem(k, createItem(new ItemStack(Material.IRON_FENCE), " ", new ArrayList<String>()));
		ItemStack pf = prefix.get(p.getName());
		if (pf != null) {
			ItemMeta im = pf.getItemMeta();
			im.getLore().add(white + " ");
			im.getLore().add(red + "칭호를 해제하려면 클릭하세요.");
			pf.setItemMeta(im);
			i.setItem(49, pf);
		} else {
			i.setItem(49,
					createItem(new ItemStack(Material.BARRIER), red + "현재 장착한 칭호가 없습니다.", new ArrayList<String>()));
		}
		return i;
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		if (e.getInventory().getTitle().contains("칭호")) {
			e.setCancelled(true);
			ItemStack item = e.getCurrentItem();
			if (item == null)
				return;
			if (item.getItemMeta() == null)
				return;
			if (item.getItemMeta().getDisplayName() == null)
				return;
			if (item.getType() != Material.ENCHANTED_BOOK)
				return;
			Player p = (Player) e.getWhoClicked();
			if (prefix.get(p.getName()) != null) {
				if (prefix.get(p.getName()).getItemMeta().getDisplayName()
						.equals(item.getItemMeta().getDisplayName())) {
					prefix.remove(p.getName());
					p.openInventory(getUI(p));
					return;
				}
			}
			prefix.put(p.getName(), item);
			p.openInventory(getUI(p));
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			ItemStack item = p.getItemInHand();
			if (item == null)
				return;
			if (item.getItemMeta() == null)
				return;
			if (item.getItemMeta().getDisplayName() == null)
				return;
			if (item.getItemMeta().getDisplayName().contains("칭호북")) {
				if (item.getItemMeta().getLore() == null)
					return;
				if (prefixs.get(p.getUniqueId()) == null)
					prefixs.put(p.getUniqueId(), new ArrayList<PrefixDTO>());
				if (prefixs.get(p.getUniqueId()).size() >= 45) {
					p.sendMessage(red + "당신은 보유할 수 있는 칭호의 갯수가 최대를 넘었습니다.");
					return;
				}
				List<String> lore = item.getItemMeta().getLore();
				for (String a : lore) {
					if (a.contains("'")) {
						String name = a.split("'", 3)[1];
						if (isSet(p.getUniqueId(), name)) {
							p.sendMessage(red + "당신은 이미 해당 칭호를 보유하고 있습니다.");
							return;
						}
						item.setAmount(item.getAmount() - 1);
						lore.remove(lore.indexOf(a));
						prefixs.get(p.getUniqueId()).add(new PrefixDTO(name, lore, new Date()));
						p.sendTitle(green + "칭호를 획득하였습니다!", gray + "칭호는 /칭호 를 통해 확인 가능합니다.");
						p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1F);
						return;
					}
				}
			}
		}
	}

	public boolean isSet(UUID uid, String display) {
		for (PrefixDTO a : prefixs.get(uid)) {
			if (a.getPrefix().contains(display))
				return true;
		}
		return false;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(PlayerChatEvent e) {
		Player p = e.getPlayer();
		if (e.isCancelled() == true)
			return;
		e.setCancelled(true);
		if (cancelChat.get(p.getName()) != null)
			return;
		if (e.getMessage().length() >= 64) {
			p.sendMessage(red + "문자는 64자를 넘을 수 없습니다.");
			return;
		}
		String msg = e.getMessage();
		TextComponent DisplayPrefix = new TextComponent("");
		TextComponent guildPrefix = new TextComponent("");
		if (prefix.get(p.getName()) != null) {
			ItemStack pre = prefix.get(p.getName());
			DisplayPrefix.addExtra(dgray + "< " + pre.getItemMeta().getDisplayName() + dgray + " >");
			/*
			DisplayPrefix.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("§7"+loreToString(pre.getItemMeta().getLore())).create()));
			*/
		}
		if (PistGuild.members.get(p.getUniqueId()) != null) {
			GuildDTO g = PistGuild.getGuildUseDTO(PistGuild.members.get(p.getUniqueId()));
			guildPrefix.addExtra(dgray + "[ " + orange
					+ PistGuild.getGuildUseDTO(PistGuild.members.get(p.getUniqueId())).getName() + dgray + " ]");
			guildPrefix.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("§6클릭하여 길드 의 정보를 봅니다.").create()));
			guildPrefix.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/길드 정보 "+g.getCode()));
		}
		
		TextComponent MainComponent = new TextComponent("");
		
		TextComponent PrefixComponent = new TextComponent(gray + p.getName());
		PrefixComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder("§7클릭하여 §a" + p.getName() + "§7의 정보를 봅니다.").create()));
		PrefixComponent.setClickEvent(
				new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/정보 " + p.getName()));
		
		TextComponent ContentComponent = new TextComponent(white+msg);
		ContentComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§c해당 텍스트를 신고합니다.").create())); 
		//ContentComponent.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, ""));
		
		//[길드] <칭호> Vencoder > 안녕하세요
		MainComponent.addExtra(guildPrefix);
		MainComponent.addExtra(" ");
		MainComponent.addExtra(DisplayPrefix);
		MainComponent.addExtra(" ");
		MainComponent.addExtra(PrefixComponent);
		MainComponent.addExtra(gray+" > ");
		MainComponent.addExtra(ContentComponent);
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (cancelChat.get(online.getName()) == null)
				online.spigot().sendMessage(MainComponent);
		}
	}

}
