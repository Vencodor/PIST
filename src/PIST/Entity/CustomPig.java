package PIST.Entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityPig;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EnumHand;

public class CustomPig extends EntityPig{

	public CustomPig(Location loc) {
		super(((CraftWorld)loc.getWorld()).getHandle());
		this.setPosition(loc.getX(), loc.getY(), loc.getZ());
	}
	
	public boolean a(EntityHuman entity, EnumHand enumhand) {
        ((Player)CraftPlayer.getEntity(entity.getWorld().getServer(), (EntityPlayer)entity)).sendMessage("§2Braaaaaains!");
        return super.a(entity, enumhand);
    }
	
	
}
