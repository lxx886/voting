package environments;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.beans.PropertyChangeListener;
import voteStrategy.VoteStrategyImp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Voter implements Cloneable{

    //private String id;//unique for each voter
    private IComponentIdentifier id; //the cid of each agent
    private HashMap<String , Double> myUtility;
    //private int myPosition;
    private Map<String , Integer> Scores;
    private String myBallot;
    private boolean isStrategy; //whether this voter converge to a ballot that is not truthful

    private int updates;//the number of time a voter updates his votes
    //private HashSet<Integer> myNeighbor; //to store the id of neighbors
    private boolean isEverStrategy; // whether it ever used strategy
    private String pBallot; //ballot of previous state
    private String tBallot;// agent's favorite ballot
    private  int[] tVoterPosition;

    /** The property change support. */
    protected SimplePropertyChangeSupport pcs;




    public Voter() {
        myUtility = new HashMap<String , Double>();
        Scores = new HashMap<String , Integer>();
        isStrategy = false;
        updates = 0;
        pcs = new SimplePropertyChangeSupport(this);
    }
    /*initiate the utility map, initate the utility and scores capacity the the number
     * of candidate; L2 NORM
     * this constructor should be changed

     * */
    public Voter(HashMap<String , Integer> candidatesPosition)
    {
        if(candidatesPosition == null) {
            System.out.println("Errorrs!!! the candidates is null");
        }else {
            //myNeighbor = new HashSet<Integer>();
            myBallot = null;
            pBallot = null;
            //define the size of the hash map
            int mapSize = (int) ((4/3.0)*candidatesPosition.size() +1);
            Scores = new HashMap<String , Integer>(mapSize);
            myUtility = new HashMap<String , Double>(mapSize);
            tVoterPosition = new int[1];
            int[] myPosition = new int[1];
            myPosition[0] =  (int)(0 + Math.random() * (100-0 +1));
            //int myPosition =  (int)(0 + Math.random() * (100-0 +1));// [0,100], the voter favors a position in the same domain
            //calculate the utilities
            for(String c : candidatesPosition.keySet()) {
                int cPosition = candidatesPosition.get(c);
                //int value =  (int) 10000.00/(Math.abs((myPosition-cPosition)) + 1);
                double value =  (double) 1.0/(Math.abs((myPosition[0]-cPosition)) + 1);
                Double.valueOf(String.format("%.4f", value ));

                //myUtility.put(c, value);
                myUtility.put(c,Double.valueOf(String.format("%.4f", value )));
                Scores.put(c, 0); //initiate the Sscore
            }
        }
        pcs = new SimplePropertyChangeSupport(this);
    }
    /**for t-Euclidean
     *
     *
     * @param t, the dimension in Euclidean
     * */
    public Voter(HashMap<String , int[]> candidatesPosition, int t)
    {
        if(candidatesPosition == null || t <=0) {
            System.out.println("Errorrs!!! the candidates is null");
        }
        else {
            tVoterPosition = new int[t];
            //myNeighbor = new HashSet<Integer>();
            myBallot = "";
            //define the size of the hash map
            int mapSize = (int) ((4/3.0)*candidatesPosition.size() +1);
            Scores = new HashMap<String , Integer>(mapSize);
            myUtility = new HashMap<String , Double>(mapSize);

            //the position of an agent
            int[] myPosition = new int[t];
            tVoterPosition = myPosition;
            //int threshold =(int) Math.pow(100, 1.0/t);
            for(int i = 0; i < t ; i++) {
                /*is it necessary to restrict the interval to [0,10] for 2-Euclidean
                 * [0,( sqrt(100))^t]
                 */
                myPosition[i]= (int) (0 + Math.random() * (100 - 0 + 1));
                //myPosition[i]= (int) (0 + Math.random() * (threshold - 0 + 1));
            }

//			tVoterPosition.put(c, myPosition.clone());

            //int myPosition =  (int)(0 + Math.random() * (100-0 +1));// [0,100], the voter favors a position in the same domain
            //calculate the utilities

            for(String c : candidatesPosition.keySet()) {
                double sum = 0;
                for(int j = 0 ; j < t ; j++) {
                    sum += Math.abs(candidatesPosition.get(c)[j] - myPosition[j]) ;
                }
                //int cPosition = candidatesPosition.get(c);
                //int value =  (int) 10000.00/(Math.abs((myPosition-cPosition)) + 1);
                //double value =  (double) 1.0/(Math.abs((myPosition-cPosition)) + 1);
                double value =  (double) 1.0/(sum + 1);
                myUtility.put(c, value);
                Scores.put(c, 0); //initiate the Sscore
            }
        }
    }

    /*before the election starts, each voter cast his real ballots according to
     * his real preferences*/
    public String castTrueBallot(VoteStrategyImp strategy, String tiebreaking) {
        String ballot = "";
        ballot = strategy.castTrueBallot(this.myUtility, tiebreaking);
        this.myBallot = ballot;
        this.pBallot = null;
        this.tBallot = ballot;
        this.isStrategy = false;
        this.isEverStrategy = false;
        return ballot;
    }

//    /**an agent revises his ballot according to its voting strategy
//     * @param strategy, the strategy class, which consists of  AU,AURevised
//     * @param choice, a string, choose which strategy to use
//     * @param Epsilon: a small constant dealing with zero utility, delete this
//     * @param alpha: the tradeoff between attainability and utility
//     * @param beta: high beta means that a small advantage in score translates to a large gap in attainability
//     * @return the chosen candidate.
//     *
//     * */
//	/* calculateAU(HashMap<String, Integer> Utilities, HashMap<String, Integer> s, double Epsilon,
//			double alpha, int beta, String tiebreaking)*/
//    public String castBallotAU(VoteStrategyImp strategy, String choice,
//                               double alpha, int beta, String tiebreaking, int round) {
//        String ballot = "";
//        if(beta >0 &&  alpha >= 0&& alpha <=2) {
//            //the ballot before strategic voting in this round
//            //String pBallot = this.myBallot;
//            if(choice.equals("AU")) {
//                ballot = strategy.calculateAU(this.myUtility, this.Scores, alpha, beta, tiebreaking);
//            }else if(choice.equals("AURevised")) {
//                ballot = strategy.calculateAURevised(this.myUtility, this.Scores, alpha, beta, tiebreaking,round);
//            }else {
//                throw new IllegalArgumentException("invalid choice " + choice);
//                //System.out.println("Wrong choice of voting strategy!!");
//            }
////
////			if(ballot == "") {
////				System.out.println("Wrong in calling the strategy method!");
////			}
//            //update myballot
//            /*problem how to measure the changes between the previour round and current round*/
//            if (!ballot.equals("") && !ballot.equals(this.myBallot)
//                    &&!ballot.equals(this.trueBallot)) {
//                this.myBallot = ballot;
//                this.isStrategy = true; // if vote strategically, then set isStrategy true
//                this.updates += 1; // increase the number of update by 1;
//                this.isEverStrategy = true;
//            } else if
//                //(!ballot.equals("") && ballot.equals(this.myBallot))
//            (!ballot.equals("") && !ballot.equals(this.myBallot)
//                            && ballot.equals(this.trueBallot))
//            {// problem , it should be castTrueBallot.equals()
//                this.isStrategy = false;
//                this.myBallot = ballot;
//                this.updates += 1;
//                this.isEverStrategy = true;
//            }else if(!ballot.equals("") && ballot.equals(this.myBallot)
//                    &&!ballot.equals(this.trueBallot)) {
//                this.isStrategy = true;
//                //this.isEverStrategy = true;
//            }else if(!ballot.equals("") && ballot.equals(this.myBallot)
//                    &&ballot.equals(this.trueBallot)) {
//                this.isStrategy = false;
//            }
//        }
//
//        return ballot;
//    }
//
//    /**an agent revises his ballot according to its voting strategy
//     * @param strategy, the strategy class, which consists of KP,  LD,LDLA models
//     * @param choice, a string, choose which strategy to use
//     * @return the chosen candidate.
//     *
//     * */
//    public String castBallot(VoteStrategyImp strategy, String choice,double r,
//                             String tiebreaking) {
//        String ballot = "";
//        switch (choice) {
//
//            case "LD":
//                if(r <= 1 && r>=0) {
//                    ballot = strategy.calculateLD(this.myUtility, this.Scores, r, tiebreaking);
//                }else {
//                    System.out.println("r is: "+r +" should be between [0,1]");
//                }
//                break;
//            case "LDLB":
//                if(r <= 1 && r>=0) {
//                    ballot = strategy.calculateLDLB(this.myUtility, this.Scores, r, tiebreaking);
//                }else {
//                    System.out.println("r is: "+r +" while it should be between [0,1] ");
//                }
//                break;
//            case "KP":
//            {
//                int k = (int) r;
//                if( k > 0 && k <= myUtility.size()) {
//                    ballot = strategy.calculateKP(this.myUtility, this.Scores, k, tiebreaking);
//                }else if(k > myUtility.size()){
//                    System.out.println("k should be no more than the number of candidates"+k);
//                }else {
//                    System.out.println("k is too small" + k);
//                }
//                break;
//            }
//            case "CV":
//            {
//                int k = (int) r; //this need to be changed
//                if( k > 0 ) {
//                    ballot = strategy.calculateCV(this.myUtility, this.Scores, k, tiebreaking);
//                }else {
//                    System.out.println("k is too small" + k);
//                }
//                break;
//            }
//
//            default:
//                throw new IllegalArgumentException("invalid choice " + choice);
//                //System.out.println("Wrong choice of voting strategy!!");
//                //break;
//        }
//        //update my ballot
//        if (!ballot.equals("") && !ballot.equals(this.myBallot)
//                &&!ballot.equals(this.trueBallot)) {
//            this.myBallot = ballot;
//            this.isStrategy = true; // if vote strategically, then set isStrategy true
//            this.updates += 1; // increase the number of update by 1;
//            this.isEverStrategy = true;
//        } else if
//            //(!ballot.equals("") && ballot.equals(this.myBallot))
//        (!ballot.equals("") && !ballot.equals(this.myBallot)
//                        && ballot.equals(this.trueBallot))
//        {// problem , it should be castTrueBallot.equals()
//            this.isStrategy = false;
//            this.myBallot = ballot;
//            this.updates += 1;
//            this.isEverStrategy = true;
//        }else if(!ballot.equals("") && ballot.equals(this.myBallot)
//                &&!ballot.equals(this.trueBallot)) {
//            this.isStrategy = true;
//            //this.isEverStrategy = true;
//        }else if(!ballot.equals("") && ballot.equals(this.myBallot)
//                &&ballot.equals(this.trueBallot)) {
//            this.isStrategy = false;
//            //this.isEverStrategy = true;
//        }
//
////		if(!ballot.equals("") && !ballot.equals(this.myBallot)) {
////			this.myBallot = ballot;
////			this.isStrategy = true; //if vote strategically, then set isStrategy true
////			this.updates += 1; // increase the number of update by 1;
////			this.isEverStrategy = true;
////		}else if(!ballot.equals("") && ballot.equals(this.myBallot)) {
////			this.isStrategy = false;
////		}
//        return ballot;
//    }

//    /**get scores from a voters's neighbors, if there exists networks
//     * @param neighbors be a SimpleVoter, how to implement?
//     * */
//    public void updateScores(HashSet<Voter>  neighbors) {
//        if(this.Scores!=null && neighbors!=null) {
//            //the score of the previous round should be clear
//            for(String c : Scores.keySet()) {
//                Scores.put(c, 0); //initiate the Sscore
//            }
//            //then the score from the neighbors should be update
//            //System.out.println("size of neighbors: " + neighbors.size());
//            for(Voter voter : neighbors) {
//                //update the score according to each neighbors ballot
//                String key = voter.getMyBallot();
//                if(this.Scores.containsKey(key)) {
//                    int value = this.Scores.get(key);
//                    this.Scores.put(key, value+ 1);
//                }else {
//                    this.Scores.put(key,1);
//                }
//
//            }
//        }else {
//            System.out.println("Initiate Scores errors in the constructor!!");
//            throw new IllegalArgumentException("invalid neighbors " + neighbors);
//        }
//    }



//    /**add the scores from the poll to the voter
//     * get scores from a voters's neighbors, if there exists networks
//     *
//     * */
//    public void updateScores(HashSet<Voter>  neighbors,
//                             HashMap<String, Integer> poll) {
//        if(this.Scores!=null && neighbors!=null) {
//            //the score of the previous round should be clear, store the
//            //poll information
//            for(String c : poll.keySet()) {
//                this.Scores.put(c, poll.get(c)); //initiate the Sscore
//            }
//            //then the score from the neighbors should be update
//            //System.out.println("size of neighbors: " + neighbors.size());
//            for(Voter voter : neighbors) {
//                //update the score according to each neighbors ballot
//                String key = voter.getMyBallot();
//                if(this.Scores.containsKey(key)) {
//                    int value = this.Scores.get(key);
//                    this.Scores.put(key, value+ 1);
//                }else {
//                    this.Scores.put(key,1);
//                }
//
//            }
//        }else {
//            System.out.println("Initiate Scores errors in the constructor!!");
//            throw new IllegalArgumentException("invalid neighbors " + neighbors);
//        }
//    }


    public int getUpdates() {
        return updates;
    }
    public void setUpdates(int updates) {
        this.updates = updates;
    }



    public HashMap<String, Double> getMyUtility() {
        return myUtility;
    }

    public void setMyUtility(HashMap<String, Double> myUtility) {
        this.myUtility = myUtility;
    }

    public Map<String, Integer> getScores() {
        return Scores;
    }


    public void setScores(Map<String, Integer> scores) {
        Scores = scores;
    }

    public String getMyBallot() {
        return myBallot;
    }
    public void setMyBallot(String myBallot) {
        String old = this.myBallot;
        this.myBallot = myBallot;
        pcs.firePropertyChange("myBallot",old,myBallot);
    }
    @Override
    public String toString() {
        return "Voter [id=" + id.toString() + ", myUtility=" + myUtility + ", Scores=" + Scores + ", myBallot=" + myBallot
                + ", isStrategy=" + isStrategy + ", updates=" + updates  + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voter voter = (Voter) o;
        return isStrategy == voter.isStrategy && updates == voter.updates && isEverStrategy == voter.isEverStrategy && Objects.equals(id.toString(), voter.id.toString()) && Objects.equals(myUtility, voter.myUtility) && Objects.equals(Scores, voter.Scores) && Objects.equals(myBallot, voter.myBallot) && Objects.equals(pBallot, voter.pBallot) && Arrays.equals(tVoterPosition, voter.tVoterPosition);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id.toString(), myUtility, Scores, myBallot, isStrategy, updates, isEverStrategy, pBallot);
        result = 31 * result + Arrays.hashCode(tVoterPosition);
        return result;
    }

    public IComponentIdentifier getId() {
        return id;
    }

    public void setId(IComponentIdentifier id) {
        this.id = id;
    }

    public boolean isStrategy() {
        return isStrategy;
    }
    public void setStrategy(boolean isStrategy) {
        this.isStrategy = isStrategy;
    }
