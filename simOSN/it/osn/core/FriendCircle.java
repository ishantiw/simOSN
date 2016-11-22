package it.osn.core;
/**
 * <h1>Friend Circle!</h1> 
 * This class has the user data object that will be used 
 * to hold variables maintained by a node locally
 * <p>
 * 
 * @author Ishan Tiwari
 * @version 1.0
 * @since 04.08.2016
 * @modified 21.11.2016
 */
import java.util.ArrayList;
import java.util.List;

import peersim.core.Network;

public class FriendCircle {
	public int size;
	public int flag;
	protected int id;
	protected List<String> locations = new ArrayList<String>();

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

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
		return "FriendCircle [size=" + size + ", flag=" + flag + ", userdata=" + userdata + "]";
	}

	public void setUserdata(UserData user) {
		this.userdata = user;
	}

	public FriendCircle(UserData userdata) {
		super();
		// this.size = (int) (Math.random() * Network.size());
		this.userdata = userdata;
		this.flag = 0;
	}

}
