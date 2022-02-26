package khaf.d4me.edcube.Class;


import android.graphics.Bitmap;

public class Cls_Devices {
    public String Id_Device;
    public String Dev_Nombre;
    public int Dev_Us;
    public int Dev_St;
    public Bitmap Dev_Img;

    public String get_IdDev(){
        return Id_Device;
    }
    public void set_IdDev(String IdDev){
        Id_Device=IdDev;
    }

    public String getDevNom(){
        return Dev_Nombre;
    }
    public void set_DevNom(String DevNom){
        Dev_Nombre=DevNom;
    }

    public int getDevUs(){
        return Dev_Us;
    }
    public void set_DevUs(int DevUs){
        Dev_Us=DevUs;
    }

    public int getDevSt(){
        return Dev_St;
    }
    public void set_DevSt(int DevSt){
        Dev_St=DevSt;
    }

    public Bitmap getDevImg(){
        return Dev_Img;
    }
    public void set_DevImg(Bitmap DevImg){
        Dev_Img=DevImg;
    }
    public Cls_Devices(){}
    public Cls_Devices(String IdDev, String DevNom,int DevUs, int DevSt,Bitmap img){
        Id_Device=IdDev;
        Dev_Nombre=DevNom;
        Dev_Us=DevUs;
        Dev_St=DevSt;
        Dev_Img = img;
    }
}