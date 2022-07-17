package PIST.AbilityManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import PIST.Pist;
import PIST.PistScoreBoard;
import PIST.DataObject.User.AttributeDTO;
import PIST.Enum.Attribute;
import PIST.ServerManager.PistPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PistAttribute extends PistPlugin implements Listener {
	
	public static HashMap<UUID,AttributeDTO> userAtt = new HashMap<UUID,AttributeDTO>();
	public static HashMap<String,Attribute> nowAtt = new HashMap<String,Attribute>();
	//public static HashMap<Attribute,ItemStack> attItems = new HashMap<Attribute,ItemStack>();
	private HashMap<String,Boolean> coolTime = new HashMap<String,Boolean>();
	Pist pist;
	
	public void setPlugin(Pist pl) {
		pist = pl;
	}
	
	public PistAttribute(Pist ser) {
		super(ser);
	}
	
	@EventHandler
	public void PlayerSwapHand(PlayerSwapHandItemsEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getOffHandItem(); //착용
		if(isContainsLore(item, "무기")) {
			String display = item.getItemMeta().getDisplayName();
			e.setCancelled(true);
			
			List<Attribute> list = getList(p);
			int nextIndex = list.indexOf(getAtt(p));
			if(nextIndex==-1) {//무기와 해당하는 플레이어 속성이 없다면 무시
				p.sendMessage(red+"당신의 무기와 일치하는 속성을 가지고 있지 않습니다.");
				return;
			}
			if(coolTime.get(p.getName())==null)
				coolTime.put(p.getName(), false);
			else if(!coolTime.get(p.getName())) {
				nextIndex++;
				PistScoreBoard sb = new PistScoreBoard(pist);
				if(nextIndex<list.size()&&list.get(nextIndex)!=null&&list.get(nextIndex)!=Attribute.NONE&&list.get(nextIndex)!=Attribute.NOT_RELEASE) { //만약 다음 속성이 있다면.
					nowAtt.put(p.getName(), list.get(nextIndex));
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
							TextComponent.fromLegacyText(gray+"( "+dgreen+"속성변경"+gray+" )  "
					+list.get(nextIndex-1).getColor()+list.get(nextIndex-1).getName()+gray+" -> "+list.get(nextIndex).getColor()+list.get(nextIndex).getName()));
					p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.3F, 1.4F);
					//p.setVelocity(new Vector(p.getLocation().getDirection().multiply(1.3).getX(),p.getLocation().getDirection().multiply(0.7).getY(),p.getLocation().getDirection().multiply(1.3).getZ()));
					sb.reloadScoreBoard(p);
					startCool(p);
				} else {
					nowAtt.put(p.getName(), list.get(0));
					
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
							TextComponent.fromLegacyText(gray+"( "+dgreen+"속성변경"+gray+" )  "
					+list.get(nextIndex-1).getColor()+list.get(nextIndex-1).getName()+gray+" -> "+list.get(0).getColor()+list.get(0).getName()));
					p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.3F, 1.4F);
					//p.setVelocity(new Vector(p.getLocation().getDirection().multiply(1.3).getX(),p.getLocation().getDirection().multiply(0.7).getY(),p.getLocation().getDirection().multiply(1.3).getZ()));
					sb.reloadScoreBoard(p);
					startCool(p);
				}
			} else {
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
						TextComponent.fromLegacyText(red+"속성 변경까지 0.2초 남았습니다."));
			}
		}
	}
	
	@EventHandler
	public void onPickUp(EntityPickupItemEvent e) {
		if(isContainsDisplay(e.getItem().getItemStack(), "속성")) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if(isContainsDisplay(e.getItemDrop().getItemStack(), "속성")) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(red+"속성 아이템은 버릴 수 없습니다!");
		}
	}
	
	private void startCool(Player p) {
		coolTime.put(p.getName(), true);
		Bukkit.getScheduler().scheduleSyncDelayedTask(pist, new Runnable() {
			@Override
			public void run() {
				coolTime.put(p.getName(), false);
			}
		}, 4L);
	}
	/*
	public HashMap<Attribute,ItemStack> setting(Player p) {
		attItems.put(Attribute.FIRE,createItem(new ItemStack(Material.DIAMOND_SWORD)
				,getItemDisplay(Attribute.FIRE),getItemLore(Attribute.FIRE)));
		
		attItems.put(Attribute.WATER,createItem(new ItemStack(Material.IRON_SWORD)
				,getItemDisplay(Attribute.WATER),getItemLore(Attribute.WATER)));
		
		attItems.put(Attribute.EARTH,createItem(new ItemStack(Material.WOOD_SWORD)
				,getItemDisplay(Attribute.EARTH),getItemLore(Attribute.EARTH)));
		
		attItems.put(Attribute.ELECTRIC,createItem(new ItemStack(Material.STONE_SWORD)
				,getItemDisplay(Attribute.ELECTRIC),getItemLore(Attribute.ELECTRIC)));
	
		attItems.put(Attribute.ICE,createItem(new ItemStack(Material.DIAMOND_SWORD)
				,getItemDisplay(Attribute.ICE),getItemLore(Attribute.ICE)));
	
		attItems.put(Attribute.METAL,createItem(new ItemStack(Material.DIAMOND_SWORD)
				,getItemDisplay(Attribute.METAL),getItemLore(Attribute.METAL)));
	
		attItems.put(Attribute.WIND,createItem(new ItemStack(Material.DIAMOND_SWORD)
				,getItemDisplay(Attribute.WIND),getItemLore(Attribute.WIND)));
	}
	*/
	public static boolean addAtt(Player p,Attribute att) {
		UUID uid = p.getUniqueId();
		checkHasAtt(p);
		AttributeDTO attDTO = userAtt.get(uid);
		if(attDTO.getFirstAtt()!=Attribute.NONE) {
			if(attDTO.getSecondAtt()!=Attribute.NONE) {
				if(attDTO.getThirdAtt()!=Attribute.NONE) {
					if(attDTO.getFourthAtt()!=Attribute.NONE) {
						return false;
					} else
						attDTO.setFourthAtt(att);
				} else
					attDTO.setThirdAtt(att);
			} else
				attDTO.setSecondAtt(att);
		} else
			attDTO.setFirstAtt(att);
		return true;
	}
	
	public static List<Attribute> getList(Player p) {
		UUID uid = p.getUniqueId();
		checkHasAtt(p);
		AttributeDTO att = userAtt.get(uid);
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(att.getFirstAtt());
		list.add(att.getSecondAtt());
		list.add(att.getThirdAtt());
		list.add(att.getFourthAtt());
		list.add(att.getFifthAtt());
		return list;
	}
	
	private static void checkHasAtt(Player p) {
		UUID uid = p.getUniqueId();
		if(userAtt.get(uid)==null) {
			userAtt.put(uid, new AttributeDTO());
			p.sendMessage(ChatColor.AQUA+"당신의 속성 데이터가 새롭게 생성 되었습니다!");
		}
	}
	
	public static Attribute getAtt(Player p) {
		if(userAtt.get(p.getUniqueId())==null)
			return Attribute.NONE;
		if(nowAtt.get(p.getName())==null)
			nowAtt.put(p.getName(), userAtt.get(p.getUniqueId()).getFirstAtt());
		return nowAtt.get(p.getName());
	}
	
	private List<String> getItemLore(Attribute att) {
		List<String> l = new ArrayList<String>();
		l.add(white+" ");
		l.add(att.getColor()+" "+att.getName()+gray+"의 속성을 가진 검입니다. ");
		l.add(white+" ");
		l.add(att.getColor()+"❘ "+gray+att.getDescription());
		l.add(white+" ");
		l.add(gray+" [ 속성을 교체하려면 F를 누르세요. ] ");
		return l;
	}
	
	private String getItemDisplay(Attribute att) {
		return gray+"[ "+green+"속성"+gray+" ]  "+att.getColor()+att.getName();
	}
	
	public Attribute getType(String s) {
		if(s.contains("불")) 
			return Attribute.valueOf("FIRE");
		if(s.contains("물")) 
			return Attribute.valueOf("WATER");
		if(s.contains("바람")) 
			return Attribute.valueOf("WIND");
		if(s.contains("땅")) 
			return Attribute.valueOf("EARTH");
		if(s.contains("전기")) 
			return Attribute.valueOf("ELECTRIC");
		if(s.contains("얼음")) 
			return Attribute.valueOf("ICE");
		if(s.contains("금속")) 
			return Attribute.valueOf("METAL");
		return Attribute.NONE;
	}
}
