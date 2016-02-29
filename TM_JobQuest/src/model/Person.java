
package model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Person {

	@SerializedName("uri")
    @Expose
    private String uri;
	@SerializedName("name")
    @Expose
    private String name;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("npa")
    @Expose
    private String npa;
    @SerializedName("birthdate")
    @Expose
    private String birthdate;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("skills")
    @Expose
    private List<Skill> skills = new ArrayList<Skill>();
    @SerializedName("jobs")
    @Expose
    private List<Job> jobs = new ArrayList<Job>();
    @SerializedName("schools")
    @Expose
    private List<School> schools = new ArrayList<School>();
    @SerializedName("files")
    @Expose
    private List<File> files = new ArrayList<File>();
    
    @SerializedName("fakeSkills")
    @Expose
    private List<Skill> fakeSkills = new ArrayList<Skill>();
    
    
    
    public List<Skill> getFakeSkills() {
		return fakeSkills;
	}

	public void setFakeSkills(List<Skill> fakeSkills) {
		this.fakeSkills = fakeSkills;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * 
     * @param firstname
     *     The firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * 
     * @return
     *     The city
     */
    public String getCity() {
        return city;
    }

    /**
     * 
     * @param city
     *     The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 
     * @return
     *     The npa
     */
    public String getNpa() {
        return npa;
    }

    /**
     * 
     * @param npa
     *     The npa
     */
    public void setNpa(String npa) {
        this.npa = npa;
    }

    /**
     * 
     * @return
     *     The birthdate
     */
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * 
     * @param birthdate
     *     The birthdate
     */
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * 
     * @return
     *     The address
     */
    public String getAddress() {
        return address;
    }

    /**
     * 
     * @param address
     *     The address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 
     * @return
     *     The skills
     */
    public List<Skill> getSkills() {
        return skills;
    }

    /**
     * 
     * @param skills
     *     The skills
     */
    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    /**
     * 
     * @return
     *     The jobs
     */
    public List<Job> getJobs() {
        return jobs;
    }

    /**
     * 
     * @param jobs
     *     The jobs
     */
    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    /**
     * 
     * @return
     *     The schools
     */
    public List<School> getSchools() {
        return schools;
    }

    /**
     * 
     * @param schools
     *     The schools
     */
    public void setSchools(List<School> schools) {
        this.schools = schools;
    }

    /**
     * 
     * @return
     *     The files
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     * 
     * @param files
     *     The files
     */
    public void setFiles(List<File> files) {
        this.files = files;
    }

}
