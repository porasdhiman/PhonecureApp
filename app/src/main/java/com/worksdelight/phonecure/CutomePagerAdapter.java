package com.worksdelight.phonecure;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class CutomePagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    String[] techText;
    String[] exper_info;
    String[] exper_info2;
int[] img;
    public CutomePagerAdapter(Context context, int[] img,String[] techText, String[] exper_info,String[] exper_info2) {
        mContext = context;
        this.img = img;
        this.techText = techText;
        this.exper_info = exper_info;
        this.exper_info2 = exper_info2;

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return img.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

      /*  ImageView center_txtView = (ImageView) itemView.findViewById(R.id.pager_img);
        center_txtView.setBackgroundResource(img[position]);
        container.addView(itemView);
*/
        ImageView firest_screen_logo=(ImageView)itemView.findViewById(R.id.first_scrren_log);
        TextView tech_txt=(TextView)itemView.findViewById(R.id.tech_txt);
        TextView exper_info_txt=(TextView)itemView.findViewById(R.id.exper_info_txt);
        TextView exper_info_txt_two=(TextView)itemView.findViewById(R.id.exper_info_txt_two);
        firest_screen_logo.setImageResource(img[position]);
        tech_txt.setText(techText[position]);
        exper_info_txt.setText(exper_info[position]);
        exper_info_txt_two.setText(exper_info2[position]);

        container.addView(itemView);
        return itemView;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

}
