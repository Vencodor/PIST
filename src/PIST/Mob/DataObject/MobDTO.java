package PIST.Mob.DataObject;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import PIST.Enum.Attribute;

public class MobDTO {
	Entity mob = null;
	Attribute mobAtt = Attribute.NONE;
	Boolean specialMob = false;
	Boolean boss = false;
	ItemStack drop = new ItemStack(Material.AIR);
	ItemStack specialDrop = new ItemStack(Material.AIR);
	int exp = 0;
	int money = 0;
	int durationCount = 1;
	MobLocationDTO parentSpawner = null;
	
	public MobLocationDTO getParentSpawner() {
		return parentSpawner;
	}

	public void setParentSpawner(MobLocationDTO parentSpawner) {
		this.parentSpawner = parentSpawner;
	}

	public Entity getMob() {
		return mob;
	}

	public void setMob(Entity mob) {
		this.mob = mob;
	}

	public Attribute getMobAtt() {
		return mobAtt;
	}

	public void setMobAtt(Attribute mobAtt) {
		this.mobAtt = mobAtt;
	}

	public Boolean getSpecialMob() {
		return specialMob;
	}

	public void setSpecialMob(Boolean specialMob) {
		this.specialMob = specialMob;
	}

	public Boolean getBoss() {
		return boss;
	}

	public void setBoss(Boolean boss) {
		this.boss = boss;
	}

	public ItemStack getDrop() {
		return drop;
	}

	public void setDrop(ItemStack drop) {
		this.drop = drop;
	}

	public ItemStack getSpecialDrop() {
		return specialDrop;
	}

	public void setSpecialDrop(ItemStack specialDrop) {
		this.specialDrop = specialDrop;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getDurationCount() {
		return durationCount;
	}

	public void setDurationCount(int durationCount) {
		this.durationCount = durationCount;
	}

	public MobDTO(Entity mob, Attribute mobAtt, Boolean specialMob, Boolean boss, ItemStack drop, ItemStack specialDrop,
			int exp, int money, int durationCount, MobLocationDTO parentSpawner) {
		super();
		this.mob = mob;
		this.mobAtt = mobAtt;
		this.specialMob = specialMob;
		this.boss = boss;
		this.drop = drop;
		this.specialDrop = specialDrop;
		this.exp = exp;
		this.money = money;
		this.durationCount = durationCount;
		this.parentSpawner = parentSpawner;
	}

	public MobDTO() {
		
	}
}
