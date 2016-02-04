package model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Position {

	
	@SerializedName("uri")
    @Expose
    private String uri;
	@SerializedName("position")
	@Expose
	private String position;
	@SerializedName("employer")
	@Expose
	private String employer;
	@SerializedName("start")
	@Expose
	private String start;
	@SerializedName("end")
	@Expose
	private String end;
	@SerializedName("description")
	@Expose
	private String description;
	@SerializedName("city")
	@Expose
	private String city;
	@SerializedName("npa")
	@Expose
	private String npa;
	@SerializedName("skills")
	@Expose
	private List<Skill> skills = new ArrayList<Skill>();
	@SerializedName("schools")
	@Expose
	private List<SchoolPosition> schools = new ArrayList<SchoolPosition>();

	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	/**
	 * 
	 * @return
	 * The position
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * 
	 * @param position
	 * The position
	 */
	public void setPosition(String position) {
		this.position = position;
	}

	/**
	 * 
	 * @return
	 * The employer
	 */
	public String getEmployer() {
		return employer;
	}

	/**
	 * 
	 * @param employer
	 * The employer
	 */
	public void setEmployer(String employer) {
		this.employer = employer;
	}

	/**
	 * 
	 * @return
	 * The start
	 */
	public String getStart() {
		return start;
	}

	/**
	 * 
	 * @param start
	 * The start
	 */
	public void setStart(String start) {
		this.start = start;
	}

	/**
	 * 
	 * @return
	 * The end
	 */
	public String getEnd() {
		return end;
	}

	/**
	 * 
	 * @param end
	 * The end
	 */
	public void setEnd(String end) {
		this.end = end;
	}

	/**
	 * 
	 * @return
	 * The description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @param description
	 * The description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 * @return
	 * The city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * 
	 * @param city
	 * The city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * 
	 * @return
	 * The npa
	 */
	public String getNpa() {
		return npa;
	}

	/**
	 * 
	 * @param npa
	 * The npa
	 */
	public void setNpa(String npa) {
		this.npa = npa;
	}

	/**
	 * 
	 * @return
	 * The skills
	 */
	public List<Skill> getSkills() {
		return skills;
	}

	/**
	 * 
	 * @param skills
	 * The skills
	 */
	public void setSkills(List<Skill> skills) {
		this.skills = skills;
	}

	/**
	 * 
	 * @return
	 * The schools
	 */
	public List<SchoolPosition> getSchoolPositions() {
		return schools;
	}

	/**
	 * 
	 * @param schools
	 * The schools
	 */
	public void setSchools(List<SchoolPosition> schools) {
		this.schools = schools;
	}

}