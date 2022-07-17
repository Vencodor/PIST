package PIST.DataObject.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import PIST.Enum.Rank;

public class UserRecordDTO implements ConfigurationSerializable{
	String name = null;
	int level = 1;
	double levelUp = 0;
	int money = 0;
	Rank rank = Rank.DEFULT;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getLevelUp() {
		return levelUp;
	}

	public void setLevelUp(double levelUp) {
		this.levelUp = levelUp;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}

	public UserRecordDTO(String name, int level, double levelUp, int money, Rank rank) {
		super();
		this.name = name;
		this.level = level;
		this.levelUp = levelUp;
		this.money = money;
		this.rank = rank;
	}

	public UserRecordDTO() {

	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> serialized = new HashMap<>();
		serialized.put("name", name);
		serialized.put("level", level);
		serialized.put("levelUp", levelUp);
		serialized.put("money", money);
		serialized.put("rank", rank.toString());
		return serialized;
	}
	
	public static UserRecordDTO deserialize(Map<String, Object> d) {
		return new UserRecordDTO((String)d.get("name"), (int)d.get("level"), 
				(double)d.get("levelUp"), (int)d.get("money"), Rank.valueOf((String)d.get("rank")));
	}
}
