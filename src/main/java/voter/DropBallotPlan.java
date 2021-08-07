package voter;

import environments.Voter;
import jadex.bdiv3.annotation.*;

@Plan
public class DropBallotPlan {
    private  String ballot;
    private Voter voter;

//    @PlanCapability
//    private  VoterBDI capa;
//    @PlanAPI
//    private IPlan rplan;

    public DropBallotPlan(String ballot, Voter voter)
    {
        this.ballot = ballot;
        this.voter = voter;
        //System.out.println("Plan: "+ voter.getId() +" constructor of plan of deleting ballot "+ ballot);
    }
    //drop the ballot
    @PlanBody
    public boolean body()
    {
        int value = 0;
        if(this.ballot!=null && this.ballot!="" && this.ballot!=" ")
        {
            if( this.voter.getScores().containsKey(this.ballot)){
                value =   this.voter.getScores().get(this.ballot);
                this.voter.getScores().put(this.ballot,value-1);

                System.out.println("Plan: " + getClass().getSimpleName()+ " deleting ballot of "+this.ballot +" from "+voter.getId() +", the score profile of is: "+ this.voter.getScores());
                return true;
            }else System.out.println("Plan error: "+ this.voter + " does not contain ballot of "+ this.ballot);
        }
        System.out.println("plan error: ballot is "+ this.ballot);
        return  false;
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
        System.out.println(voter.getId()+" "+getClass().getSimpleName()+"passed");
    }
}
