package voter;

import center.ICenterService;
import environments.Population;
import environments.Voter;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.*;
import jadex.micro.annotation.*;
import jadex.rules.eca.ChangeInfo;
import tool.MapTool;
import voteStrategy.VoteStrategyImp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Agent
@Plans(
        {
                @Plan(trigger=@Trigger(goals = VoterBDI.PerformCollectBallotGoal.class),body=@Body(CollectBallotPlan.class)),
                @Plan(trigger=@Trigger(goals = VoterBDI.PerformDropBallotGoal.class),body=@Body(DropBallotPlan.class)),
                @Plan(trigger=@Trigger(goals = VoterBDI.PerformInformALLDisconectGoal.class),body=@Body(InformALLDisconectPlan.class)),
                @Plan(trigger=@Trigger(goals = VoterBDI.PerformInformALLConectGoal.class),body=@Body(VoterBDI.InformALLConnectPlan.class)),
                @Plan(trigger = @Trigger(goals = VoterBDI.SimpleVotingGoal.class),body = @Body(SimpleVotingPlan.class)),
                @Plan(trigger = @Trigger(goals = VoterBDI.SubmitBallotGoal.class),body= @Body(SubmitPlan.class)),
                @Plan(trigger = @Trigger(goals = VoterBDI.NotifyNewBallotGoal.class),body= @Body(notifyNewBallotPlan.class))
        }
)
@Service
@ProvidedServices({@ProvidedService(type = IVoterService.class)})
@RequiredServices({@RequiredService(name="announceNeighbors", type= IVoterService.class),
                  @RequiredService(name = "collectBallot",type = ICenterService.class)
})

public class VoterBDI implements IVoterService{

    @Agent
    protected IInternalAccess agent;

    @AgentFeature
    protected IBDIAgentFeature bdiFeature;

    @AgentFeature
    protected IExecutionFeature execFeature;

    @Belief
    protected  Population population = Population.getInstance(4);//4 candidates

    @Belief
    protected Voter voter;

    /*
    * store the neighbor relationship, key:cid;value:ballot
    * */
    @Belief
    protected List<Map<IComponentIdentifier,String>> neighbors;

    private  boolean isNotInformedToAdd ;

    private  boolean isNewCreated;

    private boolean isAlive;

    private  boolean isNotInformedToDelete;

    private Map<String , Integer>myScores = new HashMap<>();
    @Belief
    public Map<String, Integer> getMyScores() {
        return this.voter.getScores();
    }
    @Belief
    public void setMyScores(Map<String, Integer> myScores) {
        //System.out.println(this.voter.getId()+ "'s my scosres: "+myScores);
        this.myScores = myScores;
    }

    @Belief(dynamic = true)
    String myBallot= (voter==null)?null:voter.getMyBallot();

    @AgentCreated
    public void init()
    {
        //initiate the voter
        System.out.println("cid"+agent.getComponentIdentifier());

        voter = new Voter(population.getCandidates().getCandidatesPosition());

        voter.setId(agent.getComponentIdentifier());

        voter.castTrueBallot(new VoteStrategyImp(),"lexico");
        this.isNotInformedToAdd = false;
        this.isNewCreated = true;
        this.isNotInformedToDelete = false;
        this.isAlive = true;
        this.myScores = this.voter.getScores();
        boolean addSuccess = this.population.add(voter);
        if(addSuccess)
        {
            this.neighbors = new ArrayList<Map<IComponentIdentifier,String>>();
            this.neighbors = this.population.getNeigbors(voter);
            System.out.println("the new voter is " + this.voter + "; " +
                    "\n, and its pre neighbor of is "+ neighbors);
        }
        else{
            System.out.println("can not join the population");
        }
    }

    @AgentKilled
    public void destroy(){
        isAlive= false;
        this.population.remove(this.voter);
        boolean removeVoterFlag = (boolean) bdiFeature.dispatchTopLevelGoal(new PerformInformALLDisconectGoal(this.neighbors,this.voter) ).get();
        System.out.println("killing voter "+ this.voter.getId() +" "+ removeVoterFlag);
    }

    @AgentBody
    public void exeBody(){

        int len = this.neighbors!=null? this.neighbors.size():0;
        if(len>0 && this.neighbors.get(len-1).size()>0)
        {
            boolean isInformedToAdd = (boolean)bdiFeature.dispatchTopLevelGoal(new PerformInformALLConectGoal(this.neighbors,this.voter)).get();
            System.out.println("Created: informed all the neighbors "+ isInformedToAdd);
        }
        isNewCreated = false;
    }


