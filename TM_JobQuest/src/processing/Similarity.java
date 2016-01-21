package processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.OutputPersonSimilarity;
import model.OutputPositionSimilarity;
import upload.CosineSimilarity;

public class Similarity {

	public static List<OutputPersonSimilarity> processSimilarityFromPosition(String positionId) {
		SaveOnSesame sesame = new SaveOnSesame();

		HashMap<String, Double> hPosition =  sesame.getHMPosition(positionId);
		HashMap<String, HashMap<String, Double>> hUsers = sesame.getAllHMUsers();

		List<OutputPersonSimilarity> similarities = getSimPositionAndUsers(hPosition, hUsers);

		return similarities;
	}

	private static List<OutputPersonSimilarity> getSimPositionAndUsers(HashMap<String, Double> hPosition,
			HashMap<String, HashMap<String, Double>> hUsers) {

		List<OutputPersonSimilarity> similarities = new ArrayList<>();

		for(String hUserId : hUsers.keySet()){
			HashMap<String, Double> hUser = hUsers.get(hUserId);
			Double sim = getSim(hUser, hPosition);
			OutputPersonSimilarity output = new OutputPersonSimilarity();
			output.similarity = sim;
			output.personId = hUserId;
			similarities.add(output);
		}

		return similarities;
	}




	private static Double getSim(HashMap<String, Double> hUser, HashMap<String, Double> hPosition) {
		//-----------------------------------CREATION DU VECTEUR DE TERME
		ArrayList<String> terms = new ArrayList<>();
		for(String key : hUser.keySet()){
			if(!terms.contains(key)){
				terms.add(key);
			}
		}
		for(String key2 : hPosition.keySet()){
			if(!terms.contains(key2)){
				terms.add(key2);
			}
		}

		//-----------------------------------COMPARAISON DE 2 VECTEURS
		double[][] matrice =  new double[2][terms.size()];

		int i = 0;
		for(String term : terms){
			if(hUser.containsKey(term)){
				matrice[0][i] = hUser.get(term);
			}
			if(hPosition.containsKey(term)){
				matrice[1][i]= hPosition.get(term);
			}
			i++;
		}

		double sim = CosineSimilarity.cosineSimilarity(matrice[0], matrice[1]);
		return sim;		
	}

	public static List<OutputPositionSimilarity> processSimilarityFromPerson(String personId) {
		SaveOnSesame sesame = new SaveOnSesame();
		HashMap<String, Double> hUser =  sesame.getHMUser(personId);
		HashMap<String, HashMap<String, Double>> hPositions = sesame.getAllHMPositions();

		List<OutputPositionSimilarity> similarities = getSimUserAndPositions(hUser, hPositions);

		return similarities;
	}

	private static List<OutputPositionSimilarity> getSimUserAndPositions(HashMap<String, Double> hUser,
			HashMap<String, HashMap<String, Double>> hPositions) {
		List<OutputPositionSimilarity> similarities = new ArrayList<>();

		for(String hPositionId : hPositions.keySet()){
			HashMap<String, Double> hPosition = hPositions.get(hPositionId);
			Double sim = getSim(hUser, hPosition);
			OutputPositionSimilarity output = new OutputPositionSimilarity();
			output.similarity = sim;
			output.positionId = hPositionId;
			similarities.add(output);
		}

		return similarities;		
	}

}
