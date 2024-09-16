package com.example.crowdspark.componentes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import androidx.annotation.NonNull;

//import com.example.crowdspark.R;
import com.example.crowdspark.R;
import com.example.crowdspark.ventanas.DetalleProyecto;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

public class ProyectCardAdapter extends ArrayAdapter<ProyectCard> {

    private static final String TAG = "ProyectCardAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;


    private static class ViewHolder {
        TextView nombre;
        TextView fecha;
        TextView descripcion;
        TextView monto;
        TextView objetivo;
        ImageView image;
        Button detalle;
    }

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public ProyectCardAdapter(Context context, int resource, ArrayList<ProyectCard> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

@NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        setupImageLoader();


        String nombre = getItem(position).getNombre();
        String fecha = getItem(position).getFecha();
        String descripcion = getItem(position).getDescripcion();
        String monto = getItem(position).getMonto();
        String objetivo = getItem(position).getObjetivo();
        String categoria = getItem(position).getCategoria();
        String imgUrl = getItem(position).getImgurl();



        try{

            final View result;

            ViewHolder holder;

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mResource, parent, false);
                holder= new ViewHolder();
                holder.nombre = (TextView) convertView.findViewById(R.id.nombre);
                holder.fecha = (TextView) convertView.findViewById(R.id.fecha);
                holder.descripcion = (TextView) convertView.findViewById(R.id.descripcion);
                holder.monto = (TextView) convertView.findViewById(R.id.monto);
                holder.objetivo = (TextView) convertView.findViewById(R.id.objetivo);
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.detalle = (Button) convertView.findViewById(R.id.detalle);

                holder.detalle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, DetalleProyecto.class);
                        intent.putExtra("nombre", nombre);
                        intent.putExtra("fecha", fecha);
                        intent.putExtra("descripcion", descripcion);
                        intent.putExtra("monto", monto);
                        intent.putExtra("objetivo", objetivo);
                        intent.putExtra("categoria", categoria);
                        intent.putExtra("imgUrl", imgUrl);
                        mContext.startActivity(intent);
                    }
                });

                result = convertView;

                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
                result = convertView;
            }


            Animation animation = AnimationUtils.loadAnimation(mContext,
                    (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
            result.startAnimation(animation);
            lastPosition = position;

            holder.nombre.setText(nombre);
            holder.fecha.setText(fecha);
            holder.descripcion.setText(descripcion);
            holder.monto.setText(monto);
            holder.objetivo.setText(objetivo);

            ImageLoader imageLoader = ImageLoader.getInstance();

            int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            imageLoader.displayImage(imgUrl, holder.image, options);

            return convertView;
        }catch (IllegalArgumentException e){
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
            return convertView;
        }

    }


    private void setupImageLoader(){
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
    }
}