    /*when agent is created, problems, do not run into the corresponding plan*/
    @Goal(deliberation=@Deliberation(inhibits ={SimpleVotingGoal.class,AddNeighborGoal.class} ))
    public class PerformInformALLConectGoal
    {
        @GoalParameter
        //private List<IComponentIdentifier> neighbors;
        private List<Map<IComponentIdentifier,String>> neighbors;

        @GoalParameter
        private Voter pvoter;

        @GoalParameter
        private IInternalAccess agent;

        @GoalResult
        private boolean isPlanSuccess;


        public PerformInformALLConectGoal(List<Map<IComponentIdentifier,String>> neighbors, Voter pvote)
        {
            this.pvoter = pvote;
            this.neighbors = neighbors;
            isNewCreated = false;
            System.out.println("*********created inform all**********");
            System.out.println("Goal: "+ getClass().getSimpleName() +" of " + voter.getId() + " trying to connect all neighbors: "+ getNeighbors().toString());
        }
        public boolean isPlanSuccess() {
            return isPlanSuccess;
        }
    }



    /*when agent is killed (leave),*/
    @Goal(deliberation = @Deliberation(inhibits = {SimpleVotingGoal.class,PerformCollectBallotGoal.class,PerformDropBallotGoal.class}))
    public class PerformInformALLDisconectGoal
    {
        @GoalParameter
        private List<Map<IComponentIdentifier,String>> neighbors;

        @GoalParameter
        private Voter pvoter;
        @GoalParameter
        private IInternalAccess agent;

        @GoalResult
        private boolean isPlanSuccess;
       //public PerformInformALLDisconectGoal(List<IComponentIdentifier> neighbors,Voter pvote)
       public PerformInformALLDisconectGoal(List<Map<IComponentIdentifier,String>> neighbors,Voter pvote)
        {
            this.pvoter = pvote;
            this.neighbors = neighbors;
            agent = getAgent();
            System.out.println("*********kill inform all**********");
            System.out.println("Goal: " + voter.getId() + " trying to disconnect all neighbors: "+ getNeighbors().toString());
        }
        public boolean isPlanSuccess() {
            return isPlanSuccess;
        }
    }

    @Goal
    public class PerformDropBallotGoal
    {
        @GoalParameter
        private String cBallot;
        @GoalParameter
        private Voter pvoter;

        @GoalResult
        private boolean isPlanSuccess;
        public PerformDropBallotGoal(String ballot)
        {
            this.cBallot = ballot;
            this.pvoter = voter;
            //System.out.println("Goal: start to creating goal on "+ this.pvoter.getId() + " for drop ballot of "+ this.cBallot);
        }


        public boolean isPlanSuccess() {
            return isPlanSuccess;
        }
    }



    /*dispatch by plan of notifyNeighborToAddPlan; when successfully notifying its neighbor,
 fetch the neihbbor's ballot */
    @Goal
    public class PerformCollectBallotGoal
    {
        @GoalParameter
        private String cBallot;
        @GoalParameter
        private Voter pvoter;

        @GoalResult
        private boolean isPlanSuccess;

        public PerformCollectBallotGoal(String ballot)
        {
            this.cBallot = ballot;
            this.pvoter = voter;
            //System.out.println("Goal: start to creating goal on "+ this.pvoter.getId());
        }

        public boolean isPlanSuccess() {
            return isPlanSuccess;
        }
    }


    @Goal(deliberation=@Deliberation(inhibits = SimpleVotingGoal.class))
    public  class SimpleVotingGoal
    {
        @GoalParameter
        private Map<String, Integer> myScores ;
        @GoalParameter
        private Voter svoter ;
        @GoalParameter
        private String gBallot ;

        @GoalResult
        private boolean isPlanSuccess;

        public SimpleVotingGoal()
        {
            this.myScores = getMyScores();
            this.svoter = getVoter();
            this.gBallot = getMyBallot();
            System.out.println("Goal: " + getClass().getSimpleName()+ " of " + voter.getId()+ ", new scores is "+getMyScores());
        }

        public boolean isPlanSuccess() {
            return isPlanSuccess;
        }



