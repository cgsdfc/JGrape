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


import java.util.*;
import java.lang.*;
import java.io.*;

class Pair <K, V> {
  private K key;
  private V val;

  public Pair (K key, V val) {
    this.key=key;
    this.val=val;
  }

  public String toString() {
    return String . format ("{{ %s, %s }}", key.toString(),
        val.toString());
  }
}

class cctemporal {
   
  public  HashMap<Integer,Integer> localComp;
  public  HashMap<Integer,Integer> globalComp;
  public  HashMap<Integer,ArrayList<Integer>> outerNodes;


  public cctemporal (
      HashMap<Integer,Integer> localComp,
      HashMap<Integer,Integer> globalComp,
      HashMap<Integer,ArrayList<Integer>> outerNodes) {
      this.localComp   =   localComp  ; 
      this.globalComp  =   globalComp ;
      this.outerNodes  =   outerNodes ;
  }

}

// template<class MessageT, class ResultT, class TemporalT>
// cc does not require a query
public class cc extends worker <pairmsg, Object>{

  protected ArrayList <HashMap<Integer,Integer>> localComp; 
  protected ArrayList <HashMap<Integer,Integer>> globalComp; 
  protected ArrayList <TreeMap<Integer,Integer>> finalresult; 
  protected ArrayList <cctemporal> temporal;

  protected cc (
      ArrayList<frag> fragments, ArrayList<Object> queries)
  {

    super(fragments, queries);
    this.finalresult=new ArrayList <> ();
    this.localComp=new ArrayList<> ();
    this.globalComp=new ArrayList <> ();
    this.temporal=new ArrayList <> ();
    for(int i=0;i<super.nfrag;++i) {
      this.localComp.add(new HashMap<> ());
      this.globalComp.add(new HashMap<> ());
    }
  }

  protected void peval(frag fragment, 
      Object query,
      mbuffer<pairmsg> messageBuffer) {

    int WID=fragment.getfid();
    HashMap<Integer, Integer> localComp=this.localComp.get(WID);
    HashMap<Integer, Integer> globalComp=this.globalComp.get(WID);
    HashMap<Integer, Integer> messages=new HashMap<> ();
    HashMap<Integer, ArrayList<Integer>> 
      outerNodes=new HashMap<> ();

    for (node N:fragment.getnodes()) {
      int xid=N.getvid();
      if (localComp.containsKey (xid))
        continue;

      ArrayDeque<Integer> queue = new ArrayDeque<> ();
      queue.add (xid);
      localComp.put(xid, Integer.MAX_VALUE);
      ArrayDeque<Integer> comp=new ArrayDeque<> ();
      ArrayList<Integer> outers=new ArrayList<> ();
      int minId = xid;
      int uid=0;
      while (!queue.isEmpty()) {
        uid=queue.poll();
        comp.add(uid);
        for (int vid: fragment.getchildren(uid)) {
          minId=Math.min(minId, vid);
          if (fragment.isbelongtome(vid)) {
            if (! localComp.containsKey (vid) ) {
              queue.add(vid);
              localComp.put(vid, Integer.MAX_VALUE);
            }
            else {
              outers.add(vid);
            }
          }
        }
        for (int vid: fragment.getparents(uid)) {
          minId=Math.min(minId, vid);
          if (fragment.isbelongtome(vid)) {
            if (! localComp.containsKey(vid)) {
              queue.add(vid);
              localComp.put(vid, Integer.MAX_VALUE);
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

        for (Map.Entry<Integer, Integer> p: messages.entrySet()) {
          uid = p.getKey();
          int compId = p.getValue();
          int fid=fragment.getnode(uid).getfid();
          messageBuffer.addoutmsg(fid,new pairmsg (uid, compId));
        }
        cctemporal temp= new cctemporal(localComp, globalComp, outerNodes);
        this.temporal.set(WID,temp);

      }
    }
  }

  protected void inceval (frag fragment,
      Object query,
      mbuffer<pairmsg> messageBuffer) {
    int WID=fragment.getfid();
    cctemporal temp=this.temporal.get(WID);
    HashMap<Integer, Integer> localComp=temp.localComp;
    HashMap<Integer, Integer> globalComp=temp.globalComp;
    HashMap<Integer, ArrayList<Integer>> outerNodes=temp.outerNodes;

    HashSet<Integer> updated = new HashSet<> ();
    for (pairmsg msg: messageBuffer.getinmsgs()) {
      int uid=msg.getvid();
      int tag=msg.getvalue();
      int compId=localComp.get(uid);

      if (globalComp.get(compId) > tag){
        globalComp.put(compId,tag);
        if (! updated.contains(compId)) {
          updated.add(compId);
        }
      }
    }

    HashMap<Integer, Integer> messages=new HashMap<>();
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
      int fid=fragment.getnode(uid).getfid();
      messageBuffer.addoutmsg(fid, new pairmsg(uid,compId));
    }

    temp.globalComp=globalComp;
    temp.localComp=localComp;

  }


  protected void assemble() {
    TreeMap<Integer, Integer> ass=new TreeMap<>();
    for 


  }
  protected void write(){
  }

  protected void clear() {
  }



}
