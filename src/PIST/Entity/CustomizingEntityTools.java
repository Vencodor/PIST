package PIST.Entity;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagDouble;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
 
 
public class CustomizingEntityTools {
   public static void setFollowRange(Entity entity, double followRange)
   {
     setOrAdAttribute(entity,"generic.followRange",followRange);
   }
   public static void setOrAdAttribute(Entity entity, String name, double base)
   {
     NBTTagCompound compound = new NBTTagCompound();
     entity.c(compound);
     NBTTagList attributeList;
     NBTTagCompound attribute = new NBTTagCompound();
     attribute.set("Name", new NBTTagString(name));
     attribute.set("Base", new NBTTagDouble(base));
     if( compound.hasKey("Attributes"))
     {
       attributeList = compound.getList("Attributes",10);
       for(int compt = 0;compt < attributeList.size();compt ++)
       {
         if(attributeList.get(compt).get("Name").equals(attribute.get("Name")))
         {
           attributeList.remove(compt);
         }
       }
       attributeList.add(attribute);
     }
     else
     {
       attributeList = new NBTTagList();
       attributeList.add(attribute);
     }
     compound.set("Attributes", attributeList);
  ((EntityLiving)entity).a(compound);
   }
}
