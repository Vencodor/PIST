package PIST.ServerManager;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;

import PIST.Pist;
import PIST.AbilityManager.PistAbility;
import PIST.DataObject.User.SettingDTO;
import PIST.SettingManager.SettingManager;
import PIST.Utility.AdvancementAPIMain;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.MinecraftServer;

public class PistPlugin {
	public Pist server;

	public PistPlugin(Pist ser) {
		server = ser;
	}

	public String bold = ChatColor.BOLD + "";
	public String line = ChatColor.STRIKETHROUGH +"";
	public String white = ChatColor.WHITE + "";
	public String black = ChatColor.BLACK + "";
	public String red = ChatColor.RED + "";
	public String dred = ChatColor.DARK_RED + "";
	public String orange = ChatColor.GOLD + "";
	public String yellow = ChatColor.YELLOW + "";
	public String green = ChatColor.GREEN + "";
	public String dgreen = ChatColor.DARK_GREEN + "";
	public String aqua = ChatColor.AQUA + "";
	public String blue = ChatColor.BLUE + "";
	public String dblue = ChatColor.DARK_BLUE + "";
	public String gray = ChatColor.GRAY + "";
	public String dgray = ChatColor.DARK_GRAY + "";
	public String purple = ChatColor.LIGHT_PURPLE + "";
	public String dpurple = ChatColor.DARK_PURPLE + "";

	public SettingDTO getSettingDTO(Player p) {
		SettingManager sManager = new SettingManager(server);
		return sManager.getSettingDTO(p);
	}

	public String getFromFile(String specPath, String fileName, String param) {
		File file = new File(server.getDataFolder().getPath() + specPath, fileName);
		if (!file.exists() || file.isDirectory())
			return null;
		try {
			YamlConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
			return fileConfig.getString(param);
		} catch (Exception ex) {
			return null;
		}
	}

	public void sendAd(String title, String description, Material material, Player player) {
		AdvancementAPIMain adAPI = new AdvancementAPIMain();
		adAPI.send(title, description, material.getNewData((byte) 0), player);
	}

