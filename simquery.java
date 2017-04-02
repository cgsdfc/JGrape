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
import java.io.*;
public class simquery implements query<Adjlist> {
  private ArrayList <Adjlist> pattern;
  private Scanner qs;

  public ArrayList<Adjlist> loadquery () {
    return pattern;
  }

  public simquery (String query) throws FileNotFoundException {
    this.qs= new Scanner (new FileReader (query));
    this.pattern=new ArrayList<>();
    graph p=new graph();
    int numberOfEdges=0;

    while(qs.hasNext()){
      String [] words=qs.nextLine().split("\\s");
      if (words.length < 3) {
        System.err.println("a line of a pattern file should contain at least 3 words!");
        System.exit(1);
      }
      // from, attr, to...
      
      int from=Integer.valueOf(words[0]);
      int nodeattr=Integer.valueOf(words[1]);
      p.addnode(new node(from, nodeattr));

      for (int i=2;i<words.length;++i) {
        int to=Integer.valueOf(words[i]);
        p.addedge(new edge(numberOfEdges++, from, to, 0));
      }
    }
    try{
      p.buildadjlist();
      this.pattern.add(p);
    }
    catch (nosuchnode n) {
      n.diag();
      System.exit(2);
    }
  }
  public static void main(String [] args) throws FileNotFoundException {
    simquery q=new simquery("sim/twitter.q");
  } 
  
}


