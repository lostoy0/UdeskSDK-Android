package udesk.sdk.demo.activity;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import udesk.sdk.demo.R;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private List<Uri> items;

    public ImageAdapter(Context context, List<Uri> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.iv);

        Uri item = items.get(position);
        imageView.setImageURI(item);

        return convertView;
    }
}
