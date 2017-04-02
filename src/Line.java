/**
 * Created by HandsomeMrChen on 2017/4/1.
 */
public class Line {
    //边的左右节点（平等）
    private Node left;
    private Node right;
    private boolean isBusy = false;                         // 在时间段内是否被占用

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    Line(Node left, Node right){
        this.left = left;
        this.right = right;
    }
}