        /** inhibit other simple votingGoal when the score is the same or
         * the candidate with the largest ballots is the same
         * */
        @GoalInhibit(SimpleVotingGoal.class)
        public boolean inhibitSimpleVotingGoal(@CheckNotNull SimpleVotingGoal other)
        {
           // return true;
            boolean ret = getMyScores().equals(other.myScores);
            if(!ret)
            {
                MapTool<String, Integer> maptool = new MapTool<>();
                String popularCan = maptool.getKeyOfMaxMap(getMyScores());
                String popularCanOther = maptool.getKeyOfMaxMap(other.myScores);
                ret = popularCan.equals(popularCanOther);
            }
            //System.out.println("inhibit method: "+this + " " +this.myScores+", \n"+other+ " "+ other.myScores+" "+ret);
            return ret;
        }


    }

    @Goal
    public static class SubmitBallotGoal
    {
        @GoalParameter
        private Voter gvoter;
        @GoalParameter
        private IInternalAccess agent;

        @GoalResult
        private boolean isCollectedSuccess;

        public boolean isCollectedSuccess() {
            return isCollectedSuccess;
        }

        public SubmitBallotGoal(VoterBDI voterBDI)
        {
            this.gvoter = voterBDI.getVoter();
            isCollectedSuccess= false;
            this.agent = voterBDI.getAgent();
            System.out.println(voterBDI.getVoter().getId().toString()+" submitBallotGoal "  + "for the ballot: "+ this.gvoter.getMyBallot());
        }

        @GoalCreationCondition(beliefs = "myBallot")
        public static Object check(ChangeEvent event, @CheckNotNull VoterBDI voterBDI)
        {
            ChangeInfo<Object> change = ((ChangeInfo<Object>)event.getValue());
            Object value = change.getValue();
            //System.out.println("the type of the event "+ event.getType() +", the changed value is "+ value);
            if(value instanceof  String)
            {
                String type = event.getType();
                String newBallot = (String) value;
                //System.out.println("the type of the event "+ type +", the changed value is "+ newBallot);

                if(newBallot!=null && !newBallot.equals("")){
                    return  new SubmitBallotGoal(voterBDI);
                }else{
                    return null;
                }
            }
            return null;
        }
    }

//    @Goal
//    public static class GoalCreation
//    {
//        public GoalCreation() {}
//        @GoalCreationCondition(beliefs = "myBallot")
//        public static Object check(ChangeEvent event, VoterBDI voterBDI)
//        {
//            ChangeInfo<Object> change = ((ChangeInfo<Object>)event.getValue());
//            Object value = change.getValue();
//            System.out.println("the type of the event "+ event.getType() +", the changed value is "+ value);
//            if(value instanceof  String)
//            {
//                String type = event.getType();
//                //ChangeInfo<String> change = ((ChangeInfo<String>)event.getValue());
//                String newBallot = (String) value;
//                //System.out.println("the type of the event "+ type +", the changed value is "+ newBallot);
//
//                if(newBallot!=null && !newBallot.equals("")){
//                    return  new submitBallotGoal(voterBDI);
//                }else{
//                    return null;
//                }
//            }
//            //adding edge
//            //if(event.getType().equals(ChangeEvent.PLANFINISHED))
////            else
////            {
////                return new AddNeighborGoal(voterBDI);
////            }
//            return null;
//        }
//    }

    @Goal
    public static class AddNeighborGoal
    {
        private  List<Map<IComponentIdentifier, String>> neighbors;
        private Voter gvoter;

        public AddNeighborGoal(VoterBDI voterBDI) {

            neighbors = voterBDI.getNeighbors();
            gvoter = voterBDI.getVoter();
            System.out.println("!!!constructor of "+ getClass().getSimpleName()+ " in "+ gvoter.getId());
            Map<IComponentIdentifier,String> neig= new HashMap<IComponentIdentifier,String>();
            neig = voterBDI.getPopulation().getNeighbor(neighbors, gvoter);
            if(neig!=null && !neighbors.contains(neig))
            {
                voterBDI.setNotInformedToAdd(true);
                neighbors.add(neig);
                System.out.println("*******" +gvoter.getId() + " adding neighbor "+ neig );
            }

        }

