package PIST.Enum;

public enum ItemRank {
	LOWEST("§8부서질듯한"),
	LOW("§7일반"),
	NORMAL("§d희귀"),
	HIGH("§6전설"),
	HIGHEST("§5신화");
	
	private final String name; // 인스턴스 필드 추가
	
	// 생성자 추가
	ItemRank(String name) {
		this.name = name; 
	}
 
	// 인스턴스 필드 get메서드 추가
	public String getName() { return name; }
}
