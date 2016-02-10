package ph.edu.sscrmnl.privatesms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import ph.edu.sscrmnl.privatesms.R;
import ph.edu.sscrmnl.privatesms.databasemodel.ModelContacts;
import ph.edu.sscrmnl.privatesms.databasemodel.ModelConversation;
import ph.edu.sscrmnl.privatesms.databasemodel.ModelSMS;
import ph.edu.sscrmnl.privatesms.databasemodel.Tables;
import ph.edu.sscrmnl.privatesms.util.SQLiteHelper;

/**
 * Created by IDcLxViI on 2/8/2016.
 */
public class SMSListAdapter extends ArrayAdapter<ModelConversation> implements Filterable{

    private SQLiteHelper DB;
    Context context;
    int layoutResourceId;

    //Two data sources, the original data and filtered data
    List<ModelConversation> filteredData = null;
    List<ModelConversation> originalData = null;

    private synchronized SQLiteHelper getDb(Context context){
        DB = new SQLiteHelper(context);
        return this.DB;
    }


    public SMSListAdapter(Context context, int resource, List<ModelConversation> data) {
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
        ModelConversation c = filteredData.get(position);
        // Check if an existing view is being reused, otherwise inflate the view

        View vi = convertView;
        if (vi == null) {
            vi = LayoutInflater.from(getContext()).inflate(layoutResourceId, parent, false);

        }
        // img
        ImageView img = (ImageView) vi.findViewById(R.id.imgSMSList);
        img.setImageResource(R.drawable.img0);

        // address text
        TextView address = (TextView) vi.findViewById(R.id.txtAddressSMSList);

        try{
            ModelContacts contact = (ModelContacts) DB.select(Tables.contacts, ModelContacts.class,
                    new Object[][] {{"address", c.getAddress()}}
                    , null);
            address.setText(contact.getName() != null ? contact.getName() : c.getAddress());
        }catch (Exception e){ address.setText( c.getAddress()); }


        // number of unread
        TextView unread =(TextView) vi.findViewById(R.id.txtUnread);
        unread.setText(c.getCount().toString());

        // last mod
        TextView datetime = (TextView) vi.findViewById(R.id.txtDatetimeSMSList);
        datetime.setText(
                new SimpleDateFormat("EEE MMM dd yyyy hh:mm aaa", Locale.getDefault()).format(c.getLastMod().longValue()));


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
                    ArrayList<ModelConversation> filteredResultsData = new ArrayList<ModelConversation>();



                    for(ModelConversation data : originalData)
                    {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional

                        try{
                            Object[] sms = DB.selectConditional(Tables.sms, ModelSMS.class,
                                    new Object[][] {
                                            { "conversation", data.getId() }
                                    },
                            null);

                            for(Object msg : sms){
                                if(((ModelSMS) msg).getBody().toLowerCase().contains(constraint.toString().toLowerCase())||
                                        data.getAddress().toLowerCase().contains(constraint.toString().toLowerCase())
                                        ) {
                                    filteredResultsData.add(data);
                                    break;
                                }
                            }

                        }catch (Exception e){ e.printStackTrace();}



                       /*
                        if(data.getAddress().toLowerCase().contains(constraint.toString().toLowerCase()))
                        {
                            filteredResultsData.add(data);
                        }
                        */
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
                filteredData = (ArrayList<ModelConversation>)results.values;
                notifyDataSetChanged();

            }


        };
    }

    public void sortAscending () {

        Collections.sort(filteredData, new Comparator<ModelConversation>() {
            @Override
            public int compare(ModelConversation lhs, ModelConversation rhs) {


                //return Long.compare(lhs.getLastMod(), rhs.getLastMod());
                return  Long.valueOf(rhs.getLastMod()).compareTo(Long.valueOf(lhs.getLastMod()));
            }

                /* This comparator will sort AppDetail objects alphabetically. */


        });
    }
    public int getCount()
    {
        return filteredData.size();
    }

    //This should return a data object, not an int
    public ModelConversation getItem(int position)
    {
        return filteredData.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }
}
