package PIST.Mob.DataObject;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import PIST.Enum.Attribute;
import PIST.Enum.Mob;

public class MobLocationDTO { //몹이 생성되는 위치를 담은 DTO 여기서 몹의 타입을 모두 설정해줌
	int code = 0;
	Location loc = null;
	Mob mob = Mob.돼지;
	int mobDuration = 10; //지속시간
	Attribute mobAtt = Attribute.NONE; //이건 같은 몹이더라도 지역에 따라 속성이 달라질 수 있기 때문에
	ItemStack dropItem = new ItemStack(Material.AIR);
	ItemStack specialDropItem = new ItemStack(Material.AIR);
	boolean spawnSpecial = false; //특별 몹 소환할건지
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public Location getLoc() {
		return loc;
	}
	public void setLoc(Location loc) {
		this.loc = loc;
	}
	public Mob getMob() {
		return mob;
	}
	public void setMob(Mob mob) {
		this.mob = mob;
	}
	public int getMobDuration() {
		return mobDuration;
	}
	public void setMobDuration(int mobDuration) {
		this.mobDuration = mobDuration;
	}
	public Attribute getMobAtt() {
		return mobAtt;
	}
	public void setMobAtt(Attribute mobAtt) {
		this.mobAtt = mobAtt;
	}
	public ItemStack getDropItem() {
		return dropItem;
	}
	public void setDropItem(ItemStack dropItem) {
		this.dropItem = dropItem;
	}
	public ItemStack getSpecialDropItem() {
		return specialDropItem;
	}
	public void setSpecialDropItem(ItemStack specialDropItem) {
		this.specialDropItem = specialDropItem;
	}
	public boolean isSpawnSpecial() {
		return spawnSpecial;
	}
	public void setSpawnSpecial(boolean spawnSpecial) {
		this.spawnSpecial = spawnSpecial;
	}
	
	public MobLocationDTO() {
		
	}
}
