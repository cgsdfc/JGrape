/* -*- mode: Java -*-  */
/* vim:set ts=8 sw=2 sts=2 et: */
/* 
 * class: parser
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

public interface parser<Q> {
        public void fillgraph(graph g);
        public ArrayList<Q> loadquery();
}
