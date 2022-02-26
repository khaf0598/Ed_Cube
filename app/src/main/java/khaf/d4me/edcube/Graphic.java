package khaf.d4me.edcube;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import khaf.d4me.edcube.Class.Cls_Devices;
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
    private static final String url = "jdbc:mysql://162.241.61.144:3306/krakenio_EdCubedb?characterEncoding=latin1";
    private static final String user = "krakenio_miguel";
    private static final String pass = "miguelitesi";
    ArrayList<String> axisData = new ArrayList<String>();
    ArrayList<Float> yAxisData = new ArrayList<Float>();
    float Max = 0;
    List yAxisValues = new ArrayList();
    List axisValues = new ArrayList();
    LineChartData data = new LineChartData();
    LinearLayout llReal;
    LinearLayout llGraphic;
    TabLayout tabVistas;
    ArrayList<Cls_Devices> device = new ArrayList<Cls_Devices>();
    //Switch swApag;
    String iddisp = "";
    int state = 0;
    LinearLayout linEstado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_graphic);
        //device = (ArrayList<Cls_Devices>) getIntent().getSerializableExtra("lista");
        iddisp = getIntent().getExtras().get("id").toString();
        state = Integer.parseInt(getIntent().getExtras().get("state").toString());
        linEstado = (LinearLayout) findViewById(R.id.layoutEstado);
        lineChartView = findViewById(R.id.chart);
        cbTie = (CheckBox) findViewById(R.id.cbTiempo);
        llReal = (LinearLayout) findViewById(R.id.llRealTime);
        llGraphic = (LinearLayout) findViewById(R.id.llGraphicM);
        tabVistas = (TabLayout) findViewById(R.id.tbMenu);
        //swApag = (Switch) findViewById(R.id.swApagar);
        Bitmap bmLocal;
        if(state==0)
            bmLocal = BitmapFactory.decodeResource(getResources(), R.drawable.apagado);
        else
            bmLocal = BitmapFactory.decodeResource(getResources(), R.drawable.encendido);
        Drawable d = new BitmapDrawable(getApplicationContext().getResources(), bmLocal);
        linEstado.setBackground(d);
        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        linEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state==0) {
                    state = 1;
                }
                else
                    state = 0;
                Cambiar cam = new Cambiar();
                cam.execute("");
            }
        });
        //swApag.setChecked(esta);
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
        tabVistas.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().equals("Real Time")){
                    llReal.setVisibility(View.VISIBLE);
                    llGraphic.setVisibility(View.GONE);
                }
                else if(tab.getText().equals("Graphic Mode")){
                    llReal.setVisibility(View.GONE);
                    llGraphic.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
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
                ResultSet rs = st.executeQuery("Select * From (Select * From sensors_data ORDER BY data_time desc Limit 20) AS TempTable " +
                        "ORDER BY TempTable.data_time ASC");
                while(rs.next()){
                    axisData.set(i,rs.getString(2));
                    yAxisData.set(i,rs.getFloat(3));
                    i++;
                }
                rs = st.executeQuery("Select MAX(d_power) From (Select * From sensors_data ORDER BY data_time desc Limit 20) AS TempTable " +
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
    private class Cambiar extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Cargando...", Toast.LENGTH_LONG).show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            String res = "";
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url,user,pass);
                Statement st = con.createStatement();
                st.executeUpdate("Update sensors Set stat = "+state+" where sensor_id = '"+iddisp+"'");
                con.close();
                st.close();
            } catch (Exception e) {
                res = e.toString();
            }
            return res;
        }
        @Override
        protected void onPostExecute(String result) {
            //mAdapter = new Adaptador_MenuR(userModels);
            //reyclerViewUser.setAdapter(mAdapter);
            Bitmap bmLocal;
            if(state == 0) {
                bmLocal = BitmapFactory.decodeResource(getResources(), R.drawable.apagado);
                Toast.makeText(getApplicationContext(), "Dispositivo Apagado", Toast.LENGTH_LONG).show();
            }
            else {
                bmLocal = BitmapFactory.decodeResource(getResources(), R.drawable.encendido);
                Toast.makeText(getApplicationContext(), "Dispositivo Encendido", Toast.LENGTH_LONG).show();
            }
            Drawable d = new BitmapDrawable(getApplicationContext().getResources(), bmLocal);
            linEstado.setBackground(d);
        }
    }
}