        @GoalCreationCondition(rawevents = @RawEvent( value=ChangeEvent.PLANFINISHED,secondc=InformALLConnectPlan.class))
        public static AddNeighborGoal checkCreate(@CheckNotNull VoterBDI voterBDI)
        {
            return new AddNeighborGoal(voterBDI);
        }
    }

    @Goal(deliberation = @Deliberation(inhibits=AddNeighborGoal.class))
    public static class DropNeighborGoal
    {
        private  List<Map<IComponentIdentifier, String>> neighbors;
        private Voter gvoter;


        public DropNeighborGoal(VoterBDI voterBDI) {

            neighbors = voterBDI.getNeighbors();
            gvoter = voterBDI.getVoter();
            System.out.println("!!!constructor of "+ getClass().getSimpleName()+ " in "+ gvoter.getId());
            if(neighbors.size()>0){
                System.out.println("!!!!!!"+gvoter.getId()+"  remove edge of "+neighbors.get(0));
                neighbors.remove(0);
                voterBDI.setNotInformedToDelete(true);
            }
        }

        @GoalCreationCondition(rawevents = @RawEvent( value=ChangeEvent.PLANFINISHED,secondc=InformALLConnectPlan.class))
        public static DropNeighborGoal checkCreate(@CheckNotNull VoterBDI voterBDI)
        {
            return new DropNeighborGoal(voterBDI);
        }
    }



    @Goal
    public class NotifyNewBallotGoal
    {
        @GoalParameter
        private List<Map<IComponentIdentifier,String>> neighbors;

        @GoalParameter
        private Voter pvoter;
        @GoalParameter
        private IInternalAccess agent;

        @GoalResult
        private boolean isPlanSuccess;

        public NotifyNewBallotGoal() {
            this.neighbors = getNeighbors();
            this.pvoter = getVoter();
            this.agent = getAgent();
            this.isPlanSuccess = false;
            System.out.println("Goal: " + getClass().getSimpleName()+ " of " + voter.getId()+ "is created.");
        }

        public boolean isPlanSuccess() {
            return isPlanSuccess;
        }
    }

    //when the score changes, agents need to reconsider its vote
    @Plan(trigger=@Trigger(factchangeds="myScores"))
    protected void simpleVotePlan( IPlan rplan,ChangeEvent event)
    {
        ChangeInfo<Map<String, Integer>> e = ( ChangeInfo<Map<String, Integer>>)event.getValue();
        System.out.println("simpleVotePlan triggered in: "+ this.voter.getId()+ ", the score is: " + e.getValue());
        Future<Boolean> ret = new Future<Boolean>();
        IFuture<SimpleVotingGoal>fut= rplan.dispatchSubgoal(new SimpleVotingGoal());
        fut.get();
        /*add result listner*/
    }

    /*agents leave, notify all its neighbors to delete it as neighbors*/
    @Plan(trigger = @Trigger(factremoveds="neighbors"))
    public IFuture<Boolean> notifyNeighborToDelPlan(ChangeEvent event, IPlan rplan){
        if(this.isNotInformedToDelete)
        {
            //ChangeInfo<IComponentIdentifier> change = ((ChangeInfo<IComponentIdentifier>)event.getValue());
            ChangeInfo<Map<IComponentIdentifier,String> > change = ((ChangeInfo<Map<IComponentIdentifier,String> >)event.getValue());

            //inform the neiVoter
            Map<IComponentIdentifier,String> tempNei = (Map<IComponentIdentifier,String>) change.getValue();
            IComponentIdentifier neicid = null;
            for(IComponentIdentifier cid: tempNei.keySet())
            {
                neicid = cid;
            }

            //IComponentIdentifier neicid = (IComponentIdentifier) change.getValue();
            String neiVoterID = neicid.toString();
            Voter deleteVoter = this.voter;
            {
                //System.out.println("DELETE: trying to notify neighbor of " + neiVoterID);
               IVoterService ser = agent.getComponentFeature
                        (IRequiredServicesFeature.class).searchService(IVoterService.class, neicid).get();


                TupleResult[] result = ser.deleteConnection(deleteVoter,neiVoterID )
                        .get().toArray(new TupleResult[0]);
                //get the two result
                String  ballot = (String)result[1].getResult();
                Map<String, Boolean> firstMap =  (Map<String, Boolean>) result[0].getResult();
                boolean isInformedSuccess = false;
                for(String key: firstMap.keySet())
                {
                    isInformedSuccess = firstMap.get(key);
                    System.out.println("Deletion: "+ deleteVoter.getId().toString() + " has accessed "+
                            key +" for informing "+neicid.toString() +", result is "+ isInformedSuccess);
                }
                if(isAlive && isInformedSuccess && ballot!=""&&ballot!=" ")
                {
                    Future<Void> ret = new Future<Void>();

                    /*justfy whether the ballot is the one stored in the caller */
                    for(int i = 0; i < getNeighbors().size(); i++)
                    {
                        //find the stored ballot of the deleted voter( the callee)
                        if(getNeighbors().get(i).containsKey(neicid))
                        {
                            String storedBallot = getNeighbors().get(i).get(neicid);
                            if(!ballot.equals(storedBallot))
                            {
                                ballot = storedBallot;
                            }
                            break;
                        }
                    }



                    boolean isCollect  = (boolean) rplan.dispatchSubgoal(new PerformDropBallotGoal(ballot)).get();
                    System.out.println("PerformDropBallotGoal "+ isCollect);
                    if(isCollect)
                    {
                        setMyScores(voter.getScores());
                    }
                }
            }
        }
        return  Future.TRUE; //for test
    }



