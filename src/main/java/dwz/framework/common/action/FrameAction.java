package dwz.framework.common.action;

public class FrameAction extends BaseAction {

    private static final long serialVersionUID = -6451883417612807359L;
    private String num;
    
    public String serial() {
        return INPUT;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
