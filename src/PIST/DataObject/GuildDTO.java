package PIST.DataObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import PIST.Utility.Utility;

public class GuildDTO implements ConfigurationSerializable{
	String code = Utility.getRandomKey(5);
	String name = "이름 설정 필요.";
	String description = "길드 소개를 작성하세요.";
	String notice = "길드 내 공지사항을 작성하세요.";
	String leader = null;
	UUID leaderUid = null;
	String deputyLeader = null;
	UUID deputyLeaderUid = null; //부 길드장
	double guildLevel = 1;
	double guildPoint = 0;
	String guildCoreName = null;
	int maxPeople = 15;
	int memberLevelLock = 0;
	boolean isPublic = true; //비공개 시 리더가 직접 초대해야함
	Date whenCreated = new Date();
	//ArrayList<String> haveFlats = new ArrayList<String>(); 이것들은 나중에 따로 List로 관리
	//ArrayList<UUID> menber = new ArrayList<UUID>();
	//ArrayList<String> guildCrown = new ArrayList<String>(); 길드 업적 구상예정
	
	public GuildDTO(String leaderName,UUID leaderUid) {
		this.leader = leaderName;
		this.leaderUid = leaderUid;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public String getNotice() {
		return notice;
	}
	public void setNotice(String notice) {
		this.notice = notice;
	}
	public String getLeader() {
		return leader;
	}
	public void setLeader(String leader) {
		this.leader = leader;
	}
	public UUID getLeaderUid() {
		return leaderUid;
	}
	public void setLeaderUid(UUID leaderUid) {
		this.leaderUid = leaderUid;
	}
	public String getDeputyLeader() {
		return deputyLeader;
	}
	public void setDeputyLeader(String deputyLeader) {
		this.deputyLeader = deputyLeader;
	}
	public UUID getDeputyLeaderUid() {
		return deputyLeaderUid;
	}
	public void setDeputyLeaderUid(UUID deputyLeaderUid) {
		this.deputyLeaderUid = deputyLeaderUid;
	}
	public double getGuildLevel() {
		return guildLevel;
	}
	public void setGuildLevel(double guildLevel) {
		this.guildLevel = guildLevel;
	}
	public double getGuildPoint() {
		return guildPoint;
	}
	public void setGuildPoint(double guildPoint) {
		this.guildPoint = guildPoint;
	}
	public String getGuildCoreName() {
		return guildCoreName;
	}
	public void setGuildCoreName(String guildCoreName) {
		this.guildCoreName = guildCoreName;
	}
	public int getMaxPeople() {
		return maxPeople;
	}
	public void setMaxPeople(int maxPeople) {
		this.maxPeople = maxPeople;
	}
	public int getMemberLevelLock() {
		return memberLevelLock;
	}
	public void setMemberLevelLock(int memberLevelLock) {
		this.memberLevelLock = memberLevelLock;
	}
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	public Date getWhenCreated() {
		return whenCreated;
	}
	public void setWhenCreated(Date whenCreated) {
		this.whenCreated = whenCreated;
	}
	
	public GuildDTO(String code, String name, String description, String notice, String leader, String leaderUid,
			String deputyLeader, String deputyLeaderUid, double guildLevel, double guildPoint, String guildCoreName,
			int maxPeople, int memberLevelLock, boolean isPublic, Date whenCreated) {
		super();
		this.code = code;
		this.name = name;
		this.description = description;
		this.notice = notice;
		this.leader = leader;
		this.leaderUid = UUID.fromString(leaderUid);
		this.deputyLeader = deputyLeader;
		this.deputyLeaderUid = deputyLeaderUid==null?null:UUID.fromString(deputyLeaderUid);
		this.guildLevel = guildLevel;
		this.guildPoint = guildPoint;
		this.guildCoreName = guildCoreName;
		this.maxPeople = maxPeople;
		this.memberLevelLock = memberLevelLock;
		this.isPublic = isPublic;
		this.whenCreated = whenCreated;
	}
	public GuildDTO() {
		
	}
	
	public static GuildDTO deserialize(Map<String, Object> d) {
		return new GuildDTO((String)d.get("code"),
				(String)d.get("name"),
				(String)d.get("description"),
				(String)d.get("notice"),
				(String)d.get("leader"),
				(String)d.get("leaderUid"),
				(String)d.get("deputyLeader"),
				(String)d.get("deputyLeaderUid"),
				(double)d.get("guildLevel"),
				(double)d.get("guildPoint"),
				(String)d.get("guildCoreName"),
				(int)d.get("maxPeople"),
				(int)d.get("memberLevelLock"),
				(boolean)d.get("isPublic"),
				(Date)d.get("whenCreated")
				);
	}
	
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> s = new HashMap<String, Object>();
		s.put("code", code);
		s.put("name", name);
		s.put("description", description);
		s.put("notice", notice);
		s.put("leader", leader);
		s.put("leaderUid", leaderUid.toString());
		s.put("deputyLeader", deputyLeader);
		s.put("deputyLeaderUid", deputyLeaderUid==null?deputyLeaderUid:deputyLeaderUid.toString());
		s.put("guildLevel", guildLevel);
		s.put("guildPoint", guildPoint);
		s.put("guildCoreName", guildCoreName);
		s.put("maxPeople", maxPeople);
		s.put("memberLevelLock", memberLevelLock);
		s.put("isPublic", isPublic);
		s.put("whenCreated", whenCreated);
		return s;
	}
}
