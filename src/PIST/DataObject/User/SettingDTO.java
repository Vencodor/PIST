package PIST.DataObject.User;

public class SettingDTO {
	String name = null;
	boolean isShowSb = true;
	boolean isShowSbLevel = true;
	boolean isShowSbMoney = true;
	boolean isShowSbRank = true;
	boolean isShowSbGuild = true;
	boolean isShowSbNowFlat = true;
	boolean isShowSbMyType = true;
	boolean isShowSbStat = true;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isShowSb() {
		return isShowSb;
	}
	public void setShowSb(boolean isShowSb) {
		this.isShowSb = isShowSb;
	}
	public boolean isShowSbLevel() {
		return isShowSbLevel;
	}
	public void setShowSbLevel(boolean isShowSbLevel) {
		this.isShowSbLevel = isShowSbLevel;
	}
	public boolean isShowSbMoney() {
		return isShowSbMoney;
	}
	public void setShowSbMoney(boolean isShowSbMoney) {
		this.isShowSbMoney = isShowSbMoney;
	}
	public boolean isShowSbRank() {
		return isShowSbRank;
	}
	public void setShowSbRank(boolean isShowSbRank) {
		this.isShowSbRank = isShowSbRank;
	}
	public boolean isShowSbGuild() {
		return isShowSbGuild;
	}
	public void setShowSbGuild(boolean isShowSbGuild) {
		this.isShowSbGuild = isShowSbGuild;
	}
	public boolean isShowSbNowFlat() {
		return isShowSbNowFlat;
	}
	public void setShowSbNowFlat(boolean isShowSbNowFlat) {
		this.isShowSbNowFlat = isShowSbNowFlat;
	}
	public boolean isShowSbMyType() {
		return isShowSbMyType;
	}
	public void setShowSbMyType(boolean isShowSbMyType) {
		this.isShowSbMyType = isShowSbMyType;
	}
	public boolean isShowSbStat() {
		return isShowSbStat;
	}
	public void setShowSbStat(boolean isShowSbStat) {
		this.isShowSbStat = isShowSbStat;
	}
	public SettingDTO(String name, boolean isShowSb, boolean isShowSbLevel, boolean isShowSbMoney, boolean isShowSbRank,
			boolean isShowSbGuild, boolean isShowSbNowFlat, boolean isShowSbMyType, boolean isShowSbStat) {
		super();
		this.name = name;
		this.isShowSb = isShowSb;
		this.isShowSbLevel = isShowSbLevel;
		this.isShowSbMoney = isShowSbMoney;
		this.isShowSbRank = isShowSbRank;
		this.isShowSbGuild = isShowSbGuild;
		this.isShowSbNowFlat = isShowSbNowFlat;
		this.isShowSbMyType = isShowSbMyType;
		this.isShowSbStat = isShowSbStat;
	}
	public SettingDTO() {	
	}
}
