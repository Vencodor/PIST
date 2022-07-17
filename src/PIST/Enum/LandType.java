package PIST.Enum;

public enum LandType {
	던전("던전"),
	이벤트_던전("이벤트 던전"),
	평지("평지"),
	스폰("스폰"),
	성("성"),
	마을("마을"),
	섬("섬"),
	보스("보스방"),
	기타("기타");
	
	private final String name; // 인스턴스 필드 추가
	
	// 생성자 추가
	LandType(String name) {
		this.name = name; 
	}
 
	// 인스턴스 필드 get메서드 추가
	public String getName() { return name; }
}
