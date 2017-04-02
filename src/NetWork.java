import java.util.ArrayList;
import java.util.HashMap;

public class NetWork {

    private static boolean isAccept = false;                                  // 此字段表示目标节点是否接收过数据包
    private static int time = 0;                                              // 时间线
    private static HashMap<String, Line> lines = new HashMap();               // 边
    private static ArrayList<Node> nodes = new ArrayList<>();                 // 节点

    public static boolean isAccept() {
        return isAccept;
    }

    static void setIsAccept(boolean isAccept) {
        NetWork.isAccept = isAccept;
    }

    public static int getTime() {
        return time;
    }

    static void running(){      //时间流逝
        time ++;
    }

    static void reset(){       //重置网络
        isAccept = false;
        time = 0;
    }

    static void link(Node left, Node right){                //节点1与2之间边的key为：(1,2)
        lines.put("("+left.getName()+","+right.getName()+")", new Line(left,right));
    }

    public static void main(String[] args) {
        defaultInitial();
    }

    /* 默认的初始化方式，创建一个有10个结点的网络
    *  从Router1出发到Router7的情况
    * */
    static void defaultInitial(){
        for(int i = 1; i <= 15; i++){
            nodes.add(new Node(Integer.toString(i)));
        }

        //顺序添加保证邻接表有序
        nodes.get(0).link(nodes.get(1), nodes.get(2), nodes.get(5));        // 节点1
        nodes.get(1).link(nodes.get(2), nodes.get(9));                      // 节点2
        nodes.get(3).link(nodes.get(5));                                    // 节点4
        nodes.get(4).link(nodes.get(5));                                    // 节点5
        nodes.get(5).link(nodes.get(7));                                    // 节点6
        nodes.get(6).link(nodes.get(7));                                    // 节点7
        nodes.get(7).link(nodes.get(8), nodes.get(9), nodes.get(14));       // 节点8
        nodes.get(8).link(nodes.get(10), nodes.get(11));
        nodes.get(9).link(nodes.get(13));
        nodes.get(10).link(nodes.get(12));
        nodes.get(11).link(nodes.get(12));

        System.out.println("设置节点1为出发节点，节点7为目标节点,拓扑图中节点1到节点7的距离为3，网络直径为6");
        nodes.get(6).setEnd(true);
        System.out.println("\nRun 1：设置跳跃数为节点1到节点7的距离，即为3\n运行结果：");
        nodes.get(0).accept(new Packet(3));
        nodes.get(0).prepareToSend();
        work();

        reset();                           //重置：将网络收到目标包的标志置为false
        System.out.println("\nRun 2：设置跳跃数为网络直径，即为6\n运行结果：");
        nodes.get(0).accept(new Packet(6));
        nodes.get(0).prepareToSend();
        work();
    }

    static HashMap<String, Line> getLines() {
        return lines;
    }

    static ArrayList<Node> getNodes() {
        return nodes;
    }

    static void work(){
        while(true){
            running();
            boolean isContinue = false;
            for(Node node : nodes){         //轮询所有节点，查看是否又需要传输数据包的节点
                if(node.work())
                    isContinue = true;
            }
            for(Line line : lines.values()){
                line.setBusy(false);
            }
            if(!isContinue) break;
        }

    }

}
