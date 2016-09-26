package hr.efzg.pculina.efzgraspored;

import android.app.Dialog;
import android.app.LauncherActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    ListView list;
    GroupsAdapter adapter;
    SchedulesAdapter adapterschedules;
    public MainActivity CustomListView = null;
    public ArrayList<ListModelGroups> CustomListViewValuesArr = new ArrayList<ListModelGroups>();
    public ArrayList<ListModelSchedules> CustomListViewValuesArrSchedules = new ArrayList<ListModelSchedules>();
    public int PROGRAM, YEAR, SUBLEVEL;

    private String SERVER_HOST = "http://efzg.pkculina.com/php/api/";
    private String SERVER_PROGRAMS = SERVER_HOST + "android_fetch_data.php";
    private String SERVER_GROUPS = SERVER_HOST + "get_groups.php";
    private String SERVER_SCHEDULES = SERVER_HOST + "get_schedules.php";

    private int[] prog_id;
    private int[] programs_id = new int[5];
    private String[] programs_name = new String[5];
    private int[] programs_years = new int[5];

    String[] group_name;
    int[] groups_id;
    int[] parent_id;

    private int[] day, units_in_day, duration, room_id, group_id, execution_type;
    private String[] period, course_name, tutor_name, tutor_surname, tutor_code, room_name;

    ProgressBar loading;
    WebView ISVU;
    RelativeLayout noInternet;
    RelativeLayout noSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView titleText = (TextView) header.findViewById(R.id.navTitle);
        titleText.setText(Html.fromHtml(getString(R.string.navTitle)));

        navigationView.setItemIconTintList(null);
        loading = (ProgressBar) findViewById(R.id.progressBarLoading);
        loading.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.colorLV3),
                android.graphics.PorterDuff.Mode.SRC_IN);

        ISVU = (WebView) findViewById(R.id.isvuWeb);
        ISVU.setVisibility(View.INVISIBLE);
        noInternet = (RelativeLayout) findViewById(R.id.noInternet);
        noInternet.setVisibility(View.INVISIBLE);
        noSchedule = (RelativeLayout) findViewById(R.id.MyScheduleEmpty);
        noSchedule.setVisibility(View.INVISIBLE);
        list = (ListView) findViewById(R.id.listViewCustom);
        list.setDivider(null);
        loadMenuItems();
        registerForContextMenu(list);
    }

    private void getGroups(final int programid, final int year, final boolean loadCached) {
        ArrayList<LauncherActivity.ListItem> listMockData = new ArrayList<LauncherActivity.ListItem>();

        RequestQueue queue;
        StringRequest sr;
        list.setAdapter(null);

        PROGRAM = programid;
        YEAR = year;
        SUBLEVEL = 0;
        if (!loadCached) {
            CustomListViewValuesArr.clear();
            list.setAdapter(null);
        }

        queue = Volley.newRequestQueue(this);
        sr = new StringRequest(Request.Method.POST, SERVER_GROUPS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String sep[] = response.split("<>");

                groups_id = new int[sep.length];
                group_name = new String[sep.length];
                parent_id = new int[sep.length];

                for (int i = 0; i < sep.length; i++) {
                    final ListModelGroups sched = new ListModelGroups();

                    String[] sep_si = sep[i].split("&");

                    if (sep_si[0].length() > 0) {
                        sep_si[0] = sep_si[0].trim();
                        groups_id[i] = Integer.parseInt(sep_si[0]);
                        group_name[i] = sep_si[1];
                        parent_id[i] = Integer.parseInt(sep_si[2]);

                        if (parent_id[i] == groups_id[i] && !group_name[i].toLowerCase().contains("dodatna")) {
                            sched.setGroupName(group_name[i]);
                            sched.setID(groups_id[i]);
                            sched.setParentID(groups_id[i]);

                            CustomListViewValuesArr.add(sched);
                        }
                    }
                }

                Resources res = getResources();
                CustomListView = MainActivity.this;
                //list = (ListView) findViewById(R.id.listViewCustom);

                if (programid == 1 && year == 4) // fix za izborne predmete na 3. i 4. godini - abecedni red
                {
                    Collections.sort(CustomListViewValuesArr, new Comparator<ListModelGroups>() {
                        @Override
                        public int compare(ListModelGroups lhs, ListModelGroups rhs) {
                            return lhs.getGroupName().compareToIgnoreCase(rhs.getGroupName());
                        }
                    });
                }

                adapter = new GroupsAdapter(CustomListView, CustomListViewValuesArr, res);

                if (CustomListViewValuesArr.size() > 0) {
                    list.setAdapter(adapter);
                } else {
                    list.setAdapter(null);
                }

                hideLoading();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoading();
                list.setVisibility(View.INVISIBLE);
                ISVU.setVisibility(View.INVISIBLE);
                noInternet.setVisibility(View.VISIBLE);
                Button retry = (Button) findViewById(R.id.noInternetBtn);

                retry.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        noInternet.setVisibility(View.INVISIBLE);
                        list.setVisibility(View.VISIBLE);
                        getGroups(programid, year, loadCached);
                    }
                });
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("programid", String.valueOf(programid));
                params.put("year", String.valueOf(year));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                return new HashMap<String, String>();
            }
        };
        showLoading();
        if (!loadCached) {
            queue.add(sr);
        } else {
            list = (ListView) findViewById(R.id.listViewCustom);
            adapter = new GroupsAdapter(CustomListView, CustomListViewValuesArr, getResources());
            list.setAdapter(adapter);
            hideLoading();
        }



        DrawerLayout d = (DrawerLayout) findViewById(R.id.drawer_layout);
        d.closeDrawers();
    }

    public void loadMySchedule(){
        noSchedule.setVisibility(View.INVISIBLE);
        SQLiteDatabase mydatabase = openOrCreateDatabase("MyScheduleDB", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Classes(" +
                "id INTEGER primary key AUTOINCREMENT," +
                "day INTEGER," +
                "units_in_day INTEGER," +
                "duration INTEGER," +
                "room_id INTEGER," +
                "group_id INTEGER," +
                "execution_type INTEGER," +
                "period VARCHAR," +
                "course_name VARCHAR," +
                "tutor_name VARCHAR," +
                "tutor_surname VARCHAR," +
                "tutor_code VARCHAR," +
                "room_name VARCHAR" +
                ")");

        String selectQuery = "SELECT * FROM Classes";
        Cursor c = mydatabase.rawQuery(selectQuery, null);


        ISVU.setVisibility(View.INVISIBLE);

        CustomListViewValuesArrSchedules.clear();
        list.setAdapter(null);

        while (c.moveToNext()) {
            Log.d("COURSE NAME", c.getString(c.getColumnIndex("course_name")));
            final ListModelSchedules sched = new ListModelSchedules();

            sched.setDay(c.getInt(c.getColumnIndex("day")));
            sched.setUnitsInDay(c.getInt(c.getColumnIndex("units_in_day")));
            sched.setDuration(c.getInt(c.getColumnIndex("duration")));
            sched.setRoomId(c.getInt(c.getColumnIndex("room_id")));
            sched.setPeriod(c.getString(c.getColumnIndex("period")));
            sched.setGroupId(c.getInt(c.getColumnIndex("group_id")));
            sched.setCourseName(c.getString(c.getColumnIndex("course_name")));
            sched.setExecutionType(c.getInt(c.getColumnIndex("execution_type")));
            sched.setTutorName(c.getString(c.getColumnIndex("tutor_name")));
            sched.setTutorSurname(c.getString(c.getColumnIndex("tutor_surname")));
            sched.setTutorCode(c.getString(c.getColumnIndex("tutor_code")));
            sched.setRoomName(c.getString(c.getColumnIndex("room_name")));

            CustomListViewValuesArrSchedules.add(sched);

        }
        c.close();

        Collections.sort(CustomListViewValuesArrSchedules, new Comparator<ListModelSchedules>() {
            @Override
            public int compare(ListModelSchedules lhs, ListModelSchedules rhs) {
                int result = Integer.compare(lhs.getDay(), rhs.getDay());
                if (result == 0) {
                    result = Integer.compare(lhs.getUnitsInDay(), rhs.getUnitsInDay());
                }
                return result;
            }
        });

        CustomListView = MainActivity.this;
        adapterschedules = new SchedulesAdapter(CustomListView, CustomListViewValuesArrSchedules, getResources());

        if (c.getCount() > 0) {
            list.setAdapter(adapterschedules);
        }

        if (c.getCount() == 0) {
            noSchedule.setVisibility(View.VISIBLE);
        }

        SUBLEVEL = 5;
        hideLoading();
        setTitle(R.string.navCat3);
    }

    public void loadMenuItems() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, SERVER_PROGRAMS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String sep[] = response.split("<>");

                try {
                    prog_id = new int[sep.length];
                    programs_id = new int[sep.length];
                    programs_name = new String[sep.length];
                    programs_years = new int[sep.length];


                    for (int i = 0; i < sep.length; i++) {
                        String[] sep_si = sep[i].split("&");

                        prog_id[i] = i;
                        Log.d("Response", sep_si[0]);
                        sep_si[0] = sep_si [0].trim();
                        programs_id[i] = Integer.parseInt(sep_si[0]);
                        programs_name[i] = sep_si[1];
                        programs_years[i] = Integer.parseInt(sep_si[2]);

                        createSubMenus(prog_id[i], programs_id[i], programs_name[i], programs_years[i]);
                    }
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this, "Dogodila se greška!\n" + e.toString(), Toast.LENGTH_LONG).show();
                }

                hideLoading();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (SUBLEVEL != 5) {
                    hideLoading();
                    list.setVisibility(View.INVISIBLE);
                    ISVU.setVisibility(View.INVISIBLE);
                    noInternet.setVisibility(View.VISIBLE);
                    Button retry = (Button) findViewById(R.id.noInternetBtn);

                    retry.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            noInternet.setVisibility(View.INVISIBLE);
                            list.setVisibility(View.VISIBLE);
                            loadMenuItems();
                        }
                    });
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("method", "0");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                return new HashMap<String, String>();
            }
        };
        showLoading();
        queue.add(sr);
        loadMySchedule();
    }

    public void createSubMenus(int id, int prog_id, final String name, int years) {
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        Menu m = nav.getMenu();

        SubMenu mi;
        if (name.toLowerCase().contains("izborni")) {
            mi = m.addSubMenu(prog_id, id, 99, name);
        } else {
            mi = m.addSubMenu(name);
        }

        for (int i = 1; i <= years; i++) {
            if (!name.toLowerCase().contains("izborni") && !name.toLowerCase().contains("diplomski") && !name.toLowerCase().contains("stručni")) // fix za izborne PE i diplomski TE JE STRUČNI IZBAČEN
            {
                final int i2 = i;
                final int prog = prog_id;
                mi.add(i + ". godina").setIcon(R.drawable.ic_menu_calendar_red).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        setTitle(item.getTitle());
                        noSchedule.setVisibility(View.INVISIBLE);
                        getGroups(prog, i2, false);
                        list.setVisibility(View.VISIBLE);
                        ISVU.setVisibility(View.INVISIBLE);
                        ISVU.loadUrl("about:blank");
                        return true;
                    }
                });
            } else if (name.toLowerCase().contains("diplomski") && i == 5 && !name.toLowerCase().contains("stručni")) // fix za diplomski
            {
                final int i2 = i;
                final int prog = prog_id;
                mi.add("Diplomski smjerovi").setIcon(R.drawable.ic_menu_calendar_red).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        setTitle(item.getTitle());
                        noSchedule.setVisibility(View.INVISIBLE);
                        getGroups(prog, i2, false);
                        list.setVisibility(View.VISIBLE);
                        ISVU.setVisibility(View.INVISIBLE);
                        ISVU.loadUrl("about:blank");
                        return true;
                    }
                });
            } else if (name.toLowerCase().contains("izborni") && i == 4 && !name.toLowerCase().contains("stručni")) // fix izborni pe
            {
                final int i2 = i;
                final int prog = prog_id;
                mi.add("Izborni predmeti").setIcon(R.drawable.ic_menu_calendar_red).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        setTitle(item.getTitle());
                        noSchedule.setVisibility(View.INVISIBLE);
                        getGroups(prog, i2, false);
                        list.setVisibility(View.VISIBLE);
                        ISVU.setVisibility(View.INVISIBLE);
                        ISVU.loadUrl("about:blank");
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (SUBLEVEL > 0 && SUBLEVEL < 5) {
                getGroups(PROGRAM, YEAR, true);
            } else {
                super.onBackPressed();
            }

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav7) {

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.about_dialog);

            TextView abouttv = (TextView) dialog.findViewById(R.id.aboutDetail);
            String about = abouttv.getText() + " " + BuildConfig.VERSION_NAME;
            abouttv.setText(about);

            Button btnClose = (Button) dialog.findViewById(R.id.dialogClose);
            TextView legal = (TextView) dialog.findViewById(R.id.legalLink);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            legal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    final Dialog dialog = new Dialog(v.getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.legal_dialog);

                    Button btnClose = (Button) dialog.findViewById(R.id.dialogClose);

                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

            TextView clientLink = (TextView)findViewById(R.id.clientLink);
            TextView devLink = (TextView) findViewById(R.id.devLink);

            clientLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.efzg.hr"));
                    startActivity(browserIntent);
                }
            });

            devLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.pkculina.com"));
                    startActivity(browserIntent);
                }
            });

            dialog.show();
        } else if (id == R.id.nav5) {
            showLoading();
            list.setVisibility(View.INVISIBLE);
            ISVU.setVisibility(View.VISIBLE);

            final ProgressBar p = loading;
            ISVU.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    hideLoading();
                }
            });
            ISVU.getSettings().setJavaScriptEnabled(true);
            ISVU.loadUrl("https://www.isvu.hr/studomat/prijava");
            setTitle(R.string.navCat5);
        } else if (id == R.id.nav3) {
            loadMySchedule();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listViewCustom && SUBLEVEL > 0) {
            if (SUBLEVEL == 5) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.lv_menu2, menu);
            } else {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.lv_menu, menu);
            }

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.myScheduleCMI:
                ListModelSchedules a = CustomListViewValuesArrSchedules.get(info.position);

                int day, units_in_day, duration, room_id, group_id, execution_type;
                String period, course_name, tutor_name, tutor_surname, tutor_code, room_name;

                day = a.getDay();
                units_in_day = a.getUnitsInDay();
                duration = a.getDuration();
                room_id = a.getRoomId();
                group_id = a.getGroupId();
                execution_type = a.getExecutionType();

                period = a.getPeriod();
                course_name = a.getCourseName();
                tutor_name = a.getTutorName();
                tutor_surname = a.getTutorSurname();
                tutor_code = a.getTutorCode();
                room_name = a.getRoomName();


                SQLiteDatabase mydatabase = openOrCreateDatabase("MyScheduleDB", MODE_PRIVATE, null);
                mydatabase.execSQL("INSERT INTO Classes (" +
                        "day, " +
                        "units_in_day, " +
                        "duration, " +
                        "room_id, " +
                        "group_id, " +
                        "execution_type, " +
                        "period, " +
                        "course_name, " +
                        "tutor_name, " +
                        "tutor_surname, " +
                        "tutor_code, " +
                        "room_name" +
                        ") VALUES (" +
                        day + ", " +
                        units_in_day + ", " +
                        duration + ", " +
                        room_id + ", " +
                        group_id + ", " +
                        execution_type + ", '" +
                        period + "', '" +
                        course_name + "', '" +
                        tutor_name + "', '" +
                        tutor_surname + "', '" +
                        tutor_code + "', '" +
                        room_name
                        + "')");
                // day (int)
                // units in day (int)
                // from
                // to
                // class_name
                // tutor_name
                // class_details
                // weeks

                Toast.makeText(MainActivity.this, R.string.addToMyScheduleSuccess, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.myScheduleCMIRemove:
                SQLiteDatabase db = openOrCreateDatabase("MyScheduleDB", MODE_PRIVATE, null);
                ListModelSchedules b = CustomListViewValuesArrSchedules.get(info.position);
                db.delete("Classes", "day" + "=" + b.getDay() + " and " + "units_in_day" + "=" + String.valueOf(b.getUnitsInDay()) + " and " + "course_name" + "='" + b.getCourseName() + "'", null);

                loadMySchedule();
                Toast.makeText(MainActivity.this, R.string.removeFromMyScheduleSuccess, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        loading.setVisibility(View.INVISIBLE);
    }

    public void loadGroupSchedule(final int gid) {
        SUBLEVEL = 1;
        CustomListViewValuesArrSchedules.clear();
//        list.notify();
        list = (ListView) findViewById(R.id.listViewCustom);
        list.setAdapter(null);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, SERVER_SCHEDULES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String sep[] = response.split("<>");
                Log.d("Group resonse", response);
                Log.d("Group response lenth", String.valueOf(response.length()));

                day = new int[sep.length];
                units_in_day = new int[sep.length];
                duration = new int[sep.length];
                room_id = new int[sep.length];
                group_id = new int[sep.length];
                execution_type = new int[sep.length];

                period = new String[sep.length];
                course_name = new String[sep.length];
                tutor_name = new String[sep.length];
                tutor_surname = new String[sep.length];
                tutor_code = new String[sep.length];
                room_name = new String[sep.length];

                if (response.length() > 0) {
                    for (int i = 0; i < sep.length; i++) {
                        final ListModelSchedules sched = new ListModelSchedules();

                        String[] sep_si = sep[i].split("&");

                        if (!Objects.equals(sep_si[0], "") && sep_si[0].length() > 0)   // greška prilikom dohvata, php echo iz nekog razloga dodaje
                        //prazna polja/znakove
                        {
                            sep_si[0] = sep_si[0].trim();
                            day[i] = Integer.parseInt(sep_si[0]);
                            sched.setDay(day[i]);

                            units_in_day[i] = Integer.parseInt(sep_si[1]);
                            sched.setUnitsInDay(units_in_day[i]);

                            duration[i] = Integer.parseInt(sep_si[2]);
                            sched.setDuration(duration[i]);

                            room_id[i] = Integer.parseInt(sep_si[3]);
                            sched.setRoomId(room_id[i]);

                            period[i] = sep_si[4];
                            sched.setPeriod(period[i]);

                            group_id[i] = Integer.parseInt(sep_si[5]);
                            sched.setGroupId(group_id[i]);

                            course_name[i] = sep_si[6];
                            sched.setCourseName(course_name[i]);

                            execution_type[i] = Integer.parseInt(sep_si[7]);
                            sched.setExecutionType(execution_type[i]);

                            tutor_name[i] = sep_si[8];
                            sched.setTutorName(tutor_name[i]);

                            tutor_surname[i] = sep_si[9];
                            sched.setTutorSurname(tutor_surname[i]);

                            tutor_code[i] = sep_si[10];
                            sched.setTutorCode(tutor_code[i]);

                            room_name[i] = sep_si[11];
                            sched.setRoomName(room_name[i]);

                            CustomListViewValuesArrSchedules.add(sched);
                        }


                    }

                    Resources res = getResources();
                    CustomListView = MainActivity.this;
                    list = (ListView) findViewById(R.id.listViewCustom);

                    Collections.sort(CustomListViewValuesArrSchedules, new Comparator<ListModelSchedules>() {
                        @Override
                        public int compare(ListModelSchedules lhs, ListModelSchedules rhs) {
                            int result = Integer.compare(lhs.getDay(), rhs.getDay());
                            if (result == 0) {
                                result = Integer.compare(lhs.getUnitsInDay(), rhs.getUnitsInDay());
                            }
                            return result;
                        }
                    });


                    adapterschedules = new SchedulesAdapter(CustomListView, CustomListViewValuesArrSchedules, res);
                    list.setAdapter(adapterschedules);
                    hideLoading();
                } else {
                    list.setAdapter(null);
                    hideLoading();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (SUBLEVEL != 5) {
                    hideLoading();
                    list.setVisibility(View.INVISIBLE);
                    ISVU.setVisibility(View.INVISIBLE);
                    noInternet.setVisibility(View.VISIBLE);
                    Button retry = (Button) findViewById(R.id.noInternetBtn);

                    retry.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            noInternet.setVisibility(View.INVISIBLE);
                            list.setVisibility(View.VISIBLE);
                            loadGroupSchedule(gid);
                        }
                    });
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("groupid", String.valueOf(gid));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                return new HashMap<String, String>();
            }
        };
        showLoading();
        queue.add(sr);
    }
}