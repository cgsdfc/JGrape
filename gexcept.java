/* -*- mode: Java -*-  */
/* vim:set ts=8 sw=2 sts=2 et: */
/* 
 * class: gexcept
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

@SuppressWarnings("serial")
class gexcept extends Throwable {}

@SuppressWarnings("serial")
class cmdargsnumberr extends gexcept {
  int formal, actual;
  cmdargsnumberr(int a, int f){
    formal=f;
    actual=a;
  }
  void diag(){
    System.err.printf("Command line arguments mismatched: required %d, actual %d%n", formal, actual);
  }
}

@SuppressWarnings("serial")
class grapherror extends gexcept {}

@SuppressWarnings("serial")
class nosuchnode extends grapherror{
  private int vid;

  void diag(){
    System.err.printf("Invalid vid %d%n", vid);
  }

  nosuchnode(int vid){
    this.vid=vid;
  }
}

