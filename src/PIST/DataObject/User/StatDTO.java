package PIST.DataObject.User;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class StatDTO implements ConfigurationSerializable{
	int power=0;
	int health=0;
	int speed=0;
	int criticalPer=0;
	int criticalDamagePer=0;
	int miss=0;
	int coolTimePer=0;
	public int getPower() {
		return power;
	}
	public void setPower(int power) {
		this.power = power;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getCriticalPer() {
		return criticalPer;
	}
	public void setCriticalPer(int criticalPer) {
		this.criticalPer = criticalPer;
	}
	public int getCriticalDamagePer() {
		return criticalDamagePer;
	}
	public void setCriticalDamagePer(int criticalDamagePer) {
		this.criticalDamagePer = criticalDamagePer;
	}
	public int getMiss() {
		return miss;
	}
	public void setMiss(int miss) {
		this.miss = miss;
	}
	public int getCoolTimePer() {
		return coolTimePer;
	}
	public void setCoolTimePer(int coolTimePer) {
		this.coolTimePer = coolTimePer;
	}
	public StatDTO(int power, int health, int speed, int criticalPer,
			int criticalDamagePer, int miss, int coolTimePer) {
		super();
		this.power = power;
		this.health = health;
		this.speed = speed;
		this.criticalPer = criticalPer;
		this.criticalDamagePer = criticalDamagePer;
		this.miss = miss;
		this.coolTimePer = coolTimePer;
	}
	public StatDTO() {
		
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> serialized = new HashMap<>();
		serialized.put("speed", speed);
		serialized.put("power", power);
		serialized.put("health", health);
		serialized.put("criticalPer", criticalPer);
		serialized.put("criticalDamagerPer", criticalDamagePer);
		serialized.put("miss", miss);
		serialized.put("coolTimePer", coolTimePer);
		return serialized;
	}
	
	public static StatDTO deserialize(Map<String, Object> d) {
		return new StatDTO((int)d.get("power"), (int)d.get("health")
				, (int)d.get("speed"), (int)d.get("criticalPer"),(int)d.get("criticalDamagerPer")
				,(int)d.get("miss"),(int)d.get("coolTimePer"));
	}
}
