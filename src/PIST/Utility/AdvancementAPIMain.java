package PIST.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.CloseableThreadContext.Instance;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import PIST.Pist;
import PIST.Utility.AdvancementAPI.FrameType;

public class AdvancementAPIMain {
	
	public List<AdvancementAPI> apiList = new ArrayList<AdvancementAPI>();
	public static AdvancementAPIMain instance;
	
	public void setInstanceThis() {
		instance = this;
	}
	
	public void send(String title, String description, MaterialData material, Player ... player){
		AdvancementAPI test = new AdvancementAPI(new NamespacedKey(Pist.getInstance(), "story/" + UUID.randomUUID().toString()))
        .withFrame(FrameType.CHALLANGE)
        .withTrigger("minecraft:impossible")
        .withIcon(material)
        .withTitle(title)
        .withDescription(description)
        .withAnnouncement(false)
		.withBackground("minecraft:textures/blocks/bedrock.png");
		test.loadAdvancement();
		test.sendPlayer(player);
		
		Bukkit.getScheduler().runTaskLater(Pist.getInstance(), new Runnable() {
			@Override
			public void run() {
				test.delete(player);
			}
		}, 10);
		
	}

	public void AddAdvancment(AdvancementAPI api){
		NamespacedKey key = api.getID();
		for(AdvancementAPI adAPI : this.apiList){
			if(adAPI.getID().toString().equalsIgnoreCase(key.toString())){
				return;
			}
		}
		this.apiList.add(api);
	}
	
	public static AdvancementAPIMain getInstance() {
		return instance;
	}
}