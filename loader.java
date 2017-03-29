/* -*- mode: Java -*-  */
/* vim:set ts=8 sw=2 sts=2 et: */
/* 
 * class: loader
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


public class loader  {
  private graph global;
  private ArrayList<frag>  fragments;
  private int nfrag;


  public loader(parser p, splitter s){
    this.fragments=new ArrayList<frag> ();
    this.global=new graph();
    p.fillgraph(this.global);
    this.nfrag = s.splitgraph(this.global);

    for(int i=0;i<this.nfrag;++i) {
      HashMap<Integer, node> nodelist=new HashMap<>();
      HashMap<Integer, edge> edgelist=new HashMap<>();
      for(node N: global.getnodes()){
        if(i != N.getfid())
          continue;
        nodelist.put(N.getvid(), N);
      }
      for(edge E: global.getedges()){
        if(global.iscrossedge(E)){
          global.setboundarywithoutgoing(E.getsrc());
          global.setboundarywithincoming(E.getdst());
          continue;
        }
        if(global.getdst(E).getfid() != i)
          continue;
        edgelist.put(E.geteid(), E);
      }
      try{
        frag newfrag=new frag(i,global, nodelist,edgelist);
        this.fragments.add(newfrag);
      }
      catch(nosuchnode n){
        n.diag();
        System.exit(1);
      }

    }
  }
  public ArrayList<frag> getfragments(){
    return this.fragments;
  }

  public frag getfragment(int fid){
    return fragments.get(fid);
  }

  public graph getglobal(){
    return global;
  }


  public static void main(String [] args)  throws FileNotFoundException {
    parser p=new gparser ("simple.v","simple.e");
    splitter s=new splitter("simple.r");
    loader l=new loader (p, s);
    squery q=new squery ("sssp-query");

    System.out.println(l.getglobal());
    for(frag f: l.getfragments()){
      System.out.println(f);
    }
    System.out.println(q.loadquery());

  }

}
