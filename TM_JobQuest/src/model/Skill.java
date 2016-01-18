
package model;

import javax.annotation.Generated;

import org.apache.commons.lang3.NotImplementedException;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Skill {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("level")
    @Expose
    private String level;
    @SerializedName("yearOfExperience")
    @Expose
    private String yearOfExperience;

    public Skill(String stringValue, int i, int j) {
		// TODO Auto-generated constructor stub
    	setName(stringValue);
    	setLevel(String.valueOf(i));
    	setYearOfExperience(String.valueOf(j));
    	
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
     *     The level
     */
    public String getLevel() {
        return level;
    }

    /**
     * 
     * @param level
     *     The level
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * 
     * @return
     *     The yearOfExperience
     */
    public String getYearOfExperience() {
        return yearOfExperience;
    }

    /**
     * 
     * @param yearOfExperience
     *     The yearOfExperience
     */
    public void setYearOfExperience(String yearOfExperience) {
        this.yearOfExperience = yearOfExperience;
    }

}