//    public HashSet<Integer> getMyNeighbor() {
//        return myNeighbor;
//    }
//   // public void setMyNeighbor(HashSet<Integer> myNeighbor) {
//        this.myNeighbor = myNeighbor;
//    }
    public boolean isEverStrategy() {
        return isEverStrategy;
    }
    public void setEverStrategy(boolean isEverStrategy) {
        this.isEverStrategy = isEverStrategy;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        Voter v = (Voter) super.clone();

        return v;
    }
    /**
     * @return the tVoterPosition
     */
    public int[] gettVoterPosition() {
        return tVoterPosition;
    }
    /**
     * @param tVoterPosition the tVoterPosition to set
     */
    public void settVoterPosition(int[] tVoterPosition) {
        this.tVoterPosition = tVoterPosition;
    }
    /**
     * @return the trueBallot
     */
    public String getpBallot() {
        return pBallot;
    }
    /**
     * @param trueBallot the trueBallot to set
     */
    public void setpBallot(String trueBallot) {
        this.pBallot = trueBallot;
    }

    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     *
     * @param listener The PropertyChangeListener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        if(listener==null)
            System.out.println("nulllllllllllllll");

        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * @param listener The PropertyChangeListener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        pcs.removePropertyChangeListener(listener);
    }

    public String getTrueBallot() {
        return tBallot;
    }

    public void setTrueBallot(String tBallot) {
        this.tBallot = tBallot;
    }
}
