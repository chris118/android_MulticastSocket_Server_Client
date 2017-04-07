package com.socketautoconnect.server;

import java.net.DatagramPacket; 
import java.net.InetAddress; 
import java.net.MulticastSocket;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle; 
import android.util.Log;
import android.view.View;    
import android.view.View.OnClickListener; 
import android.widget.Button;
import android.widget.TextView;
import java.lang.*;
   

public class SocketAutoConnectServer extends Activity implements Runnable
{
	private static String ip; //�����ip   
	private static int BROADCAST_PORT=9898;
	private static String BROADCAST_IP="224.0.0.1";   
	InetAddress inetAddress=null; 
	Thread t=null;   
	/*���͹㲥�˵�socket*/  
    MulticastSocket multicastSocket=null;   
    /*���͹㲥�İ�ť*/   
    private Button sendUDPBrocast; 
	private volatile boolean isRuning= true;
	TextView ipInfo;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ipInfo=(TextView) findViewById(R.id.ip_info);
        sendUDPBrocast=(Button) findViewById(R.id.sendUDPBrocast);
        sendUDPBrocast.setOnClickListener(new SendUDPBrocastListener());   
        //Wifi״̬�ж�  
        WifiManager wifiManager=(WifiManager) getSystemService (Context.WIFI_SERVICE); 
        if(wifiManager.isWifiEnabled())
        {
        	WifiInfo wifiInfo=wifiManager.getConnectionInfo();
        	ip=getIpString(wifiInfo.getIpAddress());
        	ipInfo.append(ip);
        	System.out.println("����˵�wifi IP:"+ip);   
        }
        try 
        {
        	inetAddress=InetAddress.getByName(BROADCAST_IP);
        	multicastSocket=new MulticastSocket(BROADCAST_PORT);
        	multicastSocket.setTimeToLive(1);
        	multicastSocket.joinGroup(inetAddress);
        	
        }catch(Exception e)
        {
        	e.printStackTrace();
        	
        }
         t=new Thread(this); 
         t.start(); 
       
    }
    
  //����ȡ����int��ipת��string����
    private String getIpString(int i)
	{  
		return (i & 0xFF) + "." +((i >> 8) & 0xFF) + "."
	           +((i >> 16) & 0xFF) + "." +(i >> 24 & 0xFF);
	} 
    
    
    class SendUDPBrocastListener implements OnClickListener
    {  
        
		@Override
		public void onClick(View v)
		{
			if(isRuning) 
			{
				isRuning=false;
				sendUDPBrocast.setText("���͹㲥");
				System.out.println("����ֹͣ�㲥..");
				 
			}else
			{  
				isRuning=true;
				sendUDPBrocast.setText("ֹͣ�㲥");
				System.out.println("���ڷ��͹㲥..");
			}
		}  
    }
     
	@Override
	public void run()  
	{ 

    	//���͵����ݰ��������ڵ����е�ַ�������յ������ݰ�  
        DatagramPacket dataPacket = null;          
        //��������IP���������д��̬��ȡ��IP����ַ�ŵ����ݰ����ʵserver�˽��յ����ݰ���Ҳ�ܻ�ȡ����������IP��  
        byte[] data =ip.getBytes();   
        dataPacket = new DatagramPacket(data, data.length, inetAddress,BROADCAST_PORT);  
		while(true)  
		{
			if(isRuning) 
			{
				try  
		        {  
		           multicastSocket.send(dataPacket); 
		           Thread.sleep(3000);  
		           System.out.println("�ٴη���ip��ַ�㲥:.....");  
		        } catch (Exception e)              
		        {    
		            e.printStackTrace();     
		        } 
			}
		} 
	}
	
 
	@Override
	protected void onDestroy()  
	{
		super.onDestroy(); 
		isRuning=false;
		multicastSocket.close();     
		System.out.println("UDP Server�����˳�,�ص�socket,ֹͣ�㲥");
		finish(); 
	}  

}