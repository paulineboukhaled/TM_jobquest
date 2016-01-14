import java.util.ArrayList;
import java.util.Date;

public class Person {
	
	private String name;
	private String firstname;
	private Date birthdate;
	private String address;
	private long npa;
	private String city;
	private ArrayList<Skill> listOfSkill;
	private	boolean firstTime;
	private	boolean isActive;
	
	public Person(String name, String firstname, Date birthdate, String address, long npa, String city, ArrayList<Skill> listOfSkill, boolean firstTime, boolean isActive){
		setName(name);
		setFirstname(firstname);
		setBirthdate(birthdate);
		setAddress(address);
		setNpa(npa);
		setCity(city);
		setListOfSkill(listOfSkill);
		setFirstTime(firstTime);
		setActive(isActive);		
	}
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public Date getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public long getNpa() {
		return npa;
	}
	public void setNpa(long npa) {
		this.npa = npa;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public ArrayList<Skill> getListOfSkill() {
		return listOfSkill;
	}
	public void setListOfSkill(ArrayList<Skill> listOfSkill) {
		this.listOfSkill = listOfSkill;
	}
	public boolean isFirstTime() {
		return firstTime;
	}
	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	

}
