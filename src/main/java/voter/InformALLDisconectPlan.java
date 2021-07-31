package voter;

import environments.Voter;
import jadex.bdiv3.annotation.*;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.List;
import java.util.Map;

@Plan
@RequiredServices({@RequiredService(name="announceNeighbors", type= IVoterService.class,
        binding=@Binding(scope= RequiredServiceInfo.SCOPE_PLATFORM))
})

public class InformALLDisconectPlan {

    //private List<IComponentIdentifier> neighbors;
    private List<Map<IComponentIdentifier,String>> neighbors;
    private Voter pvoter;
    //private  IInternalAccess agent;
//    @PlanCapability
//    private VoterBDI myagent;

    private IInternalAccess agent;



    public InformALLDisconectPlan(List<Map<IComponentIdentifier,String>> neighbors, Voter pvoter, IInternalAccess agent) {
        this.neighbors = neighbors;
        this.pvoter = pvoter;
        this.agent = agent;
    }
    @PlanBody
    public IFuture<Boolean> body()
    {
        Future<Boolean>  res = new Future<>();
        //List<IComponentIdentifier> keys = this.neighbors.
        for(int i = 0; i < this.neighbors.size(); i++)
        {
            Map<IComponentIdentifier,String> map = this.neighbors.get(i);
            for(IComponentIdentifier neicid:map.keySet())
            {
                if(neicid!=null){
                    System.out.println("DELETE:trying to find service provided by "+ neicid);
                    IVoterService ser = agent.getComponentFeature
                            (IRequiredServicesFeature.class).searchService(IVoterService.class, neicid).get();

                    System.out.println("before killed, find the service of neighbors "+ neicid.toString() +" is " + ser.toString());
                    ser.deleteConnection(pvoter,neicid.toString()).get(); //get or result
                }
            }
        }

//        for(IComponentIdentifier neicid: this.neighbors)
//        {
//            if(neicid!=null){
//                System.out.println("DELETE:trying to find service provided by "+ neicid);
//                IVoterService ser = agent.getComponentFeature
//                        (IRequiredServicesFeature.class).searchService(IVoterService.class, neicid).get();
//
//                System.out.println("before killed, find the service of neighbors "+ neicid.toString() +" is " + ser.toString());
//                ser.deleteConnection(pvoter,neicid.toString()).get(); //get or result
//            }
//
//        }

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
