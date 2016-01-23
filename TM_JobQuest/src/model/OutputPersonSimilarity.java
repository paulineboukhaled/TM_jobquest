package model;

import java.util.Comparator;

public class OutputPersonSimilarity implements Comparable<OutputPersonSimilarity>{

	public String personId;
	public double similarity;
	
	@Override
	public int compareTo(OutputPersonSimilarity o) {
		return Comparators.SIM.compare(this, o);
	}
	
	public static class Comparators {
        public static Comparator<OutputPersonSimilarity> SIM = new Comparator<OutputPersonSimilarity>() {
            @Override
            public int compare(OutputPersonSimilarity o1, OutputPersonSimilarity o2) {
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
