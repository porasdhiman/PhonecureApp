package com.worksdelight.phonecure;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by worksdelight on 12/04/17.
 */

public class CompletedFragment extends Fragment {
    ListView completed_listView;
    Dialog dialog2;
    Global global;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    String filePath;
    File pdfFile;
    ImageView back_img;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.completed_layout, container, false);
        completed_listView = (ListView) v.findViewById(R.id.completed_listView);

        global = (Global) getActivity().getApplicationContext();
        back_img=(ImageView)v.findViewById(R.id.back_img);

        for (int i = 0; i < global.getCompletedaar().length(); i++) {
            try {
                JSONObject obj = global.getCompletedaar().getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put(GlobalConstant.total_amount, obj.getString(GlobalConstant.total_amount));
                map.put(GlobalConstant.date, obj.getString(GlobalConstant.date));
                map.put(GlobalConstant.invoice, obj.getString(GlobalConstant.invoice));

                JSONObject user_detail = obj.getJSONObject(GlobalConstant.user_detail);
                map.put(GlobalConstant.name, user_detail.getString(GlobalConstant.name));
                map.put(GlobalConstant.image,user_detail.getString(GlobalConstant.image));
                list.add(map);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if(list.size()>0) {
            completed_listView.setVisibility(View.VISIBLE);
            back_img.setVisibility(View.GONE);
            completed_listView.setAdapter(new CompletedAdapter(getActivity()));
        }else{
            completed_listView.setVisibility(View.GONE);
            back_img.setVisibility(View.VISIBLE);
        }
        completed_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent detail=new Intent(getActivity(),AppointmentActivity.class);
                detail.putExtra("pos",String.valueOf(i));
                detail.putExtra("type","0");
                startActivity(detail);
                getActivity().finish();
            }
        });
        return v;
    }

    public static CompletedFragment newInstance(String text) {

        CompletedFragment f = new CompletedFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }


    class CompletedAdapter extends BaseAdapter {
        Context c;
        LayoutInflater inflatore;
        Holder holder;


        CompletedAdapter(Context c/*, ArrayList<HashMap<String, String>> list*/) {
            this.c = c;
            //this.list = list;
            inflatore = LayoutInflater.from(c);
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                holder = new Holder();

                view = inflatore.inflate(R.layout.complete_list_item, null);
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.delivered_date_txt = (TextView) view.findViewById(R.id.delivered_date_txt);
                holder.download_img = (ImageView) view.findViewById(R.id.download_img);
                holder.price_txt = (TextView) view.findViewById(R.id.price_txt);
                holder.tech_view=(ImageView)view.findViewById(R.id.tech_view);
                view.setTag(holder);
                holder.download_img.setTag(holder);

            } else {
                holder = (Holder) view.getTag();
            }


                holder.name.setText(cap(list.get(i).get(GlobalConstant.name)));
                holder.price_txt.setText("$"+list.get(i).get(GlobalConstant.total_amount));
                holder.delivered_date_txt.setText(formatdate2(list.get(i).get(GlobalConstant.date)));
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(holder.name.getText().toString().substring(0, 1).toUpperCase(), Color.parseColor("#F94444"));
            if (list.get(i).get(GlobalConstant.image).equalsIgnoreCase("")) {

                holder.tech_view.setImageDrawable(drawable);
            } else {
                Picasso.with(c).load(GlobalConstant.TECHNICIANS_IMAGE_URL + list.get(i).get(GlobalConstant.image)).placeholder(drawable).transform(new CircleTransform()).into( holder.tech_view);


                //profilepic.setImageURI(Uri.fromFile(new File(preferences.getString(GlobalConstants.IMAGE, ""))));
            }

          /*  holder.setting_layout.setVisibility(View.GONE);
            holder.name.setText(list.get(i).get(GlobalConstant.name));
            holder.location.setText(list.get(i).get(GlobalConstant.distance) + "Km Away");
            holder.rating_value.setText(list.get(i).get(GlobalConstant.average_rating));
            // holder.specilist.setText(list.get(i).get(GlobalConstant.average_rating));
            String url = GlobalConstant.TECHNICIANS_IMAGE_URL + list.get(i).get(GlobalConstant.image);
            if (url != null && !url.equalsIgnoreCase("null")
                    && !url.equalsIgnoreCase("")) {
                imageLoader.displayImage(url, holder.tech_view, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);

                            }
                        });
            } else {
                holder.tech_view.setImageResource(R.drawable.user_back);
            }
*/
            holder.download_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder=(Holder)view.getTag();
                    loadProfileImage(GlobalConstant.PDF_DOWNLOAD_URL+list.get(i).get(GlobalConstant.invoice), getActivity());
                }
            });

            return view;
        }

        public class Holder {
            ImageView cancel, chat, message, call,download_img;
            LinearLayout setting_layout, setting_call_layout;
            ImageView tech_view;
            TextView name, delivered_date_txt, price_txt, rating_value;

        }
    }
    public String formatdate2(String fdate) {
        String datetime = null;
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat d = new SimpleDateFormat("dd MMM yyyy");
        try {
            Date convertedDate = inputFormat.parse(fdate);
            datetime = d.format(convertedDate);

        } catch (ParseException e) {

        }
        return datetime;


    }
    public String cap(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
    //-------------------------background proceess for pdf file------------------
    public void loadProfileImage(String file_url, Context c) {
        String splitPath[] = file_url.split("/");
        String targetFileName = splitPath[splitPath.length - 1];


         pdfFile = new File(Environment.getExternalStorageDirectory() + "/PhoneCure/" + targetFileName);
        if (pdfFile.exists()) {


            try
            {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(pdfFile);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            catch (ActivityNotFoundException e)
            {
                Toast.makeText(getActivity(), "No PDF Viewer Installed", Toast.LENGTH_LONG).show();
            }
        } else {

            dialogWindow();
            new DownloadFileAsync().execute(file_url);

        }
    }


    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count = 0;
            int MEGABYTE = 1024;
            //Integer.parseInt(listing.get(p).get("file_size"));
            try {

                URL url = new URL(aurl[0]);
                String path = (String) aurl[0];
                String splitPath[] = path.split("/");

                String targetFileName = splitPath[splitPath.length - 1];
                File f = new File(Environment.getExternalStorageDirectory() + "/PhoneCure/");
                f.mkdir();

                 pdfFile = new File(f, targetFileName);
                try {
                    pdfFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                filePath = pdfFile.toString();
//create storage directories, if they don't exist

                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                // c.setDoOutput(true);
                c.connect();


                InputStream input = c.getInputStream();// new BufferedInputStream(url.openStream(), Integer.parseInt(listing.get(p).get("file_size")));


                FileOutputStream output = new FileOutputStream(pdfFile);
                int lenghtOfFile = c.getContentLength();



                byte data[] = new byte[MEGABYTE];

                long total = 0;

                while ((count = input.read(data)) != 0) {
                    total += count;

                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
            }
            return null;

        }

        @Override
        protected void onProgressUpdate(String... progress) {
            Log.e("ANDRO_ASYNC", progress[0]);

        }

        @Override
        protected void onPostExecute(String unused) {
            dialog2.dismiss();
            Log.e("file path", filePath);
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(pdfFile);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            catch (ActivityNotFoundException e)
            {
                Toast.makeText(getActivity(), "No PDF Viewer Installed", Toast.LENGTH_LONG).show();
            }
        }
    }
    //---------------------------Progrees Dialog-----------------------
    public void dialogWindow() {
        dialog2 = new Dialog(getActivity());
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setCancelable(false);
        dialog2.setContentView(R.layout.progrees_login);
        AVLoadingIndicatorView loaderView = (AVLoadingIndicatorView) dialog2.findViewById(R.id.loader_view);
        loaderView.setIndicatorColor(getResources().getColor(R.color.main_color));
        loaderView.show();

        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
        dialog2.show();
    }



}
