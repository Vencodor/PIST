package PIST.Entity;

import net.minecraft.server.v1_12_R1.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;

public class CustomPet extends EntityZombieHusk {

    private static CustomPet entity;

    public CustomPet(Location loc,Player p) {
        super(((CraftWorld)loc.getWorld()).getHandle());
        setCustomName("§7"+p.getName()+"");
        setInvulnerable(true);
        setCustomNameVisible(true);
        setSilent(true);
        setBaby(true);
        
        this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

        //LinkedHashSet<?> setB = (LinkedHashSet<?>) getPrivateField("b", PathfinderGoalSelector.class, goalSelector); setB.clear();
        //LinkedHashSet<?> setC = (LinkedHashSet<?>) getPrivateField("c", PathfinderGoalSelector.class, goalSelector); setC.clear();
        
        List goalB = (List)getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
        List goalC = (List)getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
        List targetB = (List)getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
        List targetC = (List)getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();
        
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, false));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class,  false));
        
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 0.0F));
        this.goalSelector.a(3, new PathfinderGoalPet(this, 1F));
        
        //this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
        //this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, false));
        
        this.setGoalTarget((EntityLiving)((CraftPlayer) p).getHandle(), TargetReason.CUSTOM, true);
        entity = this;
    }
    
    public void setGoalLoc(Location loc) {
    	this.goalSelector.a(3, new PathfinderGoalLoc(this, 1F,loc));
    }

    public static Object getPrivateField(String fieldName, Class<PathfinderGoalSelector> clazz, Object object)
    {
        Field field;
        Object o = null;
        try
        {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        }
        catch(NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return o;
    }

    public static CustomPet getVillager() {
        return entity;
    }
}