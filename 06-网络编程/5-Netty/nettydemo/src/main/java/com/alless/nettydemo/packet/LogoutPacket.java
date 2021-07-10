package com.alless.nettydemo.packet;
import com.alless.nettydemo.DataBuffer;
import com.mogujie.tt.proto.ProtoGlobal;

public class LogoutPacket extends BasePacket {

		private Header mLoginHeader;

		public LogoutPacket(int userId){

			mLoginHeader = new Header.Builder(ProtoGlobal.CLIENT_PROTO,(short)0,
					ProtoGlobal.AUT_REQ_LOGOUT).setFromUserId(userId).setToUerId(0).build();
			
		}

		//将数据转化为二进制字节   在发送时会促发调用这个函数
		@Override
		public DataBuffer encode() {
			DataBuffer headerBuffer = mLoginHeader.encode();
	        return headerBuffer;
	}


}
