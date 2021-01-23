package communication;

import java.util.ArrayList;
import io.netty.channel.Channel;

/**
 * 私聊对象缓存
 * @author Administrator
 */
public class EchoCache {
	
	/**
	 * 内部类（用户名与channel映射）
	 * @author Administrator
	 */
	private class ChannelMap {
		
		private String channelName;
		private Channel channel;

		public ChannelMap(String channelName, Channel channel) {
			this.channelName = channelName;
			this.channel = channel;
		}
		
		public String getChannelName() {
			return channelName;
		}
		
		public void setChannelName(String channelName) {
			this.channelName = channelName;
		}
		
		public Channel getChannel() {
			return channel;
		}
		
		public void setChannel(Channel channel) {
			this.channel = channel;
		}
		
	}
	
	private ArrayList<ChannelMap> channelCache; //私聊对象列表
	private int size, capacity; //大小，容量
	
	public EchoCache(int capacity) {
		size = 0;
		this.capacity = capacity;
		channelCache = new ArrayList<ChannelMap>(capacity);
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public int getSize() {
		return size;
	}

	/**
	 * 增加最近的记录缓存
	 * @param channelName
	 * @param channel
	 */
	public void add(String channelName, Channel channel) {
		ChannelMap channelMap = new ChannelMap(channelName, channel);
		if(size == capacity) {
			channelCache.remove(0);
		}
		channelCache.add(channelMap);
	}
	
	/**
	 * 根据用户名查找通道
	 * @param channelName
	 * @return
	 */
	public Channel findChannel(String channelName) {
		for(ChannelMap chm : channelCache) {
			if(channelName.equals(chm.getChannelName()))
				return chm.getChannel();
		}
		return null;
	}	
	
}
