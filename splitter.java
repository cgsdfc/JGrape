/* -*- mode: Java -*-  */
/* vim:set ts=8 sw=2 sts=2 et: */
/* 
 * class: splitter
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
public class splitter {
        private Scanner rs;
        public static final int nfrag=4;

        splitter(String path) throws FileNotFoundException {
                this.rs=new Scanner(new FileReader(path));
        }

        public int splitgraph(graph that){
                int content[] = new int [2];
                int nfrag=-1;
                while(rs.hasNext()){
                        try{
                                for(int i=0;i<2;++i) {
                                        content[i]=rs.nextInt();
                                }
                                int fid=content[1];
                                that.setfid(content[0], content[1]);
                                if(fid > nfrag){
                                        nfrag=fid;
                                }
                                
                        }
                        catch(InputMismatchException i){
                                continue;
                        }
                        catch(nosuchnode n){
                                continue;
                        }
                }
                return nfrag+1;
        }
}

