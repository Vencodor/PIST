package PIST.DataObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import PIST.Enum.LandType;

public class LandDTO implements ConfigurationSerializable {
	String code = null;
	LandType type = LandType.기타;
	String display = null;
	String name = null;
	String description = null;
	UUID owner = null;
	Location loc1 = null;
	Location loc2 = null;
	boolean lock = false;
	int levelLock = 1;
	ItemStack itemLock = null;
	boolean canBreak = false;
	boolean canPlace = false;
	boolean canChat = true;
	boolean canPvp = false;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public LandType getType() {
		return type;
	}

	public void setType(LandType type) {
		this.type = type;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public Location getLoc1() {
		return loc1;
	}

	public void setLoc1(Location loc1) {
		this.loc1 = loc1;
	}

	public Location getLoc2() {
		return loc2;
	}

	public void setLoc2(Location loc2) {
		this.loc2 = loc2;
	}

	public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}

	public int getLevelLock() {
		return levelLock;
	}

	public void setLevelLock(int levelLock) {
		this.levelLock = levelLock;
	}

	public ItemStack getItemLock() {
		return itemLock;
	}

	public void setItemLock(ItemStack itemLock) {
		this.itemLock = itemLock;
	}

	public boolean isCanBreak() {
		return canBreak;
	}

	public void setCanBreak(boolean canBreak) {
		this.canBreak = canBreak;
	}

	public boolean isCanPlace() {
		return canPlace;
	}

	public void setCanPlace(boolean canPlace) {
		this.canPlace = canPlace;
	}

	public boolean isCanChat() {
		return canChat;
	}

	public void setCanChat(boolean canChat) {
		this.canChat = canChat;
	}

	public boolean isCanPvp() {
		return canPvp;
	}

	public void setCanPvp(boolean canPvp) {
		this.canPvp = canPvp;
	}

	public LandDTO(String code, LandType type, String display, String name, String description, UUID owner,
			Location loc1, Location loc2, boolean lock, int levelLock, ItemStack itemLock, boolean canBreak,
			boolean canPlace, boolean canChat, boolean canPvp) {
		super();
		this.code = code;
		this.type = type;
		this.display = display;
		this.name = name;
		this.description = description;
		this.owner = owner;
		this.loc1 = loc1;
		this.loc2 = loc2;
		this.lock = lock;
		this.levelLock = levelLock;
		this.itemLock = itemLock;
		this.canBreak = canBreak;
		this.canPlace = canPlace;
		this.canChat = canChat;
		this.canPvp = canPvp;
	}

	public LandDTO() {

	}

	public static LandDTO deserialize(Map<String, Object> d) {
		return new LandDTO((String) d.get("code"), LandType.valueOf((String) d.get("type")), (String) d.get("display"),
				(String) d.get("name"), (String) d.get("description"), (String)d.get("owner")==null?null:UUID.fromString((String)d.get("owner")),
				(Location) d.get("loc1"), (Location) d.get("loc2"), (Boolean) d.get("lock"), (int) d.get("levelLock"),
				(ItemStack) d.get("itemLock"), (Boolean) d.get("canBreak"), (Boolean) d.get("canPlace"),
				(Boolean) d.get("canChat"), (Boolean) d.get("canPvp"));
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> s = new HashMap<String, Object>();
		s.put("code", code);
		s.put("type", type.toString());
		s.put("display", display);
		s.put("name", name);
		s.put("description", description);
		s.put("owner", owner);
		s.put("loc1", loc1);
		s.put("loc2", loc2);
		s.put("lock", lock);
		s.put("levelLock", levelLock);
		s.put("itemLock", itemLock);
		s.put("canBreak", canBreak);
		s.put("canPlace", canPlace);
		s.put("canChat", canChat);
		s.put("canPvp", canPvp);
		return s;
	}
}
