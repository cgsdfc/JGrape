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
import java.io.*;
import java.util.*;

public class sim extends worker <Integer, graph> {
   private ArrayList<HashMap<Integer, HashSet<Integer>>>
     simset,removed,tset,presim;
   private HashMap<Integer, HashSet<Integer>>finalResult;

   private ArrayList<HashMap<Integer, Integer>>
     tag2Id;
   private ArrayList<HashMap<Integer, ArrayList<Integer>>>
     count;
   private ArrayList<HashMap<Integer, Boolean>>
     active;
   private ArrayList<HashSet<Integer>>prev;


  protected sim (
      ArrayList<frag> fragments, ArrayList<graph> queries)
  {
    super(fragments, queries);
    int nfrag=super.nfrag;
    this.simset=new ArrayList<> (nfrag);
    this.removed=new ArrayList<> (nfrag);
    this.tset=new ArrayList<> (nfrag);
    this.presim=new ArrayList<> (nfrag);
    this.tag2Id=new ArrayList<> (nfrag);
    this.count=new ArrayList<> (nfrag);
    this.active=new ArrayList<> (nfrag);
    this.prev=new ArrayList<> (nfrag);
    this.finalResult=new HashMap<> ();


    for (int i=0;i<nfrag;++i) {
      this.simset.add(new HashMap<> ());
      this.removed.add(new HashMap<> ());
      this.tset.add(new HashMap<> ());
      this.presim.add(new HashMap<> ());
      this.tag2Id.add(new HashMap<> ());
      this.count.add(new HashMap<> ());
      this.active.add(new HashMap<> ());
      this.prev.add(new HashSet<> ());
    }

  }

  protected static HashSet<Integer>
    diff (HashSet<Integer> A,HashSet<Integer> B) {
    return new HashSet<Integer> ();
  }
  protected void peval(frag fragment, 
      graph query,
      mbuffer<Integer> messageBuffer) {
    int WID=fragment.getfid();
    HashMap<Integer,HashSet<Integer>> 
      simset=this.simset.get(WID);
    HashMap<Integer, HashSet<Integer>> 
      removed=this.removed.get(WID);
    HashMap<Integer, HashSet<Integer>> 
      tset=this.tset.get(WID);
    HashMap<Integer, HashSet<Integer>> 
      presim=this.presim.get(WID);
    HashMap<Integer, Integer>
      tag2Id=this.tag2Id.get(WID);
    HashMap<Integer,ArrayList<Integer>>
      count=this.count.get(WID);
    HashMap<Integer, Boolean> 
      active=this.active.get(WID);
    HashSet<Integer> prev=this.prev.get(WID);

    query.buildsignature();
    for (node N:query.getnodes()) {
      int tag=N.getattr();
      int uid=N.getvid();
      tag2Id.put(tag,uid);
      presim.put(uid, new HashSet<Integer> (query.getparents(uid)));
      simset.put(uid, new HashSet<> ());
      removed.put(uid, new HashSet<> ());
    }

    for (node N:fragment.getglobal().getnodes()){
      int tag=N.getattr();
      int vid=N.getvid();
      if (tag2Id.containsKey(tag)) {
        ArrayList<Integer> cnt=new ArrayList<> (query.nodesize());
        cnt.trimToSize();
        count.put(vid, cnt);
        active.put(vid, true);
        if (fragment.anychild(vid)) {
          prev.add(vid);
        }
      }
    } 

    for (Map.Entry<Integer, Boolean> entry: active.entrySet()) {
      int vid=entry.getKey();
      int tag=fragment.getnode(vid).getattr();
      int uid=tag2Id.get(tag);

      if (! query.anychild(uid) ||
          fragment.getglobal().anychild(vid) ||
          !fragment.isbelongtome(vid))
      {
        for (int pid: fragment.getparents(vid)) {
          if (active.containsKey(pid)) {
            removed.get(uid).add(pid);
            int cnt=count.get(pid).get(tag);
            count.get(pid).set(tag, cnt+1);
          }
        }
      }

    }

    for (node N:query.getnodes()) {
      int uid=N.getvid();
      removed.put(uid,sim.diff(prev, removed.get(uid)));
    }

    ArrayDeque <Integer> queue = new ArrayDeque<> ();
    HashSet<Integer> inqueue = new HashSet<> ();

    for (node N:query.getnodes()) {
      int uid=N.getvid();
      if (!removed.get(uid).isEmpty()) {
        queue.add(uid);
        inqueue.add(uid);
      }
    }

    while (!queue.isEmpty()) {
      int uid=queue.poll();
      inqueue.remove(uid);
      for (int wid: removed.get(uid)) {
        int tag=fragment.getnode(wid).getattr();
        if (tag2Id.containsKey(tag)) {
          int u1=tag2Id.get(tag);
          if (presim.get(uid).contains(u1) && active.get(wid)) {
            active.put(wid, false);
            for (int w1: fragment.getparents(wid)) {
              if (active.get(w1)) {
                int cnt=count.get(w1).get(tag)-1;
                count.get(w1).set(tag, cnt);
                if (cnt == 0) {
                  removed.get(u1).add(w1);
                  if (!inqueue.contains(u1)) {
                    queue.add(u1);
                    inqueue.add(u1);
                  }
                }
              }
            }
          }
        }
      }
      removed.get(uid).clear();
    }

    for (Map.Entry<Integer, Boolean> entry: active.entrySet()){
      if (entry.getValue()) {
        int vid=entry.getKey();
        int tag=fragment.getnode(vid).getattr();
        int uid=tag2Id.get(tag);
        simset.get(uid).add(vid);
      }
    }

    for (node N: fragment.getnodes()) {
      if (!N.isboundarywithincoming())
        continue;
      int tag=N.getattr();
      int vid=N.getvid();
      if (!active.get(vid) && tag2Id.containsKey(tag)) {
        for (int fid: fragment.getincomingfid(vid)) {
          messageBuffer.addoutmsg(fid, vid);
        }
      }
    }



  }

