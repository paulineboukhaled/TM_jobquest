package model;

import java.util.Comparator;

import model.OutputPersonSimilarity.Comparators;

public class OutputPositionSimilarity implements Comparable<OutputPositionSimilarity>{

	public String positionId;
	public double similarity;
	
	
	@Override
	public int compareTo(OutputPositionSimilarity o) {
		return Comparators.SIM.compare(this, o);
	}
	
	public static class Comparators {
        public static Comparator<OutputPositionSimilarity> SIM = new Comparator<OutputPositionSimilarity>() {
            @Override
            public int compare(OutputPositionSimilarity o1, OutputPositionSimilarity o2) {
        		double sim1 = o1.similarity;
        		double sim2 = o2.similarity;
        		if((sim1-sim2)==0){
        			return 0;
        		} else if((sim1-sim2)<0){
        			return -1;	
        		}
        		return 1;
            }
        };
       
    }

}
