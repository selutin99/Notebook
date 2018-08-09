package galua.com.notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ListNodes extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NodeAdapter nodeAdapter;
    private ArrayList<Node> nodes;
    private Node node;

    private TextView emptyText;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_nodes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("Notepad");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        emptyText = (TextView)findViewById(R.id.emptyText);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_main);
        navigationView.setNavigationItemSelectedListener(this);

        loadData();

        nodeAdapter = new NodeAdapter(this, nodes, emptyText);
        saveData();

        if(nodes.isEmpty()){
            emptyText.setVisibility(View.VISIBLE);
        }
        else{
            emptyText.setVisibility(View.GONE);
        }

        listView = (ListView) findViewById(R.id.list_of_nodes);
        listView.setAdapter(nodeAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                node = nodes.get(position);
                Intent i = new Intent(ListNodes.this, CreateNote.class);
                i.putExtra("nodeedit", node);
                i.putExtra("position", position);
                startActivity(i);
            }
        });
    }

    public void saveData(){
        SharedPreferences prefs = getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(nodes);
        editor.putString("task list", json);
        editor.apply();
    }

    public void loadData(){
        SharedPreferences prefs = getSharedPreferences("shared preferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("task list", null);
        Type type = new TypeToken<ArrayList<Node>>() {}.getType();
        nodes = gson.fromJson(json, type);

        if(nodes==null){
            nodes = new ArrayList<Node>();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Выход из приложения")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListNodes.super.onBackPressed();
                    }

                })
                .setNegativeButton("Нет", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_nodes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_create_note) {
            Intent i = new Intent(ListNodes.this,CreateNote.class);
            startActivity(i);
        } else if (id == R.id.nav_author) {
            Intent i = new Intent(ListNodes.this,AboutAuthor.class);
            startActivity(i);
        } else if (id == R.id.nav_list_notes) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
