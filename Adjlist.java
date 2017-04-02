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

import java.lang.*;
import java.util.*;

public interface Adjlist{
  public Collection<Integer> getparents(int vid);
  public Collection<Integer> getchildren(int vid);
  public Iterable<edge> getoutgoing(int vid);
  public Iterable<edge> getincoming(int vid);
  public int indegree(node N);
  public int outdegree(node N);
  public node getdst(edge e);
  public node getsrc(edge e);
  public node getnode(int vid);
  public edge getedge(int eid);
  public Collection<node> getnodes();
  public Collection<edge> getedges();
  public void buildsignature();
}
