package PIST.DataObject.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class PrefixDTO implements ConfigurationSerializable {
	String prefix = null;
	List<String> description = new ArrayList<String>();
	Date date = new Date();

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public List<String> getDescription() {
		return description;
	}

	public void setDescription(List<String> description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public PrefixDTO(String prefix, List<String> description, Date date) {
		super();
		this.prefix = prefix;
		this.description = description;
		this.date = date;
	}

	public PrefixDTO() {

	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> ser = new HashMap<>();
		ser.put("prefix", prefix);
		ser.put("description", description);
		ser.put("date", date);
		return ser;
	}
	
	public static PrefixDTO deserialize(Map<String, Object> d) {
		return new PrefixDTO((String)d.get("prefix"), (List<String>)d.get("description"), (Date)d.get("date"));
	}
	
}
