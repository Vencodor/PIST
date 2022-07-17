package PIST.Entity;

import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.Navigation;
import net.minecraft.server.v1_12_R1.PathEntity;
import net.minecraft.server.v1_12_R1.PathfinderGoal;
import org.bukkit.Location;

public class PathfinderGoalPet extends PathfinderGoal {

    private float speed;

    private EntityInsentient a; //펫
    private EntityLiving b; //주인
    private final float g; //펫과 주인간의 거리 기준치
    
    private double x;
    private double y;
    private double z;

    private Navigation navigation;

    public PathfinderGoalPet(EntityInsentient entity, float speed) {
        this.a = entity;
        this.navigation = (Navigation) this.a.getNavigation();
        this.speed = speed;
        this.g = 18;
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
    		this.x = this.b.locX-1;
    		this.y = this.b.locY;
    		this.z = this.b.locZ-1;
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
        //CustomizingEntityTools.setFollowRange(a, 100);
        this.navigation.a(pathEntity, speed);
    }
}