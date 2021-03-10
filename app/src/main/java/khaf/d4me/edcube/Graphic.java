package khaf.d4me.edcube;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class Graphic extends AppCompatActivity {
    LineChartView lineChartView;
    CheckBox cbTie;
    private static final String url = "jdbc:mysql://162.241.61.218:3306/nucleoi3_EdCube?characterEncoding=latin1";
    private static final String user = "nucleoi3_angelcube";
    private static final String pass = "angelcube";
    ArrayList<String> axisData = new ArrayList<String>();
    ArrayList<Float> yAxisData = new ArrayList<Float>();
    float Max = 0;
    List yAxisValues = new ArrayList();
    List axisValues = new ArrayList();
    LineChartData data = new LineChartData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);

        lineChartView = findViewById(R.id.chart);
        cbTie = (CheckBox) findViewById(R.id.cbTiempo);

        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        List lines = new ArrayList();
        lines.add(line);

        data.setLines(lines);

        new LineChartData(lines);
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setName("Time");
        axis.setTextColor(ChartUtils.COLOR_ORANGE);
        axis.setMaxLabelChars(15);
        axis.setTextSize(10);
        axis.setHasTiltedLabels(true);
        axis.setHasSeparationLine(true);
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName("Watts");
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);
        data.setAxisYLeft(yAxis);
        for(int i = 0;i<20;i++){
            axisData.add("1");
            yAxisData.add((float) 1.00);
            yAxisValues.add(1);
            axisValues.add(1);
        }
        ejecutar();
    }
    private void ejecutar(){
        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                metodoEjecutar();//llamamos nuestro metodo
                handler.postDelayed(this,1000);//se ejecutara cada 10 segundos
            }
        },1000);//empezara a ejecutarse después de 5 milisegundos
    }
    private void metodoEjecutar() {
        if(cbTie.isChecked())
        {
            Datos dat = new Datos();
            dat.execute("");
        }
    }
    private class Datos extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            //axisData = new ArrayList<String>();
            //yAxisData = new ArrayList<Float>();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                int i = 0;
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Databaseection success");
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("Select * From (Select * From sensors_data ORDER BY sensor_id desc Limit 20) AS TempTable " +
                        "ORDER BY TempTable.data_time ASC");
                while(rs.next()){
                    axisData.set(i,rs.getString(2));
                    yAxisData.set(i,rs.getFloat(3));
                    i++;
                }
                rs = st.executeQuery("Select MAX(d_power) From (Select * From sensors_data ORDER BY sensor_id desc Limit 20) AS TempTable " +
                        "ORDER BY TempTable.data_time ASC");
                while(rs.next()){
                    Max = rs.getFloat(1);
                }
                con.close();
                st.close();
                rs.close();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            for (int i = 0; i < axisData.size(); i++) {
                axisValues.set(i, new AxisValue(i).setLabel(axisData.get(i)));
            }

            for (int i = 0; i < yAxisData.size(); i++) {
                yAxisValues.set(i,new PointValue(i, yAxisData.get(i)));
            }

            lineChartView.setLineChartData(data);
            Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
            viewport.top = Max;
            lineChartView.setMaximumViewport(viewport);
            lineChartView.setCurrentViewport(viewport);
        }
    }
}