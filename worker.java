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
public abstract class worker<M,Q> {

  protected ArrayList<mbuffer<M>> messages;
  protected ArrayList<Q> query;
  protected ArrayList<frag> fragments;
  protected int nfrag;
  protected int maxstep;

  public static final int MAX_STEP=100;

  protected abstract void peval(frag fragment, Q query, mbuffer<M> message);
  protected abstract void inceval(frag fragment, Q query, mbuffer<M> message);
  protected abstract void assemble();


  protected ArrayList<Q> getquery(){
    return this.query;
  }

  protected worker(ArrayList<frag> fragments, ArrayList<Q> query){
    this.nfrag=fragments.size();
    this.messages=new ArrayList<>(nfrag);
    this.fragments=fragments;
    this.query=query;
    this.maxstep=MAX_STEP;

    for(int i=0;i<nfrag;++i) {
      this.messages.add(new mbuffer<M> (nfrag));
    }
  }

  protected boolean isfinished(){
    int halt=0;
    for(int i=0;i<this.nfrag;++i){
      if(this.messages.get(i).inmsgsize() == 0)
        ++halt;
    }
    return halt == this.nfrag;
  }

  protected void run(){
    for(Q q: this.query) {
      for(int i=0;i<this.maxstep;++i){
        for(int j=0;j<this.nfrag;++j){
          try{
            if(i==0){
              this.peval(fragments.get(j), q, messages.get(j));
            }
            else{
              this.inceval(fragments.get(j),q, messages.get(j));
            }
          }
          catch(NullPointerException n){
            System.err.println(n);
            System.exit(1);
          }

        }
        this.sync();
        if(this.isfinished()) {
          break;
        }
      }
      this.assemble();
    }

  }

  protected void sync(){
    for(int i=0;i<this.nfrag;++i){
      this.messages.get(i).inclear();
    }
    for(int i=0;i<nfrag;++i) {
      int fid=0;
      for(ArrayList<M> outs: this.messages.get(i).getoutmsg()){

        for(M out: outs){
          this.messages.get(fid).addinmsg(out);
        }   
        fid++;
      }
      messages.get(i).outclear();
    }

  }

}
