package voter;

import environments.Voter;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;

import java.util.Map;

public interface IVoterService {
    //public IFuture<Voter> getNeighbors();
    //public IFuture<Map<Integer,Boolean>> createConnection(Voter newVoter, Voter neiVoter);
    /**
     * @return  two values, map: key: id of voter; value: whether create success; string: ballot,
     * */
    public ITuple2Future<Map<String,Boolean>,String> createConnection(Voter newVoter, String neiVoterID);
    public ITuple2Future<Map<String,Boolean>,String> deleteConnection(Voter DeleVoter, String neiVoterID);
    /** when neighbors update its ballot, the agent need to update its corresponding score
     * @param curVoter, the neighbor which changed its ballot
     * */
    public IFuture<Boolean> updateScores(Voter curVoter);


}
