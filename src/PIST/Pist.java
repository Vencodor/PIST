package PIST;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandSender.Spigot;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.authlib.GameProfile;

import PIST.AbilityManager.PistAbility;
import PIST.AbilityManager.PistAttack;
import PIST.AbilityManager.PistAttribute;
import PIST.DataObject.GatherInfoDTO;
import PIST.DataObject.GuildDTO;
import PIST.DataObject.GuildMemberDTO;
import PIST.DataObject.LandDTO;
import PIST.DataObject.User.AttributeDTO;
import PIST.DataObject.User.PrefixDTO;
import PIST.DataObject.User.StatDTO;
import PIST.DataObject.User.UserRecordDTO;
import PIST.Enum.Attribute;
import PIST.Enum.CustomEntities;
import PIST.Guild.PistGuild;
import PIST.Interact.Gathering;
import PIST.ItemManager.PistItem;
import PIST.ItemManager.PistEquip;
import PIST.Land.PistLandManager;
import PIST.MobManager.PistMobManager;
import PIST.Quest.PistQuest;
import PIST.Quest.DataObject.MyQuestDTO;
import PIST.Quest.DataObject.QuestDTO;
import PIST.Riding.PistRiding;
import PIST.ServerManager.PistPlugin;
import PIST.SettingManager.SettingManager;
import PIST.Stat.Stat;
import PIST.UserManager.PistInventory;
import PIST.UserManager.PistUser;
import PIST.Utility.AdvancementAPIMain;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Pist extends JavaPlugin implements Listener {
	
	public Pist server;
	
	PluginDescriptionFile pdfile = this.getDescription();
	PluginManager pm = Bukkit.getPluginManager();
	
	PistScoreBoard scM;
	SettingManager setM;
	PistChat chatM;
	Stat statM;
	PistAbility abM;
	PistUser userM;
	PistInventory invM;
	PistAttribute attM;
	PistEquip equipM;
	PistGuild guildM;
	PistPlugin plM;
	PistAttack attackM;
	PistLandManager landM;
	PistRiding ridM;
	PistItem itemCreater;
	PistQuest questM;
	PistMobManager mobM;
	Gathering getM;
	
	AdvancementAPIMain adAPI;
	
	File settingFile = new File(getDataFolder()+"/data/data/setting.yml");
	File statFile = new File(getDataFolder()+"/data/user/stat.yml");
	File prefixFile = new File(getDataFolder()+"/data/user/prefix.yml");
	File recordFile = new File(getDataFolder()+"/data/user/userRecord.yml");
	File attFile = new File(getDataFolder()+"/data/user/attribute.yml");
	File guildFile = new File(getDataFolder()+"/data/data/guild.yml");
	File landFile = new File(getDataFolder()+"/data/data/land.yml");
	File questFile = new File(getDataFolder()+"/data/data/quest.yml");
	File myQuestFile = new File(getDataFolder()+"/data/user/questData.yml");
	File gatherFile = new File(getDataFolder()+"/data/data/gatherData.yml");
	File cacheFile = new File(getDataFolder()+"/cache.yml");

	String prefix = ChatColor.AQUA + "[Chip] " + ChatColor.WHITE + "";
	String info = ChatColor.YELLOW + "[Info] " + ChatColor.WHITE + "";
	String error = ChatColor.DARK_RED + "[Error] " + ChatColor.RED + "";
	String warning = ChatColor.DARK_RED + "[Warning] " + ChatColor.RED + "";

	String white = ChatColor.WHITE + "";
	String red = ChatColor.RED + "";
	String dred = ChatColor.DARK_RED + "";
	String gold = ChatColor.GOLD + "";
	String yellow = ChatColor.YELLOW + "";
	String green = ChatColor.GREEN + "";
	String dgreen = ChatColor.DARK_GREEN + "";
	String aqua = ChatColor.AQUA + "";
	String blue = ChatColor.BLUE + "";
	String dblue = ChatColor.DARK_BLUE + "";
	String gray = ChatColor.GRAY + "";
	String bold = ChatColor.BOLD + "";
	String plname = ChatColor.GREEN + this.getDescription().getName() + "";
	String plvers = ChatColor.GREEN + this.getDescription().getVersion() + "";
	String pname = plname + " v" + plvers + " ";
	String cinfo = info + ChatColor.YELLOW + pname + ChatColor.WHITE + " ";
	
	private static Pist instance;
	
	public static HashMap<String,String> textureCache = new HashMap<String,String>();
	public static HashMap<String,UUID> uuidCache = new HashMap<String,UUID>();
	
	private Boolean notSave = false;
	
	@Override
	public void onEnable() {
		instance = this;
		ConfigurationSerialization.registerClass(StatDTO.class);
		ConfigurationSerialization.registerClass(PrefixDTO.class); //나중에 SettingDTO 구현할것
		ConfigurationSerialization.registerClass(UserRecordDTO.class);
		ConfigurationSerialization.registerClass(AttributeDTO.class);
		ConfigurationSerialization.registerClass(GuildDTO.class);
		ConfigurationSerialization.registerClass(GuildMemberDTO.class);
		ConfigurationSerialization.registerClass(LandDTO.class);
		ConfigurationSerialization.registerClass(QuestDTO.class);
		ConfigurationSerialization.registerClass(MyQuestDTO.class);
		
		scM = new PistScoreBoard(this);
		setM = new SettingManager(this);
		chatM = new PistChat(this);
		statM = new Stat(this);
		abM = new PistAbility(this);
		userM = new PistUser(this);
		invM = new PistInventory(this);
		attM = new PistAttribute(this);
		equipM = new PistEquip(this);
		guildM = new PistGuild(this);
		adAPI = new AdvancementAPIMain();
		plM = new PistPlugin(this);
		attackM = new PistAttack(this);
		landM = new PistLandManager(this);
		ridM = new PistRiding(this);
		itemCreater = new PistItem(this);
		questM = new PistQuest(this);
		mobM = new PistMobManager(this);
		getM = new Gathering(this);
		
		adAPI.setInstanceThis();
		
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(setM, this);
		getServer().getPluginManager().registerEvents(chatM, this);
		getServer().getPluginManager().registerEvents(statM, this);
		getServer().getPluginManager().registerEvents(userM, this);
		getServer().getPluginManager().registerEvents(invM, this);
		getServer().getPluginManager().registerEvents(attM, this);
		getServer().getPluginManager().registerEvents(equipM, this);
		getServer().getPluginManager().registerEvents(guildM, this);
		getServer().getPluginManager().registerEvents(abM, this);
		getServer().getPluginManager().registerEvents(attackM, this);
		getServer().getPluginManager().registerEvents(landM, this);
		getServer().getPluginManager().registerEvents(ridM, this);
		getServer().getPluginManager().registerEvents(questM, this);
		getServer().getPluginManager().registerEvents(mobM, this);
		getServer().getPluginManager().registerEvents(getM, this);
		
		getCommand("스텟").setExecutor(statM);
		getCommand("돈").setExecutor(userM);
		getCommand("피스").setExecutor(userM);
		getCommand("레벨").setExecutor(userM);
		getCommand("길드").setExecutor(guildM);
		getCommand("땅").setExecutor(landM);
		getCommand("아이템").setExecutor(itemCreater);
		getCommand("Q").setExecutor(questM);
		getCommand("퀘스트").setExecutor(questM);
		getCommand("던전").setExecutor(mobM);
		getCommand("상호").setExecutor(getM);
		
		getCommand("피스").setTabCompleter(userM);
		
		CustomEntities.registerEntities();
		
		PistScoreBoard.setPlugin(this);
		statM.setPlugin(this);
		abM.setPlugin(this);
		attM.setPlugin(this);
		attackM.setPlugin(this);
		landM.setPlugin(this);
		
		abM.startSetAb();
		setM.settingGUI();
		scM.startScoreBoard();
		invM.settingDefult();
		questM.settingBase();
		
		try {
			FileConfiguration statCon = YamlConfiguration.loadConfiguration(statFile);
			if(!statFile.exists()) saveFile(statCon,statFile);
			if(statCon.isSet("stat")) {
				for(String a : statCon.getConfigurationSection("stat").getKeys(false)) {
					Stat.stat.put(UUID.fromString(a), (StatDTO)statCon.get("stat."+a));
				}
			} if(statCon.isSet("point")) {
				for(String a : statCon.getConfigurationSection("point").getKeys(false)) {
					Stat.point.put(UUID.fromString(a), statCon.getInt("point."+a));
				}
			}
			
			FileConfiguration prefixCon = YamlConfiguration.loadConfiguration(prefixFile);
			if(!prefixFile.exists()) saveFile(prefixCon,prefixFile);
			if(prefixCon.isSet("prefixs")) {
				for(String a : prefixCon.getConfigurationSection("prefixs").getKeys(false)) {
					ArrayList<PrefixDTO> list = new ArrayList<PrefixDTO>();
					for(String b : prefixCon.getConfigurationSection("prefixs."+a).getKeys(false)) {
						list.add((PrefixDTO)prefixCon.get("prefixs."+a+"."+b));
					}
					PistChat.prefixs.put(UUID.fromString(a), list);
				}
			} if(prefixCon.isSet("prefix")) {
				for(String a : prefixCon.getConfigurationSection("prefix").getKeys(false)) {
					PistChat.prefix.put(a, prefixCon.getItemStack("prefix."+a));
				}
			}
			
			FileConfiguration recordCon = YamlConfiguration.loadConfiguration(recordFile);
			if(!recordFile.exists()) saveFile(recordCon,recordFile);
			if(recordCon.isSet("userData")) {
				for(String a : recordCon.getConfigurationSection("userData").getKeys(false)) {
					PistUser.user.put(UUID.fromString(a), (UserRecordDTO)recordCon.get("userData."+a));
				}
			}
			
			FileConfiguration attCon = YamlConfiguration.loadConfiguration(attFile);
			if(!attFile.exists()) saveFile(attCon,attFile);
			if(attCon.isSet("attribute")) {
				for(String a : attCon.getConfigurationSection("attribute").getKeys(false)) {
					PistAttribute.userAtt.put(UUID.fromString(a), (AttributeDTO) attCon.get("attribute."+a));
				}
			}
			
			FileConfiguration guildCon = YamlConfiguration.loadConfiguration(guildFile);
			if(!guildFile.exists()) saveFile(guildCon,guildFile);
			if(guildCon.isSet("guild")) {
				for(String a : guildCon.getConfigurationSection("guild").getKeys(false)) {
					PistGuild.guilds.add((GuildDTO)guildCon.get("guild."+a));
				}
			} if(guildCon.isSet("member")) {
				for(String a : guildCon.getConfigurationSection("member").getKeys(false)) {
					PistGuild.members.put(UUID.fromString(a), (GuildMemberDTO)guildCon.get("member."+a));
				}
			}
			
			FileConfiguration landCon = YamlConfiguration.loadConfiguration(landFile);
			if(!landFile.exists()) saveFile(landCon,landFile);
			if(landCon.isSet("land")) {
				for(String a : landCon.getConfigurationSection("land").getKeys(false)) {
					PistLandManager.land.put(a, (LandDTO)landCon.get("land."+a));
				}
			}
			
			FileConfiguration questCon = YamlConfiguration.loadConfiguration(questFile);
			if(!questFile.exists()) saveFile(questCon,questFile);
			if(questCon.isSet("quest")) {
				for(String a : questCon.getConfigurationSection("quest").getKeys(false)) {
					try {
						PistQuest.q.put(Integer.parseInt(a), (QuestDTO)questCon.get("quest."+a));
					} catch(NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
			
			FileConfiguration myQuestCon = YamlConfiguration.loadConfiguration(myQuestFile);
			if(!myQuestFile.exists()) saveFile(myQuestCon,myQuestFile);
			if(myQuestCon.isSet("user")) {
				for(String a : myQuestCon.getConfigurationSection("user").getKeys(false)) {
					for(String b : myQuestCon.getConfigurationSection("user."+a).getKeys(false)) {
						HashMap<UUID,ArrayList<MyQuestDTO>> hash = PistQuest.userQ;
						UUID uid = UUID.fromString(a);
						if(hash.get(uid)==null) hash.put(uid, new ArrayList<MyQuestDTO>());
						hash.get(uid).add((MyQuestDTO)myQuestCon.get("user."+a+"."+b));
					}
				}
			}
			
			FileConfiguration gatherCon = YamlConfiguration.loadConfiguration(gatherFile);
			if(!gatherFile.exists()) saveFile(gatherCon,gatherFile);
			if(gatherCon.isSet("gather")) {
				for(String a : gatherCon.getConfigurationSection("gather").getKeys(false)) {
					try {
						Gathering.gathers.put(Integer.parseInt(a),(GatherInfoDTO)gatherCon.get(a));
					} catch(NumberFormatException e) {
						e.printStackTrace();
						System.out.println(dred+"문자를 숫자로 변환하는 도중, 오류가 발생하였습니다.");
					}
				}
			}
			
			FileConfiguration cacheCon = YamlConfiguration.loadConfiguration(cacheFile);
			if(!cacheFile.exists()) saveFile(cacheCon,cacheFile);
			if(cacheCon.isSet("texture")) {
				for(String a : cacheCon.getConfigurationSection("texture").getKeys(false)) {
					textureCache.put(a, (String) cacheCon.get("texture."+a));
				}
			}
			if(cacheCon.isSet("uuid")) {
				for(String a : cacheCon.getConfigurationSection("uuid").getKeys(false)) {
					uuidCache.put(a, UUID.fromString((String)cacheCon.get("uuid."+a)));
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			
			notSave = true;
			System.out.println(dred+"데이터를 불러오던 중 오류가 발생하였습니다.");
			Bukkit.shutdown();
		}
	}
	
	@Override
	public void onDisable() {
		if(notSave) return;
		try {
			saveStat();
			saveAttribute();
			saveRecord();
			savePrefix();
			saveGuild();
			saveLand();
			saveCache();
			saveQuest();
			saveMyQuest();
			saveGather();
		} catch(Exception e) {
			e.printStackTrace();
			return;
		} finally {
			PistRiding.removeAllHorse();
			CustomEntities.unregisterEntities();
		}
	}
	
	public static Pist getInstance() {
		return instance;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		e.setJoinMessage(null);
		
		TextComponent joinM = new TextComponent(gray+" [ " +green+ "+" +gray+ " ]" + bold + " " + p.getName());
		joinM.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(new Date()+"").create()));
		
		for(Player online : Bukkit.getOnlinePlayers()) {
			if(!online.getName().equals(p.getName()))
				online.spigot().sendMessage(joinM);
		}
		
		p.sendMessage(gray+"Pist서버에 오신걸 환영합니다.");
		
		scM.reloadScoreBoard(p);
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		if(e.getCurrentItem()!=null) {
			Player p = (Player)e.getWhoClicked();
			p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.15F, 0.7F);
		}
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player)sender;
		if(label.equalsIgnoreCase("p")) {
			if(args.length==0) {
				//help
				p.setAllowFlight(true);
				p.setFlying(true);
			} else {
				
			}
		} else if(label.equalsIgnoreCase("설정")) {
			p.openInventory(setM.setGUI);
		} else if(label.equalsIgnoreCase("칭호")) {
			p.openInventory(chatM.getUI(p));
		} else if(label.equalsIgnoreCase("칭호북")) {
			if(!p.isOp())
				return false;
			if(args.length==0||args.length==1) {
				p.sendMessage(gray+"/"+red+"칭호북 칭호 설명 "+gray+"(칭호에서 -는 띄어쓰기, 설명에서 _는 들여쓰기 입니다.)");
			} else {
				args[0] = args[0].replace("-", " ");
				String description = "";
				for(int i=1; i<args.length; i++) {
					if(args[i]!=null) {
						description = description+args[i]+" ";
					}
				}
				List<String> lore = new ArrayList<String>();
				lore.add(gray+"우클릭 시 "+gold+"'"+args[0]+"'"+gray+" 칭호를 획득합니다.");
				lore.add(white+" ");
				String des = "";
				for(char a : description.toCharArray()) {
					if(a == '_') {
						lore.add(gray+des);
						des = "";
					} else {
						des = des + a;}
				}
				lore.add(gray+des);
				p.getInventory().addItem(chatM.giveItem(gray+"[ "+yellow+"칭호북"+gray+" ]", lore));
			}
		} else if(label.equalsIgnoreCase("장비창")||label.equalsIgnoreCase("장비")) {
			p.openInventory(invM.getInv(p));
		} else if(label.equalsIgnoreCase("정보")) {
			if(args.length==0) {
				p.openInventory(invM.getInv(p));
			} else {
				Player target = Bukkit.getPlayer(args[0]);
				if(target.isOnline()) {
					p.openInventory(invM.getInfoInv(target.getPlayer()));
				}
			}
		} else if(label.equalsIgnoreCase("속성")) {
			if(!p.isOp())
				return false;
			if(args.length==0) {
				p.sendMessage(gray+"/속성 초기화 [대상]");
				p.sendMessage(gray+"/속성 아이템 [속성]");
				p.sendMessage(gray+"/속성 지급 [대상]");
			} else if(args[0].equalsIgnoreCase("초기화")) {
				if(args.length==1) {
					p.sendMessage(red+"/속성 초기화 [대상]");
				} else {
					OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
					if(target==null||PistAttribute.userAtt.get(target.getUniqueId())==null) {
						p.sendMessage(red+"대상이 존재하지 않습니다.");
					} else {
						PistAttribute.userAtt.remove(target.getUniqueId());
						p.sendMessage(green+"성공.");
					}
				}
			} else if(args[0].equalsIgnoreCase("아이템")) {
				if(args.length==1) {
					p.sendMessage(red+"/속성 아이템 [속성]");
				} else {
					if(Attribute.valueOf(args[1])==null)
						p.sendMessage(red+"해당 속성은 존재하지 않습니다.");
					else {
						//p.getInventory().addItem(PistAttribute.attItems.get(Attribute.valueOf(args[1])));
						p.sendMessage(red+"Error! 해당 기능은 삭제되었습니다.");
					}
				}
			} else if(args[0].equalsIgnoreCase("지급")) {
				if(args.length==1) {
					p.sendMessage(red+"/속성 지급 [대상]");
				} else {
					OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
					if(target==null||PistAttribute.userAtt.get(target.getUniqueId())==null) {
						p.sendMessage(red+"대상이 존재하지 않습니다.");
					} else {
						PistAttribute.userAtt.put(target.getUniqueId(),new AttributeDTO(Attribute.FIRE,Attribute.WIND,Attribute.ELECTRIC,Attribute.EARTH,Attribute.WATER));
						p.sendMessage(green+"성공.");
					}
				}
			}
			
		}
		return true;
	}
	
	public void saveCache() {
		FileConfiguration con = YamlConfiguration.loadConfiguration(cacheFile);
		for(String a : textureCache.keySet()) {
			con.set("texture."+a, textureCache.get(a));
		}
		for(String a : uuidCache.keySet()) {
			con.set("uuid."+a, uuidCache.get(a).toString());
		}
		saveFile(con,cacheFile);
	}
	
	public void saveStat() {
		FileConfiguration con = YamlConfiguration.loadConfiguration(statFile);
		HashMap<UUID,StatDTO> hash = Stat.getStatHash();
		for(UUID a : hash.keySet()) {
			con.set("stat."+a, hash.get(a));
		}
		HashMap<UUID,Integer> pointHash = Stat.getPointHash();
		for(UUID a : pointHash.keySet()) {
			con.set("point."+a, pointHash.get(a));
		}
		saveFile(con, statFile);
	}
	
	public void savePrefix() {
		FileConfiguration con = YamlConfiguration.loadConfiguration(prefixFile);
		HashMap<UUID,ArrayList<PrefixDTO>> hash = PistChat.prefixs;
		for(UUID a : hash.keySet()) {
			for(PrefixDTO b : hash.get(a)) {
				//ArrayList<PrefixDTO> list = prefixsHash.get(a);
				con.set("prefixs."+a+"."+b.getPrefix(), b);
			}
		}
		HashMap<String,ItemStack> prefixHash = PistChat.prefix;
		for(String a : prefixHash.keySet()) {
			con.set("prefix."+a, prefixHash.get(a));
		}
		saveFile(con,prefixFile);
	}
	
	public void saveRecord() {
		FileConfiguration con = YamlConfiguration.loadConfiguration(recordFile);
		HashMap<UUID,UserRecordDTO> hash = PistUser.user;
		for(UUID a : hash.keySet()) {
			con.set("userData."+a, hash.get(a));
		}
		saveFile(con,recordFile);
	}
	
	public void saveLand() {
		FileConfiguration con = YamlConfiguration.loadConfiguration(landFile);
		HashMap<String,LandDTO> hash = PistLandManager.land;
		for(String a : hash.keySet()) {
			con.set("land."+a, hash.get(a));
		}
		saveFile(con,landFile);
	}
	
	public void saveGuild() {
		FileConfiguration con = YamlConfiguration.loadConfiguration(guildFile);
		ArrayList<GuildDTO> hash = PistGuild.guilds;
		for(GuildDTO a : hash) {
			con.set("guild."+a.getCode(), a);
		}
		HashMap<UUID,GuildMemberDTO> memHash = PistGuild.members;
		for(UUID a : memHash.keySet()) {
			con.set("member."+a, memHash.get(a));
		}
		saveFile(con,guildFile);
	}
	
	public void saveAttribute() {
		FileConfiguration con = YamlConfiguration.loadConfiguration(attFile);
		HashMap<UUID,AttributeDTO> hash = PistAttribute.userAtt;
		for(Object a : hash.keySet()) {
			con.set("attribute."+a, hash.get(a));
		}
		saveFile(con,attFile);
	}
	
	public void saveFile(FileConfiguration ymlConfig, File ymlFile) {
		try {
			ymlConfig.save(ymlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveQuest() {
		FileConfiguration con = YamlConfiguration.loadConfiguration(questFile);
		HashMap<Integer, QuestDTO> hash = PistQuest.q;
		for(int a : hash.keySet()) {
			con.set("quest."+a, hash.get(a));
		}
		saveFile(con,questFile);
	}
	
	public void saveMyQuest() {
		FileConfiguration con = YamlConfiguration.loadConfiguration(myQuestFile);
		HashMap<UUID, ArrayList<MyQuestDTO>> hash = PistQuest.userQ;
		for(UUID a : hash.keySet()) {
			for(MyQuestDTO b : hash.get(a)) {
				con.set("user."+a+"."+b.getQuestCode(), b);
			}
		}
		saveFile(con,myQuestFile);
	}
	
	public void saveGather() {
		FileConfiguration con = YamlConfiguration.loadConfiguration(gatherFile);
		HashMap<Integer, GatherInfoDTO> hash = Gathering.gathers;
		for(int a : hash.keySet()) {
			con.set("gather."+a,hash.get(a));
		}
		saveFile(con,gatherFile);
	}
}
