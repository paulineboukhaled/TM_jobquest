package processing;

import java.net.URISyntaxException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;

import org.codehaus.jackson.map.introspect.BasicClassIntrospector.GetterMethodFilter;
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
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.qos.logback.classic.LoggerContext;
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

		f = repo.getValueFactory();


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
		POSITION =  f.createURI(Parameter.NAMESPACE,"Position");

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
	public URI POSITION = null;



	public  URI saveCandidat(Person newCandidat){
		RepositoryConnection conn = repo.getConnection();
		try {
			conn.begin();
			Date date = new Date();	
			String identifier = escape(newCandidat.getName()) + date.getTime();
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

	private String escape(String name) {
		return name.replaceAll(" ", "");
	}

	public void saveSkill(Skill skill, URI candidatIdentifier, RepositoryConnection conn, ValueFactory f){
		Date date = new Date();
		String identifier = escape(skill.getName()) + date.getTime();
		URI skillSesame = f.createURI(Parameter.NAMESPACE, identifier);
		conn.add(candidatIdentifier, HAS_SKILL, skillSesame);
		conn.add(skillSesame, HAS_NAME, f.createLiteral(skill.getName(), XMLSchema.STRING));
		conn.add(skillSesame, HAS_LEVEL, f.createLiteral(skill.getLevel(), XMLSchema.STRING));
		conn.add(skillSesame, HAS_YEAROFEXPERIENCE, f.createLiteral(skill.getYearOfExperience(), XMLSchema.STRING));
	}

	public void saveRequiredSkill(Skill skill, URI candidatIdentifier, RepositoryConnection conn, ValueFactory f){
		Date date = new Date();
		String identifier = escape(skill.getName()) + date.getTime();
		URI skillSesame = f.createURI(Parameter.NAMESPACE, identifier);
		conn.add(candidatIdentifier, REQUIRE_SKILL, skillSesame);
		conn.add(skillSesame, HAS_NAME, f.createLiteral(skill.getName(), XMLSchema.STRING));
		conn.add(skillSesame, HAS_LEVEL, f.createLiteral(skill.getLevel(), XMLSchema.STRING));
		conn.add(skillSesame, HAS_YEAROFEXPERIENCE, f.createLiteral(skill.getYearOfExperience(), XMLSchema.STRING));
	}

	public void saveJob(Job job, URI candidatIdentifier, RepositoryConnection conn, ValueFactory f){
		Date date = new Date();
		String identifier = escape(job.getPosition()) + date.getTime();
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
		String identifier = escape(school.getSchool()) + date.getTime();
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
		String identifier = escape(school.getSchool()) + date.getTime();
		URI schoolSesame = f.createURI(Parameter.NAMESPACE, identifier);
		conn.add(positionIdentifier, REQUIRE_SCHOOL, schoolSesame);
		conn.add(schoolSesame, HAS_TITLE, f.createLiteral(school.getTitle(), XMLSchema.STRING));
		conn.add(schoolSesame, HAS_SCHOOL, f.createLiteral(school.getSchool(), XMLSchema.STRING));
		conn.add(schoolSesame, HAS_SPECIALITY, f.createLiteral(school.getSpeciality(), XMLSchema.STRING));

	}

	public URI savePosition(Position newPosition){
		RepositoryConnection conn = repo.getConnection();
		try {
			conn.begin();
			Date date = new Date();	
			String identifier = escape(newPosition.getPosition()) + date.getTime();

			// Add statements to the rep John is a Person, John's name is John 
			URI jobSesame = f.createURI(Parameter.NAMESPACE, identifier);
			conn.add(jobSesame, RDF.TYPE, POSITION);

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
		/*CONNECTION OPEN*/
		Repository repo = new HTTPRepository(Parameter.SESAMESERVER, Parameter.REPOSITORYID);
		repo.initialize();
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
		/*CONNECTION CLOSE*/
		conn.close();
		return skill;
	}


	public School getSchool(java.net.URI identifier) {
		
		/*CONNECTION OPEN*/
		Repository repo = new HTTPRepository(Parameter.SESAMESERVER, Parameter.REPOSITORYID);
		repo.initialize();
		RepositoryConnection conn = repo.getConnection();

		String query="SELECT * WHERE { <"+identifier+"> ?p ?o }";
		System.out.println(query);

		TupleQuery result = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult res = result.evaluate();

		School school = new School();

		while(res.hasNext()) {
			BindingSet actualRes = res.next();			
			String predicate = actualRes.getValue("p").stringValue();
			String value = actualRes.getValue("o").stringValue();
			if(predicate.equals(HAS_TITLE.stringValue())) {
				school.setTitle(value);
			} else if(predicate.equals(HAS_SCHOOL.stringValue())) {
				school.setSchool(value);
			} else if(predicate.equals(HAS_SPECIALITY.stringValue())) {
				school.setSpeciality(value);
			}

		}
		
		/*CONNECTION CLOSE*/
		conn.close();
		return school;
	}


	public Position getPosition(java.net.URI identifier) throws URISyntaxException {
		
		/*CONNECTION OPEN*/
		Repository repo = new HTTPRepository(Parameter.SESAMESERVER, Parameter.REPOSITORYID);
		repo.initialize();
		RepositoryConnection conn = repo.getConnection();

		String query="SELECT * WHERE { <"+identifier+"> ?p ?o }";
		System.out.println(query);

		TupleQuery result = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult res = result.evaluate();

		Position job = new Position();

		while(res.hasNext()) {
			BindingSet actualRes = res.next();			
			String predicate = actualRes.getValue("p").stringValue();
			String value = actualRes.getValue("o").stringValue();
			if(predicate.equals(HAS_EMPLOYER.stringValue())) {
				job.setEmployer(value);
			} else if(predicate.equals(HAS_POSITION.stringValue())) {
				job.setPosition(value);
			} else if(predicate.equals(HAS_DESCRIPTION.stringValue())) {
				job.setDescription(value);
			} else if(predicate.equals(HAS_START.stringValue())) {
				job.setStart(value);
			} else if(predicate.equals(HAS_END.stringValue())) {
				job.setEnd(value);
			} else if(predicate.equals(HAS_NPA.stringValue())) {
				job.setNpa(value);
			} else if(predicate.equals(HAS_CITY.stringValue())) {
				job.setCity(value);
			} 
			else if(predicate.equals(REQUIRE_SKILL.stringValue())) {
				Skill s = getSkill(new java.net.URI(value));
				job.getSkills().add(s);
			} 
			else if(predicate.equals(REQUIRE_SCHOOL.stringValue())) {
				School s = getSchool(new java.net.URI(value));
				job.getSchools().add(s);
			}
		}

		/*CONNECTION CLOSE*/
		conn.close();
		return job;
	}

	public ArrayList<Position> getPositions() throws URISyntaxException {
		ArrayList<Position> positions = new ArrayList<>();
		
		/*CONNECTION OPEN*/
		Repository repo = new HTTPRepository(Parameter.SESAMESERVER, Parameter.REPOSITORYID);
		repo.initialize();
		RepositoryConnection conn = repo.getConnection();

		String query="SELECT * WHERE { ?s <"+RDF.TYPE+"> <"+POSITION+"> }";
		System.out.println(query);

		TupleQuery result = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult res = result.evaluate();

		while(res.hasNext()) {
			BindingSet actualRes = res.next();			
			String uri = actualRes.getValue("s").stringValue();
			Position p = getPosition(new java.net.URI(uri));
			positions.add(p);
		}
		
		/*CONNECTION CLOSE*/
		conn.close();
		return positions;
	}

	public static void main(String [] args) {

		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.stop();

		SaveOnSesame onSesame = new SaveOnSesame();

		try {
			//Skill s = onSesame.getSkill(new java.net.URI("http://jobquest/skill21453146848023"));

			//Position p = onSesame.getPosition(new java.net.URI("http://jobquest/Job11453235330727"));


			Gson gson = new GsonBuilder().create();

			//System.out.println(gson.toJson(onSesame.getPositions()));
			System.out.println(gson.toJson(onSesame.getUsers()));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	

	public Person getUser(java.net.URI identifier) throws URISyntaxException {

		Repository repo = new HTTPRepository(Parameter.SESAMESERVER, Parameter.REPOSITORYID);
		repo.initialize();
		RepositoryConnection conn = repo.getConnection();

		String query="SELECT * WHERE { <"+identifier+"> ?p ?o }";
		System.out.println(query);

		TupleQuery result = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult res = result.evaluate();

		Person person = new Person();			

		while(res.hasNext()) {
			BindingSet actualRes = res.next();			
			String predicate = actualRes.getValue("p").stringValue();
			String value = actualRes.getValue("o").stringValue();
			if(predicate.equals(FOAF.NAME.stringValue())) {
				person.setName(value);
			} else if(predicate.equals(FOAF.FIRST_NAME.stringValue())) {
				person.setFirstname(value);
			} else if(predicate.equals(FOAF.BIRTHDAY.stringValue())) {
				person.setBirthdate(value);
			} else if(predicate.equals(HAS_ADDRESS.stringValue())) {
				person.setAddress(value);
			} else if(predicate.equals(HAS_NPA.stringValue())) {
				person.setNpa(value);
			} else if(predicate.equals(HAS_CITY.stringValue())) {
				person.setCity(value);
			} else if(predicate.equals(HAS_FILE.stringValue())) {
				File f = new File();
				f.setId(value);
				person.getFiles().add(f);
			} else if(predicate.equals(REQUIRE_SCHOOL.stringValue())) {
				School f = getSchool(new java.net.URI(value));
				person.getSchools().add(f);
			} else if(predicate.equals(HAS_SKILL.stringValue())) {
				Skill s = getSkill(new java.net.URI(value));
				person.getSkills().add(s);
			} 
		}


		conn.close();
		return person;

	}


	public ArrayList<Person> getUsers() throws URISyntaxException {
		ArrayList<Person> users = new ArrayList<>();

		/*CONNECTION OPEN*/
		Repository repo = new HTTPRepository(Parameter.SESAMESERVER, Parameter.REPOSITORYID);
		repo.initialize();
		RepositoryConnection conn = repo.getConnection();

		String query="SELECT * WHERE { ?s <"+RDF.TYPE+"> <"+FOAF.PERSON+"> }";
		System.out.println(query);

		TupleQuery result = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult res = result.evaluate();

		while(res.hasNext()) {
			BindingSet actualRes = res.next();			
			String uri = actualRes.getValue("s").stringValue();
			Person p = getUser(new java.net.URI(uri));
			users.add(p);
		}

		/*CONNECTION CLOSE*/
		conn.close();
		return users;
	}


}
