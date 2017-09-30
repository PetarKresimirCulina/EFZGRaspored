package hr.efzg.pculina.efzgraspored;

import android.Manifest;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
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
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    ListView list;
    private GroupsAdapter adapter;
    private SchedulesAdapter adapterschedules;
    private MainActivity CustomListView = null;
    private final ArrayList<ListModelGroups> CustomListViewValuesArr = new ArrayList<ListModelGroups>();
    private final ArrayList<ListModelSchedules> CustomListViewValuesArrSchedules = new ArrayList<>();
    public static ArrayList<ListModelSearch> CustomSearchValues = new ArrayList<>();
    private int PROGRAM;
    private int YEAR;
    private int SUBLEVEL;

    private final String SERVER_HOST = "http://efzg.pkculina.com/php/api/";
    private final String SERVER_PROGRAMS = SERVER_HOST + "android_fetch_data.php";
    private final String SERVER_GROUPS = SERVER_HOST + "get_groups.php";
    private final String SERVER_SCHEDULES = SERVER_HOST + "get_schedules.php";
    private final String SERVER_DURATION = SERVER_HOST + "get_duration.php";
    private final String SERVER_ALL = SERVER_HOST + "get_all_for_search.php";

    private int[] prog_id;
    private int[] programs_id = new int[5];
    private String[] programs_name = new String[5];
    private int[] programs_years = new int[5];

    private String[] group_name;
    private int[] groups_id;
    private int[] parent_id;

    private int[] day, units_in_day, duration, room_id, group_id, execution_type;
    private String[] period, course_name, tutor_name, tutor_surname, tutor_code, room_name;

    private ProgressBar loading;
    private WebView ISVU;
    private RelativeLayout noInternet;
    private RelativeLayout noSchedule;
    private boolean showSync = true;
    private boolean showSearch = false;

    private MenuItem addCalendar = null;
    private MenuItem searchItem = null;

    public boolean ListSelectionInProgress = false;
    private long startD;
    private long endD;
    public String lastProgramName;


    SearchAdapter adaptersearch;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView titleText = (TextView) header.findViewById(R.id.navTitle);
        titleText.setText(Html.fromHtml(getString(R.string.navTitle)));

        navigationView.setItemIconTintList(null);
        loading = (ProgressBar) findViewById(R.id.progressBarLoading);
        loading.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.colorLV3),
                PorterDuff.Mode.SRC_IN);

        ISVU = (WebView) findViewById(R.id.isvuWeb);
        ISVU.setVisibility(View.INVISIBLE);
        ISVU.setFocusable(true);
        ISVU.setFocusableInTouchMode(true);

        noInternet = (RelativeLayout) findViewById(R.id.noInternet);
        noInternet.setVisibility(View.INVISIBLE);
        noSchedule = (RelativeLayout) findViewById(R.id.MyScheduleEmpty);
        noSchedule.setVisibility(View.INVISIBLE);
        list = (ListView) findViewById(R.id.listViewCustom);
        list.setDivider(null);
        loadMenuItems();
        //    registerForContextMenu(list);
        showSyncMenuItem(true);
        loadDuration();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

    private void showSyncMenuItem(boolean val) {
        showSync = val;
        showSearch = false;
        supportInvalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(showSearch) {
            MenuInflater i = getMenuInflater();
            i.inflate(R.menu.search, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);

            SearchView searchView = null;

            if (searchItem != null) {
                searchView = (SearchView) searchItem.getActionView();
            }
            if (searchView != null) {
                searchView.setQueryHint(getResources().getString(R.string.search));
                searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));

                //final android.widget.Filter filter = adaptersearch.getFilter();

                SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adaptersearch.getFilter().filter(newText);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        adaptersearch.getFilter().filter(query);
                        return true;
                    }
                };
                searchView.setOnQueryTextListener(queryTextListener);
                list.invalidateViews();
            }
        }

        if (showSync) {
            addCalendar = menu.add(Menu.NONE, 9999, Menu.NONE, R.string.addToCalendar).setIcon(R.drawable.ic_menu_calendar_white);
            addCalendar.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } else if (!showSync && addCalendar != null) { // makni ovo kasnije u novim verzijama
            menu.removeItem(addCalendar.getItemId());
            addCalendar = null;
        }

        return true;
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void getGroups(final int programid, final int year, final boolean loadCached) {

        Log.d("programID", String.valueOf(programid));
        Log.d("year", String.valueOf(year));

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

                Log.d("getGrPerYr", response);
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
                Map<String, String> params = new HashMap<>();
                params.put("programid", String.valueOf(programid));
                params.put("year", String.valueOf(year));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                return new HashMap<>();
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

    private void loadMySchedule() {
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
                "room_name VARCHAR," +
                "group_name VARCHAR" +
                ")");

        String selectQuery = "SELECT * FROM Classes";
        Cursor c = mydatabase.rawQuery(selectQuery, null);


        ISVU.setVisibility(View.INVISIBLE);

        CustomListViewValuesArrSchedules.clear();
        list.setAdapter(null);

        while (c.moveToNext()) {
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
            sched.setGroupName(c.getString(c.getColumnIndex("group_name")));

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

        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle(Integer.toString(list.getCheckedItemCount()));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                Log.d("Called", "true");
                MenuInflater inflater = mode.getMenuInflater();
                if (SUBLEVEL == 1) {
                    inflater.inflate(R.menu.action_mode_add, menu);
                } else if (SUBLEVEL == 5) // SATNICA
                {
                    inflater.inflate(R.menu.action_mode_remove, menu);
                }

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                int c;
                switch (item.getItemId()) {
                    case R.id.selAll:
                        for (int i = 0; i < list.getAdapter().getCount(); i++) {
                            list.setItemChecked(i, true);
                        }
                        break;
                    case R.id.addSch:
                        c = 0;
                        for (int i = 0; i < list.getAdapter().getCount(); i++) {
                            if (list.isItemChecked(i)) {
                                addToSchedule(i, 0);
                                c++;
                            }
                        }
                        if (c > 1) {
                            Toast.makeText(MainActivity.this, R.string.addToMyScheduleSuccessPlural, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.addToMyScheduleSuccess, Toast.LENGTH_SHORT).show();
                        }
                        mode.finish();
                        break;
                    case R.id.remSch:
                        c = 0;
                        if (list.getAdapter() != null) {
                            for (int i = 0; i < list.getAdapter().getCount(); i++) {
                                if (list.isItemChecked(i)) {
                                    removeFromSchedule(i);
                                    c++;
                                }
                            }
                            if (c > 1) {
                                Toast.makeText(MainActivity.this, R.string.removeFromMyScheduleSuccessPlural, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, R.string.removeFromMyScheduleSuccess, Toast.LENGTH_SHORT).show();
                            }
                            loadMySchedule();
                            mode.finish();
                        }

                        break;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                ListSelectionInProgress = false;
            }
        });

        setTitle(R.string.navCat3);
    }

    @SuppressWarnings("WeakerAccess")
    private void loadMenuItems() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, SERVER_PROGRAMS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("RESPONSE", response.toString());
                String sep[] = response.split("<>");

                try {
                    prog_id = new int[sep.length];
                    programs_id = new int[sep.length];
                    programs_name = new String[sep.length];
                    programs_years = new int[sep.length];


                    for (int i = 0; i < sep.length; i++) {
                        String[] sep_si = sep[i].split("&");
                        Log.d("entry", sep[i]);

                        prog_id[i] = i;
                        sep_si[0] = sep_si[0].trim();
                        programs_id[i] = Integer.parseInt(sep_si[0]);
                        programs_name[i] = sep_si[1];
                        programs_years[i] = Integer.parseInt(sep_si[2]);

                        createSubMenus(prog_id[i], programs_id[i], programs_name[i], programs_years[i]);
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, R.string.internet, Toast.LENGTH_SHORT).show();
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
                Map<String, String> params = new HashMap<>();
                params.put("method", "0");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                return new HashMap<>();
            }
        };
        showLoading();
        queue.add(sr);
        loadMySchedule();
    }

    private void createSubMenus(int id, int prog_id, final String name, int years) {
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        Menu m = nav.getMenu();

        SubMenu mi; // PRVO DODAJ GLAVNE PODIZBORNIKE
        if (name.toLowerCase().contains("izborni")) {
            mi = m.addSubMenu(prog_id, id, 99, name);
        } else {
            mi = m.addSubMenu(name);
        }
        // DODAJ VRIJEDNOSTI PODIZBORNICIMA
        for (int i = 1; i <= years; i++) {
            if (!name.toLowerCase().contains("izborni") && !name.toLowerCase().contains("diplomski") && !name.toLowerCase().contains("stručni")) // fix za izborne PE i diplomski TE JE STRUČNI IZBAČEN
            {
                final int i2 = i;
                final int prog = prog_id;
                mi.add(i + ". godina").setIcon(R.drawable.ic_menu_calendar_red).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        setTitle(item.getTitle());
                        showSyncMenuItem(false);
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
                        showSyncMenuItem(false);
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
                        showSyncMenuItem(false);
                        noSchedule.setVisibility(View.INVISIBLE);
                        getGroups(prog, i2, false);
                        list.setVisibility(View.VISIBLE);
                        ISVU.setVisibility(View.INVISIBLE);
                        ISVU.loadUrl("about:blank");
                        return true;
                    }
                });
            } else if (name.toLowerCase().contains("stručni")) // fix stručni studij
            {
                final int i2 = i;
                final int prog = prog_id;
                mi.add("Stručni studij " + i).setIcon(R.drawable.ic_menu_calendar_red).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        setTitle(item.getTitle());
                        showSyncMenuItem(false);
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
                setTitle(lastProgramName);
            } else {
                super.onBackPressed();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (addCalendar != null && item.getItemId() == addCalendar.getItemId()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.addToCalendarTitle);
            builder.setMessage(R.string.addToCalendarQuestion);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (startD != 0 && endD != 0) {
                        pushToCalendar();
                    } else {
                        Toast.makeText(MainActivity.this, R.string.internet, Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //TODO
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        hideSoftKeyboard();
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

            TextView clientLink = (TextView) dialog.findViewById(R.id.clientLink);
            TextView devLink = (TextView) dialog.findViewById(R.id.devLink);

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
            showSyncMenuItem(false);
            showLoading();
            list.setVisibility(View.INVISIBLE);
            ISVU.setVisibility(View.VISIBLE);

            ISVU.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    hideLoading();
                }
            });
            ISVU.getSettings().setJavaScriptEnabled(true);
            ISVU.loadUrl("https://www.isvu.hr/studomat/prijava");
            setTitle(R.string.navCat5);
        } else if (id == R.id.nav3) {
            showSyncMenuItem(true);
            loadMySchedule();
        } else if (id == R.id.nav4) {
            showSyncMenuItem(false);
            showSearch = true;
            noSchedule.setVisibility(View.INVISIBLE);
            loadAllSchedules();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loading.setVisibility(View.INVISIBLE);
    }

    private void removeFromSchedule(int pos) {
        SQLiteDatabase db = openOrCreateDatabase("MyScheduleDB", MODE_PRIVATE, null);
        ListModelSchedules b = CustomListViewValuesArrSchedules.get((Integer) list.getItemAtPosition(pos));
        db.delete("Classes", "day" + "=" + b.getDay() + " and " + "units_in_day" + "=" + String.valueOf(b.getUnitsInDay()) + " and " + "course_name" + "='" + b.getCourseName() + "'", null);
    }

    private void addToSchedule(int pos, int type) { //type 0 ListModelSchedules | type 1 ListModelSearch

        int day, units_in_day, duration, room_id, group_id, execution_type;
        String period, course_name, tutor_name, tutor_surname, tutor_code, room_name, group_name;

        if(type == 0) {
            ListModelSchedules a = CustomListViewValuesArrSchedules.get((Integer) list.getItemAtPosition(pos));

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
            group_name = a.getGroupName();
        }
        else {
            ListModelSearch a = adaptersearch.getItemF(pos);

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
            group_name = a.getGroupName();
        }


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
                "room_name, " +
                "group_name" +
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
                room_name + "', '" +
                group_name
                + "')");
        // day (int)
        // units in day (int)
        // from
        // to
        // class_name
        // tutor_name
        // class_details
        // weeks
    }

    private void loadDuration() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, SERVER_DURATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.length() > 0) {
                    String sep[] = response.split("&");

                    startD = Long.parseLong(sep[0].trim());
                    endD = Long.parseLong(sep[1].trim());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, R.string.internet, Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(sr);
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
                group_name = new String[sep.length];

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

                            group_name[i] = sep_si[12];
                            if (!Objects.equals(group_name[i], "null")) {
                                group_name[i] = group_name[i].replaceAll("(?i)(?<=[0-9])_(?=[A-Z])", " ");
                                sched.setGroupName(group_name[i]);
                            } else {
                                sched.setGroupName("null");
                            }


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
                Map<String, String> params = new HashMap<>();
                params.put("groupid", String.valueOf(gid));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                return new HashMap<>();
            }
        };
        showLoading();
        queue.add(sr);


        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle(Integer.toString(list.getCheckedItemCount()));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                ListSelectionInProgress = true;
                MenuInflater inflater = mode.getMenuInflater();
                if (SUBLEVEL == 1) {
                    inflater.inflate(R.menu.action_mode_add, menu);
                } else if (SUBLEVEL == 5) // SATNICA
                {
                    inflater.inflate(R.menu.action_mode_remove, menu);
                }

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int c;
                switch (item.getItemId()) {
                    case R.id.selAll:
                        for (int i = 0; i < list.getAdapter().getCount(); i++) {
                            list.setItemChecked(i, true);
                        }
                        break;
                    case R.id.addSch:
                        c = 0;
                        for (int i = 0; i < list.getAdapter().getCount(); i++) {
                            if (list.isItemChecked(i)) {
                                addToSchedule(i, 0);
                                c++;
                            }
                        }
                        if (c > 1) {
                            Toast.makeText(MainActivity.this, R.string.addToMyScheduleSuccessPlural, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.addToMyScheduleSuccess, Toast.LENGTH_SHORT).show();
                        }
                        mode.finish();
                        break;
                    case R.id.remSch:
                        c = 0;
                        if (list.getAdapter() != null) {
                            for (int i = 0; i < list.getAdapter().getCount(); i++) {
                                if (list.isItemChecked(i)) {
                                    removeFromSchedule(i);
                                    c++;
                                }
                            }
                            if (c > 1) {
                                Toast.makeText(MainActivity.this, R.string.removeFromMyScheduleSuccessPlural, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, R.string.removeFromMyScheduleSuccess, Toast.LENGTH_SHORT).show();
                            }
                            loadMySchedule();
                            mode.finish();
                        }

                        break;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                ListSelectionInProgress = false;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pushToCalendar();
                } else {
                    Toast.makeText(MainActivity.this, "Morate dozvoliti zapis u kalendar kako bi ova funkcionalnost bila omogućena", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void pushToCalendar() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR}, 1);
        } else {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
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
                            "room_name VARCHAR," +
                            "group_name VARCHAR" +
                            ")");

                    String selectQuery = "SELECT * FROM Classes";
                    Cursor c = mydatabase.rawQuery(selectQuery, null);

                    if (c != null && c.getCount() > 0) {
                        try {
                            while (c.moveToNext()) {
                                int setDay = c.getInt(c.getColumnIndex("day"));

                                int setStartTime = c.getInt(c.getColumnIndex("units_in_day"));
                                int setEndTime = c.getInt(c.getColumnIndex("duration"));
                                String room = c.getString(c.getColumnIndex("room_name")) + " - Ekonomski fakultet Zagreb\n" +
                                        "Trg Johna Kennedyja 6, 10000, Zagreb, Croatia";
                                String title = c.getString(c.getColumnIndex("course_name"));
                                String desc;

                                switch (c.getInt(c.getColumnIndex("execution_type"))) {
                                    case 1:
                                        desc = "Predavanje";
                                        break;
                                    case 3:
                                        desc = "Predavanje";
                                        break;
                                    default:
                                        desc = "Seminar";
                                        break;
                                }
                                if (!Objects.equals(c.getString(c.getColumnIndex("group_name")), "null")) {
                                    desc = desc + " " + c.getString(c.getColumnIndex("group_name")) + "\n" + "Predavač: " + c.getString(c.getColumnIndex("tutor_name")) + " " + c.getString(c.getColumnIndex("tutor_surname"));
                                } else {
                                    desc = desc + "\n" + "Predavač: " + c.getString(c.getColumnIndex("tutor_name")) + " " + c.getString(c.getColumnIndex("tutor_surname"));
                                }

                                switch (setDay) {
                                    case 0:
                                        setDay = Calendar.MONDAY;
                                        break;
                                    case 1:
                                        setDay = Calendar.TUESDAY;
                                        break;
                                    case 2:
                                        setDay = Calendar.WEDNESDAY;
                                        break;
                                    case 3:
                                        setDay = Calendar.THURSDAY;
                                        break;
                                    case 4:
                                        setDay = Calendar.FRIDAY;
                                        break;
                                    case 5:
                                        setDay = Calendar.SATURDAY;
                                        break;
                                    case 6:
                                        setDay = Calendar.SUNDAY;
                                        break;
                                }

                                setStartTime = (int) (setStartTime * 0.5);
                                setStartTime = 7 + setStartTime; // npr 17

                                setEndTime = (int) (setEndTime * 0.5); // npr 2
                                setEndTime = setStartTime + setEndTime; // npr 17 + 2 = 19 traje do 19 sati

                                if (setDay != 0 && title != null && desc != null && room != null && setStartTime != 0 && setEndTime != 0) {
                                    addToCalendar(setDay, title, desc, room, setStartTime, setEndTime);
                                }
                            }
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(MainActivity.this, R.string.addToCalendarSuccess, Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (final Exception e) {

                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, R.string.addToCalendarEmpty, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    c.close();
                }
            });
        }
    }

    private void addToCalendar(int DAY_WEEK, String TITLE, String DESC, String LOC, int hour_s, int hour_e) {

        /* Construct event details */
        long startMillis;
        long endMillis;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);

        Calendar now = Calendar.getInstance();

        now.setTimeInMillis(startD);
        now.setFirstDayOfWeek(Calendar.MONDAY );

        //Set the holding calendar variable
        Calendar beginTime = Calendar.getInstance();

        if(now.get(Calendar.DAY_OF_WEEK) != DAY_WEEK )
        {
            //Let's assume its Wednesday (2) and we need monday (0)
            int diff = DAY_WEEK - now.get(Calendar.DAY_OF_WEEK);

            if(diff < 0)
            {
                //We go back in time because the current day in week is bigger than the needed one
                beginTime = now; //set it to current date
                beginTime.add(Calendar.DAY_OF_MONTH, -diff);
            }
            else
            {
                //difference is lower than 0. Let's say it's WED (2) and we need Friday (4), diff=2
                beginTime = now;
                beginTime.add(Calendar.DAY_OF_MONTH, diff);
            }
        }
        else
        {
            beginTime = now;
        }
