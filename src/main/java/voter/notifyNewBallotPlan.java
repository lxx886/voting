package voter;

import environments.Voter;
import jadex.bdiv3.annotation.*;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.List;
import java.util.Map;

@Plan
public class notifyNewBallotPlan {
    private List<Map<IComponentIdentifier,String>> neighbors;
    private Voter voter;
    private IInternalAccess agent;


    public notifyNewBallotPlan(List<Map<IComponentIdentifier, String>> neighbors, Voter pvoter, IInternalAccess agent)
    {
        this.neighbors = neighbors;
        this.voter = pvoter;
        this.agent = agent;
    }

    @PlanBody
    public IFuture<Boolean> body()
    {

        Future<Boolean> res = new Future<>();

        for(int i = 0; i < this.neighbors.size(); i++)
        {
            Map<IComponentIdentifier,String> map = this.neighbors.get(i);
            for(IComponentIdentifier neicid:map.keySet())
            {
                if(neicid!=null){
                    IVoterService ser = agent.getComponentFeature
                            (IRequiredServicesFeature.class).searchService(IVoterService.class, neicid).get();

                    //System.out.println("before killed, find the service of neighbors "+ neicid.toString() +" is " + ser.toString());
                   ser.updateScores(voter).addResultListener(new DefaultResultListener<Boolean>() {
                       @Override
                       public void resultAvailable(Boolean result)
                       {
                           System.out.println(voter.getId() +" notify "+ neicid + " to update score"+ " is "+ result);
                       }
                   });
                }
            }
        }



        return res;
    }
    @PlanFailed
    public void failed(Exception e){
        System.out.println("plan failed: " + e.toString());
    }

    @PlanAborted
    public  void abort(){
        System.out.println("Plan: "+getClass().getSimpleName()+" of "+voter.getId()+" "+ " aborted");
    }

    @PlanPassed
    public void pass(){
        System.out.println("Plan: "+getClass().getSimpleName()+" of "+voter.getId()+" "+" passed");
    }


}
