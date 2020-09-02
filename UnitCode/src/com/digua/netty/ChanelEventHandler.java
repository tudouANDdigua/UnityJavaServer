package com.digua.netty;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ChanelEventHandler extends ChannelInboundHandlerAdapter{
	private final static Logger logger = LoggerFactory.getLogger(ChanelEventHandler.class);
	
	// channel 连接已经建立
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		
		String ip = ((InetSocketAddress)channel.remoteAddress()).getAddress().toString().substring(1);
		logger.info(ip + " enter");
	}
	
	// 当channel有数据可读的时候， channelRead
	// 不要再IO线程里面来处理业务逻辑;
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Channel channel = ctx.channel();
		
		String ip = ((InetSocketAddress)channel.remoteAddress()).getAddress().toString().substring(1);
		logger.info(ip + ":" + (String) msg);
		
		// test
		channel.writeAndFlush(msg);
	}
	
	// channel不可用的时候，channelInactive
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		String ip = ((InetSocketAddress)channel.remoteAddress()).getAddress().toString().substring(1);
		logger.info(ip + ": exit");
	}
	
	// channel数据通讯中断以后，我们就会调用;
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Channel channel = ctx.channel();
		
		if (channel.isActive() || channel.isOpen()) {
			String ip = ((InetSocketAddress)channel.remoteAddress()).getAddress().toString().substring(1);
			logger.info(ip + " close channel");
			ctx.close();
		}
		if (!(cause instanceof IOException)) {
			logger.error("remote:" + channel.remoteAddress(), cause);
		}
	}
}