  protected void inceval (frag fragment,
      graph query,
      mbuffer<Integer> messageBuffer) {
    int WID=fragment.getfid();
    HashMap<Integer,HashSet<Integer>> 
      simset=this.simset.get(WID);
    HashMap<Integer, HashSet<Integer>> 
      removed=this.removed.get(WID);
    HashMap<Integer, HashSet<Integer>> 
      tset=this.tset.get(WID);
    HashMap<Integer, HashSet<Integer>> 
      presim=this.presim.get(WID);
    HashMap<Integer, Integer>
      tag2Id=this.tag2Id.get(WID);
    HashMap<Integer,ArrayList<Integer>>
      count=this.count.get(WID);
    HashMap<Integer, Boolean> 
      active=this.active.get(WID);
    HashSet<Integer> prev=this.prev.get(WID);

    HashMap<Integer, HashSet<Integer>> delta=
      new HashMap<>();
    ArrayDeque<Integer> simque=
      new ArrayDeque<> ();

    for (int vid: messageBuffer.getinmsgs()){
      if (active.get(vid)) {
        active.put(vid, false);
      }
    }

    for (node N:query.getnodes()){
      int uid=N.getvid();
      delta.put(uid, new HashSet<> ());
    }

    while (!simque.isEmpty()) {
      int vid=simque.poll();

      if (fragment.getnode(vid).isboundarywithincoming()) {
        for (int fid: fragment.getincomingfid(vid)) {
          messageBuffer.addoutmsg(fid, vid);
        }
      }

      int tag=fragment.getnode(vid).getattr();
      int uid=tag2Id.get(tag);
      delta.get(uid).add(vid);
      for (int v1: fragment.getparents(vid)){
        if (active.get(v1)) {
          int cnt=count.get(v1).get(tag)-1;
          count.get(v1).set(tag,cnt);
          if (cnt == 0) {
            int u1=tag2Id.get(
                fragment.getnode(v1).getattr()
                );
            if (presim.get(uid).contains(u1)) {
              active.put(v1,false);
              simque.add(v1);
            }
          }
        }
      }
    }
    for (node N: query.getnodes()) {
      int uid=N.getvid();
      simset.put(uid, sim.diff(simset.get(uid), delta.get(uid)));
    }

  }

  protected void assemble() {
  
    for (HashMap<Integer, HashSet<Integer>> resResult : 
        this.simset) {
      for (Map.Entry<Integer, HashSet<Integer>> simU : 
          resResult.entrySet()) {
        int uid=simU.getKey();
        for (int vid : simU.getValue()) {
          finalResult.get(uid).add(vid);
        }
      }
    }
  }

  protected void write () {
      FileWriter writer=null;
      String path=String.format ("%s/%s-%s-query.dat" ,
          super.prefix,
          super.graphname, 
          super.algorithm);
      try {
        writer = new FileWriter (path);
        for (Map.Entry<Integer, HashSet<Integer>> simU : 
            finalResult.entrySet()) {
          int uid=simU.getKey();
          for (int vid : simU.getValue()) {
            writer.write(String.format (
                  "%d -> %d\n",
                  vid,
                  uid));
          }
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


