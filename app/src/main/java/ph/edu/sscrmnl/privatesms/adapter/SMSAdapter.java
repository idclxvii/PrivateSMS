package ph.edu.sscrmnl.privatesms.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ph.edu.sscrmnl.privatesms.R;
import ph.edu.sscrmnl.privatesms.databasemodel.ModelSMS;
import ph.edu.sscrmnl.privatesms.util.SQLiteHelper;

/**
 * Created by IDcLxViI on 2/8/2016.
 */
public class SMSAdapter extends ArrayAdapter<ModelSMS> implements Filterable{

    private SQLiteHelper DB;
    Context context;
    int layoutResourceId;

    //Two data sources, the original data and filtered data
    List<ModelSMS> filteredData = null;
    List<ModelSMS> originalData = null;

    private synchronized SQLiteHelper getDb(Context context){
        DB = new SQLiteHelper(context);
        return this.DB;
    }


    public SMSAdapter(Context context, int resource, List<ModelSMS> data) {
        super(context, resource, data);
        this.context = context;
        this.layoutResourceId = resource;
        this.originalData = data;
        this.filteredData = data;

        this.DB = getDb(context);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ModelSMS sms = filteredData.get(position);
        // Check if an existing view is being reused, otherwise inflate the view

        View vi = convertView;
        if (vi == null)
            vi =LayoutInflater.from(getContext()).inflate(
                    sms.getType() == ModelSMS.SMS_RECEIVED ?
                            R.layout.sms_received_row :
                            R.layout.sms_sent_row
                    , parent, false);
            //vi =LayoutInflater.from(getContext()).inflate(layoutResourceId, parent, false);

        // differentiate rendering according to sms type
        if(sms.getType() == ModelSMS.SMS_RECEIVED) {
            // this sms is received

            // SMS Body
            TextView body = (TextView) vi.findViewById(R.id.txtSMSReceivedBody);
            body.setText(sms.getBody());

            // SMS datetime
            TextView datetime = (TextView) vi.findViewById(R.id.txtSMSReceivedDatetime);
            datetime.setText(new SimpleDateFormat("EEE MMM dd yyyy hh:mm aaa",
                    Locale.getDefault()).format(sms.getDatetime().longValue()));

        }else{
            // this sms is sent

            // default bg #92d5ff #d3d3d3 if fail

            LinearLayout layout = (LinearLayout) vi.findViewById(R.id.layoutSMSSent);
            layout.setBackgroundColor(sms.getStatus() == ModelSMS.SMS_STATUS_SEND_SUCCESS ?
                    // we can use 0xff<hexa w/o #> for code swag: #d3d3d3 => 0xffd3d3d3
                    Color.parseColor("#92d5ff") : Color.parseColor("#d3d3d3"));


            // SMS Body
            TextView body = (TextView) vi.findViewById(R.id.txtSMSSentBody);
            body.setText(sms.getBody());

            // SMS datetime
            TextView datetime = (TextView) vi.findViewById(R.id.txtSMSSentDatetime);
            datetime.setText(
                    // sms sending status success
                    sms.getStatus() == ModelSMS.SMS_STATUS_SEND_SUCCESS ?
                            "Sent: " + new SimpleDateFormat("EEE MMM dd yyyy hh:mm aaa",
                                    Locale.getDefault()).format(sms.getDatetime().longValue())

                            :
                            // sms sending status failed
                            "Failed to send: " + new SimpleDateFormat("EEE MMM dd yyyy hh:mm aaa",
                                    Locale.getDefault()).format(sms.getDatetime().longValue())

            );

        }

        return vi;



    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // TODO Auto-generated method stub
            /*
             * Here, you take the constraint and let it run against the array
             * You return the result in the object of FilterResults in a form
             * you can read later in publichResults.
             */
                FilterResults results = new FilterResults();

                //If there's nothing to filter on, return the original data for your list
                if(constraint == null || constraint.length() == 0)
                {
                    results.values = originalData;
                    results.count = originalData.size();
                }
                else
                {
                    ArrayList<ModelSMS> filteredResultsData = new ArrayList<ModelSMS>();

                    for(ModelSMS data : originalData)
                    {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
                        if(data.getBody().toLowerCase().contains(constraint.toString().toLowerCase()))
                        {
                            filteredResultsData.add(data);
                        }
                    }

                    results.values = filteredResultsData;
                    results.count = filteredResultsData.size();
                }

                return results;
            }

            /* (non-Javadoc)
             * @see android.widget.Filter#publishResults(java.lang.CharSequence, android.widget.Filter.FilterResults)
             */
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // TODO Auto-generated method stub
            /*
             * Here, you take the result, put it into Adapters array
             * and inform about the the change in data.
             */
                filteredData = (ArrayList<ModelSMS>)results.values;
                notifyDataSetChanged();

            }


        };
    }

    public int getCount()
    {
        return filteredData.size();
    }

    //This should return a data object, not an int
    public ModelSMS getItem(int position)
    {
        return filteredData.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }
}
