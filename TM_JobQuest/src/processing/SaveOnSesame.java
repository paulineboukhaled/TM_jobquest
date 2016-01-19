package processing;

import java.net.URISyntaxException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;

import constante.Parameter;
import model.File;
import model.Job;
import model.Person;
import model.Position;
import model.School;
import model.Skill;


public class SaveOnSesame {

	private Repository repo = null;
	private ValueFactory f = null;

	public SaveOnSesame() {
		repo = new HTTPRepository(Parameter.SESAMESERVER, Parameter.REPOSITORYID);
		repo.initialize();
		ValueFactory f = repo.getValueFactory();


		HAS_ADDRESS = f.createURI(Parameter.NAMESPACE, "hasAddress");
		HAS_NPA = f.createURI(Parameter.NAMESPACE, "hasNPA");
		HAS_CITY = f.createURI(Parameter.NAMESPACE, "hasCity");
		HAS_FILE = f.createURI(Parameter.NAMESPACE, "hasFile");
		HAS_NAME = f.createURI(Parameter.NAMESPACE, "hasName");
		HAS_SKILL = f.createURI(Parameter.NAMESPACE, "hasSkill");
		HAS_LEVEL = f.createURI(Parameter.NAMESPACE, "hasLevel");
		HAS_YEAROFEXPERIENCE = f.createURI(Parameter.NAMESPACE, "hasYearOfExperience");
		HAS_JOB = f.createURI(Parameter.NAMESPACE, "hasJob");
		HAS_POSITION = f.createURI(Parameter.NAMESPACE, "hasPosition");
		HAS_EMPLOYER = f.createURI(Parameter.NAMESPACE, "hasEmployer");
		HAS_DESCRIPTION = f.createURI(Parameter.NAMESPACE, "hasDescription");
		HAS_START = f.createURI(Parameter.NAMESPACE, "hasStart");
		HAS_END = f.createURI(Parameter.NAMESPACE, "hasEnd");
		HAS_SCHOOL = f.createURI(Parameter.NAMESPACE, "hasSchool");
		REQUIRE_SCHOOL = f.createURI(Parameter.NAMESPACE, "requireSchool");
		REQUIRE_SKILL = f.createURI(Parameter.NAMESPACE, "requireSkill");
		HAS_TITLE = f.createURI(Parameter.NAMESPACE, "hasTitle");
		HAS_SPECIALITY = f.createURI(Parameter.NAMESPACE, "hasSpeciality");
	}

	public URI HAS_ADDRESS = null;
	public URI HAS_NPA = null;
	public URI HAS_CITY = null;
	public URI HAS_FILE = null;
	public URI HAS_NAME = null;
	public URI HAS_SKILL =null;
	public URI HAS_LEVEL = null;
	public URI HAS_YEAROFEXPERIENCE = null;
	public URI HAS_JOB = null;
	public URI HAS_POSITION = null;
	public URI HAS_EMPLOYER = null;
	public URI HAS_DESCRIPTION = null;
	public URI HAS_START = null;
	public URI HAS_END = null;
	public URI HAS_SCHOOL = null;
	public URI REQUIRE_SCHOOL = null;
	public URI REQUIRE_SKILL = null;
	public URI HAS_TITLE = null;
	public URI HAS_SPECIALITY = null;









