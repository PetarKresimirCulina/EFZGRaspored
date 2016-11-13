package hr.efzg.pculina.efzgraspored;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Petar-Kresimir Culina on 3/1/2016.
 */
public class SchedulesAdapter extends BaseAdapter {

    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources res;
    ListModelSchedules tempValues = null;
    int i = 0;

    /*************
     * CustomAdapter Constructor
     *****************/
    public SchedulesAdapter(Activity a, ArrayList d, Resources resLocal) {

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
        return true;
    }

    /*********
     * Create a holder Class to contain inflated xml file elements
     *********/
    public static class ViewHolder {

        public TextView time_from;
        public TextView time_to;
        public TextView course;
        public TextView tutor;
        public TextView course_room_details;
        public TextView course_weeks;
        public TextView day;
        public boolean selected;
        public RelativeLayout daybg;


    }

    /******
     * Depends upon data size called for each row , Create each ListView row
     *****/
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.listview_item, parent, false);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.time_from = (TextView) vi.findViewById(R.id.timeFrom);

            holder.time_to = (TextView) vi.findViewById(R.id.timeTo);
            holder.course = (TextView) vi.findViewById(R.id.className);


            holder.tutor = (TextView) vi.findViewById(R.id.classTutor);

            holder.course_room_details = (TextView) vi.findViewById(R.id.classTypePlace);

            holder.course_weeks = (TextView) vi.findViewById(R.id.classDate);

            holder.day = (TextView) vi.findViewById(R.id.DAY1);

            holder.daybg = (RelativeLayout) vi.findViewById(R.id.lday);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() <= 0) {
            holder.time_from.setText("-");
            holder.time_to.setText("-");
            holder.course.setText("-");
            holder.tutor.setText("-");
            holder.course_room_details.setText("-");
            holder.course_weeks.setText("-");
            holder.day.setText("-");

        } else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (ListModelSchedules) data.get(position);

            /************  Set Model values in Holder elements ***********/
            int t_int = tempValues.getUnitsInDay();
            t_int = (int) (t_int * 0.5);
            t_int = 7 + t_int; // npr 17
            int t_int2 = tempValues.getDuration();
            t_int2 = (int) (t_int2 * 0.5); // npr 2
            t_int2 = t_int + t_int2; // npr 17 + 2 = 19 traje do 19 sati

            String tf = String.valueOf(t_int);
            if (tf.length() < 2) {
                tf = "0" + tf;
            }

            String tt = String.valueOf(t_int2);
            if (tt.length() < 2) {
                tt = "0" + tt;
            }

            holder.time_from.setText(tf);
            holder.time_to.setText(tt);
            holder.course.setText(tempValues.getCourseName());

            //if (tempValues.getTutorCode().equals("none")) {
            holder.tutor.setText(parent.getContext().getString(R.string.tutorname_1, tempValues.getTutorName(), tempValues.getTutorSurname()));
            /*} else {
                holder.tutor.setText(parent.getContext().getString(R.string.tutorname_2, tempValues.getTutorCode(), tempValues.getTutorName(), tempValues.getTutorSurname()));
            }*/

            if (tempValues.getExecutionType() == 1 || tempValues.getExecutionType() == 3) {
                if (Objects.equals(tempValues.getGroupName(), "null")) {
                    holder.course_room_details.setText(parent.getContext().getString(R.string.schedule_type_1, tempValues.getRoomName()));
                } else {
                    holder.course_room_details.setText(parent.getContext().getString(R.string.schedule_type_11, tempValues.getRoomName(), tempValues.getGroupName()));
                }

            } else {
                if (Objects.equals(tempValues.getGroupName(), "null")) {
                    holder.course_room_details.setText(parent.getContext().getString(R.string.schedule_type_2, tempValues.getRoomName()));
                } else {
                    holder.course_room_details.setText(parent.getContext().getString(R.string.schedule_type_21, tempValues.getRoomName(), tempValues.getGroupName()));
                }
            }

            holder.course_weeks.setText(parent.getContext().getString(R.string.schedule_week, tempValues.getPeriod()));

            switch (tempValues.getDay()) {
                case 0:
                    holder.day.setText(R.string.mon);
                    holder.daybg.setBackgroundColor(ContextCompat.getColor(vi.getContext(), R.color.day1));
                    break;
                case 1:
                    holder.day.setText(R.string.tue);
                    holder.daybg.setBackgroundColor(ContextCompat.getColor(vi.getContext(), R.color.day2));
                    break;
                case 2:
                    holder.day.setText(R.string.wed);
                    holder.daybg.setBackgroundColor(ContextCompat.getColor(vi.getContext(), R.color.day3));
                    break;
                case 3:
                    holder.day.setText(R.string.thu);
                    holder.daybg.setBackgroundColor(ContextCompat.getColor(vi.getContext(), R.color.day4));
                    break;
                case 4:
                    holder.day.setText(R.string.fri);
                    holder.daybg.setBackgroundColor(ContextCompat.getColor(vi.getContext(), R.color.day5));
                    break;
                case 5:
                    holder.day.setText(R.string.sat);
                    holder.daybg.setBackgroundColor(ContextCompat.getColor(vi.getContext(), R.color.colorPrimary));
                    break;
            }

        }
        return vi;
    }

    public String getCourseName() {
        return tempValues.getCourseName();
    }

}
