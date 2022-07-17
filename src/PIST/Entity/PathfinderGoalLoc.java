package PIST.Entity;

import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.Navigation;
import net.minecraft.server.v1_12_R1.PathEntity;
import net.minecraft.server.v1_12_R1.PathfinderGoal;
import org.bukkit.Location;

import com.google.gson.JsonElement;

public class PathfinderGoalLoc extends PathfinderGoal {

    private float speed;

    private EntityInsentient a; //펫
    private EntityLiving b; //주인
    private Location loc; //목적지
    private final float g; //펫과 주인간의 거리 기준치
    
    private double x;
    private double y;
    private double z;

    private Navigation navigation;

    public PathfinderGoalLoc(EntityInsentient entity, float speed,Location loc) {
        this.a = entity;
        this.navigation = (Navigation) this.a.getNavigation();
        this.speed = speed;
        this.loc = loc;
        this.g = 20;
    }

    @Override
    public boolean a() { //매 초마다 실행, 리턴이 true면 길찾기
    	this.b = this.a.getGoalTarget();
    	if(this.b == null)
    		return false;
    	else if(this.a.getName() == null)
    		return false;
    	//else if(!(this.a.getName().toString().contains(this.b.getName())))
    		//return false;
    	else if(this.b.h(this.a) > (double) (this.g * this.g)) { //거리가 떨어져있다면
    		a.setPosition(this.b.locX, this.b.locY, this.b.locZ);
    		return false;
    	} else {
    		this.x = this.loc.getX();
    		this.y = this.loc.getY();
    		this.z = this.loc.getZ();
    		return true; // c() 실행
    	}
    }

    @Override
    public boolean b() { //c()가 실행된 이후에 실행
        return false;
    }

    @Override
    public void c() { //Running
        PathEntity pathEntity = this.navigation.a(this.x, this.y, this.z);
        CustomizingEntityTools.setFollowRange(a, 2000);
        this.navigation.a(pathEntity, speed);
    }
}