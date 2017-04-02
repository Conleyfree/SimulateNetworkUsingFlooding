import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by HandsomeMrChen on 2017/3/31.
 */
public class Node {

    private String name;                                    // 结点名称
    private boolean isEnd = false;                          // 是否结束节点
    private Set<Node> relativeNodes = new HashSet<Node>();     // 该节点相邻节点集

    private ArrayList<Packet> accepts = new ArrayList<>();  // 某一个时刻点收到的包
    private ArrayList<Packet> willsends = new ArrayList<>(); // 某一实际将要寻求发送的包
    private ArrayList<Packet> wait4sends = new ArrayList<>();// 某一个由于线路被占用而无法发送的包

    public Node(String name) {
        this.name = name;
    }

    public void link(Node... nodes) {
        for (Node node : nodes) {
            this.relativeNodes.add(node);
            node.getRelativeNodes().add(this);
            NetWork.link(this, node);
        }
    }

    public Set<Node> getRelativeNodes() {
        return relativeNodes;
    }

    /* 接受从其他节点发来的包 */
    public void accept(Packet packet) {
        // 记录当前节点
        if(packet.getRoute().contains(this.name)){      //判断该包是否到过本结点
            packet.getRoute().add(this.name);           //在包里面的路径加上本节点
            System.out.print("传输失败，本结点收到重复的包: " + packet);
            System.out.println("; 当前所在结点：" + this.name + "; 当前时间：" + NetWork.getTime() + "s.");

        }else{
            packet.getRoute().add(this.name);   //在包里面的路径加上本节点
            this.accepts.add(packet);           //添加到此时刻接收到的包的集合中

            // 如果计数器仍然等于零 或 当前节点已经是最终节点，则打印路由信息
            // 否则继续传输，否则输出报文传输路径
            if (this.isEnd) {
                if( !NetWork.isAccept()){                       //之前目标结点没有接收到包
                    System.out.println("传输成功: " + packet + "; 当前时间：" + NetWork.getTime() + "s.");
                    NetWork.setIsAccept(true);
                }else{
                    System.out.println("目标结点发现重复的数据包！该包传输路线：" + packet + "; 当前时间：" + NetWork.getTime() + "s.");
                }
                accepts.clear();    //不需要转发了

            }
        }
    }

    /* 节点接到一个包或者产生了一个包(后者对出发节点)准备发出到网络 */
    public void prepareToSend(){
        willsends.clear();                   // 清空上一时刻要发出的数据包
        willsends.addAll(wait4sends);         // 加入上一时刻未发出的数据包
        willsends.addAll(accepts);           // 加入上一时刻接收到的数据包
        accepts.clear();                    // 清空上一时刻接收到的数据包
        wait4sends.clear();                  // 清空上一时刻未发出的数据包
    }

    /* 把这一时刻需要发送的包全部发出，通道被占用的包放入wait4send里面 */
    public void send(){
        for(Packet packet : willsends) {
            if (packet.getCounter() == 0) {
                System.out.print("传输失败，已超出生命周期: " + packet);
                System.out.println("; 当前所在结点：" + this.name + "; 当前时间：" + NetWork.getTime() + "s.");
            } else {
                packet.decrement();
                boolean isAvailableNodeExist = false;

                if(packet.getNext() != null){       // 这是之前未发出的包，需要定向发送
                    if (!packet.willSend(this, packet.getNext())){
                        Packet duplication = packet.clone();
                        duplication.readdback();                         // 由于阻塞没办法发出的，需要把前面多减去的那一跳加回来
                        duplication.setNext(packet.getNext());           // 需要设置该包要传递的方向
                        wait4sends.add(duplication);
                    }else{
                        packet.getNext().accept(packet.clone());
                    }
                    continue;   //这轮循环后面的代码不需要执行了
                }

                for (Node nextNode : relativeNodes) {   // 这是这一时刻刚接到的包，广播给所有与本结点邻接的结点

                    if (packet.getRoute().size() == 1 ||
                            !nextNode.getName().equals(packet.getRoute().get(packet.getRoute().size() - 2))) {      //不会往刚传过来的结点传包
                        isAvailableNodeExist = true;
                        //nextNode.accept(packet.clone());  //想办法不用递归
                        if (!packet.willSend(this, nextNode)) {      // 要发送的路径已经被占用
                            Packet duplication = packet.clone();
                            duplication.readdback();                 // 由于阻塞没办法发出的，需要把前面多减去的那一跳加回来
                            duplication.setNext(nextNode);           // 需要设置该包要传递的方向
                            wait4sends.add(duplication);
                        } else {
                            nextNode.accept(packet.clone());
                        }

                    }//end outer if
                }
                if (!isAvailableNodeExist) {
                    System.out.print("传输失败，无法找到下一结点: " + packet);
                    System.out.println("; 当前所在结点：" + this.name + "; 当前时间：" + NetWork.getTime() + "s.");
                }
            }
        }// end of outer for
    }

    public void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public String getName() {
        return this.name;
    }

    public boolean work(){
        if(accepts.size() == 0 && willsends.size() == 0 && wait4sends.size() == 0)     return false;   //不需要工作了

        if(willsends.size() != 0){
            send();                 // 发送数据包
            willsends.clear();       // 清空待发出数据包集合
        }
        prepareToSend();            // 发送完后把这一时刻未能发送出去的包，以及收到的包，放入待发送集合中准备下一刻发送

        return true;
    }
}
