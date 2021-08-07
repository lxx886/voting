package voter;


import center.ICenterService;
import environments.Voter;
import jadex.bdiv3.annotation.*;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.IResultListener;

/*submit the sincere and revised ballot*/
@Plan
public class SubmitPlan {
    private Voter voter;
    private  IInternalAccess agent;


    public SubmitPlan(Voter voter, IInternalAccess agent)
    {
        this.voter = voter;
        this.agent = agent;
    }


    @PlanBody
    public  boolean body(){
        //get the requried service
        ICenterService ser = agent.getComponentFeature
                (IRequiredServicesFeature.class).searchService(ICenterService.class,RequiredServiceInfo.SCOPE_GLOBAL).get();

        ser.collectBallot(voter).addResultListener(new IResultListener<Boolean>() {
            @Override
            public void exceptionOccurred(Exception exception) {
                exception.printStackTrace();
                System.out.println(exception);
            }
            @Override
            public void resultAvailable(Boolean result) {
                System.out.println(voter.getId()+ " submit result: "+result);
            }
        });
        return  true;
    }


    @PlanFailed
    public void failed(Exception e){
        System.out.println("Plan: "+getClass().getSimpleName() +" of " + voter.getId()+" "+ " failed: " + e.toString());
    }

    @PlanAborted
    public  void abort(){
        System.out.println("Plan: "+getClass().getSimpleName() +" of " + voter.getId()+" "+"aborted");
    }

    @PlanPassed
    public void pass(){
        System.out.println("Plan: "+getClass().getSimpleName() +" of "+ voter.getMyBallot()+" in " + voter.getId()+" "+"passed");
    }
}
