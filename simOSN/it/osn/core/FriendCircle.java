package it.osn.core;
import peersim.core.Network;

public class FriendCircle {
	public int size;
	
	public UserData userdata;

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public UserData getUserdata() {
		return userdata;
	}

	@Override
	public String toString() {
		return "FriendCircle [size=" + size + ", user=" + userdata + "]";
	}

	public void setUserdata(UserData user) {
		this.userdata = user;
	}

	public FriendCircle(UserData userdata) {
		super();
		//this.size = (int) (Math.random() * Network.size());
		this.userdata = userdata;
	}
	
	
	
}
