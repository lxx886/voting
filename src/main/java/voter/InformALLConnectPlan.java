package voter;

import environments.Voter;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.TupleResult;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.List;
import java.util.Map;
/*not used*/

@RequiredServices({@RequiredService(name="announceNeighbors", type= IVoterService.class)
})
@Plan
public class InformALLConnectPlan {
    @PlanAPI
    protected IPlan rplan;

    @PlanCapability
    protected VoterBDI capa;


    private List<IComponentIdentifier> neighbors;
    private Voter pvoter;
    private IInternalAccess agent;



    public InformALLConnectPlan(List<IComponentIdentifier> neighbors, Voter pvoter,IInternalAccess agent) {
        this.neighbors = neighbors;
        this.pvoter = pvoter;
        this.agent = agent;
        System.out.println("plan of " + getClass() + "created");
    }
    @PlanBody
    public IFuture<Boolean> body()
    {
        Future<Boolean> res = new Future<>();
        Voter newVoter = this.pvoter;
        for(IComponentIdentifier neicid: this.neighbors)
        {
            if(neicid!=null){
                //System.out.println("Creation:trying to find service provided by "+ neicid);
                IVoterService ser = agent.getComponentFeature
                        (IRequiredServicesFeature.class).searchService(IVoterService.class, neicid).get();


                System.out.println("after created, find the service of neighbor: " + ser.toString());

                //get the result, and collect the neighbor's score

                TupleResult[] result = ser.createConnection
                        (pvoter,neicid.toString()).get().toArray(new TupleResult[0]);
                //get the two result
                String  ballot = (String)result[1].getResult();
                Map<String, Boolean> firstMap =  (Map<String, Boolean>) result[0].getResult();
                boolean isInformedSuccess = false;
                for(String key: firstMap.keySet())
                {
                    isInformedSuccess = firstMap.get(key);
                    System.out.println( newVoter.getId().toString() + " has accessed "+
                            key +" for informing "+neicid.toString() +", result is "+ firstMap.get(key));
                }
                if(ballot!="" &&isInformedSuccess && !ballot.equals(" "))
                {
                    //System.out.println("goal of " + newVoter.getId()+ ": collect ballot of "+ result +" from "+ neighborID);
                    Future<Void> ret = new Future<Void>();

                   // IFuture<Boolean> isCollect  = (IFuture<Boolean>) rplan.dispatchSubgoal(capa.new PerformCollectBallotGoal(ballot)).get();
                    //IFuture<Boolean> isCollect  = (IFuture<Boolean>) rplan.dispatchSubgoal(new VoterBDI().new PerformCollectBallotGoal(ballot)).get();
                    //System.out.println("PerformCollectBallotGoal is achieved"+ isCollect);
                }

            }
        }
        return Future.TRUE;
    }
    @PlanFailed
    public void failed(Exception e){
        System.out.println("plan failed: " + e.toString());
    }

    @PlanAborted
    public  void abort(){
        System.out.println("plan aborted");
    }

    @PlanPassed
    public void pass(){
        System.out.println("plan passed");
    }
}
