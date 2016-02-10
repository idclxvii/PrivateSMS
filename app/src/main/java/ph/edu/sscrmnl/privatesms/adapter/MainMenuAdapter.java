package ph.edu.sscrmnl.privatesms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ph.edu.sscrmnl.privatesms.R;

/**
 * Created by IDcLxViI on 2/7/2016.
 */
public class MainMenuAdapter extends BaseAdapter{
    private Context context;
    private String[] data;
    private static LayoutInflater inflater = null;

    public MainMenuAdapter(Context context, String[] data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.main_menu_row, null);
        ImageView img = (ImageView) vi.findViewById(R.id.img);
        switch (position){
            case 0:
                img.setImageResource(R.drawable.img0);
                break;
            case 1:
                img.setImageResource(R.drawable.img1);
                break;
            case 2:
                img.setImageResource(R.drawable.img2);
                break;
        }
        TextView text = (TextView) vi.findViewById(R.id.txtName);

        text.setText(data[position]);
        return vi;
    }
}
