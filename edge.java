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

public class edge{
        private int src, dst;
        private int eid;
        private int attr;

        public edge(int src, int dst, int attr){
                this.eid=-1;
                this.src=src;
                this.dst=dst;
                this.attr=attr;
        }

        public edge(int eid){
                this.eid=eid;
                this.src=-1;
                this.dst=-1;
                this.attr=0;
        }

        public edge(int eid, int src, int dst, int attr){
                this.eid=eid;
                this.src=src;
                this.dst=dst;
                this.attr=attr;
        }

        public String toString(){
                String s=String.format("(%d) -[:%d]-> (%d)",
                                src, attr, dst);
                return s;
        }

        public void seteid(int eid){
                this.eid=eid;
        }

        public int getsrc(){
                return this.src;
        }

        public int getattr(){
                return this.attr;
        }
        
        public int geteid(){
                return this.eid;
        }
        public int getdst(){
                return this.dst;
        }

        
        public static void main(String [] args) {
                edge rel=new edge(1,0,4,10);
                System.out.println(rel);
        }
}


