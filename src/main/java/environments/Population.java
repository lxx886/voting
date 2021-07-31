package environments;

import jadex.bridge.IComponentIdentifier;

import java.util.*;

//all the voters, the
public class Population {
    private ArrayList<Voter> voters;
    private Candidates candidates;
    /** The singleton. */
    private static Population instance;

    //private int idBuilder;


    private Population(int numCan)
    {
        //idBuilder = 0;
        voters = new ArrayList<Voter>();
        //initiate candidates
        String[] canNames = new String[numCan];
        for(int i = 1; i <= numCan; i++)
        {
            canNames[i-1]= "c"+i;
        }
        candidates = new Candidates(canNames);
    }

    public static synchronized Population getInstance(int numCan){
        if(instance==null)
        {
            instance = new Population(numCan);
        }
        return instance;
    }


    /**add voter v into the population*/
    public boolean add(Voter v){

        if(v != null&& !voters.contains(v)){
            //int id = voters.size();
            voters.add(v);

            //v.setId(id);

            Collections.shuffle(voters);
            //idBuilder = idBuilder + 1;
            return true;
        }
        return false;
    }

    public boolean remove(Voter v){
        if(voters.contains(v)){
            voters.remove(v);
            return true;
        }else return false;
    }
    /** returning one unique new neighbor from the population
     * @param nei the existed neighbor of voter v
     * @param v , the voter
     * @return String, the new neighbor's id
     * */

    //public IComponentIdentifier getNeighbor(List<IComponentIdentifier>nei, Voter v)
    public Map<IComponentIdentifier,String> getNeighbor(List< Map<IComponentIdentifier,String>>nei, Voter v)
    {
        Map<IComponentIdentifier,String> temp = new HashMap<IComponentIdentifier,String>();
        for (int i = voters.size()-1; i >=0 ; i--)
        {
            //find the new neighbor
            if(!v.getId().equals(voters.get(i).getId()))
            {
                //if(!nei.contains(voters.get(i).getId()))
                temp.put(this.voters.get(i).getId(),this.voters.get(i).getMyBallot());
                if(!nei.contains(temp))
                {
                    //temp.put(this.voters.get(i).getId(),this.voters.get(i).getMyBallot());
                    //return voters.get(i).getId();
                    return temp;
                }
                temp.clear();
            }
        }
        return null;
    }
    /**
     * @param v the new voter
     * @return ArrayList, returning a list of neighbors from the population
     * */
    //public ArrayList<IComponentIdentifier> getNeigbors(Voter v)
    public ArrayList<Map<IComponentIdentifier,String>> getNeigbors(Voter v) {
        //ArrayList<IComponentIdentifier> res = new ArrayList<IComponentIdentifier>();
        ArrayList<Map<IComponentIdentifier,String>> res;

        if(v!=null && this.voters.contains(v)){
            if(this.voters.size()==1){
                return null;
            }

            res = new ArrayList<Map<IComponentIdentifier,String>>();
            //the number of neighbors should not be too large
            int size = ((voters.size()/1)==0)?1:(voters.size()/1);
            int index = new Random().nextInt(size);
            Map<IComponentIdentifier,String> temp = new HashMap<IComponentIdentifier,String>();
            for(int i = 0; i <= index ; i++)
            {
                if(!this.voters.get(i).getId().equals(v.getId()))
                {
                    temp.put(this.voters.get(i).getId(),this.voters.get(i).getMyBallot());
                }
            }
            if(temp!=null){
                res.add(temp);
            }
            return  res;
        }
        return null;
    }

    public int getSizeVoters(){
        return voters.size();
    }

    public Candidates getCandidates() {
        return candidates;
    }

    @Override
    public String toString() {
        return "Population{" +
                "voters=" + voters +
                ", candidates=" + candidates +
                '}';
    }
}
