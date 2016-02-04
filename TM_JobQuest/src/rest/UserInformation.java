package rest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.net.URISyntaxException;
import java.net.URL;

import javax.print.attribute.standard.Media;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
//import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.qos.logback.classic.LoggerContext;
import model.Person;
import model.Position;
import model.RequestLabel;
import model.ResponseLabel;
import model.Skill;
import model.Tag;
import processing.PDFWeigth;
import processing.SaveOnSesame;
import processing.SkillsWeight;
import processing.SkillsWeight2;
import upload.UploadPDF;

@Path("/user")
public class UserInformation {
	//static final int NUMBER_OF_TAGS = 10;
	
	SaveOnSesame sesame = new SaveOnSesame();

	
	@POST
	@Path("/getForm")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendInformation(InputStream incomingData) {
		StringBuilder crunchifyBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				crunchifyBuilder.append(line);
			}
		} catch (Exception e) {
			System.err.println("Error Parsing: - ");
		}
		System.out.println("Data Received2: " + crunchifyBuilder.toString());
		
		
		Gson gson = new GsonBuilder().create();
		
		Person newCandidat = gson.fromJson(crunchifyBuilder.toString(), Person.class);
		
		URI identifier = sesame.saveCandidat(newCandidat);

		if(identifier==null){
			return Response.status(500).build();
		}
		
		HashMap<String, Double> weigthSkills = SkillsWeight2.getAllSkillsWeight(newCandidat.getSkills());
		System.out.println(weigthSkills);
		HashMap<String, Long> weightPDFs = new HashMap<>();
		for(model.File nameFile: newCandidat.getFiles()){
			File f = new File(nameFile.getId());
			try {
				HashMap<String, Long> weightPDFs_ = PDFWeigth.getHashMapAttachedFiles(f);
				for(String key : weightPDFs_.keySet()){
					if(weightPDFs.containsKey(key)){
						weightPDFs.put(key, weightPDFs.get(key)+weightPDFs_.get(key));
					}else{
						weightPDFs.put(key, weightPDFs_.get(key));
					}
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		HashMap<String, Double> hashmapFinal = SkillsWeight.mergeSkillAndPDF(weigthSkills, weightPDFs);
		System.out.println(hashmapFinal);
		
		 sesame.saveHM(identifier, hashmapFinal);

		
		// return HTTP response 200 in case of success
		return Response.status(200).entity(crunchifyBuilder.toString()).build();
	}
	
	
	@PUT
	@Path("/getForm/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response modifyInformation(@PathParam("id") String id, InputStream incomingData) throws URISyntaxException {
		StringBuilder crunchifyBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				crunchifyBuilder.append(line);
			}
		} catch (Exception e) {
			System.err.println("Error Parsing: - ");
		}
		System.out.println("Data Received2: " + crunchifyBuilder.toString());
		
		
		java.net.URI uri = new java.net.URI("http://jobquest/"+ id);
		
		sesame.deleteUserPosition(uri);
		
		Gson gson = new GsonBuilder().create();
		
		Person newCandidat = gson.fromJson(crunchifyBuilder.toString(), Person.class);
		
		URI identifier = sesame.saveCandidat(newCandidat, id);

		if(identifier==null){
			return Response.status(500).build();
		}
		
		HashMap<String, Double> weigthSkills = SkillsWeight2.getAllSkillsWeight(newCandidat.getSkills());
		System.out.println(weigthSkills);
		HashMap<String, Long> weightPDFs = new HashMap<>();
		for(model.File nameFile: newCandidat.getFiles()){
			File f = new File(nameFile.getId());
			try {
				HashMap<String, Long> weightPDFs_ = PDFWeigth.getHashMapAttachedFiles(f);
				for(String key : weightPDFs_.keySet()){
					if(weightPDFs.containsKey(key)){
						weightPDFs.put(key, weightPDFs.get(key)+weightPDFs_.get(key));
					}else{
						weightPDFs.put(key, weightPDFs_.get(key));
					}
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		HashMap<String, Double> hashmapFinal = SkillsWeight.mergeSkillAndPDF(weigthSkills, weightPDFs);
		System.out.println(hashmapFinal);
		
		 sesame.saveHM(identifier, hashmapFinal);

		
		// return HTTP response 200 in case of success
		return Response.status(200).entity(crunchifyBuilder.toString()).build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCandidat(@PathParam("id") String id) throws URISyntaxException   {
		java.net.URI uri = new java.net.URI("http://jobquest/"+ id);
		Person person = sesame.getUser(uri);
		
		Gson gson = new GsonBuilder().create();			
		return Response.status(200).entity(gson.toJson( person)).build();
	}
	
	@DELETE
	@Path("/{id}")
	public Response deleteCandidat(@PathParam("id") String id) throws URISyntaxException   {
		java.net.URI uri = new java.net.URI("http://jobquest/"+ id);
		System.out.println(uri.toString());
		try {
			sesame.deleteUserPosition(uri);
		} catch(Exception e) {
		
		}
		return Response.status(200).build();
		
		
	}
	

	
	@GET
	@Path("/globallist")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGlobalList(InputStream incomingData) throws URISyntaxException {
		ArrayList<Person> listPosition = sesame.getUsers();
		Gson gson = new GsonBuilder().create();			
		return Response.status(200).entity(gson.toJson(listPosition)).build();
	}
	
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getList(InputStream incomingData) throws URISyntaxException {
		ArrayList<Person> listPerson = sesame.getUsers();
		
		Gson gson = new GsonBuilder().create();
		String result = "{";
		int i = 0;
		for(Person person : listPerson){
			result += "\""+i+"\": { \"lastname\":\""+ person.getName()+"\", \"firstname\":\""+ person.getFirstname() +"\", \"position\": \""+(i+1)+"\", \"isSelected\":\"false\", \"skills\":{ \"computer\":{";
			int j = 0;
			for(Skill s: person.getSkills()){
				result+="\""+s.getName()+"\":{ \"years\": "+ s.getYearOfExperience()+", \"level\":"+ s.getLevel()+"}";	
				if(j != person.getSkills().size()-1){
					result +=",";
				}
				j++;
			}
			result+="}}}";
			if(i != listPerson.size()-1){
				result +=",";	
			}
			i++;
		}
		result+="}";

		System.out.println(result);
		
				
		return Response.status(200).entity(result).build();
	}
	
	
	

}