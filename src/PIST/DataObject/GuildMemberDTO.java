package PIST.DataObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import PIST.Enum.GuildRank;

public class GuildMemberDTO implements ConfigurationSerializable {
	String guildCode = null;
	GuildRank rank = GuildRank.MEMBER;
	public String getGuildCode() {
		return guildCode;
	}
	public void setGuildCode(String guildCode) {
		this.guildCode = guildCode;
	}
	public GuildRank getRank() {
		return rank;
	}
	public void setRank(GuildRank rank) {
		this.rank = rank;
	}
	public GuildMemberDTO(String guildCode, GuildRank rank) {
		super();
		this.guildCode = guildCode;
		this.rank = rank;
	}
	public GuildMemberDTO() {
		
	}
	@Override
	public Map<String, Object> serialize() {
		HashMap<String ,Object> s = new HashMap<String,Object>();
		s.put("guildCode", guildCode);
		s.put("rank", rank.toString());
		return s;
	}
	
	public static GuildMemberDTO deserialize(Map<String, Object> d) {
		return new GuildMemberDTO((String)d.get("guildCode"),GuildRank.valueOf((String)d.get("rank")));
	}
}
