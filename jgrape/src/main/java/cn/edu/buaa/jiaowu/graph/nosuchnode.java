package jgrape.src.main.java.cn.edu.buaa.jiaowu.graph;
@SuppressWarnings("serial")
public class nosuchnode extends grapherror {
  private int vid;
  public void diag(){
    System.err.printf("Invalid vid %d%n", vid);
  }

 public nosuchnode(int vid){
    this.vid=vid;
  }
}
