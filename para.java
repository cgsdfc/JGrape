
/* -*- mode: Java -*-  */
/* vim:set ts=8 sw=2 sts=2 et: */
/* 
 * class: para
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

import java.util.ArrayList;
import java.io.*;

public class para{

  private static final int CMD_NUM = 6;
  public static void main(String [] args) {
    try{
      if(args.length != CMD_NUM){
        System.err.printf("Usage: java para algorithm graph vfile efile rfile query%n");
        System.exit(1);
      }

      // get the actual arguments
      String argv_a=args[0];
      String argv_g=args[1];
      String argv_v=args[2];
      String argv_e=args[3];
      String argv_r=args[4];
      String argv_q=args[5];
      String argv_p=argv_a;

      // new the objects
      parser para_p=new gparser(argv_v, argv_e);
      splitter para_s=new splitter(argv_r);
      loader para_l=new loader(para_p, para_s);

      
      if (argv_a.equals("sssp")) {
        query<Integer> para_q=new squery(argv_q);
        worker<pairmsg, Integer> para_w=new sssp( para_l.getfragments(), para_q.loadquery());
        para_w.setparameter(argv_a, argv_g, argv_p);
        para_w.pararun();
        para_w.write();
      }


      else if (argv_a.equals("bfs")){
        query<pairmsg> para_q=new bquery(argv_q);
        worker<pairmsg, pairmsg> para_w=new bfs( para_l.getfragments(), para_q.loadquery());
        para_w.setparameter(argv_a, argv_g, argv_p);
        para_w.pararun();
        para_w.write();
      }



      else if (argv_a.equals("cc")){
        ArrayList<Object> dummyQ=new ArrayList<> ();
        dummyQ.add(null);
        worker<pairmsg, Object> para_w=
          new cc( para_l.getfragments(), dummyQ);       
        para_w.setparameter(argv_a, argv_g, argv_p);
        para_w.pararun();
        para_w.write();
      }


      else {
        System.err.println("Unsupported Algorithm " + argv_a);
        System.exit(1);
      }
    }
    
    catch(FileNotFoundException f){
      System.err.println("file not found!!");
      System.exit(1);
    }

  }
}
