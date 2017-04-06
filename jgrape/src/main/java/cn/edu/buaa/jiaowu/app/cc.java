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


// cc does not require a query
public class cc extends worker <pairmsg, Object>{

  protected ArrayList <HashMap<Integer,Integer>> localComp; 
  protected ArrayList <HashMap<Integer,Integer>> globalComp; 
  protected ArrayList <HashMap<Integer, ArrayList<Integer>>>
    outerNodes;
  protected TreeMap<Integer,Integer> finalresult; 

  // queries.size() should at least be 1
  public cc (
      ArrayList<frag> fragments, ArrayList<Object> queries)
  {

    super(fragments, queries);
    int nfrag=super.nfrag;
    this.finalresult=new TreeMap<> ();
    this.localComp=new ArrayList<> (nfrag);
    this.globalComp=new ArrayList <> (nfrag);
    this.outerNodes=new ArrayList <> (nfrag);
    for(int i=0;i<super.nfrag;++i) {
      this.localComp.add(new HashMap<Integer,Integer> ());
      this.globalComp.add(new HashMap<Integer,Integer> ());
      this.outerNodes.add(new HashMap<Integer,ArrayList<Integer>> ());
    }
  }

  protected void peval(frag fragment, 
      Object query,
      mbuffer<pairmsg> messageBuffer) {

    int WID=fragment.getfid();
    HashMap<Integer, Integer> messages=new HashMap<> ();
    HashMap<Integer, Integer> localComp=this.localComp.get(WID);
    HashMap<Integer, Integer> globalComp=this.globalComp.get(WID);
    HashMap<Integer, ArrayList<Integer>> 
      outerNodes=this.outerNodes.get(WID);
    ArrayDeque<Integer> queue = new ArrayDeque<> ();
    ArrayDeque<Integer> comp=new ArrayDeque<> ();

    for (node N:fragment.getnodes()) {
      int xid=N.getvid();
      queue.clear();
      comp.clear();
      if (localComp.containsKey (xid))
        continue;

      queue.add (xid);
      localComp.put(xid, Integer.MAX_VALUE);
      ArrayList<Integer> outers=new ArrayList<> ();
      int minId = xid;
      int uid=0;
      while (!queue.isEmpty()) {
        uid=queue.poll();
        comp.add(uid);
        for (int vid: fragment.getglobal().getchildren(uid)) {
          minId=Math.min(minId, vid);
          if (fragment.isbelongtome(vid)) {
            if (! localComp.containsKey (vid) ) {
              queue.add(vid);
              localComp.put(vid, Integer.MAX_VALUE);
            }
            
          }
          else {
              outers.add(vid);
          }
        }
        for (int vid: fragment.getglobal().getparents(uid)) {
          minId=Math.min(minId, vid);
          if (fragment.isbelongtome(vid)) {
            if (! localComp.containsKey(vid)) {
              queue.add(vid);
              localComp.put(vid, Integer.MAX_VALUE);
            }
          }
          else {
            outers.add(vid);
          }
        }
      }
      while (! comp.isEmpty()) {
        uid=comp.poll();
        localComp.put(uid,minId);
      }
      globalComp.put(minId,minId);
      outerNodes.put(minId, outers);

        for (int vid: outers){
          if (! messages.containsKey(vid) || 
              minId < messages.get(vid)) {
            messages.put(vid, minId);
          }
        }
    }
    for (Map.Entry<Integer, Integer> p: messages.entrySet()) {
      int uid = p.getKey();
      int compId = p.getValue();
      int fid=fragment.getglobal().getnode(uid).getfid();
      messageBuffer.addoutmsg(fid,new pairmsg (uid, compId));
    }

  }
    
  

  protected void inceval (frag fragment,
      Object query,
      mbuffer<pairmsg> messageBuffer) {
    int WID=fragment.getfid();
    HashMap<Integer, Integer> messages=new HashMap<>();
    HashMap<Integer, Integer> localComp=this.localComp.get(WID);
    HashMap<Integer, Integer> globalComp=this.globalComp.get(WID);
    HashMap<Integer, ArrayList<Integer>> 
      outerNodes=this.outerNodes.get(WID);

    HashSet<Integer> updated = new HashSet<> ();
    for (pairmsg msg: messageBuffer.getinmsgs()) {
      int uid=msg.getvid();
      int tag=msg.getvalue();
      int compId=localComp.get(uid);
      // compId is used to index into the outerNodes
      // to send updating msg to them
      // <compId, [vid in other fragments]> ==> outerNodes

      if (globalComp.get(compId) > tag){
        // tag is the updated global comp id
        globalComp.put(compId,tag);
        if (! updated.contains(compId)) {
          updated.add(compId);
        }
      }
    // <local, global> ==> globalComp
    // <vid, local> ==> localComp
    }

    // messages = [ <vid, global> : min { global } && unique { vid } ]
    for (int uid: updated){
      int tag=globalComp.get(uid);
      for (int vid: outerNodes.get(uid)) {
        if (! messages.containsKey(vid) ||
            messages.get(vid) > tag) {
            messages.put(vid,tag);
            }
      }
    }

    for (Map.Entry<Integer,Integer> p: messages.entrySet()) {
      int uid=p.getKey();
      int compId=p.getValue();
      int fid=fragment.getglobal().getnode(uid).getfid();
      messageBuffer.addoutmsg(fid, new pairmsg(uid,compId));
    }

  }


  protected void assemble() {
    for (int i=0;i<super.nfrag;++i ) {
      HashMap<Integer, Integer> localComp=this.localComp.get(i);
      HashMap<Integer, Integer> globalComp=this.globalComp.get(i);

      for (Map.Entry<Integer, Integer> p: localComp.entrySet()) {
        int uid=p.getKey();
        int compId=p.getValue();
        int minHash=globalComp.get(compId);
        this.finalresult.put(uid, minHash);
      }
    }
  }

  
    protected void write(){
      FileWriter writer=null;
      String path=String.format ("%s/%s-%s-query.dat" ,
          super.prefix,
          super.graphname, 
          super.algorithm);
      try {
        writer = new FileWriter (path);
        for (Map.Entry<Integer, Integer> p:
            this.finalresult.entrySet()) {
          writer.write(String.format ("%d\t%d\n", 
                p.getKey(),
                p.getValue()));
        }
        writer.close();
      }
      catch (IOException e){
        System.err.println("IOException hitted when writing" + path );
        System.exit(1);
      }

    }

    protected void clear() { }

}
