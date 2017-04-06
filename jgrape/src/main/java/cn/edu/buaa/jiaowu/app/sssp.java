/* -*- mode: Java -*-  */
/* vim:set ts=8 sw=2 sts=2 et: */
/* 
 * class: sssp 
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

package jgrape.src.main.java.cn.edu.buaa.jiaowu.app;
import jgrape.src.main.java.cn.edu.buaa.jiaowu.core.*;
import jgrape.src.main.java.cn.edu.buaa.jiaowu.graph.*;
import java.util.*;
import java.io.*;


public class sssp extends worker<pairmsg, Integer> {


  private ArrayList<HashMap<Integer, Integer>> partials;
  private ArrayList<TreeMap<Integer, Integer>> finalresult;

  protected void peval(frag fragment, Integer query, mbuffer<pairmsg> message){
    HashMap <Integer, Integer> partial = this.partials.get(fragment.getfid());

    for(node N: fragment.getnodes()){
      partial.put(N.getvid(), Integer.MAX_VALUE);
    }
    if(!fragment.isbelongtome(query)) {
      return ;
    }

    HashSet<Integer> visited = new HashSet<> ();
    PriorityQueue<pairmsg> heap = new PriorityQueue<> (fragment.nodesize());

    heap.add(new pairmsg(query, 0));
    partial.put(query, 0);

    while (!heap.isEmpty()) {
      pairmsg pair = heap.poll();
      int vid = pair.getvid();
      int dist = pair.getvalue();

      if(visited.contains(vid))
        continue;

      if(fragment.getnode(vid). isboundarywithoutgoing()){
        for(Integer fid: fragment.getoutgoingfid(vid)){
          message.addoutmsg(fid, pair);
        }
      }

      visited.add(vid);

      for(edge E: fragment.getoutgoing(vid)){
        node that = fragment.getdst(E);
        int olddist= partial.get(that.getvid());
        int len = E.getattr();
        int newdist = len + dist;
        if(newdist < olddist){
          partial.put(that.getvid(), newdist);
          heap.add(new pairmsg(that.getvid(), newdist));
        }
      } 
    }

  }
  protected void inceval(frag fragment, Integer query, mbuffer<pairmsg> message){
    HashSet<Integer> visited = new HashSet<> ();
    PriorityQueue<pairmsg> heap = new PriorityQueue<> (fragment.nodesize());
    HashMap <Integer, Integer> partial = this.partials.get(fragment.getfid());

    for(pairmsg IN: message.getinmsgs()){
      for(edge E: fragment.getglobal().getoutgoing(IN.getvid())) {
        node N = fragment.getglobal().getdst(E);
        if(!fragment.isbelongtome(N))
          continue;
        int olddist = partial.get(N.getvid());
        int newdist = IN.getvalue() + E.getattr();
        if(newdist < olddist) {
          partial.put(N.getvid(), newdist);
          heap.add(new pairmsg(N.getvid(), newdist));
        }
      }
    }

    while (!heap.isEmpty()) {
      pairmsg pair = heap.poll();
      int vid = pair.getvid();
      int dist = pair.getvalue();

      if(visited.contains(vid))
        continue;

      if(fragment. getnode(vid).isboundarywithoutgoing()){
        for(Integer fid: fragment.getoutgoingfid(vid)){
          message.addoutmsg(fid, pair);
        }
      }

      visited.add(vid);

      for(edge E: fragment.getoutgoing(vid)){
        node that = fragment.getdst(E);
        int olddist= partial.get(that.getvid());
        int len = E.getattr();
        int newdist = len + dist;
        if(newdist < olddist){
          partial.put(that.getvid(), newdist);
          heap.add(new pairmsg(that.getvid(), newdist));
        }
      } 
    }
  }


  protected void assemble(){

    TreeMap<Integer, Integer> thisresult = new TreeMap<>();

    for(HashMap<Integer, Integer> result: this.partials) {
      for(Map.Entry<Integer, Integer> entry: result.entrySet() ){
        thisresult.put(entry.getKey(), entry.getValue());
      }
    }
    this.finalresult.add(thisresult);
  }

  protected void clear () {}

  protected void write(){
    String path="";
    FileWriter writer=null;
    int qindex=0;

    for (Integer query: super.queries) {
      path=String.format ("%s/%s-%s-query-%d.dat",super.prefix, super.graphname, super.algorithm, query);
      try {
        writer = new FileWriter (path);
        for (Map.Entry<Integer,Integer> item: 
            this.finalresult.get(qindex).entrySet () ) {
          writer.write(String.format (
                "%d\t%d\n", 
                item.getKey(),
                item.getValue()));
            }
        qindex ++;
        writer.close();
      }
      catch (IOException e){
        System.err.println("IOException hitted when writing" + path );
        System.exit(1);
      }

    }
  }


  public sssp(ArrayList<frag> fragments, ArrayList<Integer> queries){
    super(fragments, queries);
    this.partials= new ArrayList<> (super.nfrag);
    this.finalresult= new ArrayList<> (super.queries.size());
    for(int i=0;i<super.nfrag;++i) {
      this.partials.add(new HashMap<Integer, Integer> ());
    }
  }


  public ArrayList<TreeMap<Integer, Integer>> getfinalresult(){
    return this.finalresult;
  }


}
