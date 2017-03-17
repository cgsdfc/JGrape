/* -*- mode: Java -*-  */
/* vim:set ts=8 sw=2 sts=2 et: */
/* 
 * class: mbuffer
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

public class mbuffer<M> {
        ArrayList<M> inputm;
        ArrayList<ArrayList<M>> outputm;

        public String toString(){
                StringBuilder sb=new StringBuilder("");
                sb.append("\n======================\nIN-MESSAGE:\n");
                for(M in: inputm){
                        sb.append(in.toString());
                        sb.append("\n");
                }

                sb.append("OUT-MESSAGE:\n");
                int i=0;
                for(ArrayList<M> outs: outputm){
                        sb.append(String.format("[%d]:", i++));
                        for(M m: outs){
                                sb.append(m.toString());
                                sb.append(" ");
                        }
                        sb.append("\n");
                }
                return sb.toString();
        }


        public mbuffer(int nfrag){
                this.inputm=new ArrayList<>();
                this.outputm=new ArrayList<>();
                for(int i=0;i<nfrag;++i){
                        this.outputm.add(new ArrayList<>());
                }
        }

        public void inclear(){
                this.inputm.clear();
        }

        public void outclear(){
                for(ArrayList<M> out: this.outputm){
                        out.clear();
                }
        }

        public void addinmsg(M m){
                this.inputm.add(m);
        }

        public void addoutmsg(int fid, M m){
                this.outputm.get(fid).add(m);
        }

        public ArrayList<ArrayList<M>> getoutmsg(){
                return this.outputm;
        }

        public ArrayList<M> getinmsgs(){
                return this.inputm;
        }

        public int inmsgsize(){
                return this.inputm.size();
        }


        public static void main(String [] args) {
                mbuffer<Integer>  messageBuffer = new mbuffer<> (4);
                for(int i=0;i<4;++i){
                        messageBuffer.addinmsg(i);
                        messageBuffer.addoutmsg(0, i);
                }
                System.out.println(messageBuffer);
        }



}

