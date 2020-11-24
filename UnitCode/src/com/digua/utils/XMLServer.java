package com.digua.utils;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.Element;

@Root(name = "server")
public class XMLServer {
	/** 服务器id */
	@Element(required = true)
	public int serverId;
	/** 服务器端口 */
	@Element(required = true)
	public int serverPort;

	/** 后台管理端口 */
	@Element(required = true)
	public int adminPort;
	/** 后台白名单模式 */
	@Element(required = true)
	public String whiteIps;

	/** 匹配服http地址 */
	@Element(required = true)
	public String matchUrl;
	/** 本服是否為跨服 */
	@Element(required = true)
	public boolean fight;
	/** 对外跨服端口 */
	@Element(required = true)
	public int crossPort;

	/** redis server url {http:port} */
	@Element(required = true)
	public String redisUrl;
}
