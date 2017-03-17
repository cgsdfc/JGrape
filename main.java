/* -*- mode: Java -*-  */
/* vim:set ts=8 sw=2 sts=2 et: */
/* 
 * class: main
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

public class main{

  private static final int CMD_NUM = 6;
  public static void main(String [] args) {
    try{
      if(args.length != CMD_NUM){
        throw new cmdargsnumberr(args.length, CMD_NUM);
      }

      String a=args[0];
      String g=args[1];
      String v=args[2];
      String e=args[3];
      String r=args[4];
      String q=args[5];

      parser<Integer> p=new gparser (v, e, q);
      splitter s=new splitter(r);
      loader<Integer> l=new loader<> (p, s);
      sssp ssspworker = new sssp(l.getfragments(), l.getquery());
      ssspworker.run();
      teller t = new teller(ssspworker.getfinalresult(), g, a, ssspworker. getquery());
      t.write();
    }
    catch(cmdargsnumberr c) {
      System.err.printf("Usage: java main algorithm graph vfile efile rfile query%n");
      c.diag();
      System.exit(1);
    }
    catch(FileNotFoundException f){
      System.err.println("file not found!!");
      System.exit(1);
    }

  }
}
