
package model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Job {

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
    @SerializedName("npa")
    @Expose
    private String npa;
    @SerializedName("city")
    @Expose
    private String city;

    /**
     * 
     * @return
     *     The position
     */
    public String getPosition() {
        return position;
    }

    /**
     * 
     * @param position
     *     The position
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * 
     * @return
     *     The employer
     */
    public String getEmployer() {
        return employer;
    }

    /**
     * 
     * @param employer
     *     The employer
     */
    public void setEmployer(String employer) {
        this.employer = employer;
    }

    /**
     * 
     * @return
     *     The start
     */
    public String getStart() {
        return start;
    }

    /**
     * 
     * @param start
     *     The start
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * 
     * @return
     *     The end
     */
    public String getEnd() {
        return end;
    }

    /**
     * 
     * @param end
     *     The end
     */
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * 
     * @return
     *     The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    public void setDescription(String description) {
        this.description = description;
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