	public  URI saveCandidat(Person newCandidat){
		RepositoryConnection conn = repo.getConnection();
		try {
			conn.begin();

			Date date = new Date();	
			String identifier = newCandidat.getName() + date.getTime();
			URI candidatSesame = f.createURI(Parameter.NAMESPACE, identifier);

			// Add statements to the rep John is a Person, John's name is John 
			conn.add(candidatSesame, RDF.TYPE, FOAF.PERSON);
			conn.add(candidatSesame, FOAF.NAME, f.createLiteral(newCandidat.getName(), XMLSchema.STRING));
			conn.add(candidatSesame, FOAF.FIRST_NAME, f.createLiteral(newCandidat.getFirstname(), XMLSchema.STRING));
			conn.add(candidatSesame, FOAF.BIRTHDAY, f.createLiteral(newCandidat.getBirthdate(), XMLSchema.STRING));
			conn.add(candidatSesame, HAS_ADDRESS, f.createLiteral(newCandidat.getAddress(), XMLSchema.STRING));
			conn.add(candidatSesame, HAS_NPA, f.createLiteral(newCandidat.getNpa(), XMLSchema.STRING));
			conn.add(candidatSesame, HAS_CITY, f.createLiteral(newCandidat.getCity(), XMLSchema.STRING));
			for(File file : newCandidat.getFiles()){
				conn.add(candidatSesame, HAS_FILE, f.createLiteral(file.getId(), XMLSchema.STRING));
			}
			for(Skill s : newCandidat.getSkills()){
				saveSkill(s, candidatSesame, conn, f);
			}
			for(Job j: newCandidat.getJobs()){
				saveJob(j, candidatSesame, conn, f);
			}
			for(School school: newCandidat.getSchools()){
				saveSchool(school, candidatSesame, conn, f);
			}
			conn.commit();	
			return candidatSesame;

		}finally{
			conn.close();
		} 
	}

	public void saveSkill(Skill skill, URI candidatIdentifier, RepositoryConnection conn, ValueFactory f){
		Date date = new Date();
		String identifier = skill.getName() + date.getTime();
		URI skillSesame = f.createURI(Parameter.NAMESPACE, identifier);
		conn.add(candidatIdentifier, HAS_SKILL, skillSesame);
		conn.add(skillSesame, HAS_NAME, f.createLiteral(skill.getName(), XMLSchema.STRING));
		conn.add(skillSesame, HAS_LEVEL, f.createLiteral(skill.getLevel(), XMLSchema.STRING));
		conn.add(skillSesame, HAS_YEAROFEXPERIENCE, f.createLiteral(skill.getYearOfExperience(), XMLSchema.STRING));
	}

	public void saveRequiredSkill(Skill skill, URI candidatIdentifier, RepositoryConnection conn, ValueFactory f){
		Date date = new Date();
		String identifier = skill.getName() + date.getTime();
		URI skillSesame = f.createURI(Parameter.NAMESPACE, identifier);
		conn.add(candidatIdentifier, REQUIRE_SKILL, skillSesame);
		conn.add(skillSesame, HAS_NAME, f.createLiteral(skill.getName(), XMLSchema.STRING));
		conn.add(skillSesame, HAS_LEVEL, f.createLiteral(skill.getLevel(), XMLSchema.STRING));
		conn.add(skillSesame, HAS_YEAROFEXPERIENCE, f.createLiteral(skill.getYearOfExperience(), XMLSchema.STRING));
	}

	public void saveJob(Job job, URI candidatIdentifier, RepositoryConnection conn, ValueFactory f){
		Date date = new Date();
		String identifier = job.getPosition() + date.getTime();
		URI jobSesame = f.createURI(Parameter.NAMESPACE, identifier);
		conn.add(candidatIdentifier, HAS_JOB, jobSesame);
		conn.add(jobSesame, HAS_POSITION, f.createLiteral(job.getPosition(), XMLSchema.STRING));
		conn.add(jobSesame, HAS_EMPLOYER, f.createLiteral(job.getEmployer(), XMLSchema.STRING));
		conn.add(jobSesame, HAS_DESCRIPTION, f.createLiteral(job.getDescription(), XMLSchema.STRING));
		conn.add(jobSesame, HAS_START, f.createLiteral(job.getStart(), XMLSchema.STRING));
		conn.add(jobSesame, HAS_END, f.createLiteral(job.getEnd(), XMLSchema.STRING));
		conn.add(jobSesame, HAS_NPA, f.createLiteral(job.getNpa(), XMLSchema.STRING));
		conn.add(jobSesame, HAS_CITY, f.createLiteral(job.getCity(), XMLSchema.STRING));

	}

