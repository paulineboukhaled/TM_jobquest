package model;

public class SkillBDD {
	private String name;
	private String uri;
	
	
	public SkillBDD(String name, String uri){
		setName(name);
		setUri(uri);	
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	

}
