package com.alless.nettydemo.packet;

import com.alless.nettydemo.DataBuffer;
import com.mogujie.tt.proto.BBProtocol;

public class LoginPacketOther extends BasePacket {
	
	private Header mLoginHeader;
	private BBProtocol.PBReqLogin mPbProto;
	//private short packetNum;
	
	//构造函数   组包
	public LoginPacketOther(String strAccount, String strPassword){
		
		//packetNum = SequenceNumberMaker.getInstance().make();
		
		mPbProto  = BBProtocol.PBReqLogin.newBuilder().
				setStrAccount(strAccount).
				setStrPassword(strPassword).
				setStrSSO("").
				setDwCliType(1).
				setDwLoginType(1).
				setDwLoginStatu(1).
				setDwVersion(1).build();
		mLoginHeader = new Header.Builder((short)0x3BB3,(short)mPbProto.getSerializedSize(),
				(short) 513).
				setFromUserId(0).
				setToUerId(0).
				setwSeqNo((short)0).build();

	}

	//将数据转化为二进制字节   在发送时会促发调用这个函数
	@Override
	public DataBuffer encode() {
		DataBuffer headerBuffer = mLoginHeader.encode();
        DataBuffer bodyBuffer = new DataBuffer();
    
        bodyBuffer.writeBytes(mPbProto.toByteArray());

        int headLength = headerBuffer.readableBytes();
        
        int bodyLength = bodyBuffer.readableBytes();

        DataBuffer buffer = new DataBuffer(headLength + bodyLength);
        buffer.writeDataBuffer(headerBuffer);
        buffer.writeDataBuffer(bodyBuffer);

        return buffer;
	}




}
