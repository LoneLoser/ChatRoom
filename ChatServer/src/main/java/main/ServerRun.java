package main;

import communication.EchoServer;

/**
 * 服务端运行类
 * @author Administrator
 */
public class ServerRun {
	
	public static void main(String[] args) {
		try {
            new EchoServer().start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	
}
