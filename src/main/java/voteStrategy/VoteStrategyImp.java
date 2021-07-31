package voteStrategy;

import tool.MapTool;

import java.util.*;

public class VoteStrategyImp implements VoteStrategy{

    @Override
    public String castTrueBallot(HashMap<String, Double> Utilities, String tiebreaking) {

        String myballot;
        MapTool<String,Double> mapTool = new MapTool<>();
        List<Map.Entry<String, Double>> list = mapTool.sortMapByValue(Utilities);

        // consider tie-breaking
        double value = list.get(0).getValue(); // the largest value
        ArrayList<String> tieCandidates = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (value == list.get(i).getValue()) {
                tieCandidates.add(list.get(i).getKey());
            } else {
                break;
            }
        }
        if (tieCandidates.size() > 1) {
            // break tie randomly
            myballot = tieBreaking(tieCandidates, tiebreaking);
        } else {// only one
            myballot = tieCandidates.get(0);
        }
        return myballot;
    }


/**tie-breaking strategy, randomly or lexicographically
 * @param  tieCandidates , set of candidates who are tied
 * @param flag, the strategy used for tie-breaking,"random" means break tie randomly;
 * "lexico" break tie lexicographically
 * */
    @Override
    public String tieBreaking(ArrayList<String> tieCandidates, String flag) {
        if(tieCandidates==null) {
            System.out.println("errors: tie Candidates is null");
            throw new IllegalArgumentException("illegal argument tieCandidates");
            //return null;
        }else if (tieCandidates.size()==1) {
            System.out.println("errors: tie Candidates only has one");
            return null;
        }
        String myballot ="";
        if(flag.equals("random")) { //randomly selecting a candidate
            int len = tieCandidates.size();
            int index = (int)(0 + Math.random() * (len));
            myballot = tieCandidates.get(index);
        }

        //break tie lexicographically
        else if(flag.equals("lexico")){
            Collections.sort(tieCandidates); //sort in Ascending order by the name of candidates
            myballot = tieCandidates.get(0);
        }
        else {
            System.out.println("Errors in tie-breaking");
            throw new IllegalArgumentException("illegal argument tiebreaking");
        }
        return myballot;
    }
}