    @Plan(trigger = @Trigger(factaddeds="neighbors"))
    public IFuture<Void> notifyNeighborToAddPlan(ChangeEvent event, IPlan rplan){
        if(!isNewCreated && this.isNotInformedToAdd==true)
        {
            //ChangeInfo<IComponentIdentifier> change = ((ChangeInfo<IComponentIdentifier>)event.getValue());
            ChangeInfo<Map<IComponentIdentifier,String> > change = ((ChangeInfo<Map<IComponentIdentifier,String> >)event.getValue());


            //inform the neiVoter
            //IComponentIdentifier neicid = (IComponentIdentifier) change.getValue();
            Map<IComponentIdentifier,String> tempNei = (Map<IComponentIdentifier,String>) change.getValue();
            IComponentIdentifier neicid = null;
            for(IComponentIdentifier cid: tempNei.keySet())
            {
                neicid = cid;
            }
            String neiVoterID = neicid.toString();
            Voter newVoter = this.voter;
           {
                //inform the neighbor
                System.out.println(this.voter.getId()+" trying to notify neighbor of " + neiVoterID);

               IVoterService ser = agent.getComponentFeature
                        (IRequiredServicesFeature.class).searchService(IVoterService.class, neicid).get();

               TupleResult[] result = ser.createConnection
                       (this.voter,neicid.toString()).get().toArray(new TupleResult[0]);
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
               if(ballot!="" && isInformedSuccess && !ballot.equals(" "))
               {
                   Future<Void> ret = new Future<Void>();
                   boolean isCollect  = (boolean) rplan.dispatchSubgoal(new PerformCollectBallotGoal(ballot)).get();
                   System.out.println("PerformCollectBallotGoal is achieved "+ isCollect);
                   if(isCollect)
                   {
                       setMyScores(voter.getScores());
                   }
               }
            }
        }
        return  Future.DONE;
  }



    @Plan(trigger = @Trigger(goals=PerformInformALLConectGoal.class))
    public class InformALLConnectPlan {
        @PlanAPI
        protected IPlan rplan;


        //private List<IComponentIdentifier> neighbors;
        private List<Map<IComponentIdentifier,String>> neighbors;

        private Voter pvoter;
        private IInternalAccess agent;


