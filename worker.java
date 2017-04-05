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
  protected ArrayList<Q> queries;
  protected ArrayList<frag> fragments;
  protected int nfrag;
  protected int maxstep;
  protected String prefix, algorithm, graphname;
  protected Thread [] threads;

  public static final int MAX_STEP=100;

  protected abstract void peval(frag fragment, Q query, mbuffer<M> message);
  protected abstract void inceval(frag fragment, Q query, mbuffer<M> message);
  protected abstract void assemble();
  protected abstract void write();
  protected abstract void clear();


  protected ArrayList<Q> getqueries(){
    return this.queries;
  }

  public void setparameter (String algorithm, String graphname, String prefix) {
    this.algorithm=algorithm;
    this.prefix=prefix;
    this.graphname=graphname;
  }


  protected worker(ArrayList<frag> fragments, ArrayList<Q> queries){
    this.nfrag=fragments.size();
    this.messages=new ArrayList<>(nfrag);
    this.fragments=fragments;
    this.queries=queries;
    this.maxstep=MAX_STEP;
    this.algorithm=null;
    this.graphname=null;
    this.threads=new Thread[this.nfrag];

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

  class workerthread<M,Q> extends Thread {
    private worker<M,Q> w;
    private int superstep, WID;
    private Q q;

    public void run () {
      if (superstep == 0) {
        w.peval(w.fragments.get(WID),this.q,w.messages.get(WID));
      }
      else {
        w.inceval(w.fragments.get(WID),this.q,w.messages.get(WID));
      }
    }

    public workerthread (worker<M,Q> w, int superstep, int WID, Q q) {
      this.w=w;
      this.superstep=superstep;
      this.WID=WID;
      this.q=q;
    }
  }


  private void sandbox  (int superstep, int WID, Q q) throws NullPointerException{
      workerthread<M,Q> t=new workerthread<> (this, superstep, WID, q);
      t.start();
      this.threads[WID]=t;
  }

  protected void pararun(){
    for(Q q: this.queries) {
      for(int i=0;i<this.maxstep;++i){
        try {
          for(int j=0;j<this.nfrag;++j){
            this.sandbox(i,j,q);
          }
          for(int j=0;j<this.nfrag;++j){
            this.threads[j].join();
          }
        }
        catch (Throwable t) {
          t.printStackTrace();
          System.exit(1);
        }
        this.exchange();
        if(this.isfinished()) {
          break;
        }
      } 
      this.assemble();
      this.clear();
    }
  }


  protected void run(){
    for(Q q: this.queries) {
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
            n.printStackTrace();
            System.exit(1);
          }

        }
        this.exchange();
        if(this.isfinished()) {
          break;
        }
      }
      this.assemble();
      this.clear();
    }

  }

  protected void exchange(){
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
