package center;

import environments.Voter;
import jadex.commons.future.IFuture;

public interface ICenterService {


    /**collect the ballot of voter
     * @param voter, the voter that submits its ballot
     * @return boolean, whether the ballot of voter has been successfully collected
     * */
    public IFuture<Boolean> collectBallot(Voter voter);
}
