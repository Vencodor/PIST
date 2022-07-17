package PIST.Enum;

public enum GatherType {
	버튼("§a당신은 스위치를 작동했습니다!"),
	함정("§c당신은 어딘가 이상한느낌이 듭니다."),
	수집("§6당신은 길거리에 떨어진 물건을 주웠습니다.");
	
	private final String msg; // 인스턴스 필드 추가
	
	// 생성자 추가
	GatherType(String msg) {
		this.msg = msg; 
	}
 
	// 인스턴스 필드 get메서드 추가
	public String getMessage() { return msg; }
}
