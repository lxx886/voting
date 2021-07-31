package voteStrategy;

import java.util.ArrayList;
import java.util.HashMap;

public interface VoteStrategy {
    /**cast ballot based on preference, support the candidate with the greatest utility
     * two schemes of tie-breaking,"lexico" or "random"
     * @param Utilities, an agent's utility map over candidates, the greater the
     * utility over a candidate, the much preferred a candidate is to the agent.
     * */
    public abstract String castTrueBallot(HashMap<String , Double> Utilities, String tiebreaking);
    /**tie-breaking strategy, randomly or lexicographically
     * @param  tieCandidates , set of candidates who are tied
     * @param flag, the strategy used for tie-breaking
     * */
    public abstract String tieBreaking(ArrayList<String> tieCandidates, String flag);
}
