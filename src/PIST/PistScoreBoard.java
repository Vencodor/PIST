package PIST;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import PIST.AbilityManager.PistAttribute;
import PIST.DataObject.User.SettingDTO;
import PIST.DataObject.User.UserRecordDTO;
import PIST.Guild.PistGuild;
import PIST.Land.PistLandManager;
import PIST.ServerManager.PistPlugin;
import PIST.SettingManager.SettingManager;
import PIST.UserManager.PistUser;
import net.md_5.bungee.api.ChatColor;

public class PistScoreBoard extends PistPlugin{

	public PistScoreBoard(Pist ser) {
		super(ser);
	}
	public static Pist plugin;
	public static PistLandManager landM;
	private ScoreboardManager sm = Bukkit.getScoreboardManager();
	
	public static void setPlugin(Pist pl) {
		plugin = pl;
		landM = new PistLandManager(pl);
	}

	public void startScoreBoard() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					//String name = p.getName();
					p.setScoreboard(getSc(p));
				}
			}
		},30L,20L*3);
	}
	
	public void reloadScoreBoard(Player p) {
		p.setScoreboard(getSc(p));
	}

	private Scoreboard getSc(Player p) {
		SettingDTO s = getSettingDTO(p);
		if(!s.isShowSb()) {
			return Bukkit.getScoreboardManager().getNewScoreboard();
		}
		//p.sendMessage(s.isShowSb()+"");
		Scoreboard board = sm.getNewScoreboard();
		Objective obj = board.registerNewObjective("board", "dummy");
		obj.setDisplayName(gray + "       " + red + ChatColor.BOLD + "PIST.kr" + gray + "       ");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		Score[] score = new Score[18];
		int n = 0;
		int maxLen = 15;
		
		UserRecordDTO user = PistUser.getInfo(p);
		
		score[0] = obj.getScore(white + " ");
		if(s.isShowSbLevel()) score[++n] = obj.getScore(gray + "    레벨 : "+green+user.getLevel()+"Lv");
		if(s.isShowSbMoney()) score[++n] = obj.getScore(gray + "    피스 : "+green+showMoney(user.getMoney()));
		if(s.isShowSbMoney()) score[++n] = obj.getScore(white+"      ");
		
		if(s.isShowSbRank()) score[++n] = obj.getScore(gray + "    등급 : "+green+user.getRank().getName());
		if(s.isShowSbRank()) score[++n] = obj.getScore(white+"   ");
		
		if(s.isShowSbNowFlat()) score[++n] = obj.getScore(gray + "  현재 땅 : "+orange+landM.getPlayerAreaName(p));
		if(s.isShowSbMyType()) score[++n] = obj.getScore(gray+"  현재 속성 : "+red+PistAttribute.getAtt(p).getName());
		if(s.isShowSbMyType()) score[++n] = obj.getScore(white + "    ");
		
		if(PistGuild.members.get(p.getUniqueId())==null) {
			if(s.isShowSbGuild()) score[++n] = obj.getScore(gray + "  소속 길드 : "+yellow+"없음");
		} else {
			if(s.isShowSbGuild()) score[++n] = obj.getScore(gray + "  소속 길드 : "+yellow+PistGuild.getGuildUseCode(
					PistGuild.members.get(p.getUniqueId()).getGuildCode() ).getName());
		}
		if(s.isShowSbGuild()) score[++n] = obj.getScore(white+"      ");
		
		if(s.isShowSbStat()) {
			score[++n] = obj.getScore(gray + "  체력 : "+aqua+Double.parseDouble(String.format("%.2f",p.getHealth()))+gray+"/"+aqua+p.getMaxHealth());
			score[++n] = obj.getScore(white+"     ");
			score[++n] = obj.getScore(gray + "  버프 상태 ("+aqua+"0"+gray+"/"+aqua+"2"+gray+")");
			score[++n] = obj.getScore(gray + "   - "+red+"초심자 버프");
			score[++n] = obj.getScore(gray + "   - "+red+"마녀의 저주");
		}

		for (int i = 0; i < score.length; i++) {
			if (score[i] != null)
				score[i].setScore(maxLen);
			maxLen--;
		}

		return board;

	}

}
