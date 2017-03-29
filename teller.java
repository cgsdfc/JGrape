/* -*- mode: Java -*-  */
/* vim:set ts=8 sw=2 sts=2 et: */
/* 
 * class: teller
 * author: cong feng
 * date: March 17 2017
 * 
GRAPE: Graph Engine
Copyright (C) 2016-2017  Cong Feng    <cgsdfc@126.com>        
BeiHang University, Changping, Beijing, China

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 
02110-1301 USA

*/

import java.util.*;
import java.io.*;


public class teller <Q> {

  private ArrayList<TreeMap<Integer, Integer>> finalresult;
  private String gname, alname,output;
  private ArrayList<Q> query;

  public void write(){
    int i=0;
    for(TreeMap<Integer, Integer> result: finalresult){
      String name=String.format("%s/%s-%s-%s.res",output,gname, alname, query.get(i).toString());
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

  public teller(ArrayList<TreeMap<Integer,Integer>> finalresult, String gname, String alname, ArrayList<Q> query, String output){
    this.output=output;
    this.finalresult=finalresult;
    this.gname=gname;
    this.alname=alname;
    this.query=query;
  }

}
