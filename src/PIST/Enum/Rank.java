package PIST.Enum;

public enum Rank {
	DEFULT("시민"),
	KNIGHT0("준기사"),
	KNIGHT1("평기사"),
	CASTELLEN("성주"),
	BARON("남작"),
	COUNT("백작"),
	MARQUIS("후작"),
	PEACOCK("공작"),
	KING("국왕");
	
	
	//국왕 > 공작 > 후작 > 백작 > 남작 > 성주 > 평기사 > 준기사
	
	private final String name; // 인스턴스 필드 추가
	
	// 생성자 추가
	Rank(String name) {
		this.name = name; 
	}
 
	// 인스턴스 필드 get메서드 추가
	public String getName() { return name; }
	
	public static Rank[] getRanks() {
		Rank[] rk = {DEFULT,KNIGHT0,KNIGHT1,CASTELLEN,BARON,COUNT,MARQUIS,PEACOCK,KING};
		return rk;
	}
}
