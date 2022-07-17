package PIST.DataObject.User;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import PIST.Enum.Attribute;
import PIST.Enum.Rank;

public class AttributeDTO implements ConfigurationSerializable {
	Attribute firstAtt = Attribute.NONE;
	Attribute secondAtt = Attribute.NONE;
	Attribute thirdAtt = Attribute.NONE;
	Attribute fourthAtt = Attribute.NONE;
	Attribute fifthAtt = Attribute.NOT_RELEASE;
	
	public Attribute getFirstAtt() {
		return firstAtt;
	}
	public void setFirstAtt(Attribute firstAtt) {
		this.firstAtt = firstAtt;
	}
	public Attribute getSecondAtt() {
		return secondAtt;
	}
	public void setSecondAtt(Attribute secondAtt) {
		this.secondAtt = secondAtt;
	}
	public Attribute getThirdAtt() {
		return thirdAtt;
	}
	public void setThirdAtt(Attribute thirdAtt) {
		this.thirdAtt = thirdAtt;
	}
	public Attribute getFourthAtt() {
		return fourthAtt;
	}
	public void setFourthAtt(Attribute fourthAtt) {
		this.fourthAtt = fourthAtt;
	}
	public Attribute getFifthAtt() {
		return fifthAtt;
	}
	public void setFifthAtt(Attribute fifthAtt) {
		this.fifthAtt = fifthAtt;
	}
	public AttributeDTO(Attribute firstAtt, Attribute secondAtt, Attribute thirdAtt, Attribute fourthAtt,Attribute fifthAtt) {
		super();
		this.firstAtt = firstAtt;
		this.secondAtt = secondAtt;
		this.thirdAtt = thirdAtt;
		this.fourthAtt = fourthAtt;
		this.fifthAtt = fifthAtt;
	}
	public AttributeDTO() {
		
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> serialize = new HashMap<String,Object>();
		serialize.put("first", firstAtt.toString());
		serialize.put("second", secondAtt.toString());
		serialize.put("third", thirdAtt.toString());
		serialize.put("fourth", fourthAtt.toString());
		serialize.put("fifth", fifthAtt.toString());
		return serialize;
	}
	
	public static AttributeDTO deserialize(Map<String, Object> d) {
		return new AttributeDTO(
				Attribute.valueOf((String)d.get("first")),
				Attribute.valueOf((String)d.get("second")), 
				Attribute.valueOf((String)d.get("third")),
				Attribute.valueOf((String)d.get("fourth")), 
				Attribute.valueOf((String)d.get("fifth")));
	}
	
}
