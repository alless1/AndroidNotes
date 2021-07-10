package com.alless.nettydemo;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 3.6版本的是ChannelBuffer,4.x是ByteBuf
 * 数据缓冲区对象(ByteBuf)
 * @author 程杰
 */
public class DataBuffer {

	public ByteBuf buffer;

	//构造函数生成heapBuffer
	public DataBuffer(){
		buffer = Unpooled.buffer();
	}
	public DataBuffer(ByteBuf byteBuf){
		buffer = byteBuf;
	}
	public DataBuffer(int length){
		buffer = Unpooled.buffer(length);
	}
	public byte[] array(){
		return buffer.array();
	}
	public void setOrignalBuffer(ByteBuf byteBuf){
		buffer = byteBuf;
	}
	public ByteBuf getOrignalBuffer() {
		return buffer;
	}
	public void writeByte(int value){
		buffer.writeByte(value);
	}
	public byte[] readBytes(int length){
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return bytes;
    }
    public int readInt(){
        if(buffer.isReadable()){
            return buffer.readInt();
        }else{
            return 0;
        }
    }
    public void writeShort(short value) {
        buffer.writeShort(value);
    }

    public short readShort() {
        if (buffer.isReadable()) {
            return buffer.readShort();
        } else {
            return 0;
        }
    }
    public byte readByte(){
        if (buffer.isReadable()) {
            return buffer.readByte();
        } else {
            return 0;
        }
    }

    public void writeInt(int value) {
        buffer.writeInt(value);
    }

    public char readChar() {
        return buffer.readChar();
    }

    public void writeChar(char c) {
        buffer.writeChar(c);
    }

    public long readLong() {
        return buffer.readLong();
    }

    public void writeLong(long value) {
        buffer.writeLong(value);
    }

    public double readDouble() {
        return buffer.readDouble();
    }

    public void writeDouble(double value) {
        buffer.writeDouble(value);
    }
    //获取有效可读的byte字节数
    public int readableBytes(){
        return buffer.readableBytes();
    }
    public void writeBytes(byte[] bytes) {
        buffer.writeBytes(bytes);
    }

    public void writeDataBuffer(DataBuffer inputBuffer) {
        if (null == inputBuffer || inputBuffer.readableBytes() == 0) {
            return;
        }
        buffer.writeBytes(inputBuffer.buffer);
    }

    public void discardReadBytes()
    {
        buffer.discardReadBytes();
    }

    public void skip(int size){
        buffer.skipBytes(size);
    }

    public DataBuffer readBuffer(int pbLength) {
        return new DataBuffer(buffer.readBytes(pbLength));

    }

}
