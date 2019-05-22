package cn.whzwl.xbs.serialport_demo;


import android.util.Log;


/**
 * Created by Administrator on 2018/5/10.
 */

public abstract class SerialUilt {
    public static SerialPort serial;

    public static Thread receiveThread = null;

    public SerialUilt() {
        serial = new SerialPort();

    }

    /**
     * 开启串口，并且接收执行线程。
     */
    public  void  Open()
    {
        serial.Open(3,9600);
        receiveSerialPort();
    }


    /**
     * 接收串口数据的方法
     */
    public void receiveSerialPort() {

        if (receiveThread != null)
            return;
        /*定义一个handler对象要来接收子线程中接收到的数据
            并调用Activity中的刷新界面的方法更新UI界面
         */

        /*创建子线程接收串口数据
         */
        receiveThread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(100);
                        String data =  Recv();

                        if (!data.equals("")) {
                            onDataReceived(data);
                        }


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }

            }
        };
        //启动接收线程
        receiveThread.start();
    }

    /**
     * @param serialData
     * 抽象方法主要用于在调用界面从这个里面获取值
     */
    protected abstract void onDataReceived(String serialData);

    /**
     * @param tx 传入指令参数
     *           发送指令
     */
    public void Send(CharSequence tx) {

        int[] text = new int[tx.length()];
        for (int i = 0; i < tx.length(); i++) {
            text[i] = tx.charAt(i);
        }
        serial.Write(text, tx.length());
        Log.e("serialUilt","发送  "+tx);
    }

    /**
     * @return 获取指令返回值
     */
    private String Recv() {
        int[] RX = serial.Read();   //
        if (RX == null) {
            return "";
        } else {
         Log.e("serialUilt","接收  长度："+RX.length  +"  数据："+new String(RX, 0, RX.length));
            return new String(RX, 0, RX.length);
        }
    }

    /**
     * 关闭串口，关闭线程
     */
    public  void Close() {
        if (receiveThread!=null){
            receiveThread.interrupt();
            receiveThread=null;
        }

        serial.Close();

    }


}
