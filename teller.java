import java.util.*;
import java.io.*;


public class teller {

        private   ArrayList<HashMap<Integer, Integer>> finalresult;
        private String gname, alname;

        public void write(){
                int i=0;
                for(HashMap<Integer, Integer> result: finalresult){
                        String name=String.format("%s-%s-query-%d",gname, alname, i++);
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

        public teller(ArrayList<HashMap<Integer,Integer>> finalresult, String gname, String alname){
                this.finalresult=finalresult;
                this.gname=gname;
                this.alname=alname;
        }

}