	public void saveSchool(School school, URI candidatIdentifier, RepositoryConnection conn, ValueFactory f){
		Date date = new Date();
		String identifier = school.getSchool() + date.getTime();
		URI schoolSesame = f.createURI(Parameter.NAMESPACE, identifier);
		conn.add(candidatIdentifier, REQUIRE_SCHOOL, schoolSesame);
		conn.add(schoolSesame, HAS_TITLE, f.createLiteral(school.getTitle(), XMLSchema.STRING));
		conn.add(schoolSesame, HAS_SCHOOL, f.createLiteral(school.getSchool(), XMLSchema.STRING));
		conn.add(schoolSesame, HAS_SPECIALITY, f.createLiteral(school.getSpeciality(), XMLSchema.STRING));
		conn.add(schoolSesame, HAS_START, f.createLiteral(school.getStart(), XMLSchema.STRING));
		conn.add(schoolSesame, HAS_END, f.createLiteral(school.getEnd(), XMLSchema.STRING));
	}

	public void savePositionSchool(School school, URI positionIdentifier, RepositoryConnection conn, ValueFactory f){
		Date date = new Date();
		String identifier = school.getSchool() + date.getTime();
		URI schoolSesame = f.createURI(Parameter.NAMESPACE, identifier);
		conn.add(positionIdentifier, HAS_SCHOOL, schoolSesame);
		conn.add(schoolSesame, HAS_TITLE, f.createLiteral(school.getTitle(), XMLSchema.STRING));
		conn.add(schoolSesame, HAS_SCHOOL, f.createLiteral(school.getSchool(), XMLSchema.STRING));
		conn.add(schoolSesame, HAS_SPECIALITY, f.createLiteral(school.getSpeciality(), XMLSchema.STRING));

	}

	public URI savePosition(Position newPosition){
		RepositoryConnection conn = repo.getConnection();
		try {
			conn.begin();

			Date date = new Date();	
			String identifier = newPosition.getPosition() + date.getTime();

			// Add statements to the rep John is a Person, John's name is John 
			URI jobSesame = f.createURI(Parameter.NAMESPACE, identifier);
			conn.add(jobSesame, RDF.TYPE, f.createURI(Parameter.NAMESPACE,"Position"));

			conn.add(jobSesame, HAS_POSITION, f.createLiteral(newPosition.getPosition(), XMLSchema.STRING));
			conn.add(jobSesame, HAS_EMPLOYER, f.createLiteral(newPosition.getEmployer(), XMLSchema.STRING));
			conn.add(jobSesame, HAS_DESCRIPTION, f.createLiteral(newPosition.getDescription(), XMLSchema.STRING));
			conn.add(jobSesame, HAS_START, f.createLiteral(newPosition.getStart(), XMLSchema.STRING));
			conn.add(jobSesame, HAS_END, f.createLiteral(newPosition.getEnd(), XMLSchema.STRING));
			conn.add(jobSesame, HAS_NPA, f.createLiteral(newPosition.getNpa(), XMLSchema.STRING));
			conn.add(jobSesame, HAS_CITY, f.createLiteral(newPosition.getCity(), XMLSchema.STRING));
			for(Skill s : newPosition.getSkills()){
				saveRequiredSkill(s, jobSesame, conn, f);
			}

			for(School school: newPosition.getSchools()){
				savePositionSchool(school, jobSesame, conn, f);
			}
			conn.commit();	

			return jobSesame;

		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			conn.close();

		} 


	}

	public Skill getSkill(java.net.URI identifier) {
		RepositoryConnection conn = repo.getConnection();

		String query="SELECT * WHERE { <"+identifier+"> ?p ?o }";
		System.out.println(query);

		TupleQuery result = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult res = result.evaluate();

		Skill skill = new Skill();

		while(res.hasNext()) {
			BindingSet actualRes = res.next();			
			String predicate = actualRes.getValue("p").stringValue();

			String value = actualRes.getValue("o").stringValue();
			if(predicate.equals(HAS_LEVEL.stringValue())) {
				skill.setLevel(value);
			} else if(predicate.equals(HAS_NAME.stringValue())) {
				skill.setName(value);
			} else if(predicate.equals(HAS_YEAROFEXPERIENCE.stringValue())) {
				skill.setYearOfExperience(value);
			}

		}

		return skill;
	}

