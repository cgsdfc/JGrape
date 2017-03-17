/* -*- mode: Java -*-  */
/* vim:set ts=8 sw=2 sts=2 et: */
/* 
 * class: gparser
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

public class gparser implements parser<Integer> {
        
        private Scanner vs,es,qs;

        public ArrayList<Integer> loadquery(){
                ArrayList<Integer> q=new ArrayList<>();
                while(qs.hasNext()){
                        int i=qs.nextInt();
                        q.add(i);
                }
                return q;
        }

        public void fillgraph(graph g){
                fillnodes(g);
                filledges(g);
                try{
                        g.buildadjlist();
                }
                catch(nosuchnode n){
                        n.diag();
                }
        }

        public void  fillnodes(graph g){
                int content[] = new int[2];

                while(vs.hasNext()){
                        try{
                                for(int i=0;i<2;++i){
                                        content[i]=vs.nextInt();
                                }
                                g.addnode(new node(content[0], content[1]));
                        }
                        catch(InputMismatchException i){
                                continue;
                        }
                }
        }

        public void  filledges(graph g){
                int content[] = new int[3];
                int en=0;
                while(es.hasNext()){
                        try{
                                for(int i=0;i<3;++i){
                                        content[i]=es.nextInt();
                                }
                                g.addedge( new edge(en++, content[0], content[1], content[2]));
                        }
                        catch(InputMismatchException i){
                                continue;
                        }
                }
        }


        public gparser(String vfile, String efile, String query) throws FileNotFoundException {
                this.vs=new Scanner(new FileReader(vfile));
                this.es=new Scanner(new FileReader(efile));
                this.qs=new Scanner(new FileReader(query));
        } 

        public static void main(String [] args) throws FileNotFoundException {
                gparser gp=new gparser("simple.v", "simple.e", "simple.q");
                graph g=new graph();
                gp.fillgraph(g);
                System.out.println(g);

                ArrayList<Integer> query=gp.loadquery();
                System.out.println(query);
        }
}
