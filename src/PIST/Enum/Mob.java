package PIST.Enum;

import org.bukkit.entity.EntityType;

public enum Mob {
	// 이름 몹타입 몹레벨 몹체력 몹데미지 몹속도(기본 0.15) 처치시 돈 처치시 경험치 몹속성 몹설명
	돼지("꿀돼지", EntityType.PIG, 3, 10.0, 2, 0.15F, 300, 15, Attribute.NONE, "가장 흔히 볼 수 있는 몹이다.");
	
	private final String name;
	private final EntityType type;
	private final int level;
	private final double health;
	private final double damage;
	private final float speed;
	private final int money;
	private final int exp;
	private final Attribute att;
	private final String description;

	private Mob(String name, EntityType type, int level, double health, double damage, float speed, int money, int exp,
			Attribute att, String description) {
		this.name = name;
		this.type = type;
		this.level = level;
		this.health = health;
		this.damage = damage;
		this.speed = speed;
		this.money = money;
		this.exp = exp;
		this.att = att;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public EntityType getType() {
		return type;
	}

	public int getLevel() {
		return level;
	}

	public double getHealth() {
		return health;
	}

	public double getDamage() {
		return damage;
	}

	public float getSpeed() {
		return speed;
	}

	public int getMoney() {
		return money;
	}

	public int getExp() {
		return exp;
	}

	public Attribute getAtt() {
		return att;
	}

	public String getDescription() {
		return description;
	}

	public static Mob[] getMobs() {
		Mob[] m = {};
		return m;
	}
}
