package rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.http.HTTPRepository;

import com.google.common.reflect.Parameter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.Person;
import model.Position;
import model.Skill;
import processing.PDFWeigth;
import processing.SaveOnSesame;
import processing.SkillsWeight;
import processing.SkillsWeight2;

@Path("/position")
public class PositionInformation {

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
		
		Position newPosition = gson.fromJson(crunchifyBuilder.toString(), Position.class);

		URI identifier = sesame.savePosition(newPosition);
		System.out.println(identifier);
		

		if(identifier==null){
			return Response.status(500).build();
		}
		
		HashMap<String, Double> weigthSkills = SkillsWeight2.getAllSkillsWeight(newPosition.getSkills());
		System.out.println(weigthSkills);		
		
		 sesame.saveHM(identifier,weigthSkills);

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

				
		Position newPosition = gson.fromJson(crunchifyBuilder.toString(), Position.class);

		URI identifier = sesame.savePosition(newPosition, id);
		System.out.println(identifier);
		

		if(identifier==null){
			return Response.status(500).build();
		}
		
		HashMap<String, Double> weigthSkills = SkillsWeight2.getAllSkillsWeight(newPosition.getSkills());
		System.out.println(weigthSkills);		
		
		 sesame.saveHM(identifier,weigthSkills);

		// return HTTP response 200 in case of success
		return Response.status(200).entity(crunchifyBuilder.toString()).build();
	}
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePosition(@PathParam("id") String id) throws URISyntaxException   {
		java.net.URI uri = new java.net.URI("http://jobquest/"+ id);
		sesame.deleteUserPosition(uri);
		return Response.status(200).build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPosition(@PathParam("id") String id) throws URISyntaxException   {
		java.net.URI uri = new java.net.URI("http://jobquest/"+ id);
		Position position = sesame.getPosition(uri);
		
		Gson gson = new GsonBuilder().create();			
		return Response.status(200).entity(gson.toJson( position)).build();
	}
	
	@GET
	@Path("/globallist")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGlobalList(InputStream incomingData) throws URISyntaxException {
		ArrayList<Position> listPosition = sesame.getPositions();
		Gson gson = new GsonBuilder().create();			
		return Response.status(200).entity(gson.toJson(listPosition)).build();
	}
	
	
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getList(InputStream incomingData) throws URISyntaxException {
		
		
		ArrayList<Position> listPosition = sesame.getPositions();
				
		Gson gson = new GsonBuilder().create();
		String result = "{";
		int i = 0;
		for(Position position : listPosition){
			result += "\""+i+"\": { \"name\":\""+ position.getPosition()+"\", \"position\": \""+(i+1)+"\", \"isSelected\":\"false\" , \"skills\":{ \"computer\":{";
			int j = 0;
			for(Skill s: position.getSkills()){
				result+="\""+s.getName()+"\":{ \"years\": "+ s.getYearOfExperience()+", \"level\":"+ s.getLevel()+"}";	
				if(j != position.getSkills().size()-1){
					result +=",";
				}
				j++;
			}
			result+="}}}";
			if(i != listPosition.size()-1){
				result +=",";	
			}
			i++;
		}
		result+="}";

		System.out.println(result);
				
		return Response.status(200).entity(result).build();
	}
}
