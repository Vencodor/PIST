package PIST.Enum;

import org.bukkit.Color;

public enum Attribute {
	NOT_RELEASE("해금되지 않음","§4","아직 해금되지 않은 속성칸입니다.",Color.BLACK,1,0),
	NONE("없음","§4","아직 속성이 부여되지 않았습니다.",Color.BLACK,1,0),
	FIRE("불","§c","데미지가 강하고, 광역기가 많습니다.",Color.RED,7,1.2),
	WATER("물","§9","데미지는 약하되, 신체강화 스킬이 있습니다.",Color.BLUE,7,1.2),
	EARTH("땅","§a","상대적으로 튼튼하지만, 둔합니다.",Color.MAROON,16,1.6),
	WIND("바람","§b","데미지가 약하지만, 빠릅니다.",Color.WHITE,3,0.9),
	ELECTRIC("전기","§e","데미지가 매우 약하지만, 매우 빠릅니다.",Color.YELLOW,1,0.7),
	ICE("얼음","§b","상대적으로 둔하지만, 디버프형 스킬이 있습니다.",Color.AQUA,10,1.3),
	METAL("금속","§7","매우 튼튼하지만, 매우 둔합니다.",Color.SILVER,26,2.1);

	private final String name;
	private final String prefixColor;
	private final String description;
	private final Color color;
	private final long coolTime;
	private final double damageAdd;
	
	Attribute(String name,String prefixColor,String description,Color color,long coolTime,double damageAdd) {
		this.name = name;
		this.prefixColor = prefixColor;
		this.description = description;
		this.color = color;
		this.coolTime = coolTime;
		this.damageAdd = damageAdd;
	}
	
	public double getDamageAdd() { return damageAdd; }
	
	public Color getRGBColor() { return color; }
	
	public Long getCoolTime() { return coolTime; }
	
	public String getColor() { return prefixColor; }
	
	public String getDescription(){ return description; }
	
	public String getName() { return name; }
	
	public static Attribute[] getAttribute() {
		Attribute[] ab = {FIRE,WATER,EARTH,WIND,ELECTRIC,ICE,METAL};
		return ab;
	}
	
	//불,물,땅,바람,전기,얼음,금속
}
