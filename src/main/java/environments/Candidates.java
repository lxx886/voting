package environments;

import java.util.Arrays;
import java.util.HashMap;

/**
 * constructor: initiate the candidatesPosition map
 * */
public class Candidates {
    private HashMap<String , Integer> candidatesPosition; //this should be changed
    private String[] candidates;

    private HashMap<String, int[]> tCandidatesPosition;
    /*constructor: initiate the candidates' position*/
    public Candidates(String[] candidates) {
        if (candidates == null) {
            System.out.println("Errors: null candidates!!!");
            throw new IllegalArgumentException("invalid arguments: candidates is null " + candidates);
        } else {
            int mapSize = (int) (3 / 4) * candidates.length + 1;
            candidatesPosition = new HashMap<String, Integer>(mapSize);
            this.candidates = candidates;
            // each candidate support a specific position
            for (String c : candidates) {
                // [0,100], the voter favors a position in the same domain
                int myPosition = (int) (0 + Math.random() * (100 - 0 + 1));
                candidatesPosition.put(c, myPosition);
            }
            //tCandidatesPosition = candidatesPosition;
        }
    }

    /**Constructor, t-dimensional hypercube in Euclidean,
     * the position of candidate lies in t-Euclidean
     * @param candidates, the name of each candidates
     * @param t, the dimension in Euclidean*/
    public Candidates(String[] candidates, int t) {
        if (candidates == null || t <=0) {
            System.out.println("Errors: null candidates!!!");
            throw new IllegalArgumentException("invalid arguments: candidates is null " + candidates);
        } else {
            int mapSize = (int) (3 / 4) * candidates.length + 1;
            this.tCandidatesPosition = new HashMap<String, int[]>(mapSize);
            this.candidates = candidates;
            int[] myPosition = new int[t];
            // each candidate support a specific position
            for (String c : candidates) {
                // [0,100], the voter favors a position in the same domain
                /*myPosition has t-dimensions, candidatesPosition need to be changed
                 * it should be HashMap<String, int[]>
                 * */
                //int threshold =(int) Math.pow(100, 1.0/t);
                for(int i = 0; i < t ;i++) {
                    /*is it necessary to restrict the interval to [0,10] for 2-Euclidean
                     * [0,( sqrt(100))^t]
                     */
                    myPosition[i]= (int) (0 + Math.random() * (100 - 0 + 1));
                    //myPosition[i]= (int) (0 + Math.random() * (10 - 0 + 1));
                    //myPosition[i]= (int) (0 + Math.random() * (threshold - 0 + 1));
                }

                //int myPosition = (int) (0 + Math.random() * (100 - 0 + 1));
                tCandidatesPosition.put(c, myPosition.clone());

            }
        }
    }

    public HashMap<String, Integer> getCandidatesPosition() {
        return candidatesPosition;
    }

    public void setCandidatesPosition(HashMap<String, Integer> candidatesPosition) {
        this.candidatesPosition = candidatesPosition;
    }

    public String[] getCandidates() {
        return candidates;
    }

    public void setCandidates(String[] candidates) {
        this.candidates = candidates;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Candidates [candidatesPosition=" + candidatesPosition + ", candidates=" + Arrays.toString(candidates)
                +"]";
    }

    /**
     * @return the tCandidatesPosition
     */
    public HashMap<String, int[]> gettCandidatesPosition() {
        return tCandidatesPosition;
    }

    /**
     * @param tCandidatesPosition the tCandidatesPosition to set
     */
    public void settCandidatesPosition(HashMap<String, int[]> tCandidatesPosition) {
        this.tCandidatesPosition = tCandidatesPosition;
    }
}