package com.yushan.canvasdemo;

import android.app.Activity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity implements CanvasView.IActionEndListener {

    private CanvasView canvasView;
    private LinkedList[] pointValues;
    private LinkedList[] tempValues;
    private List<DataEntity> date = null;
    private List<String[]> mXAValues = null;
    private int mCenter = 0;// 基本数据中心点

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView() {
        canvasView = (CanvasView) findViewById(R.id.rl_show);
        canvasView.setOnEndListener(this);
    }

    private void initData() {
        String data = "[{\"recordtime\":\"1473264000\",\"dataOne\":\"150\",\"dataTwo\":\"84\",\"dataThree\":\"67\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2016-09-08 00:00:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"1\\u7ea7\\u9ad8\\u8840\\u538b\"},{\"recordtime\":\"1473177600\",\"dataOne\":\"60\",\"dataTwo\":\"84\",\"dataThree\":\"67\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2016-09-07 00:00:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"\\u6b63\\u5e38\\u9ad8\\u503c\"},{\"recordtime\":\"1473091200\",\"dataOne\":\"150\",\"dataTwo\":\"84\",\"dataThree\":\"67\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2016-09-06 00:00:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"1\\u7ea7\\u9ad8\\u8840\\u538b\"},{\"recordtime\":\"1473004800\",\"dataOne\":\"150\",\"dataTwo\":\"84\",\"dataThree\":\"67\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2016-09-05 00:00:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"1\\u7ea7\\u9ad8\\u8840\\u538b\"},{\"recordtime\":\"1472140800\",\"dataOne\":\"145\",\"dataTwo\":\"84\",\"dataThree\":\"67\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2016-08-26 00:00:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"1\\u7ea7\\u9ad8\\u8840\\u538b\"},{\"recordtime\":\"1439819880\",\"dataOne\":\"97\",\"dataTwo\":\"69\",\"dataThree\":\"100\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2015-08-17 21:58:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"\\u6b63\\u5e38\\u8840\\u538b\"},{\"recordtime\":\"1439557440\",\"dataOne\":\"98\",\"dataTwo\":\"59\",\"dataThree\":\"81\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2015-08-14 21:04:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"\\u4f4e\\u8840\\u538b\"},{\"recordtime\":\"1439391300\",\"dataOne\":\"105\",\"dataTwo\":\"62\",\"dataThree\":\"76\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2015-08-12 22:55:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"\\u6b63\\u5e38\\u8840\\u538b\"},{\"recordtime\":\"1439301000\",\"dataOne\":\"93\",\"dataTwo\":\"53\",\"dataThree\":\"91\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2015-08-11 21:50:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"\\u4f4e\\u8840\\u538b\"},{\"recordtime\":\"1439215080\",\"dataOne\":\"105\",\"dataTwo\":\"52\",\"dataThree\":\"71\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2015-08-10 21:58:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"\\u4f4e\\u8840\\u538b\"},{\"recordtime\":\"1439214960\",\"dataOne\":\"97\",\"dataTwo\":\"59\",\"dataThree\":\"79\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2015-08-10 21:56:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"\\u4f4e\\u8840\\u538b\"},{\"recordtime\":\"1439041980\",\"dataOne\":\"109\",\"dataTwo\":\"70\",\"dataThree\":\"85\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2015-08-08 21:53:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"\\u6b63\\u5e38\\u8840\\u538b\"},{\"recordtime\":\"1438952760\",\"dataOne\":\"119\",\"dataTwo\":\"65\",\"dataThree\":\"49\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2015-08-07 21:06:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"\\u6b63\\u5e38\\u8840\\u538b\"},{\"recordtime\":\"1438871520\",\"dataOne\":\"99\",\"dataTwo\":\"61\",\"dataThree\":\"80\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2015-08-06 22:32:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"\\u6b63\\u5e38\\u8840\\u538b\"},{\"recordtime\":\"1438780800\",\"dataOne\":\"102\",\"dataTwo\":\"62\",\"dataThree\":\"80\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2015-08-05 21:20:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"\\u6b63\\u5e38\\u8840\\u538b\"},{\"recordtime\":\"1438609560\",\"dataOne\":\"94\",\"dataTwo\":\"60\",\"dataThree\":\"72\",\"pulseresult\":\"0\",\"sporttag\":\"0\",\"mytag\":null,\"timetag\":2,\"date\":\"2015-08-03 21:46:00\",\"pulseresultcontent\":\"\\u65e0\\u5fc3\\u5f8b\\u4e0d\\u9f50\",\"bloodresultcontent\":\"\\u6b63\\u5e38\\u8840\\u538b\"}]";

        JSONArray outerData = null;
        try {
            outerData = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        date = new ArrayList<>();
        mXAValues = new ArrayList<>();
        pointValues = new LinkedList[2];
        pointValues[0] = new LinkedList<Integer>();
        pointValues[1] = new LinkedList<Integer>();
        tempValues = new LinkedList[1];
        tempValues[0] = new LinkedList<Integer>();

        try {
            for (int i = 0; i < outerData.length(); i++) {
                DataEntity bean = new DataEntity();
                JSONObject jObj = outerData.getJSONObject(i);
                String dataOne = jObj.getString("dataOne");
                String dataTwo = jObj.getString("dataTwo");
                String dateTime = jObj.getString("date");

                bean.setDataOne(dataOne);
                bean.setDataTwo(dataTwo);
                bean.setDataThree(jObj.getString("dataThree"));
                bean.setDataTime(dateTime);

                int i_dataOne = Integer.parseInt(dataOne);
                int i_dataTwo = Integer.parseInt(dataTwo);
                int dataThreeValue = Integer.parseInt(jObj.getString("dataThree"));
                String[] date_x = new String[2];
                date_x[0] = dateTime.substring(5, 10).replace("-", "/");
                date_x[1] = dateTime.substring(dateTime.length() - 8,
                        dateTime.length() - 3);

                mXAValues.add(0, date_x); //X轴时间
                date.add(0, bean); //数据

                pointValues[0].add(0, i_dataOne);
                pointValues[1].add(0, i_dataTwo);
                // 将数据添加到容器当中
                tempValues[0].add(0, setPulseY((-1) * dataThreeValue));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        canvasView.setPoints(pointValues, tempValues, canvasView.getPointColors(), mXAValues, mCenter, date.size() - 1);
    }

    @Override
    public void actionEnd(int position) {

    }

    /**
     * @param dataThreeValue 数据
     * @return
     * @author yushan
     * 设置脉搏曲线图的Y轴值
     */
    private int setPulseY(int dataThreeValue) {
        if (dataThreeValue == 0 || dataThreeValue >= -40) {
            dataThreeValue = -40;
        }
        if (dataThreeValue < 0 && dataThreeValue >= -120) {
            dataThreeValue = -120 - dataThreeValue;
        }
        if (dataThreeValue < -120) {
            dataThreeValue = 0;
        }
        return dataThreeValue;
    }
}