	public static void main(String [] args) {

		SaveOnSesame onSesame = new SaveOnSesame();

		try {
			Skill s = onSesame.getSkill(new java.net.URI("http://jobquest/skill21453146848023"));
			System.out.println("");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


		public static ArrayList<Position> getPositions() {
	
			
			ArrayList<Position> listOfPosition = new ArrayList<>();
			Position devCpp = new Position();
			devCpp.setPosition("Dev CPP");			
			Skill java = new Skill();
			java.setLevel("3");
			java.setYearOfExperience("1");
			java.setName("Java_(programming_language)");
			Skill c = new Skill();
			c.setLevel("1");
			c.setYearOfExperience("1");
			c.setName("C_(programming_language)");
			Skill php = new Skill();
			php.setLevel("2");
			php.setYearOfExperience("3");
			php.setName("PHP");
			
			ArrayList<Skill> listOfSkill = new ArrayList<>();
			listOfSkill.add(java);
			listOfSkill.add(c);
			listOfSkill.add(php);


			devCpp.setSkills(listOfSkill);
			
			
			Position ITmanager = new Position();
			ITmanager.setPosition("IT Manager");
			ITmanager.setSkills(listOfSkill);
			
			Position networkadministrator = new Position();
			networkadministrator.setPosition("Network Administrateur");
			networkadministrator.setSkills(listOfSkill);
			
			Position jobelca = new Position();
			jobelca.setPosition("Job Elca");
			jobelca.setSkills(listOfSkill);
			

			
			listOfPosition.add(devCpp);
			listOfPosition.add(ITmanager);
			listOfPosition.add(jobelca);
			listOfPosition.add(networkadministrator);

			
			return listOfPosition;
	
//			Repository repo = new HTTPRepository(constante.Parameter.SESAMESERVER, constante.Parameter.REPOSITORYID);
//			repo.initialize();
//			ValueFactory f = repo.getValueFactory();
//			RepositoryConnection conn = repo.getConnection();
//			try {
//				String query="Select ?position where {?person <"+RDF.TYPE+"> <"+FOAF.PERSON+">"+
//						" . ?person <"+RDFS.LABEL+"> ?name"
//						+" . ?person <"+FOAF.FAMILY_NAME+"> ?familyname}";
//				System.out.println(query);
//				TupleQuery result = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
//				TupleQueryResult res = result.evaluate();
//				while(res.hasNext()){
//					BindingSet actualRes = res.next();
//					personJohn.setNom(actualRes.getValue("name").stringValue());
//					personJohn.setPrenom(actualRes.getValue("familyname").stringValue());
//					System.out.println("Name: "+actualRes.getValue("name")+" Family Name:"+actualRes.getValue("familyname"));
//				}
//	
	
		}

	public static ArrayList<Person> getUser() {
		
		
		ArrayList<Person> listOfUser = new ArrayList<>();
		Person paulineboukhaled = new Person();
		paulineboukhaled.setName("Bou Khaled");
		paulineboukhaled.setFirstname("Pauline");
		
		Skill java = new Skill();
		java.setLevel("3");
		java.setYearOfExperience("2");
		java.setName("Java_(programming_language)");
		Skill c = new Skill();
		c.setLevel("2");
		c.setYearOfExperience("1");
		c.setName("C_(programming_language)");
		Skill php = new Skill();
		php.setLevel("0");
		php.setYearOfExperience("8");
		php.setName("PHP");
		
		ArrayList<Skill> listOfSkill = new ArrayList<>();
		listOfSkill.add(java);
		listOfSkill.add(c);
		listOfSkill.add(php);


		paulineboukhaled.setSkills(listOfSkill);
		
		
		Person paulinemarmet = new Person();
		paulinemarmet.setName("Marmet");
		paulinemarmet.setFirstname("Pauline");
		paulinemarmet.setSkills(listOfSkill);
		
		Person blancheponroy = new Person();
		blancheponroy.setName("Ponroy");
		blancheponroy.setFirstname("Blanche");
		blancheponroy.setSkills(listOfSkill);
		
		listOfUser.add(paulineboukhaled);
		listOfUser.add(paulinemarmet);
		listOfUser.add(blancheponroy);
		
		return listOfUser;

	}


}
