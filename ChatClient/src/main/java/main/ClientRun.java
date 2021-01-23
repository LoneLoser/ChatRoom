package main;

import communication.EchoClient;

/**
 * 客户端运行类
 * @author Administrator
 */
public class ClientRun {

	public static void main(String[] args) {
		try {
			new EchoClient("127.0.0.1", 8001).start();
			//new EchoClient("192.168.0.5", 8001).start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
