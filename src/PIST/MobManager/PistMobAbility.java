package PIST.MobManager;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import PIST.Pist;
import PIST.Enum.Mob;
import PIST.ServerManager.PistPlugin;

public class PistMobAbility extends PistPlugin {

	public PistMobAbility(Pist ser) {
		super(ser);
	}
	
	public static Entity spawnMob(Mob m,Location l) {
		Entity e = l.getWorld().spawnEntity(l, m.getType());
		return e;
	}
	
}
