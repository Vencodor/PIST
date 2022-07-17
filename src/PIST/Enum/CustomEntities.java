package PIST.Enum;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.EntityType;

import PIST.Entity.CustomPet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum CustomEntities {

   PET("PET", 120, EntityType.ZOMBIE, EntityZombieHusk.class, CustomPet.class);

   private static Method validateEntityMethod;

   static  {
      try {
         validateEntityMethod = World.class.getDeclaredMethod("b", Entity.class);
         validateEntityMethod.setAccessible(true);
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      }
   }

   private String name;
   private int id;
   private EntityType entityType;
   private Class<? extends Entity> nmsClass;
   private Class<? extends Entity> customClass;
   private MinecraftKey key;
   private MinecraftKey oldKey;

   private CustomEntities(String name, int id, EntityType entityType, Class<? extends Entity> nmsClass, Class<? extends Entity> customClass) {
	   this.name = name;
      this.id = id;
      this.entityType = entityType;
      this.nmsClass = nmsClass;
      this.customClass = customClass;
      this.key = new MinecraftKey(name);
      this.oldKey = EntityTypes.b.b(nmsClass);
   }

   public static void registerEntities() { for (CustomEntities ce : CustomEntities.values()) ce.register(); }
   public static void unregisterEntities() { for (CustomEntities ce : CustomEntities.values()) ce.unregister(); }

   private void register() {
      EntityTypes.d.add(key);
      EntityTypes.b.a(id, key, customClass);
   }

   private void unregister() {
      EntityTypes.d.remove(key);
      EntityTypes.b.a(id, oldKey, nmsClass);
   }
/*
   public void spawn(Location spawnLoc) {
      try {
         Constructor customClassConstructor = customClass.getConstructor(World.class, Location.class);
         EntityLiving entity = (EntityLiving) customClassConstructor.newInstance(((CraftWorld) spawnLoc.getWorld()).getHandle(), spawnLoc);

         if(addEntityToWorld(((CraftWorld) spawnLoc.getWorld()).getHandle(), entity)) {
            System.out.println("DEBUG: ADD ENTITY=TRUE");
         }
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      }
   }
*/
   private boolean addEntityToWorld(WorldServer nmsWorld, Entity nmsEntity) {
      final int chunkX = MathHelper.floor(nmsEntity.locX / 16.0);
      final int chunkZ = MathHelper.floor(nmsEntity.locZ / 16.0);

      if (!nmsWorld.getChunkProviderServer().isLoaded(chunkX, chunkZ)) {
         nmsEntity.dead = true;
         return false;
      }

      nmsWorld.getChunkAt(chunkX, chunkZ).a(nmsEntity);
      nmsWorld.entityList.add(nmsEntity);

      try {
         validateEntityMethod.invoke(nmsWorld, nmsEntity);
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }

      return true;
   }

   public String getName() {
      return name;
   }

   public int getID() {
      return id;
   }

   public EntityType getEntityType() {
      return entityType;
   }

   public Class<?> getCustomClass() {
      return customClass;
   }
}