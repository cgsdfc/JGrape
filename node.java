/* -*- mode: Java -*-  */
/* vim:set ts=8 sw=2 sts=2 et: */
/* 
 * class: node
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
import java.util.HashSet;

public class node{
        private int vid,fid;
        private int attr;
        private boolean isboundarywithincoming, isboundarywithoutgoing;


        public boolean isboundarywithincoming(){
                return this.isboundarywithincoming;
        }

        public boolean isboundarywithoutgoing(){
                return this.isboundarywithoutgoing;
        }

        public void setboundarywithincoming(){
                this.isboundarywithincoming=true;
        }

        public void setboundarywithoutgoing(){
                this.isboundarywithoutgoing=true;
        }

        private void init(){
                this.fid=-1;
                this.isboundarywithincoming=false;
                this.isboundarywithoutgoing=false;
                this.attr=0;

        }

        public node(int vid, int attr){
                init();
                this.vid=vid;
                this.attr=attr;
        }
        
        public node(int vid){
                init();
                this.vid=vid;
        }


        public void setfid(int fid){
                this.fid=fid;
        }

        public int getfid(){
                return this.fid;
        }
        public int getvid(){
                return this.vid;
        }

        public String toString(){
                String s=String.format("(%d)", vid);
                return s;
        }


        public static void main(String [] args) {
                node node=new node(1,2);
                System.out.println(node);
        }
}
