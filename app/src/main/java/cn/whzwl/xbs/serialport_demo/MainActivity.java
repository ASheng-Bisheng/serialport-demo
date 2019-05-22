package cn.whzwl.xbs.serialport_demo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button open_close, send, empty;
    private TextView content;
    private ScrollView scrollView;
    private EditText send_content;
    SerialUilt serialUilt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        init();
        listening();
        serialUilt = new SerialUilt() {
            @Override
            protected void onDataReceived(final String serialData) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        stringBuffer.append(serialData);//Android串口开发中 很容易出现被截断接收 比如第一次接收的是4个字节 然后第二次就将剩余的全部返回
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
                        //我这边总体判断长度  如果长度一样 就确定是正确串口消息
                        if (stringBuffer.toString().length() == 26) {

                            //在串口消息中需要订协议来通讯 要判断包头  包尾，我这里包头用!- 代替  包尾用-！代替
                            if (stringBuffer.toString().substring(0, 3).equals("!--") && stringBuffer.toString().substring(stringBuffer.toString().length() - 3, stringBuffer.toString().length()).equals("--!")) {
                                Log.e("serialUilt", "接收到完整数据：" + stringBuffer.toString().length() + "\r\n");
                                content.append("\r\n"+stringBuffer.toString()+"\r\n");
                                stringBuffer.setLength(0);

                            }
                        }
                    }
                });
            }

        };
        serialUilt.Open();
    }


    StringBuffer stringBuffer;

    private void init() {
        open_close = findViewById(R.id.open_close);
        send = findViewById(R.id.send);
        send.setEnabled(false);
        content = findViewById(R.id.content);
        scrollView = findViewById(R.id.scrollView);
        send_content = findViewById(R.id.send_content);
//        cycle=findViewById(R.id.cycle);
        empty = findViewById(R.id.empty);


        stringBuffer = new StringBuffer();


    }

    boolean judge = false;


    private void listening() {
        //串口开启关闭
        open_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (judge) {
                    serialUilt.Close();
                    judge = false;
                    open_close.setText("已关闭");

                    send.setEnabled(false);
                } else {
                    serialUilt.Open();
                    judge = true;
                    open_close.setText("已开启");
                    send.setEnabled(true);

                }
            }
        });

        //发送串口
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                serialUilt.Send(send_content.getText().toString());
                content.append("\r\n发出消息:" + send_content.getText().toString() + "\r\n");
            }
        });

        //清空接收
        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.setText("");
                stringBuffer.setLength(0);
            }
        });


    }

    /**
     * 点击空白区域隐藏键盘.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
