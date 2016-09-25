package hr.efzg.pculina.efzgraspored;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Petar-Kresimir Culina on 2/28/2016.
 */
public class GroupsAdapter extends BaseAdapter {

    private ArrayList data;
    private static LayoutInflater inflater = null;
    private Context mContext;
    public Resources res;
    ListModelGroups tempValues = null;
    int i = 0;

    /*************
     * CustomAdapter Constructor
     *****************/
    public GroupsAdapter(Activity a, ArrayList d, Resources resLocal) {

        /********** Take passed values **********/
        /********** Declare Used Variables */
        data = d;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) a.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    /********
     * What is the size of Passed Arraylist Size
     ************/
    public int getCount() {

        if (data.size() <= 0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    /*********
     * Create a holder Class to contain inflated xml file elements
     *********/
    public static class ViewHolder {

        public TextView text;

    }

    /******
     * Depends upon data size called for each row , Create each ListView row
     *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.listview_item_group, parent, false);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.text = (TextView) vi.findViewById(R.id.classDay);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() <= 0) {
            holder.text.setText("-");

        } else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (ListModelGroups) data.get(position);

            /************  Set Model values in Holder elements ***********/

            holder.text.setText(tempValues.getGroupName());

            final int gid = tempValues.getID();
            final String name = tempValues.getGroupName();
            vi.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((MainActivity) mContext).setTitle(name);
                    ((MainActivity) mContext).loadGroupSchedule(gid);

                }
            });

        }
        this.mContext = vi.getContext();
        return vi;
    }

}
