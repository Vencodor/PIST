package PIST.Quest.DataObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class QuestDTO implements ConfigurationSerializable{
	int code = 0;
	String name = "설정되지 않음";
	String title = "설정되지 않음";
	String type = "메인";
	ArrayList<String> messages = new ArrayList<String>(); //Message 에 구분자로 누가 말하는건지 표시하도록
	ArrayList<ItemStack> needItems = new ArrayList<ItemStack>();
	HashMap<String,Integer> needMobs = new HashMap<String,Integer>(); //몬스터 이름 / 숫자
	ArrayList<Location> needLoc = new ArrayList<Location>();
	ArrayList<ItemStack> rewardItem = new ArrayList<ItemStack>();
	int rewardMoney = 0;
	int rewardExp = 0;
	String rewardBuf = null;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<String> getMessages() {
		return messages;
	}
	public void setMessages(ArrayList<String> messages) {
		this.messages = messages;
	}
	public ArrayList<ItemStack> getNeedItems() {
		return needItems;
	}
	public void setNeedItems(ArrayList<ItemStack> needItems) {
		this.needItems = needItems;
	}
	public HashMap<String, Integer> getNeedMobs() {
		return needMobs;
	}
	public void setNeedMobs(HashMap<String, Integer> needMobs) {
		this.needMobs = needMobs;
	}
	public ArrayList<Location> getNeedLoc() {
		return needLoc;
	}
	public void setNeedLoc(ArrayList<Location> needLoc) {
		this.needLoc = needLoc;
	}
	public ArrayList<ItemStack> getRewardItem() {
		return rewardItem;
	}
	public void setRewardItem(ArrayList<ItemStack> rewardItem) {
		this.rewardItem = rewardItem;
	}
	public int getRewardMoney() {
		return rewardMoney;
	}
	public void setRewardMoney(int rewardMoney) {
		this.rewardMoney = rewardMoney;
	}
	public int getRewardExp() {
		return rewardExp;
	}
	public void setRewardExp(int rewardExp) {
		this.rewardExp = rewardExp;
	}
	public String getRewardBuf() {
		return rewardBuf;
	}
	public void setRewardBuf(String rewardBuf) {
		this.rewardBuf = rewardBuf;
	}
	public QuestDTO() {
		
	}
	public QuestDTO(int code, String name, String title, String type, ArrayList<String> messages,
			ArrayList<ItemStack> needItems, HashMap<String, Integer> needMobs, ArrayList<Location> needLoc,
			ArrayList<ItemStack> rewardItem, int rewardMoney, int rewardExp, String rewardBuf) {
		super();
		this.code = code;
		this.name = name;
		this.title = title;
		this.type = type;
		this.messages = messages;
		this.needItems = needItems;
		this.needMobs = needMobs;
		this.needLoc = needLoc;
		this.rewardItem = rewardItem;
		this.rewardMoney = rewardMoney;
		this.rewardExp = rewardExp;
		this.rewardBuf = rewardBuf;
	}
	@SuppressWarnings("unchecked")
	public static QuestDTO deserialize(Map<String, Object> d) {
//		HashMap<String,Integer> mobs = new HashMap<String,Integer>();
//		for(String a : (ArrayList<String>)d.get("needMobs")) {
//			try {
//				String[] args = a.split("|");
//				mobs.put(args[0], Integer.parseInt(args[1]));
//			} catch(Exception e) {
//				e.printStackTrace();
//			}
//		}
		return new QuestDTO((int)d.get("code"),
				(String)d.get("name"),
				(String)d.get("title"),
				(String)d.get("type"),
				(ArrayList<String>)d.get("messages"),
				(ArrayList<ItemStack>)d.get("needItems"),
				(HashMap<String,Integer>)d.get("needMobs"),
				(ArrayList<Location>)d.get("needLoc"),
				(ArrayList<ItemStack>)d.get("rewardItem"),
				(int)d.get("rewardMoney"),
				(int)d.get("rewardExp"),
				(String)d.get("rewardBuf")
			);
	}
	@Override
	public Map<String, Object> serialize() {
		ArrayList<String> mobs = new ArrayList<String>();
		for(String a : needMobs.keySet()) {
			mobs.add(a+"|"+needMobs.get(a));
		}
		HashMap<String, Object> s = new HashMap<String, Object>();
		s.put("code", code);
		s.put("name", name);
		s.put("title", title);
		s.put("type", type);
		s.put("messages", messages);
		s.put("needItems", needItems);
		s.put("needMobs", needMobs);
		s.put("needLoc", needLoc);
		s.put("rewardItem", rewardItem);
		s.put("rewardMoney", rewardMoney);
		s.put("rewardExp", rewardExp);
		s.put("rewardBuf", rewardBuf);
		return s;
	}
}
