package PIST.ItemManager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import PIST.Pist;
import PIST.ServerManager.PistPlugin;
import net.md_5.bungee.api.ChatColor;

public class PistItem extends PistPlugin implements CommandExecutor{
	public PistItem (Pist ser) {
		super(ser);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player)sender;
		if(label.equalsIgnoreCase("아이템")) {
			if(!p.isOp()) return true;
			
			if(args.length==0) {
				p.sendMessage(gray+"/아이템 무기 "+green+"[이름] [등급] [데미지] [효과] [효과설명] "+gray+"(베이스 아이템을 들고 명령어 입력)");
				p.sendMessage(gray+" ( _ 는 띄어쓰기로 대체됩니다. 드_락사르 -> 드 락사르 ) ");
			} else {
				if(args[0].equalsIgnoreCase("무기")) {
					if(args.length<4) {
						p.sendMessage(gray+"/아이템 무기 "+green+"[이름] [등급] [데미지] [효과] [효과설명]");
					} else {
						ItemStack baseItem = p.getInventory().getItemInMainHand();
						if(baseItem==null||baseItem.getType() == Material.AIR) baseItem = new ItemStack(Material.DIAMOND_SWORD);
						if(args.length==4) {
							p.getInventory().addItem(createSword(baseItem,args[1],args[2],args[3],null,null));
						} else {
							p.getInventory().addItem(createSword(baseItem,args[1],args[2],args[3],args[4],stringToLore(args[5])));
						}
					}
				} else if(args[0].equalsIgnoreCase("전리품")) {
					
				}
			}
		}
		return true;
	}
	
	public static ItemStack createSword(ItemStack materi,String name,String rank,String damage,String effectName,List<String> effect) {
		ItemStack i = materi;
		ItemMeta im = i.getItemMeta();
		List<String> l = new ArrayList<String>();
		l.add(ChatColor.WHITE+" ");
		l.add("§8● §l유형 : §6무기");
		l.add("§8● §l등급 : §4"+rank.replace("&", "§"));
		l.add(ChatColor.WHITE+" ");
		l.add("§8● §l공격력 : §c"+damage.replace("&", "§"));
		l.add(ChatColor.WHITE+" ");
		if(effectName!=null&&effect!=null) {
			l.add("   §6§l"+effectName.replace("_", " ").replace("&", "§"));
			for(String a : effect) {
				l.add("     §7"+a.replace("_", " ").replace("&", "§"));
			}
			//l.add("     §7상대를 §a3번 §7타격시 추가로");
			//l.add("     §c공격력의 10% 피해§7를 줍니다.");
			l.add(ChatColor.WHITE+" ");
		} else {
			l.add("   §c이 무기에 대한 효과가 없습니다.");
			l.add(ChatColor.WHITE+" ");
		}
		im.setDisplayName("§6§l"+name.replace("_", " ").replace("&", "§"));
		im.setUnbreakable(true);
		im.setLore(l);
		i.setItemMeta(im);
		return i;
	}
	
	public List<String> stringToLore(String str) {
		StringBuffer sb = new StringBuffer();
		List<String> lore = new ArrayList<String>();
		for(int i=0; i<str.length(); i++) {
			if(sb.length()>= 12) {
				lore.add(sb.toString());
				sb = new StringBuffer();
			}
			sb.append(str.charAt(i));
		}
		return lore;
	}
}
