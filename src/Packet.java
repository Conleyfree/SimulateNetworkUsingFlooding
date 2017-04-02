import java.util.ArrayList;
import java.util.List;

/**
 * Created by HandsomeMrChen on 2017/3/31.
 */
public class Packet implements Cloneable{

    // 计数器
    private int counter;
    // 传输路径
    private ArrayList<String> route = new ArrayList<String>();
    // 下一个发往路由的名字，只有在包的传输线路上被阻塞时才有用，其他时候置为null
    private Node next = null;

    public Packet(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public List<String> getRoute() {
        return route;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void decrement() {
        this.counter = this.counter - 1;
    }
    public void readdback() {
        this.counter ++;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Packet clone() {
        Packet result = null;
        try {
            result = (Packet) super.clone();
            result.route = (ArrayList<String>) this.route.clone();
            result.next = null;         //把下一节点的名字置为null，因为在下一节点并不知道这个包会发到哪里
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("报文的传输路径为: %s", route);
    }

    /* 数据包发送方法 */
    public boolean willSend(Node sender ,Node receiver){
        Line line = NetWork.getLines().get("("+sender.getName()+","+receiver.getName()+")");
        if(line == null)    line = NetWork.getLines().get("("+receiver.getName()+","+sender.getName()+")");

        if(!line.isBusy()){
            line.setBusy(true);
            return true;
        }
        return false;
    }
}
