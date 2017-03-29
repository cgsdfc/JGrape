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
public class bquery implements query<pairmsg> {
  private ArrayList<pairmsg> queries;
  private Scanner qs;

  public ArrayList<pairmsg> loadquery(){
    return this.queries;
  }

  public bquery (String queryfile) throws FileNotFoundException {
    this.qs=new Scanner (new FileReader (queryfile) );
    this.queries=new ArrayList<> ();
    int [] val = new int [2];

    while (qs.hasNext()) {
      val[0]=qs.nextInt ();
      val[1]=qs.nextInt();
      queries.add(new pairmsg (val[0], val[1]));
    }
  }

  public static int getbound(pairmsg p){
    return p.getvalue();
  }


  public static int getroot(pairmsg p){
    return p.getvid();
  }

}
