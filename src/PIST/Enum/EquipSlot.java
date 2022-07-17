package PIST.Enum;

import java.util.ArrayList;
import java.util.List;

public enum EquipSlot {
	RING_1("반지_1",10),
	RING_2("반지_2",19),
	
	PENDANT_1("목걸이_1",28),
	PENDANT_2("목걸이_2",37),
	
	EARRING_1("귀걸이_1",11),
	EARRING_2("귀걸이_2",20),
	
	RIDING("라이딩_1",50),
	PET("펫",49),
	
	ARTIFACT_1("아티펙트_1",22),
	ARTIFACT_2("아티펙트_2",31),
	CREATURE("크리쳐",23),
	RUNE("룬",32),
	
	ATTRIBUTE("속성",5);
	
	private final String name;
	private final int slot;
	
	EquipSlot(String name,int slot) {
		this.name = name;
		this.slot = slot;
	}
	
	public String getName() { return name; }
	public int getSlot() { return slot; }
	
	public static List<EquipSlot> getList() {
		List<EquipSlot> list = new ArrayList<EquipSlot>();
		list.add(RING_1);
		list.add(RING_2);
		list.add(PENDANT_1);
		list.add(PENDANT_2);
		list.add(EARRING_1);
		list.add(EARRING_2);
		list.add(RIDING);
		list.add(PET);
		list.add(ARTIFACT_1);
		list.add(ARTIFACT_2);
		list.add(CREATURE);
		list.add(RUNE);
		list.add(ATTRIBUTE);
		return list;
	}
}
