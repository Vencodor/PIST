package PIST.DataObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import PIST.Enum.GatherType;

public class GatherInfoDTO implements ConfigurationSerializable{ //나중에 Gatter and Setter 다시 설정할것
	int code = 0;
	GatherType type = GatherType.수집;
	ArrayList<UUID> clearUser = new ArrayList<UUID>();
	String name = null;
	String description = null;
	Location loc = null;
	ItemStack item = new ItemStack(Material.AIR);
	NeedDataDTO need = new NeedDataDTO();
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public ArrayList<UUID> getClearUser() {
		return clearUser;
	}
	public void setClearUser(ArrayList<UUID> clearUser) {
		this.clearUser = clearUser;
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
	public Location getLoc() {
		return loc;
	}
	public void setLoc(Location loc) {
		this.loc = loc;
	}
	public ItemStack getItem() {
		return item;
	}
	public void setItem(ItemStack item) {
		this.item = item;
	}
	public NeedDataDTO getNeed() {
		return need;
	}
	public void setNeed(NeedDataDTO need) {
		this.need = need;
	}
	public GatherInfoDTO(int code, ArrayList<UUID> clearUser, String name, String description, Location loc,
			ItemStack item, NeedDataDTO need) {
		super();
		this.code = code;
		this.clearUser = clearUser;
		this.name = name;
		this.description = description;
		this.loc = loc;
		this.item = item;
		this.need = need;
	}
	public GatherInfoDTO() {
		
	}
	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> s = new HashMap<String,Object>();
		s.put("code", code);
		s.put("clearUser", clearUser);
		s.put("description", description);
		s.put("loc", loc);
		s.put("item", item);
		s.put("need", need);
		return s;
	}
	
	public static GatherInfoDTO deserialize(Map<String, Object> d) {
		return new GatherInfoDTO((int)d.get("code"),
				(ArrayList<UUID>)d.get("clearUser"),
				(String)d.get("name"),
				(String)d.get("description"),
				(Location)d.get("loc"),
				(ItemStack)d.get("item"),
				(NeedDataDTO)d.get("need")
				);
	}
}
