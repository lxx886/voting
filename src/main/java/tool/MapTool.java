package tool;

import java.util.*;

public class MapTool <E,T>{


    //public  <T extends Number> void

    /**find a mimimal value in a hashmap whether the value is int or double; if the value
     * is int, then the return value need to be casted to int.
     * @param s the hashmap
     * @return the minimal value, double
     * */
//    public  <T extends Number> double minMap(HashMap<String , T> s) {
//        double score = Double.MAX_VALUE;
//        for(String key : s.keySet()) {
//            double temp = s.get(key).doubleValue();
//            if(score >= temp) {
//                score = temp;
//            }
//        }
//        //System.out.println(score);
//        return score;
//    }
    public  <T extends Number> double minMap(Map<E , T> s) {
        double score = Double.MAX_VALUE;
        for(E key : s.keySet()) {
            double temp = s.get(key).doubleValue();
            if(score >= temp) {
                score = temp;
            }
        }
        //System.out.println(score);
        return score;
    }

    /**find a maximal value in a hashmap whether the value is int or double; if the value
     * is int, then the return value need to be casted to int.
     * @param s the hashmap
     * @return the masimal value, double
     * */
//    public  <T extends Number> double maxMap(HashMap<String , T>  s) {
//        double score = Double.MIN_VALUE;
//        for(String key : s.keySet()) {
//            double temp = s.get(key).doubleValue();
//            if(score <= temp) {
//                score = temp;
//            }
//        }
//        //System.out.println(score);
//        return score;
//    }

    public  <T extends Number> double maxMap(Map<E, T>  s) {
        double score = Double.MIN_VALUE;
        for(E key : s.keySet()) {
            double temp = s.get(key).doubleValue();
            if(score <= temp) {
                score = temp;
            }
        }
        //System.out.println(score);
        return score;
    }
    //return the item in the map which has the highest value
    public  <T extends Number> Map<E, Double>  maxEntry(Map<E, T>  s) {
        Map<E, Double> res = new HashMap<E, Double>();
        double score = Double.MIN_VALUE;

        E key = null;
        for(E e : s.keySet()) {
            double temp = s.get(e).doubleValue();

            if(score <= temp) {
                score = temp;
                key = e;
            }
        }
        //System.out.println(score);
        res.put(key,score);
        return res;
    }

    public  <T extends Number> E getKeyOfMaxMap(Map<E, T>  s) {
        double score = Double.MIN_VALUE;
        E res = null;
        for(E key : s.keySet()) {
            double temp = s.get(key).doubleValue();
            if(score < temp) {
                score = temp;
                res = key;
            }
        }
        //System.out.println(score);
        return res;
    }
    /** compute the sum value of a hashmap whether the value is int or double; if the value
     * is int, then the return value need to be casted to int.
     * @param s the hashmap
     * @return the sum, double
     * */
//    public  <T extends Number> double sumMap(HashMap<String , T>  s) {
//        double sum = 0;
//        for(String key : s.keySet()) {
//            sum = sum + s.get(key).doubleValue();
//        }
//        return sum;
//    }
        public  <T extends Number> double sumMap(Map<E , T>  s) {
        double sum = 0;
        for(E key : s.keySet()) {
            sum = sum + s.get(key).doubleValue();
        }
        return sum;
    }



    /**sort map based on the value
     * @param map, the hashmap needed to sort
     * @return return the sorted            list
     * */
//
//    public <T extends Number> List<Map.Entry<String, T>> sortMapByValue(HashMap<String, T> map) {
//
//        // test for hash map sorted based on the value
//        List<Map.Entry<String, T>> list = new ArrayList<Map.Entry<String, T>>();
//        list.addAll(map.entrySet());
//        Collections.sort(list, new Comparator<Map.Entry<String, T>>() {
//
//            public int compare(Map.Entry<String, T> o1, Map.Entry<String, T> o2) {
//                return (int) ((o2.getValue().doubleValue())*100000.0 - (o1.getValue().doubleValue())*100000.0); // there may exsists problems
//            }
//        });
//        return list;
//    }

    public <T extends Number> List<Map.Entry<E, T>> sortMapByValue(Map<E, T> map) {

        // test for hash map sorted based on the value
        List<Map.Entry<E, T>> list = new ArrayList<>();
        list.addAll(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<E, T>>() {

            public int compare(Map.Entry<E, T> o1, Map.Entry<E, T> o2) {
                return (int) ((o2.getValue().doubleValue())*100000.0 - (o1.getValue().doubleValue())*100000.0); // there may exsists problems
            }
        });
        return list;
    }


}
