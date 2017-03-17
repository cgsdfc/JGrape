import java.util.*;
import java.io.*;


public class teller {

        private   ArrayList<TreeMap<Integer, Integer>> finalresult;
        private String gname, alname;
        private ArrayList<Integer> query;

        public void write(){
                int i=0;
                for(TreeMap<Integer, Integer> result: finalresult){
                        String name=String.format("%s-%s-query-%d.dat",gname, alname, query.get(i));
                        i++;
                        try{
                                FileWriter fw = new FileWriter(name);
                                for(Map.Entry<Integer, Integer> entry: result.entrySet()){
                                        String line=String.format("%d\t%d\n", entry.getKey(), entry.getValue());
                                        fw.write(line);
                                }
                                fw.close();
                        }
                        catch(IOException e) {

                        }
                }
        }

        public teller(ArrayList<TreeMap<Integer,Integer>> finalresult, String gname, String alname, ArrayList<Integer> query){
                this.finalresult=finalresult;
                this.gname=gname;
                this.alname=alname;
                this.query=query;
        }

}
