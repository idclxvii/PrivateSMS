package ph.edu.sscrmnl.privatesms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ph.edu.sscrmnl.privatesms.databasemodel.ModelContacts;

/**
 * Created by IDcLxViI on 2/8/2016.
 */
public class ContactsAdapter extends ArrayAdapter<ModelContacts> implements Filterable{

    Context context;
    int layoutResourceId;

    //Two data sources, the original data and filtered data
    List<ModelContacts> filteredData = null;
    List<ModelContacts> originalData = null;


    public ContactsAdapter(Context context, int resource, List<ModelContacts> data) {
        super(context, resource, data);
        this.context = context;
        this.layoutResourceId = resource;
        this.originalData = data;
        this.filteredData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ModelContacts c = filteredData.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layoutResourceId, parent, false);
        }
        // Lookup view for data population

        ((TextView)convertView).setText(c.getName());
        // Populate the data into the template view using the data object

        // Return the completed view to render on screen
        return convertView;
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
                    ArrayList<ModelContacts> filteredResultsData = new ArrayList<ModelContacts>();

                    for(ModelContacts data : originalData)
                    {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
                        if(data.getName().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                data.getAddress().contains(constraint.toString()))
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
                filteredData = (ArrayList<ModelContacts>)results.values;
                notifyDataSetChanged();

            }


        };
    }

    public void sortAscending () {

        Collections.sort(filteredData, new Comparator<ModelContacts>() {
            @Override
            public int compare(ModelContacts lhs, ModelContacts rhs) {

                return lhs.getName().compareTo(rhs.getName());
            }

                /* This comparator will sort AppDetail objects alphabetically. */


        });
    }

    public int getCount()
    {
        return filteredData.size();
    }

    //This should return a data object, not an int
    public ModelContacts getItem(int position)
    {
        return filteredData.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }
}
