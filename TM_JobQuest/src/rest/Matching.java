package rest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openrdf.model.URI;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.qos.logback.core.net.SyslogOutputStream;
import model.InputPersonId;
import model.InputPositionId;
import model.Position;
import model.OutputPersonSimilarity;
import model.OutputPositionSimilarity;
import processing.Similarity;
import processing.SkillsWeight;

@Path("/matching")
public class Matching {
	
	/*
	 * {
	 * 	positionId : URI
	 * }
	 */
	
	/*
	 * [
	 * 	{
	 * 		userId : URI
	 * 		similarity : double
	 * 	},
	 * 	{
	 * 		userId : URI
	 * 		similarity : double
	 * 	}
	 * ]
	 */
	@POST
	@Path("/position")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response findBestUsersforPosition(String incomingData) {
		Gson gson = new GsonBuilder().create();
		
		InputPositionId inputPositionId = gson.fromJson(incomingData, InputPositionId.class);
		
		List<OutputPersonSimilarity> outputPersonSimilarities = Similarity.processSimilarityFromPosition(inputPositionId.positionId);
		Collections.sort(outputPersonSimilarities, Collections.reverseOrder(OutputPersonSimilarity.Comparators.SIM));
		//Collections.sort(outputPersonSimilarities, OutputPersonSimilarity.Comparators.SIM);
		List<OutputPersonSimilarity> output = new ArrayList<>(); 
		int i = 0;
		for(OutputPersonSimilarity outputPersonSimilaritie : outputPersonSimilarities){
			output.add(outputPersonSimilaritie);
			if(i == inputPositionId.numberOfResults-1){
				break;
			}
			i++;
		}

		String o = gson.toJson(output);
		return Response.status(200).entity(o).build();
	}
	
	
	/*
	 * {
	 * 	userId : URI
	 * }
	 */
	
	/*
	 * [
	 * 	{
	 * 		positionId : URI
	 * 		similarity : double
	 * 	},
	 * 	{
	 * 		positionId : URI
	 * 		similarity : double
	 * 	}
	 * ]
	 */
	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response findBestPositionsforUser(String incomingData) {

		System.out.println(incomingData);
		Gson gson = new GsonBuilder().create();
		
		InputPersonId inputPersonId = gson.fromJson(incomingData, InputPersonId.class);
		
		List<OutputPositionSimilarity> outputPositionSimilarities = Similarity.processSimilarityFromPerson(inputPersonId.personId);
		Collections.sort(outputPositionSimilarities, Collections.reverseOrder(OutputPositionSimilarity.Comparators.SIM));
		//Collections.sort(outputPositionSimilarities, OutputPositionSimilarity.Comparators.SIM);

		
		List<OutputPositionSimilarity> output = new ArrayList<>(); 
		int i = 0;
		for(OutputPositionSimilarity outputPersonSimilaritie : outputPositionSimilarities){
			output.add(outputPersonSimilaritie);
			if(i == inputPersonId.numberOfResults-1){
				break;
			}
			i++;

		}
		
		System.out.println(gson.toJson(output).toString());

		
		return Response.status(200).entity(gson.toJson(output)).build();

	}

}
