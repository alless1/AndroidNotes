package com.alless.nettydemo.packet;

import com.alless.nettydemo.DataBuffer;
import com.alless.nettydemo.utils.Logger;

/**
 * TCP协议的头文件
 * 
 * @author dolphinWang
 * @time 2014/04/30
 */
public class Header {
	private short wMgc;    //客户端固定
	private short pbLenth;  //pb长度
	private short msgType;   //消息类型
	private byte bRet;	
	private byte tIndc;
	private short wSeqNo;    //消息序列号
	private int fromUserId;
	private int toUerId;

	//private short mClientProto = 0x3BB3;
	//public int headerLength = 18;


	public Header() {
		wMgc = 0;
		pbLenth = 0;
		msgType = 0;
		bRet = 0;
		tIndc = 0;
		wSeqNo = 0;
		fromUserId = 0;
		toUerId = 0;
	}

	public Header(Builder builder) {
		this.wMgc = builder.wMgc;
		this.pbLenth = builder.pbLenth;
		this.msgType = builder.msgType;
		this.bRet = builder.bRet;
		this.tIndc = builder.tIndc;
		this.wSeqNo = builder.wSeqNo;
		this.fromUserId = builder.fromUserId;
		this.toUerId = builder.toUerId;
	}

	/**
	 * 头文件的压包函数
	 * 
	 * @return 数据包
	 */
	public DataBuffer encode() {
		DataBuffer db = new DataBuffer(18);
		db.writeShort(wMgc);
		db.writeShort(pbLenth);
		db.writeShort(msgType);
		db.writeByte(bRet);
		db.writeByte(tIndc);
		db.writeShort(wSeqNo);
		db.writeInt(fromUserId);
		db.writeInt(toUerId);
		return db;
	}
	
	/**
	 * 头文件的解包函数
	 * 
	 * @param buffer
	 */
	public void decode(DataBuffer buffer) {
		if (null == buffer)
			return;
		try {
			wMgc = buffer.readShort();
			pbLenth = buffer.readShort();
			msgType = buffer.readShort();
			bRet = buffer.readByte();
			tIndc = buffer.readByte();
			wSeqNo = buffer.readShort();
			fromUserId = buffer.readInt();
			toUerId = buffer.readInt();
		} catch (Exception e) {
			Logger.e("Header#Exception--",e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "Header{" +
				"wMgc=" + wMgc +
				", pbLenth=" + pbLenth +
				", msgType=" + msgType +
				", bRet=" + bRet +
				", tIndc=" + tIndc +
				", wSeqNo=" + wSeqNo +
				", fromUserId=" + fromUserId +
				", toUerId=" + toUerId +
				'}';
	}


	public int getwMgc() {
		return wMgc;
	}

	public void setwMgc(short wMgc) {
		this.wMgc = wMgc;
	}

	public short getPbLenth() {
		return pbLenth;
	}

	public void setPbLenth(short pbLenth) {
		this.pbLenth = pbLenth;
	}

	public short getMsgType() {
		return msgType;
	}

	public void setMsgType(short msgType) {
		this.msgType = msgType;
	}

	public byte getbRet() {
		return bRet;
	}

	public void setbRet(byte bRet) {
		this.bRet = bRet;
	}

	public byte gettIndc() {
		return tIndc;
	}

	public void settIndc(byte tIndc) {
		this.tIndc = tIndc;
	}

	public short getwSeqNo() {
		return wSeqNo;
	}

	public void setwSeqNo(short wSeqNo) {
		this.wSeqNo = wSeqNo;
	}

	public int getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(int fromUserId) {
		this.fromUserId = fromUserId;
	}

	public int getToUerId() {
		return toUerId;
	}

	public void setToUerId(int toUerId) {
		this.toUerId = toUerId;
	}
	
	public static class Builder {
		private short wMgc;    //客户端固定
		private short pbLenth;  //pb长度
		private short msgType;   //消息类型
		private byte bRet;	
		private byte tIndc;
		private short wSeqNo;    //消息序列号
		private int fromUserId;
		private int toUerId;
		
		 /* 
         * 非空属性，必须在构造器中指定。 
         */  
        public Builder(short clientproto,short pbLenth,short msgType) {  
          this.wMgc = clientproto;
          this.pbLenth = pbLenth;
          this.msgType = msgType;
        }

		

		public Builder setbRet(byte bRet) {
			this.bRet = bRet;
			return this;
		}

		

		public Builder settIndc(byte tIndc) {
			this.tIndc = tIndc;
			return this;
		}

	

		public Builder setwSeqNo(short wSeqNo) {
			this.wSeqNo = wSeqNo;
			return this;
		}

		
		public Builder setFromUserId(int fromUserId) {
			this.fromUserId = fromUserId;
			return this;
		}

		

		public Builder setToUerId(int toUerId) {
			this.toUerId = toUerId;
			return this;
		} 
        
        /* 
         * 可选择属性，提供特殊的setter方法。 
         */  
		
		public Header build() {  
            /* 检查Builder对象中的数据是否合法。 
             * 针对这个例子，就是检查主键冲突，外键制约等 
             * 如果不满足我们可以抛出一个IllegalArgumentException 
             */  
            return new Header(this);  
              
        }  
        
		
		
		
	}


	

}
