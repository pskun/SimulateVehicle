package edu.bupt.sv.tm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

import edu.bupt.sv.utils.LogUtil;

class ClientInput extends Thread {

	public Client _client;
	
	private final short LENBYTES;
	
	public LinkedList<String> ret_msg = new LinkedList<String>();
	
	public ClientInput(Client _client)
	{
		this._client = _client;
		LENBYTES = _client.LENBYTES;
	}
	
	@Override
	public void run()
	{
		try{
			InputStream is = _client.socket.getInputStream();
			
			while (true){
				byte[] recvBuffer = new byte[LENBYTES];
				int readBytes = 0;
				int restBytes = LENBYTES;
				int nreads = 0;
				do{
					nreads = is.read(recvBuffer, readBytes, restBytes);
					readBytes += nreads;
					restBytes -= nreads;
				}while(restBytes > 0);
				restBytes = 0;
				for (int i = 0; i < LENBYTES; i++)
					restBytes = restBytes * 128 + (int)recvBuffer[i];
				recvBuffer = new byte[restBytes];
				readBytes = 0;
				do{
					nreads = is.read(recvBuffer, readBytes, restBytes);
					readBytes += nreads;
					restBytes -= nreads;
				}while(restBytes > 0);
				synchronized(_client){
					ret_msg.add(new String(recvBuffer, 0, readBytes));
					_client.notifyAll();
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			_client.closeReceive();
			//_client.notifyAll();
		}
	}
}

public class Client {
	public Socket socket;
	
	public final short LENBYTES = 4;
	
	private ClientInput ci;
	
	private boolean isValid = false;
	
	public Client(Socket socket){
		this.socket = socket;
		ci = new ClientInput(this);
		ci.start();
	}
	
	public Client (String host, int port) throws IOException {
		try{
			this.socket = new Socket();
			socket.connect(new InetSocketAddress(host, port));
			ci = new ClientInput(this);
			ci.start();
			isValid = true;
		}catch (IOException e){
			LogUtil.verbose("socket connect timeout.");
			throw e;
		}
	}
	
	public Client() throws IOException {
		this.socket = new Socket("127.0.0.1", 8888);
		ci = new ClientInput(this);
		ci.start();
	}
	
	public boolean send (String msg)    //发送一个请求
	{
		int len = msg.length();
		byte[] sendBuffer = new byte[len + LENBYTES];
		int tempLen = len;
		for (int i = LENBYTES -1; i >= 0; i--){
			sendBuffer[i] = (byte) (tempLen % 128);    //JAVA没有unsigned byte，最高位为1时会被认作负数
			tempLen /= 128;
		}
		System.arraycopy(msg.getBytes(), 0, sendBuffer, LENBYTES, len);
		try{
			OutputStream os = socket.getOutputStream();
			os.write(sendBuffer);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String receive() throws InterruptedException
	{
		String ret_msg;
		synchronized(this){
			while (isValid && ci.ret_msg.isEmpty())
				wait();
			// TODO poll vs pollFirst
			if (!ci.ret_msg.isEmpty())
				ret_msg = new String(ci.ret_msg.poll());    //从头部取走一个消息（如果没有则返回null)
			else
				throw new InterruptedException();
		}
		return ret_msg;
	}
	
	public String sendAndDeal (String msg) throws InterruptedException    //一次发送一个请求并接受一个回复
	{
		int len = msg.length();
		byte[] sendBuffer = new byte[len + LENBYTES];
		int tempLen = len;
		for (int i = LENBYTES -1; i >= 0; i--){
			sendBuffer[i] = (byte) (tempLen % 128);    //JAVA没有unsigned byte，最高位为1时会被认作负数
			tempLen /= 128;
		}
		System.arraycopy(msg.getBytes(), 0, sendBuffer, LENBYTES, len);
		String ret_msg = "";
		try{
			OutputStream os = socket.getOutputStream();
			os.write(sendBuffer);
			synchronized(this){
				while (ci.ret_msg == null)
					wait();
				// TODO poll vs pollFirst
				ret_msg = ci.ret_msg.poll();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		return ret_msg;
	}
	
	public void closeReceive() {
		isValid = false;
	}
	
	public void closeClient() {
		if(ci != null) {
			ci.interrupt();
		}
	}
	
	public static void main(String[] args) throws Exception{
		Client myClient = new Client();
		while (true){
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String str = new String(reader.readLine());
			System.out.println(myClient.sendAndDeal(str));
		}
	}
}