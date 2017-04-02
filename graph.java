/* -*- mode: Java -*-  */
/* vim:set ts=8 sw=2 sts=2 et: */
/* 
 * class: graph
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

import java.lang.StringBuilder;
import java.lang.IndexOutOfBoundsException;
import java.util.*;


public class graph implements Adjlist {
  private HashMap<Integer, ArrayList<edge>> incoming, outgoing;
  private HashMap<Integer, node> nodelist;
  private HashMap<Integer, edge> edgelist;

  private void init(){
    incoming=new HashMap<> ();
    outgoing=new HashMap<> ();
    nodelist=new HashMap<>();
    edgelist=new HashMap<>();
  }

  public graph(){
    init();
  }
  public void buildsignature(){

    //TODO
  }

  public boolean anychild(int vid){
    return !this.outgoing.get(vid).isEmpty();
  }

  public boolean anyparent(int vid){
    return !this.incoming.get(vid).isEmpty();
  }

  public graph(HashMap<Integer, node> nodelist, HashMap<Integer, edge> edgelist) throws nosuchnode{
    this.nodelist=nodelist;
    this.edgelist=edgelist;
    this.incoming=new HashMap<>();
    this.outgoing=new HashMap<>();
    this.buildadjlist();
  }

  public String toString(){
    StringBuilder sb=new StringBuilder("graph:\nOUTGOING:");
    for(Map.Entry<Integer, ArrayList<edge>> entry: outgoing.entrySet()){
      int vid=entry.getKey();
      sb.append(String.format("\n(%d) -> (", vid));
      for(edge e: entry.getValue()) {
        sb.append(String.format("%d, ", e.getdst()));
      }
      sb.append(")");
    }

    sb.append("\nINCOMING:");
    for(Map.Entry<Integer, ArrayList<edge>> entry: incoming.entrySet()){
      int vid=entry.getKey();
      sb.append(String.format("\n(%d) <- (", vid));
      for(edge e: entry.getValue()) {
        sb.append(String.format("%d, ", e.getsrc()));
      }
      sb.append(")");
    }

    return sb.toString();
  }

  public int nodesize(){
    return nodelist.size(); 

  }
  public int edgesize(){
    return edgelist.size();
  }

  public int indegree(node N) {
    return this.incoming.get(N.getvid()).size();
  }

  public int outdegree(node N) {
    return this.outgoing.get(N.getvid()).size();
  }

  public ArrayList<edge> getincoming(int vid) {
    return this.incoming.get(vid);
  }


  public ArrayList<edge> getoutgoing(int vid) {
    return this.outgoing.get(vid);
  }

  public Collection<node> getnodes(){
    return this.nodelist.values();
  }

  public Collection<edge> getedges(){
    return this. edgelist.values();
  }

  public node getnode(int vid){
    return this.nodelist.get(vid);
  }

  public edge getedge(int eid){
    return this.edgelist.get(eid);
  }

  public node getdst(edge e){
    return this.nodelist.get(e.getdst());
  }

  public node getsrc(edge e){
    return this.nodelist.get(e.getsrc());
  }

  public void addnode(node Node){
    this.nodelist.put(Node.getvid(), Node);
  }

  public void addedge(edge Edge){
    this.edgelist.put(Edge.geteid(), Edge);
  }

  public boolean iscrossedge(edge Edge){
    return getdst(Edge).getfid() != getsrc(Edge).getfid();
  }

  public ArrayList<Integer> getparents(int vid) {
    ArrayList<Integer> parents=new ArrayList<>();
    for (edge E:this.getincoming(vid)) {
      parents.add(E.getsrc());
    }
    return parents;
  }
  
  public ArrayList<Integer> getchildren(int vid) {
    ArrayList<Integer> children=new ArrayList<>();
    for (edge E:this.getoutgoing(vid)) {
      children.add(E.getdst());
    }
    return children;
  }

  public void buildadjlist() throws nosuchnode {
    for(node Node: this.nodelist.values()){
      this.incoming.put(Node.getvid(), new ArrayList<edge> ());
      this.outgoing.put(Node.getvid(), new ArrayList<edge> ());
    }
    for(edge Edge: this.edgelist.values()){
      ArrayList<edge> in=this.incoming.get(Edge.getdst());
      if(in == null)
        throw new nosuchnode(Edge.getdst());
      in.add(Edge);

      ArrayList<edge> out=this.outgoing.get(Edge.getsrc());
      if(out == null)
        throw new nosuchnode(Edge.getsrc());
      out.add(Edge);
    }
  }

  public void setboundarywithincoming(int vid){
    this.nodelist.get(vid).setboundarywithincoming();
  }

  public void setboundarywithoutgoing(int vid){
    this.nodelist.get(vid).setboundarywithoutgoing();
  }

  void setfid(int vid, int fid) throws nosuchnode {
    node N = this.nodelist.get(vid);
    if(null == N){
      throw new nosuchnode(vid);
    }
    N.setfid(fid);
  }

  public static void main(String [] args) {

  }
}