/*


        int day = now.get(Calendar.DAY_OF_WEEK);
        int daym = now.get(Calendar.DAY_OF_MONTH);
        int start_day;

        if (day != DAY_WEEK) {

            if (daym - day < 0) {
                start_day = daym + DAY_WEEK;
            } else {
                start_day = daym - (day - DAY_WEEK);
            }

        } else {
            start_day = daym;
        }
*/

        //EVENT DATA
        /*Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month, start_day, hour_s, 0);*/
        //Set starting hour and minutes
        beginTime.set(Calendar.HOUR_OF_DAY, hour_s);
        beginTime.set(Calendar.MINUTE, 0);
        startMillis = beginTime.getTimeInMillis();
      //  Calendar endTime = Calendar.getInstance();
       // endTime.set(year, month, start_day, hour_e, 0);
        beginTime.set(Calendar.HOUR_OF_DAY, hour_e);
        beginTime.set(Calendar.MINUTE, 0);
        endMillis = beginTime.getTimeInMillis();

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(endD);
        final String RRuleEnd = dateFormat.format(cal.getTime());

        // Insert Event
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        TimeZone timeZone = TimeZone.getDefault();
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, endMillis);
        values.put(Events.EVENT_TIMEZONE, timeZone.getID());


        values.put(Events.RRULE, "FREQ=WEEKLY;UNTIL=" + RRuleEnd);
        values.put(Events.HAS_ALARM, 1);

        values.put(Events.TITLE, TITLE);
        values.put(Events.DESCRIPTION, DESC);
        values.put(Events.EVENT_LOCATION, LOC);
        values.put(Events.CALENDAR_ID, 1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, 101);

        }

        Uri uri = cr.insert(Events.CONTENT_URI, values);


        // Retrieve ID for new event
        String eventID = uri.getLastPathSegment();


        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminders.put(CalendarContract.Reminders.MINUTES, 15);

        cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public void loadAllSchedules() {
        SUBLEVEL = 6;
        CustomSearchValues.clear();
//        list.notify();
        list = (ListView) findViewById(R.id.listViewCustom);
        list.setAdapter(null);


        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, SERVER_ALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String sep[] = response.split("<>");

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
                group_name = new String[sep.length];

                if (response.length() > 0) {
                    for (int i = 0; i < sep.length; i++) {
                        final ListModelSearch sched = new ListModelSearch();

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

                            group_name[i] = sep_si[12];
                            if (!Objects.equals(group_name[i], "null")) {
                                group_name[i] = group_name[i].replaceAll("(?i)(?<=[0-9])_(?=[A-Z])", " ");
                                sched.setGroupName(group_name[i]);
                            } else {
                                sched.setGroupName("null");
                            }


                            CustomSearchValues.add(sched);
                        }


                    }

                    Resources res = getResources();
                    CustomListView = MainActivity.this;
                    list = (ListView) findViewById(R.id.listViewCustom);

                    Collections.sort(CustomSearchValues, new Comparator<ListModelSearch>() {

                        @Override
                        public int compare(ListModelSearch lhs, ListModelSearch rhs) {
                            int result = Integer.compare(lhs.getDay(), rhs.getDay());
                            if (result == 0) {
                                result = Integer.compare(lhs.getUnitsInDay(), rhs.getUnitsInDay());
                            }
                            return result;
                        }
                    });


                    adaptersearch = new SearchAdapter(CustomListView, CustomSearchValues, res);
                    list.setAdapter(adaptersearch);


                    hideLoading();
                } else {
                    list.setAdapter(null);
                    hideLoading();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (SUBLEVEL == 6) {
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
                            loadAllSchedules();
                        }
                    });
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                return new HashMap<>();
            }
        };
        showLoading();
        queue.add(sr);


        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle(Integer.toString(list.getCheckedItemCount()));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                ListSelectionInProgress = true;
                MenuInflater inflater = mode.getMenuInflater();
                if (SUBLEVEL == 6) { // search ssatnicu
                    inflater.inflate(R.menu.action_mode_add, menu);
                } else if (SUBLEVEL == 5) // SATNICA
                {
                    inflater.inflate(R.menu.action_mode_remove, menu);
                }

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int c;
                switch (item.getItemId()) {
                    case R.id.selAll:
                        for (int i = 0; i < list.getAdapter().getCount(); i++) {
                            list.setItemChecked(i, true);
                        }
                        break;
                    case R.id.addSch:
                        c = 0;
                        Log.d("list adapter size", String.valueOf(list.getAdapter().getCount()));
                        for (int i = 0; i < list.getAdapter().getCount(); i++) {
                            if (list.isItemChecked(i)) {
                                addToSchedule(i, 1);
                                c++;
                            }
                        }
                        if (c > 1) {
                            Toast.makeText(MainActivity.this, R.string.addToMyScheduleSuccessPlural, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.addToMyScheduleSuccess, Toast.LENGTH_SHORT).show();
                        }
                        mode.finish();
                        break;
                    case R.id.remSch:
                        c = 0;
                        if (list.getAdapter() != null) {
                            for (int i = 0; i < list.getAdapter().getCount(); i++) {
                                if (list.isItemChecked(i)) {
                                    removeFromSchedule(i);
                                    c++;
                                }
                            }
                            if (c > 1) {
                                Toast.makeText(MainActivity.this, R.string.removeFromMyScheduleSuccessPlural, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, R.string.removeFromMyScheduleSuccess, Toast.LENGTH_SHORT).show();
                            }
                            loadMySchedule();
                            mode.finish();
                        }

                        break;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                ListSelectionInProgress = false;
            }
        });
        setTitle(R.string.navCat4);
    }
}

