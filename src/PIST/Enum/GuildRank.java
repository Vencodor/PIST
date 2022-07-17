package PIST.Enum;

public enum GuildRank {
	MEMBER("길드원"),
	DEPUTY_LEADER("부 길드장"),
	LEADER("길드장");
	
	private final String name; // 인스턴스 필드 추가
	
	// 생성자 추가
	GuildRank(String name) {
		this.name = name; 
	}
 
	// 인스턴스 필드 get메서드 추가
	public String getName() { return name; }
}
