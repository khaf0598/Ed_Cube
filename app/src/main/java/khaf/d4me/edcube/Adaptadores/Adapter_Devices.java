package khaf.d4me.edcube.Adaptadores;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import khaf.d4me.edcube.Class.Cls_Devices;
import khaf.d4me.edcube.MainActivity;
import khaf.d4me.edcube.R;


public class Adapter_Devices extends RecyclerView.Adapter<Adapter_Devices.ViewHolder> {
    private ArrayList<Cls_Devices> userModelList;
    private MainActivity.RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;

    private ViewGroup parent;
    private int viewType;
    private Context context;
    public Adapter_Devices(ArrayList<Cls_Devices> userModelList,
                           @NonNull MainActivity.RecyclerViewOnItemClickListener
                                       recyclerViewOnItemClickListener, Context con) {
        this.userModelList = userModelList;
        context = con;
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.devices_view, parent, false);
        Adapter_Devices.ViewHolder viewHolder = new Adapter_Devices.ViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = userModelList.get(position).getDevNom();
        String mac = userModelList.get(position).get_IdDev();
        int usu = userModelList.get(position).getDevUs();
        int state = userModelList.get(position).getDevSt();
        Bitmap img = userModelList.get(position).getDevImg();

        holder.name.setText(name);
        //holder.name.setText(mac);
        //holder.name.setText(desc);
        Drawable d = new BitmapDrawable(context.getResources(), img);
        holder.linimg.setBackground(d);
    }
    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {
        private TextView name;
        //private ImageView fot;
        private LinearLayout linimg;
        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.lblNombre);
            linimg = (LinearLayout) v.findViewById(R.id.linImg);
            v.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            recyclerViewOnItemClickListener.onClick(v, getAdapterPosition());
        }
    }
}
