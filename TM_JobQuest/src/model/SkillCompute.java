package model;

public class SkillCompute extends Skill {
	private double weight=0;
	private String URI="";

	public SkillCompute(String stringValue, int i, int j, int k, String stringValue2) {
		super(stringValue, i, j);
		setWeight(k);
		setURI(stringValue2);
		// TODO Auto-generated constructor stub
	}

	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public String getURI() {
		return URI;
	}
	public void setURI(String uRI) {
		URI = uRI;
	}
	
	
}
