package processing;

import java.util.Date;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;

import model.File;
import model.Job;
import model.Person;
import model.School;
import model.Skill;

public class SaveOnSesame {
	static final String  NAMESPACE = "http://jobquest/";
	static final String SESAMESERVER = "http://jobrequest.tic.heia-fr.ch:8080/openrdf-sesame/";
	static final String REPOSITORYID = "tmjobquest";
	
	public static URI saveCandidat(Person newCandidat){
		String sesameServer = SESAMESERVER;
		String repositoryID = REPOSITORYID;

		Repository repo = new HTTPRepository(sesameServer, repositoryID);
		repo.initialize();
		ValueFactory f = repo.getValueFactory();
		String namespace = NAMESPACE;
		RepositoryConnection conn = repo.getConnection();
		try {
			conn.begin();
			
			Date date = new Date();	
			String identifier = newCandidat.getName() + date.getTime();
			URI candidatSesame = f.createURI(namespace, identifier);
			
			// Add statements to the rep John is a Person, John's name is John 
			conn.add(candidatSesame, RDF.TYPE, FOAF.PERSON);
			conn.add(candidatSesame, FOAF.NAME, f.createLiteral(newCandidat.getName(), XMLSchema.STRING));
			conn.add(candidatSesame, FOAF.FIRST_NAME, f.createLiteral(newCandidat.getFirstname(), XMLSchema.STRING));
			conn.add(candidatSesame, FOAF.BIRTHDAY, f.createLiteral(newCandidat.getBirthdate(), XMLSchema.STRING));
			conn.add(candidatSesame, f.createURI(namespace, "hasAddress"), f.createLiteral(newCandidat.getAddress(), XMLSchema.STRING));
			conn.add(candidatSesame, f.createURI(namespace, "hasNPA"), f.createLiteral(newCandidat.getNpa(), XMLSchema.STRING));
			conn.add(candidatSesame, f.createURI(namespace, "hasCity"), f.createLiteral(newCandidat.getCity(), XMLSchema.STRING));
			for(File file : newCandidat.getFiles()){
				conn.add(candidatSesame, f.createURI(namespace, "hasFile"), f.createLiteral(file.getId(), XMLSchema.STRING));
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
	
	public static void saveSkill(Skill skill, URI candidatIdentifier, RepositoryConnection conn, ValueFactory f){
			Date date = new Date();
			String identifier = skill.getName() + date.getTime();
			URI skillSesame = f.createURI(NAMESPACE, identifier);
			conn.add(candidatIdentifier, f.createURI(NAMESPACE, "hasSkill"), skillSesame);
			conn.add(skillSesame, f.createURI(NAMESPACE, "hasName"), f.createLiteral(skill.getName(), XMLSchema.STRING));
			conn.add(skillSesame, f.createURI(NAMESPACE, "hasLevel"), f.createLiteral(skill.getLevel(), XMLSchema.STRING));
			conn.add(skillSesame, f.createURI(NAMESPACE, "hasYearOfExperience"), f.createLiteral(skill.getYearOfExperience(), XMLSchema.STRING));
	}
	
	public static void saveJob(Job job, URI candidatIdentifier, RepositoryConnection conn, ValueFactory f){
			Date date = new Date();
			String identifier = job.getPosition() + date.getTime();
			URI jobSesame = f.createURI(NAMESPACE, identifier);
			conn.add(candidatIdentifier, f.createURI(NAMESPACE, "hasJob"), jobSesame);
			conn.add(jobSesame, f.createURI(NAMESPACE, "hasPosition"), f.createLiteral(job.getPosition(), XMLSchema.STRING));
			conn.add(jobSesame, f.createURI(NAMESPACE, "hasEmployer"), f.createLiteral(job.getEmployer(), XMLSchema.STRING));
			conn.add(jobSesame, f.createURI(NAMESPACE, "hasDescription"), f.createLiteral(job.getDescription(), XMLSchema.STRING));
			conn.add(jobSesame, f.createURI(NAMESPACE, "hasStart"), f.createLiteral(job.getStart(), XMLSchema.STRING));
			conn.add(jobSesame, f.createURI(NAMESPACE, "hasEnd"), f.createLiteral(job.getEnd(), XMLSchema.STRING));
			conn.add(jobSesame, f.createURI(NAMESPACE, "hasNPA"), f.createLiteral(job.getNpa(), XMLSchema.STRING));
			conn.add(jobSesame, f.createURI(NAMESPACE, "hasCity"), f.createLiteral(job.getCity(), XMLSchema.STRING));
			
	}
	
	public static void saveSchool(School school, URI candidatIdentifier, RepositoryConnection conn, ValueFactory f){
			Date date = new Date();
			String identifier = school.getSchool() + date.getTime();
			URI schoolSesame = f.createURI(NAMESPACE, identifier);
			conn.add(candidatIdentifier, f.createURI(NAMESPACE, "hasSchool"), schoolSesame);
			conn.add(schoolSesame, f.createURI(NAMESPACE, "hasTile"), f.createLiteral(school.getTitle(), XMLSchema.STRING));
			conn.add(schoolSesame, f.createURI(NAMESPACE, "hasSchool"), f.createLiteral(school.getSchool(), XMLSchema.STRING));
			conn.add(schoolSesame, f.createURI(NAMESPACE, "hasSpeciality"), f.createLiteral(school.getSpeciality(), XMLSchema.STRING));
			conn.add(schoolSesame, f.createURI(NAMESPACE, "hasStart"), f.createLiteral(school.getStart(), XMLSchema.STRING));
			conn.add(schoolSesame, f.createURI(NAMESPACE, "hasEnd"), f.createLiteral(school.getEnd(), XMLSchema.STRING));
	}


}
