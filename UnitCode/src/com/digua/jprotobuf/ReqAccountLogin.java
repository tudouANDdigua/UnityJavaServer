package com.digua.jprotobuf;
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;//注解

public class ReqAccountLogin {
	//oder:字段的顺序；
	//require:true 这个字段必须要有数据 反之 可以不用数据
	//fieldType:我们的数据描述的类型，=====》protobuf
	
	@Protobuf(order = 1, required = true, fieldType = FieldType.INT32)
	public long accountId;
	
	@Protobuf(order = 2, required = true)
	public String password;
}
