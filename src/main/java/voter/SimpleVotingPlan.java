package voter;

import environments.Voter;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.Map;

@Plan
public class SimpleVotingPlan {

    private Map<String, Integer> myScores;
    private Voter voter ;
    private String gBallot;

    @PlanAPI
    protected IPlan rplan;
    @PlanCapability
    protected VoterBDI capa;

    public SimpleVotingPlan(Map<String, Integer> myScores, Voter voter, String gBallot) {
        this.myScores = myScores;
        this.voter = voter;
        this.gBallot = gBallot;
        System.out.println("Plan: "+ getClass().getSimpleName()+" of " + voter.getId()+ " constructor.");
    }


    @PlanBody
    public boolean body()
    {
        /*if the majority of an agent's neighbor support
        a candidate, the agent will support it too.

        1. find the candidate with the largest ballot
        2. check whether it is supported by the majority of the agent
        */
        int maxBallot = -1;
        int totalBallot = 0;
        String maxCan= "";
        for(String s : myScores.keySet())
        {
            int temp = myScores.get(s);
            totalBallot += temp;
            if(maxBallot < temp) {
                maxBallot = temp;
                maxCan = s;
            }
        }

        if(maxBallot> (totalBallot+1)/2)
        {
            if(!maxCan.equals(voter.getMyBallot())){
                //voter.setpBallot(voter.getMyBallot());
                voter.setpBallot(voter.getMyBallot());
                voter.setMyBallot(maxCan);
                System.out.println(this.voter.getId()+"'s ballot changed from "+ voter.getpBallot()  + " to "+ voter.getMyBallot() );

                IFuture<VoterBDI.NotifyNewBallotGoal> fut= rplan.dispatchSubgoal(capa. new NotifyNewBallotGoal());
               // fut.get();
                Future<Boolean> ret = new Future<Boolean>();
                fut.addResultListener(new ExceptionDelegationResultListener<VoterBDI.NotifyNewBallotGoal,Boolean>(ret)
                                      {
                                          public void customResultAvailable(VoterBDI.NotifyNewBallotGoal result)
                                          {
                                              //do not run into this block
                                              System.out.println("dispatch simple goal of notifyNewBallotGoal is achieved "+ result.isPlanSuccess());
                                          }
                                      }
                );

            }else{
                System.out.println("same ballot");
            }

        }
        else{
            System.out.println("same ballot");
        }
        return true;
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
