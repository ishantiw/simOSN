package it.osn.core;
import peersim.core.Network;

public class FriendCircle {
	public int size;
	
	public UserData user;

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public UserData getUser() {
		return user;
	}

	@Override
	public String toString() {
		return "FriendCircle [size=" + size + ", user=" + user + "]";
	}

	public void setUser(UserData user) {
		this.user = user;
	}

	public FriendCircle(UserData user) {
		super();
		this.size = (int) (Math.random() * Network.size());
		this.user = user;
	}
	
	
	
}
