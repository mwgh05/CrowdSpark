package com.example.crowdspark.componentes;

import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;

import com.example.crowdspark.R;
import com.example.crowdspark.admin.DetalleProyectoAdmin;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

public class ProyectCardAdapterAdmin extends ArrayAdapter<ProyectCard> {

    private static final String TAG = "ProyectCardAdapterAdmin";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Holds variables in a View
     */
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
    public ProyectCardAdapterAdmin(Context context, int resource, ArrayList<ProyectCard> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //sets up the image loader library
        setupImageLoader();

        //get the persons information
        String nombre = getItem(position).getNombre();
        String fecha = getItem(position).getFecha();
        String descripcion = getItem(position).getDescripcion();
        String monto = getItem(position).getMonto();
        String objetivo = getItem(position).getObjetivo();
        String categoria = getItem(position).getCategoria();
        String imgUrl = getItem(position).getImgurl();



        try{


            //create the view result for showing the animation
            final View result;

            //ViewHolder object
            ProyectCardAdapterAdmin.ViewHolder holder;

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mResource, parent, false);
                holder= new ProyectCardAdapterAdmin.ViewHolder();
                holder.nombre = (TextView) convertView.findViewById(R.id.nombre);
                holder.fecha = (TextView) convertView.findViewById(R.id.fecha);
                holder.descripcion = (TextView) convertView.findViewById(R.id.descripcion);
                holder.monto = (TextView) convertView.findViewById(R.id.monto);
                holder.objetivo = (TextView) convertView.findViewById(R.id.objetivo);
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.detalle = (Button) convertView.findViewById(R.id.detalle);



                result = convertView;

                convertView.setTag(holder);
            }
            else{
                holder = (ProyectCardAdapterAdmin.ViewHolder) convertView.getTag();
                result = convertView;
            }


            /*Animation animation = AnimationUtils.loadAnimation(mContext,
                    (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
            result.startAnimation(animation);*/
            lastPosition = position;

            holder.detalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetalleProyectoAdmin.class);
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

            holder.nombre.setText(nombre);
            holder.fecha.setText(fecha);
            holder.descripcion.setText(descripcion);
            holder.monto.setText(monto);
            holder.objetivo.setText(objetivo);

            //create the imageloader object
            ImageLoader imageLoader = ImageLoader.getInstance();

            int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());

            //create display options
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            //download and display image from url
            imageLoader.displayImage(imgUrl, holder.image, options);

            return convertView;
        }catch (IllegalArgumentException e){
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
            return convertView;
        }

    }

    /**
     * Required for setting up the Universal Image loader Library
     */
    private void setupImageLoader(){
        // UNIVERSAL IMAGE LOADER SETUP
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
        // END - UNIVERSAL IMAGE LOADER SETUP
    }
}
