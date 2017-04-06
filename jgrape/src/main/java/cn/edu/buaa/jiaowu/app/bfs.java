/* -*- mode: Java -*-  */
/* vim:set ts=8 sw=2 sts=2 et: */
/* 
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
import jgrape.src.main.java.cn.edu.buaa.jiaowu.query.*;
import jgrape.src.main.java.cn.edu.buaa.jiaowu.core.*;
import jgrape.src.main.java.cn.edu.buaa.jiaowu.graph.*;

import java.util.*;
import java.io.*;

class pairmsg_falling implements Comparator <pairmsg> {
  public int compare (pairmsg s, pairmsg t) {
    // 4 3 2 1 on getvalue()
    return t.getvalue() - s.getvalue();
  }
}

public class bfs extends worker <pairmsg, pairmsg> {

  private ArrayList<HashSet<Integer>> partials;
  private ArrayList<TreeSet<Integer>> finalresult;


  public bfs (ArrayList<frag> fragments, ArrayList<pairmsg> queries){
    super(fragments, queries);
    this.partials= new ArrayList<> (super.nfrag);
    this.finalresult= new ArrayList<> (super.queries.size());
    for(int i=0;i<super.nfrag;++i) {
      this.partials.add(new HashSet<Integer> ());
    }
  }

  protected void peval(frag fragment, pairmsg query, mbuffer<pairmsg> message) {
    int k=bquery.getbound(query);
    int root=bquery.getroot(query);

    PriorityQueue<pairmsg> queue=null;
    HashSet<Integer> result=this.partials.get(fragment.getfid());


    //     pairmsg.vid=root , vid
    //     pairmsg.val=bound, distance
    if (fragment.isbelongtome(root)){
      queue=new PriorityQueue<> (fragment.nodesize(), new pairmsg_falling() );
      queue.add(new pairmsg (root, k));
    }
    else return;

    while (!queue.isEmpty()) {
      pairmsg pair=queue.poll();
      int distance=pair.getvalue();
      int vid=pair.getvid();

      if (result.contains(vid))
        continue;

      result.add(vid);

      if(fragment.getnode(vid).isboundarywithoutgoing()){
        for(Integer fid: fragment.getoutgoingfid(vid)){
          message.addoutmsg(fid, pair);
        }
      }

      if (distance > 0){
        for (edge e: fragment.getoutgoing(vid)) {
            queue.add( new pairmsg (e.getdst(), distance-1));
          
        }
      }
    }
  }

  protected void inceval (frag fragment, pairmsg query, mbuffer<pairmsg> message) {
    HashSet<Integer> result=this.partials.get(fragment.getfid());
    PriorityQueue<pairmsg> queue = new PriorityQueue<> (fragment.nodesize(), new pairmsg_falling());
    HashSet<Integer> visited = new HashSet<> (fragment.nodesize());
    for (pairmsg pair : message.getinmsgs()) {
      int vid=pair.getvid();
      int distance=pair.getvalue();
      queue.add(new pairmsg(vid, distance));
    }

    while (!queue.isEmpty()){
      pairmsg pair=queue.poll();
      int distance=pair.getvalue();
      int vid=pair.getvid();

      if (visited.contains(vid)) {
        continue;
      }

      visited.add(vid);

      if (fragment.isbelongtome(vid)) {
        result.add(vid);
        if (fragment.getnode(vid).isboundarywithoutgoing()) {
          for (int fid: fragment.getoutgoingfid(vid)) {
            message.addoutmsg(fid, pair);
          }
        }
      }

      if (distance > 0){
        for (edge e: fragment.getglobal().getoutgoing(vid)) {
          int dst=e.getdst();
          if (fragment.isbelongtome(dst)) {
            queue.add( new pairmsg (dst, distance-1));
          }
        }
      }
    }
  }

  protected void assemble() {
    TreeSet <Integer> result = new TreeSet<> ();
    for (HashSet<Integer> partial : this.partials ){
      for (Integer item: partial) {
        result.add(item);
      }
    }
    this.finalresult.add (result);
  }

  protected void write(){
    String path="";
    FileWriter writer=null;
    int qindex=0;

    for (pairmsg query: super.queries) {
      path=String.format ("%s/%s-%s-query-%d-%d.dat"
          ,super.prefix,
          super.graphname, 
          super.algorithm,
          bquery.getroot(query),
          bquery.getbound(query));
      try {
        writer = new FileWriter (path);
        for (Integer item: 
            this.finalresult.get(qindex)) {
          writer.write(String.format (
                "%d\n", 
                item));
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

  protected void clear() {
    for (HashSet<Integer> partial : this.partials) {
      partial.clear();
    }
    for (mbuffer<pairmsg> message: super.messages) {
      message.inclear();
      message.outclear();
    }
  }

}