        //public InformALLConnectPlan(List<IComponentIdentifier> neighbors, Voter pvoter,IInternalAccess agent) {
        public InformALLConnectPlan(List<Map<IComponentIdentifier,String>> neighbors, Voter pvoter,IInternalAccess agent) {
            this.neighbors = neighbors;
            this.pvoter = pvoter;
            this.agent = agent;
            System.out.println("Plan: " + getClass().getSimpleName()+" of "+voter.getId() + " is created");
        }
        @PlanBody
        public IFuture<Boolean> body()
        {
            Future<Boolean> res = new Future<>();
            Voter newVoter = this.pvoter;
            for(int i = 0; i < this.neighbors.size(); i++) {
                Map<IComponentIdentifier, String> map = this.neighbors.get(i);
                for (IComponentIdentifier neicid : map.keySet()) {
                    if(neicid!=null)
                    {
                        IVoterService ser = agent.getComponentFeature
                                (IRequiredServicesFeature.class).searchService(IVoterService.class, neicid).get();


                        //System.out.println("after created, find the service of neighbor: " + ser.toString());

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
                        if(ballot!="" && isInformedSuccess && !ballot.equals(" "))
                        {
                            Future<Void> ret = new Future<Void>();
                            boolean isCollect  = (boolean) rplan.dispatchSubgoal(new PerformCollectBallotGoal(ballot)).get();
                            System.out.println("PerformCollectBallotGoal is achieved "+ isCollect);
                            if(isCollect)
                            {
                                setMyScores(voter.getScores());
                            }
                        }
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
            System.out.println("InformALLConnectPlan passed");
        }
    }







    /** create a connection with the new agent, add the new agent to neighbor set and collect its ballot
     * @param newVoter: the new agent
     * @param  neiVoterID: the neighbor of the new agent, I think it should be the id
     * */
    @Override
    public ITuple2Future<Map<String,Boolean>,String> createConnection(Voter newVoter, String neiVoterID) {
        /*find the neighbors*/
        //System.out.println("enter the service");
        Tuple2Future<Map<String,Boolean>,String> res=   new Tuple2Future<Map<String,Boolean>,String> ();
        Map<String,Boolean> temp = new HashMap<>();

        if(neiVoterID.equals(this.voter.getId().toString()) )
        {
           // System.out.println("informer: "+ newVoter.getId().toString() +" find the service of createConnection" +" provided by neighbors of " +this.voter.getId());
            Map<IComponentIdentifier,String> newVoterItem = new HashMap<>();
            newVoterItem.put(newVoter.getId(),newVoter.getMyBallot());
            //if(!this.neighbors.contains(newVoter.getId()))
            if(this.neighbors==null){
                this.neighbors=new ArrayList<Map<IComponentIdentifier,String>>();
            }
            if(!this.neighbors.contains(newVoterItem))
            {
                this.isNotInformedToAdd = false;
                this.isNewCreated = false;
                //this.neighbors.add(newVoter.getId());
//                Map<IComponentIdentifier,String> tempNei = new HashMap<IComponentIdentifier,String>();
//                tempNei.put(newVoter.getId(),newVoter.getMyBallot());
                this.neighbors.add(newVoterItem);


                //collect the ballot from the new agent
                if(newVoter.getMyBallot()!=null && newVoter.getMyBallot()!="")
                {
                    int value = 0;
                    if(  this.voter.getScores().containsKey(newVoter.getMyBallot())){
                        value =   this.voter.getScores().get(newVoter.getMyBallot());
                    }
                    this.voter.getScores().put(newVoter.getMyBallot(),value+1);
                    //add for testing when score changes
                    setMyScores( this.voter.getScores());
                }


                temp.put(this.voter.getId().toString(),true);
                //res.setFirstResult(temp);//the found neighbors
                //res.setSecondResult(this.voter.getMyBallot());
                System.out.println( "informee (existed): the score of " +  this.voter.getId()+" is "+ this.voter.getScores() + "," + " the added neighbor is " + this.neighbors );
            }else
            {
                this.isNotInformedToAdd = false;
                temp.put(this.voter.getId().toString(),true);
                //res.setFirstResult(temp);//the found neighbors
               // res.setSecondResult(this.voter.getMyBallot());
            }
            res.setFirstResult(temp);//the found neighbors
            res.setSecondResult(this.voter.getMyBallot());


        }
        else
        {
            temp.put(this.voter.getId().toString(),false);
            res.setFirstResult(temp);
            res.setSecondResult("");
        }
        //temp.put(this.voter.getId(),false);
        //res.setResult(temp);
        return res;
    }

    @Override
    public ITuple2Future<Map<String,Boolean>,String> deleteConnection(Voter DeleVoter, String neiVoterID) {
        Tuple2Future<Map<String,Boolean>,String> res =new Tuple2Future<Map<String,Boolean>,String> ();
        Map<String,Boolean> temp = new HashMap<>();
        //System.out.println("Deletion: "+ DeleVoter.getId() + " has accessed "+ this.voter.getId() +" for inform "+ neiVoterID);
        if(neiVoterID.equals(this.voter.getId().toString()) )
        {
            //System.out.println("find the neighbors of " + neiVoterID);
            this.isNotInformedToDelete = false;

            Map<IComponentIdentifier,String> tempNei = new HashMap<IComponentIdentifier,String>();
            tempNei.put(DeleVoter.getId(),DeleVoter.getMyBallot());

            //if(this.neighbors.contains(DeleVoter.getId()))
            if(this.neighbors.contains(tempNei))
            {
                //this.neighbors.remove(DeleVoter.getId());
                this.neighbors.remove(tempNei);
                temp.put(this.voter.getId().toString(),true);

                //update the score
                String ballot = DeleVoter.getMyBallot();
                //get the stored ballot of DeleVoter
                for(int i = 0 ; i < getNeighbors().size(); i++)
                {
                    if(getNeighbors().get(i).containsKey(DeleVoter.getId()))
                    {
                        String preBallot = getNeighbors().get(i).get(DeleVoter.getId());
                        ballot = preBallot;
                        break;
                    }
                }


                if(this.voter.getScores().containsKey(ballot))
                {
                    int value = this.voter.getScores().get(ballot);
                    this.voter.getScores().put(ballot,value-1);
                    setMyScores( this.voter.getScores());
                }
                //change the score belief

            }else
            {
                //if doesnot contain the informer, should return false, the sugboal should not be added
                temp.put(this.voter.getId().toString(),true);
            }
            //wrap the result
            res.setFirstResult(temp);//the found neighbors
            res.setSecondResult(this.voter.getMyBallot());
            System.out.println( "informee (existed): the score of " +  this.voter.getId().toString()+" is "+ this.voter.getScores() + "," + " the deleted neighbor is " + this.neighbors );

        }else
        {
            temp.put(this.voter.getId().toString(),false);
            res.setFirstResult(temp);
            res.setSecondResult("");

        }
        return res;
    }

    @Override
    public IFuture<Boolean> updateScores(Voter curVoter) {
        Future<Boolean> res = new Future<>();
        /*
        * 1. get the previous ballot of curVoter
        * 2. decrease the value of previous ballot by 1
        * 3. add the value of current ballot by 1
        * 4. update the stored ballot of curVoter
        * */
        String preBallot = "";
        String curBallot = curVoter.getMyBallot();
        int index = -1;
        for(int i = 0 ; i < getNeighbors().size(); i++)
        {
           if(getNeighbors().get(i).containsKey(curVoter.getId()))
           {
               String temp = getNeighbors().get(i).get(curVoter.getId());
               if(!temp.equals(curVoter.getpBallot()))
               {
                   System.out.println("warning: "+ this.voter.getId() +" did not collect ballot of "+curVoter.getpBallot()+" " + curVoter.getId());
                   //preBallot =temp;
               }
               preBallot =temp;
               index = i;
               break;
            }

        }
        if(index == -1)
        {
            res = new Future<>(false);
            return res;
        }
        //step 2
        if(voter.getScores().containsKey(preBallot))
        {
            int value = voter.getScores().get(preBallot);
            voter.getScores().put(preBallot,value-1);
        }
        else
        {
            System.out.println("error: "+ voter.getId() +" did not collect the ballot from " + curVoter.getId());
            //this.tScoreMap.put(curBallot,1.0);
        }

        //step 3
        if(voter.getScores().containsKey(curBallot))
        {
            int value =  voter.getScores().get(curBallot);
            voter.getScores().put(curBallot, value+1);
        }else{
            voter.getScores().put(curBallot,1);
        }
        setMyScores(voter.getScores());

        //step 4
        getNeighbors().get(index).put(curVoter.getId(),curBallot);

        res = new Future<>(true);
        return res;
    }


    //getter and setters
    public IInternalAccess getAgent() {
        return agent;
    }

    public List<Map<IComponentIdentifier, String>> getNeighbors() {
        return neighbors;
    }

    public Voter getVoter() {
        return voter;
    }

    public String getMyBallot() {
        return myBallot;
    }

    public Population getPopulation() {
        return population;
    }

    public boolean isNotInformedToAdd() {
        return isNotInformedToAdd;
    }

    public void setNotInformedToAdd(boolean notInformedToAdd) {
        isNotInformedToAdd = notInformedToAdd;
    }

    public boolean isNotInformedToDelete() {
        return isNotInformedToDelete;
    }

    public void setNotInformedToDelete(boolean notInformedToDelete) {
        isNotInformedToDelete = notInformedToDelete;
    }
}
