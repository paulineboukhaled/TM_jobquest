
public class Skill {
	private String name = "";
	private int numberOfYear=0;
	private int levelOfExpertise=0;
	private double weight=0;
	private String URI="";
	
	public Skill(String name, int numberOfYear, int levelOfExpertise, double weight, String URI){
		setName(name);
		setNumberOfYear(numberOfYear);
		setLevelOfExpertise(levelOfExpertise);
		setWeight(weight);
		setURI(URI);	
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumberOfYear() {
		return numberOfYear;
	}

	public void setNumberOfYear(int numberOfYear) {
		this.numberOfYear = numberOfYear;
	}

	public int getLevelOfExpertise() {
		return levelOfExpertise;
	}

	public void setLevelOfExpertise(int levelOfExpertise) {
		this.levelOfExpertise = levelOfExpertise;
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
