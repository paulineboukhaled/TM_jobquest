
package model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class SchoolPosition {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("school")
    @Expose
    private String school;

    @SerializedName("speciality")
    @Expose
    private String speciality;
    @SerializedName("npa")
    @Expose
    private String npa;
    @SerializedName("city")
    @Expose
    private String city;

    /**
     * 
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The school
     */
    public String getSchool() {
        return school;
    }

    /**
     * 
     * @param school
     *     The school
     */
    public void setSchool(String school) {
        this.school = school;
    }

    /**
     * 
     * @return
     *     The speciality
     */
    public String getSpeciality() {
        return speciality;
    }

    /**
     * 
     * @param speciality
     *     The speciality
     */
    public void setSpeciality(String speciality) {
        this.speciality = speciality;
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

}
