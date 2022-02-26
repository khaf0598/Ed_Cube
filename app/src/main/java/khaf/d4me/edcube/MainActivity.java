package khaf.d4me.edcube;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import khaf.d4me.edcube.Adaptadores.Adapter_Devices;
import khaf.d4me.edcube.Class.Cls_Devices;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;

public class MainActivity extends AppCompatActivity {
    private Button btnIni,btnVolv;
    private RecyclerView recv;
    private static final String url = "jdbc:mysql://162.241.61.144:3306/krakenio_EdCubedb?characterEncoding=latin1";
    private static final String user = "krakenio_miguel";
    private static final String pass = "miguelitesi";
    private ProgressBar progressBar;
    private Cls_Devices devices;
    private Adapter_Devices AdaptadorDev;
    private ArrayList<Cls_Devices> listaDev = new ArrayList<Cls_Devices>();
    RecyclerView listDev;
    private TextView lblCar;
    enum  ProviderType{
        BASIC,
        GOOGLE
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_main);
        //btnIni = (Button) findViewById(R.id.btnIniciar);
        recv = (RecyclerView) findViewById(R.id.recView);
        recv.setHasFixedSize(true);
        recv.setLayoutManager(new GridLayoutManager(this,2));
        progressBar = (ProgressBar) findViewById(R.id.pbCarga);
        lblCar = (TextView) findViewById(R.id.txtCargando);
        btnVolv = (Button) findViewById(R.id.btnVolver);
        //Setup
        Bundle bundle = getIntent().getExtras();
        String email = bundle != null ? bundle.getString("Email") : null;
        String provider = bundle != null ? bundle.getString("Provider") : null;
        //String var10001 = email;
        //if (email == null) {
        //    var10001 = "";
        //}
        btnVolv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CerrarSesion();
            }
        });
        //String var10002 = provider;
        //if (provider == null) {
        //    var10002 = "";
        //}

        //this.setup(var10001, var10002);
        SharedPreferences.Editor prefs = this.getSharedPreferences(this.getString(R.string.prefs_file), 0).edit();
        prefs.putString("Email", email);
        prefs.putString("Provider", provider);
        prefs.apply();
        //btnIni.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        Intent i = new Intent(MainActivity.this,Graphic.class);
        //        startActivity(i);
        //    }
        //});
        TraerDisp traer = new TraerDisp();
        traer.execute("");
    }

    @Override
    public void onBackPressed() {
    }

    private class TraerDisp extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            lblCar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Databaseection success");
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("Select * From sensors");
                while(rs.next()){
                    devices = new Cls_Devices();
                    devices.Id_Device = rs.getString(1);
                    devices.Dev_Nombre = rs.getString(2);
                    devices.Dev_Us = rs.getInt(3);
                    devices.Dev_St = rs.getInt(4);
                    byte[] photo = rs.getBytes(5);
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
                    Bitmap bmLocal = BitmapFactory.decodeStream(imageStream);
                    devices.Dev_Img = bmLocal;
                    listaDev.add(devices);
                }
                devices = new Cls_Devices();
                devices.Id_Device = "";
                devices.Dev_Nombre = "Nuevo Dispositivo";
                devices.Dev_Us = 0;
                devices.Dev_St = 0;
                Bitmap bmLocal = BitmapFactory.decodeResource(getResources(), R.drawable.nuevo);
                devices.Dev_Img = bmLocal;
                listaDev.add(devices);
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
            recv.setAdapter(new Adapter_Devices(listaDev,new MainActivity.RecyclerViewOnItemClickListener(){
                @Override
                public void onClick(View v, int position) {
                    if(!listaDev.get(position).Id_Device.equals(""))
                        Escojer(position);
                }
            },getApplicationContext()));
            progressBar.setVisibility(View.GONE);
            lblCar.setVisibility(View.GONE);
        }
    }

    int actD = 0;
    private void Escojer(final int pos) {
        final Button btnVerInfo,btnCancelar;
        final TextView txtNombre;
        final Switch State;
        final LayoutInflater inflater = getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.opciones_devices, null);

        btnVerInfo = (Button) dialoglayout.findViewById(R.id.btnVer);
        btnCancelar = (Button) dialoglayout.findViewById(R.id.btnCancelar);
        txtNombre = (TextView) dialoglayout.findViewById(R.id.lblNombre);
        State = (Switch) dialoglayout.findViewById(R.id.swApagar);
        boolean st;
        if(listaDev.get(pos).Dev_St==0)
            st = false;
        else
            st = true;
        State.setChecked(st);
        txtNombre.setText(listaDev.get(pos).getDevNom());
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(dialoglayout);
        final AlertDialog d = builder.create();
        btnVerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,Graphic.class);
                //i.putExtra("lista",listaDev);
                i.putExtra("state",listaDev.get(pos).getDevSt());
                i.putExtra("id",listaDev.get(pos).get_IdDev());
                //i.putExtra("pos",pos);

                startActivity(i);
                actD = 0;
                d.dismiss();
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actD = 0;
                d.dismiss();
            }
        });
        State.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(seguir==0){
                    postcam = listaDev.get(pos).Id_Device;
                    seguir = 1;
                    if(isChecked)
                        comprobar = 1;
                    else
                        comprobar = 0;
                    Cambiar cam = new Cambiar();
                    cam.execute("");
                }
            }
        });
        if(actD==0) {
            d.show();
            actD = 1;
        }
    }
    String postcam = "";
    int seguir = 0;
    public interface  RecyclerViewOnItemClickListener {
        void onClick(View v, int position);
    }
    int comprobar;
    private class Cambiar extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            listaDev = new ArrayList<>();
            String res = "";
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url,user,pass);
                Statement st = con.createStatement();
                st.executeUpdate("Update sensors Set stat = "+comprobar+" where sensor_id = '"+postcam+"'");
                con.close();
                st.close();
            } catch (Exception e) {
                res = e.toString();
            }

            return res;
            /*
            userModels = new ArrayList<>();
            for(int i = 1; i < 100; i++) {
                userModels.add(new ClsPedidos(i,"Pedir comida","Pago efectivo: $200","Coca","Jejejeje",
                        100,"Enviar a Mi"));
            }
            return null;*/
        }
        @Override
        protected void onPostExecute(String result) {
            //mAdapter = new Adaptador_MenuR(userModels);
            //reyclerViewUser.setAdapter(mAdapter);
            if(comprobar == 0)
                Toast.makeText(getApplicationContext(),"Dispositivo Apagado",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(),"Dispositivo Encendido",Toast.LENGTH_LONG).show();
            TraerDisp car = new TraerDisp();
            car.execute();
            seguir=0;
        }
    }
    int cerrarInt = 0;
    private void CerrarSesion() {
        final Button btnAceptar;
        final Button btnCancelar;

        final LayoutInflater inflater = getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.ask_close, null);

        btnAceptar = (Button) dialoglayout.findViewById(R.id.btnAceptar);
        btnCancelar = (Button) dialoglayout.findViewById(R.id.btnCancelar);

        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(dialoglayout);
        final AlertDialog d = builder.create();
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor prefs = MainActivity.this.getSharedPreferences(MainActivity.this.getString(R.string.prefs_file), 0).edit();
                prefs.clear();
                prefs.apply();
                FirebaseAuth.getInstance().signOut();
                actD = 0;
                d.dismiss();
                MainActivity.super.onBackPressed();
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplication().getApplicationContext(),"Puede iniciar la lista de compras en otro momento",Toast.LENGTH_SHORT).show();
                actD = 0;
                d.dismiss();
            }
        });
        if(actD==0) {
            d.show();
            actD = 1;
        }
    }
}