package voter;

import environments.Voter;
import jadex.bdiv3.annotation.*;

// HOW TO connect the plan and the goal
@Plan
public class CollectBallotPlan {
    private  String ballot;
    private Voter voter;

//
//    @PlanCapability
//    protected testBDI capa;
//
//    @PlanAPI
//    protected IPlan rplan;


    public CollectBallotPlan(String ballot, Voter voter)
    {
        this.ballot = ballot;
        this.voter = voter;
        //System.out.println("Plan: "+ voter.getId() +" constructor of plan of adding ballot "+ ballot);
    }
    //collect the ballot
    @PlanBody
    public boolean body()
    {
        int value = 0;
        if(this.ballot!=null && this.ballot!="")
        {
            if(  this.voter.getScores().containsKey(this.ballot)){
                value =   this.voter.getScores().get(this.ballot);
            }
            this.voter.getScores().put(this.ballot,value+1);
            System.out.println("Plan: "+getClass().getSimpleName()+" of "+voter.getId()+" "+", after adding ballot of "+this.ballot +", thes score profile of " +this.voter.getId()+ " is: "+ this.voter.getScores());
           // return  Future.TRUE;
            return true;
        }
        System.out.println("plan error: ballot is "+ this.ballot);
        //return  Future.FALSE;
        return  false;
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
