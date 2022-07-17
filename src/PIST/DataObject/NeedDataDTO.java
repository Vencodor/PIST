package PIST.DataObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import PIST.Enum.Rank;

public class NeedDataDTO implements ConfigurationSerializable { //다른 DTO에서 유저 제한 조건을 쉽게 정의하기 위해 집합해놓은 DTO
	int level = 0;
	int money = 0;
	Rank rank = Rank.DEFULT;
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
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
	public NeedDataDTO(int level, int money, Rank rank) {
		super();
		this.level = level;
		this.money = money;
		this.rank = rank;
	}
	public NeedDataDTO() {
		
	}
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> s = new HashMap<String, Object>();
		s.put("level", level);
		s.put("money", money);
		s.put("rank", rank);
		return null;
	}
	public static NeedDataDTO deserialize(Map<String, Object> d) {
		return new NeedDataDTO((int)d.get("level"),
				(int)d.get("money"),
				Rank.valueOf((String)d.get("rank"))
				);
	}
}
