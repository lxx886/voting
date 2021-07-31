package center;

import environments.Voter;
import jadex.bdiv3.annotation.*;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.micro.annotation.*;
import tool.MapTool;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Agent
@Service
@ProvidedServices({@ProvidedService(type = ICenterService.class,scope=RequiredServiceInfo.SCOPE_GLOBAL)})

public class collectorBDI implements ICenterService{

    //store all the voters who have submitted their ballots: key:id,value:ballot
    private Map<IComponentIdentifier,String> tPopulationMap;

    //store the score of all candidate:key:candidate,value:number of ballots
    @Belief
    private Map<String,Double> tScoreMap;
    @Agent
    protected IInternalAccess agent;

    private  JFrame f;
    private  PropertiesPanel pp;
//    private  JTextField tfc1;
//    private  JTextField tfc2;
//    private  JTextField tfc3;
    private  JTextField[] tf;
    private  int LEN = 4;
    private String[] Candidates;




    @AgentCreated
    public void init(){
        tPopulationMap= new HashMap<IComponentIdentifier,String>();
        tScoreMap = new HashMap<String,Double>();


    }

    @AgentBody
    public void exeBody(){
        f = new JFrame("current score of candidates");
        f.setSize(300, 300);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pp = new PropertiesPanel();
        //int Len = 3;
        Candidates = new String[LEN];
        tf = new JTextField[LEN+1];
        for(int i = 0 ; i < Candidates.length; i++){
            Candidates[i] = "c"+(i+1);
            tf[i] = pp.createTextField(Candidates[i], "0", false);
        }
        tf[LEN] = pp.createTextField("winner", "", false);
        //final  JTextField jfw =  pp.createTextField("winner", "", false);

        f.add(pp, BorderLayout.CENTER);
        f.pack();
        f.setLocation(SGUI.calculateMiddlePosition(f));
        f.setVisible(true);
    }
    @Goal
    public  class calculateWinnerGoal
    {
        @GoalCreationCondition(beliefs ="tScoreMap" )
        public calculateWinnerGoal() {

        }
    }


    @Plan(trigger = @Trigger(goals = calculateWinnerGoal.class))
    public void calculateWinnerPlan()
    {
        MapTool<String,Double> mapTool = new MapTool<String,Double>();
        String winner = mapTool.getKeyOfMaxMap(tScoreMap);
        tf[LEN].setText(winner);
        System.out.println("--------------------winner is: "+ winner);
    }




    /*collect the ballot of voter
    * 1. check whether voter has submitted its ballot before
    * 2. update the tScoreMap
    * 3. update the tPopulationMap
    * 4. show  the ballot
    * */
    @Override
    public IFuture<Boolean> collectBallot(Voter voter) {
        Future<Boolean> res = new Future<Boolean>();
        //voter has submitted its ballot before

        String curBallot = voter.getMyBallot();
        if(!tPopulationMap.containsKey(voter.getId()))
        {
            System.out.println(voter.getId()+": the first time submit.");
        }
        //voter has submitted before
        else
        {
            //String preBallot = tPopulationMap.get(voter.getId());
            String preBallot = tPopulationMap.get(voter.getId());
            if(this.tScoreMap.containsKey(preBallot))
            {
                double value = this.tScoreMap.get(preBallot);
                this.tScoreMap.put(preBallot,value-1);
            }
            else
            {
                System.out.println("error: "+ voter.getId() +" did not submit its ballot before");
                //this.tScoreMap.put(curBallot,1.0);
            }
        }
        //add the ballot
        if(tScoreMap.containsKey(curBallot))
        {
            double value =  this.tScoreMap.get(curBallot);
            this.tScoreMap.put(curBallot, value+1);
        }else{
            this.tScoreMap.put(curBallot,1.0);
        }
        /*2. change the population, storing the current ballot
         */
        //tPopulationMap.put(voter.getId(),voter.getpBallot());
        tPopulationMap.put(voter.getId(),voter.getMyBallot());
        System.out.println("the total score: "+ tScoreMap);

        res.setResult(true);
        showBallot();
        return res;
    }

    public void showBallot()
    {
        for(int i = 0; i < LEN; i++)
        {
            //tf[i].addActionListener();
            double value=0;
            String key = Candidates[i];
            if(this.tScoreMap.containsKey(key))
            {
                value = tScoreMap.get(key);
            }
            String text = String.valueOf((int)value);
            //System.out.println(text);
            tf[i].setText(text);
        }
    }

}