	public void dirSetting(String folderName) {
		String path = server.getDataFolder().getAbsolutePath() + "\\" + folderName;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	public static ItemStack getSkull(String url) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		if (url.isEmpty())
			return head;

		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		byte[] encodedData = Base64.getEncoder()
				.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
		profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
		Field profileField = null;
		try {
			profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		head.setItemMeta(headMeta);
		return head;
	}
	/*
	public void setSlow(Player p,boolean isCancel) {
		PistAbility pa = new PistAbility(server);
		PistAbility.setSlow.put(p.getName(), isCancel);
		pa.reloadSpeedAb(p);
	}
	*/
	
	public List<String> stringToLore(String str) {
		StringBuffer sb = new StringBuffer();
		List<String> lore = new ArrayList<String>();
		for(int i=0; i<str.length(); i++) {
			if(sb.length()>= 15) {
				lore.add(sb.toString());
				sb = new StringBuffer();
			}
			sb.append(str.charAt(i));
		}
		lore.add(sb.toString());
		return lore;
	}
	
	public String loreToString(List<String> lore) {
		String s = "";
		if(lore.size()==0) return "";
		for(String a : lore) {
			s = s + a + " ";
		}
		return s;
	}
	
	public UUID getUUID(String name) {
		UUID uid;
		if(Pist.uuidCache.get(name)!=null)
			uid = Pist.uuidCache.get(name);
		else {
			OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
			uid = offline.getUniqueId();
			if(!offline.hasPlayedBefore()&&!offline.isOnline())
				return null;
			Pist.uuidCache.put(name, uid);
		}
		return uid;
	}
	
	public <K, V> K getKey(Map<K, V> map, V value) {
		 
        for (K key : map.keySet()) {
            if (value.equals(map.get(key))) {
                return key;
            }
        }
        return null;
    }
	
	public String getName(UUID uid) { //value 를 통해 키를 찾기
		if(getKey(Pist.uuidCache,uid)!=null)
			return getKey(Pist.uuidCache,uid);
		else {
			OfflinePlayer offline = Bukkit.getOfflinePlayer(uid);
			if(!offline.hasPlayedBefore())
				return null;
			Pist.uuidCache.put(offline.getName(), uid);
			return offline.getName();
		}
	}
	
//https://www.spigotmc.org/threads/cache-player-skull-heads.147544/
	@SuppressWarnings("deprecation")
	public ItemStack getHead(OfflinePlayer player) {
		String skinURL = null;
		loadSkin: if (Pist.textureCache.containsKey(player.getName()))
			skinURL = Pist.textureCache.get(player.getName());
		else {
			try {
				GameProfile profile = new GameProfile(player.getUniqueId(), player.getName());
				Field field;
				field = MinecraftServer.class.getDeclaredField("W");
				field.setAccessible(true);
				Object value = field.get(MinecraftServer.getServer());
				if (!(value instanceof MinecraftSessionService))
					break loadSkin;
				MinecraftSessionService ss = (MinecraftSessionService) value;
				ss.fillProfileProperties(profile, true);
				Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = ss.getTextures(profile, true);
				if (textures.containsKey(MinecraftProfileTexture.Type.SKIN)) {
					MinecraftProfileTexture tex = textures.get(MinecraftProfileTexture.Type.SKIN);
					skinURL = tex.getUrl();
				}
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		if (skinURL == null)
			return head;
		Pist.textureCache.put(player.getName(), skinURL);
		ItemMeta headMeta = head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		byte[] encodedData = Base64.getEncoder()
				.encode((String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", skinURL).getBytes()));
		profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
		Field profileField = null;
		try {
			profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		head.setItemMeta(headMeta);
		return head;
	}

	public String showDate(Date date) {
		DateFormat format1 = DateFormat.getDateInstance(DateFormat.FULL);
		return format1.format(date);
	}

	public ItemStack createItem(ItemStack item, String name, List<String> lore) {
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		im.setLore(lore);
		item.setItemMeta(im);
		return item;
	}
	
	public boolean isInArea(Location pl, Location l1, Location l2) {
		double minX = l1.getX() < l2.getX() ? l1.getX() : l2.getX();
		double maxX = l1.getX() > l2.getX() ? l1.getX() : l2.getX();
		double minY = l1.getY() < l2.getY() ? l1.getY() : l2.getY();
		double maxY = l1.getY() > l2.getY() ? l1.getY() : l2.getY();
		double minZ = l1.getZ() < l2.getZ() ? l1.getZ() : l2.getZ();
		double maxZ = l1.getZ() > l2.getZ() ? l1.getZ() : l2.getZ();

		if (minX <= pl.getX() && maxX >= pl.getX() && minY <= pl.getY() && maxY >= pl.getY() && minZ <= pl.getZ()
				&& maxZ >= pl.getZ()) {
			return true;
		} else {
			return false;
		}
	}

	public void sayMessage(String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(message);
		}
	}

	public void sayHoverMessage(TextComponent message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.spigot().sendMessage(message);
		}
	}

	public boolean isContainsDisplay(ItemStack item, String display) {
		if (item == null)
			return false;
		if (item.getItemMeta() == null)
			return false;
		if (item.getItemMeta().getDisplayName() == null)
			return false;
		if (item.getItemMeta().getDisplayName().contains(display))
			return true;
		else
			return false;
	}

	public boolean isContainsLore(ItemStack item, String name) {
		if (item == null)
			return false;
		if (item.getItemMeta() == null)
			return false;
		if (item.getItemMeta().getLore() == null)
			return false;
		List<String> lore = item.getItemMeta().getLore();
		for (String a : lore) {
			if (a.contains(name))
				return true;
		}
		return false;
	}

	public String showMoney(int m) {
		DecimalFormat formatter = new DecimalFormat("###,###");

		return formatter.format(m);
	}

	public double getMaxLevel(int l) {
		double level = l * (l + 1) * 25 - 50;
		if (level <= 0)
			level = 1;
		return level;
	}

	public double getLevelPer(int l, double lUp) {
		return (lUp / getMaxLevel(l)) * 100;
	}
/*
	public static ItemStack getHead(Player p, String display, List<String> lore) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skull = (SkullMeta) item.getItemMeta();
		skull.setDisplayName(display);
		skull.setLore(lore);
		skull.setOwningPlayer(p);
		item.setItemMeta(skull);
		return item;
	}
*/
	public boolean hasItem(Player p, ItemStack item, int amt) {
		if(item==null)
			return true;
		int tamt = amt;
		for (int i = 0; i < p.getInventory().getSize(); i++) {
			if (tamt > 0) {
				ItemStack pitem = p.getInventory().getItem(i);
				if (pitem != null && pitem == item) {
					tamt -= pitem.getAmount();
					if (tamt <= 0) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean hasItem(Player p, Material itemMt, int amt) {
		int tamt = amt;
		for (int i = 0; i < p.getInventory().getSize(); i++) {
			if (tamt > 0) {
				ItemStack pitem = p.getInventory().getItem(i);
				if (pitem != null && pitem.getType() == itemMt) {
					tamt -= pitem.getAmount();
					if (tamt <= 0) {
						return true;
					}
				}
			}
		}

		return false;
	}
}
