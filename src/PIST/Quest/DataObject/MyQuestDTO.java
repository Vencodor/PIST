package PIST.Quest.DataObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class MyQuestDTO implements ConfigurationSerializable { //아이템 얻기 퀘스트는 바로바로 확인
	int questCode = 0;
	boolean clear = false; //클리어 했는가
	boolean reward = false; //보상을 받았는가
	HashMap<String,Integer> mobKill = new HashMap<String,Integer>(); //몹을 얼마나 잡았는가
	HashMap<Location,Boolean> location = new HashMap<Location,Boolean>(); //좌표 이동 퀘스트를 클리어 했는가
	String land = null; //어느 땅에서 퀘스트를 받았는가
	String npcName = null; //어떤 npc한테서 퀘스트를 받았는가
	Date date = new Date(); //언제 퀘스트를 받았는가
	public int getQuestCode() {
		return questCode;
	}
	public void setQuestCode(int questCode) {
		this.questCode = questCode;
	}
	public boolean isClear() {
		return clear;
	}
	public void setClear(boolean clear) {
		this.clear = clear;
	}
	public boolean isReward() {
		return reward;
	}
	public void setReward(boolean reward) {
		this.reward = reward;
	}
	public HashMap<String, Integer> getMobKill() {
		return mobKill;
	}
	public void setMobKill(HashMap<String, Integer> mobKill) {
		this.mobKill = mobKill;
	}
	public HashMap<Location, Boolean> getLocation() {
		return location;
	}
	public void setLocation(HashMap<Location, Boolean> location) {
		this.location = location;
	}
	public String getLand() {
		return land;
	}
	public void setLand(String land) {
		this.land = land;
	}
	public String getNpcName() {
		return npcName;
	}
	public void setNpcName(String npcName) {
		this.npcName = npcName;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public MyQuestDTO(int questCode, boolean clear, boolean reward, HashMap<String, Integer> mobKill,
			HashMap<Location, Boolean> location, String land, String npcName, Date date) {
		super();
		this.questCode = questCode;
		this.clear = clear;
		this.reward = reward;
		this.mobKill = mobKill;
		this.location = location;
		this.land = land;
		this.npcName = npcName;
		this.date = date;
	}
	public MyQuestDTO() {
		
	}
	@SuppressWarnings("unchecked")
	public static MyQuestDTO deserialize(Map<String, Object> d) {
		return new MyQuestDTO((int)d.get("questCode"),
				(boolean)d.get("clear"),
				(boolean)d.get("reward"),
				(HashMap<String, Integer>)d.get("mobKill"),
				(HashMap<Location, Boolean>)d.get("location"),
				(String)d.get("land"),
				(String)d.get("npcName"),
				(Date)d.get("date")
			);
	}
	@Override
	public Map<String, Object> serialize() {
		HashMap<String,Object> s = new HashMap<String,Object>();
		s.put("questCode", questCode);
		s.put("clear", clear);
		s.put("reward", reward);
		s.put("mobKill", mobKill);
		s.put("location", location);
		s.put("land", land);
		s.put("npcName", npcName);
		s.put("date", date);
		return s;
	}
}
