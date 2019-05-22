package cn.whzwl.xbs.serialport_demo;

/**
 * 项目名称:SerialPort_Demo
 * <p>
 * 版权：智味来 版权所有
 * <p>
 * 作者：ASheng
 * <p>
 * 创建日期：2019/5/16 12:33
 * <p>
 * 描述：
 */
public class SerialPort {
    static {
        System.loadLibrary("serialport");
    }
    public   native int 	Open(int Port,int Rate);
    public   native int 	Close();
    public   native int[]Read();
    public  native int 	Write(int[] buffer,int len);

}
