package vo;

public class NodeVO {
	private int idx, current_owner, basic_reward, node_exp;
	private double gps_x, gps_y;
	private String node_name, node_img, capture_time;
	
	
	public int getCurrent_owner() {
		return current_owner;
	}
	public void setCurrent_owner(int current_owner) {
		this.current_owner = current_owner;
	}
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	
	public int getBasic_reward() {
		return basic_reward;
	}
	public void setBasic_reward(int basic_reward) {
		this.basic_reward = basic_reward;
	}
	public int getNode_exp() {
		return node_exp;
	}
	public void setNode_exp(int node_exp) {
		this.node_exp = node_exp;
	}
	public double getGps_x() {
		return gps_x;
	}
	public void setGps_x(double gps_x) {
		this.gps_x = gps_x;
	}
	public double getGps_y() {
		return gps_y;
	}
	public void setGps_y(double gps_y) {
		this.gps_y = gps_y;
	}
	public String getNode_name() {
		return node_name;
	}
	public void setNode_name(String node_name) {
		this.node_name = node_name;
	}
	public String getNode_img() {
		return node_img;
	}
	public void setNode_img(String node_img) {
		this.node_img = node_img;
	}
	public String getCapture_time() {
		return capture_time;
	}
	public void setCapture_time(String capture_time) {
		this.capture_time = capture_time;
	}
	
	
}
