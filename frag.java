/* -*- mode: Java -*-  */
/* vim:set ts=8 sw=2 sts=2 et: */
/* 
 * class: frag 
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



public class frag extends graph {
  private int fid;
  Adjlist global;
  private HashMap<Integer, HashSet<Integer>> outgoingfid;
  private HashMap<Integer, HashSet<Integer>> incomingfid;

  frag(int fid, Adjlist global, HashMap<Integer, node> nodelist, HashMap<Integer, edge> edgelist) throws nosuchnode {
    super(nodelist, edgelist);
    this.fid=fid;
    this.global=global;
    this.outgoingfid=new HashMap<>();
    this.incomingfid=new HashMap<>();

    for(node N: super.getnodes()){
      int vid=N.getvid();
      if(N.isboundarywithoutgoing()){
        outgoingfid.put(vid, new HashSet<>());
        for(edge E: global.getoutgoing(vid)){
          node NN=global.getdst(E);
          if(this.isbelongtome(NN)){
            continue;
          }
          this.outgoingfid.get(vid).add(NN.getfid()); 
        }
      }
      if(N.isboundarywithincoming()){
        incomingfid.put(vid, new HashSet<>());
        for(edge E: global.getincoming(vid)){
          node NN=global.getsrc(E);
          if(this.isbelongtome(NN)){
            continue;
          }
          this.incomingfid.get(vid).add(NN.getfid()); 
        }
      }

    }
  }


  public String toString(){
    StringBuilder sb=new StringBuilder("");
    sb.append(String.format("ID=[%d]\n\n", fid));
    sb.append("outgoingfid:\n");
    sb.append(this.outgoingfid.toString());
    sb.append("\n\nincomingfid:\n");
    sb.append(this.incomingfid.toString());
    sb.append("\n\ngraph:");
    sb.append(super.toString());
    sb.append("\n");

    return sb.toString();
  }

  boolean isbelongtome(node n){
    return this.fid == n.getfid();
  }

  boolean isbelongtome(int vid){
    return global.getnode(vid).getfid() == this.fid;
  }

  HashSet<Integer> getincomingfid(int vid){
    return this.incomingfid.get(vid);
  }
  HashSet<Integer> getoutgoingfid(int vid){
    return this.outgoingfid.get(vid);
  }

  public Adjlist getglobal(){
    return global;
  }

  public int getfid(){
    return this.fid;
  }

}

