package galua.com.notepad;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateNote extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextInputEditText textInput;

    private Node node;
    private ArrayList<Node> nodes;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private Intent intentOfActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_main);
        navigationView.setNavigationItemSelectedListener(this);

        loadData();

        textInput = (TextInputEditText)findViewById(R.id.textInput);

        intentOfActivity = getIntent();

        if(getIntent().hasExtra("nodeedit")) {
            Node editNode = (Node) getIntent().getSerializableExtra("nodeedit");
            textInput.setText(editNode.getText());
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
                        if(intentOfActivity.hasExtra("position") && intentOfActivity.hasExtra("nodeedit")){
                            intentOfActivity.removeExtra("nodeedit");
                            intentOfActivity.removeExtra("position");
                        }
                        CreateNote.super.onBackPressed();
                    }

                })
                .setNegativeButton("Нет", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_button) {
            if(!(textInput.getText().length()==0)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Сохранение");

                final EditText input = new EditText(this);
                builder.setView(input);

                if(intentOfActivity.hasExtra("nodeedit")) {
                    Node editNode = (Node) getIntent().getSerializableExtra("nodeedit");
                    input.setText(editNode.getName());
                }

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().length() != 0) {
                            node = new Node(input.getText().toString(), textInput.getText().toString(), new SimpleDateFormat("dd.MM.yyyy; HH:mm:ss").format(Calendar.getInstance().getTime()));
                            nodes.add(node);

                            if(intentOfActivity.hasExtra("position")){
                                int pos = (int) getIntent().getSerializableExtra("position");
                                nodes.remove(pos);

                                intentOfActivity.removeExtra("nodeedit");
                                intentOfActivity.removeExtra("position");
                            }

                            saveData();
                            Toast toast = Toast.makeText(getApplicationContext(), "Записка сохранена", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNote.this);
                            builder.setTitle("Пустой текст!").setMessage("Введите название записки, а потом сохраняйте!").setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                    }
                });
                builder.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateNote.this);
                builder.setTitle("Пустой текст!")
                        .setMessage("Введите текст записки, а потом сохраняйте!")
                        .setCancelable(false)
                        .setNegativeButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_create_note) {

        } else if (id == R.id.nav_author) {
            if(intentOfActivity.hasExtra("position") && intentOfActivity.hasExtra("nodeedit")){
                intentOfActivity.removeExtra("nodeedit");
                intentOfActivity.removeExtra("position");
            }
            Intent intent = new Intent(CreateNote.this,AboutAuthor.class);
            startActivity(intent);
        } else if (id == R.id.nav_list_notes) {
            if(intentOfActivity.hasExtra("position") && intentOfActivity.hasExtra("nodeedit")){
                intentOfActivity.removeExtra("nodeedit");
                intentOfActivity.removeExtra("position");
            }
            Intent intent = new Intent(CreateNote.this, ListNodes.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void saveData(){
        prefs = getSharedPreferences("shared preferences",MODE_PRIVATE);
        editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(nodes);
        editor.putString("task list", json);
        editor.apply();
    }

    public void loadData(){
        prefs = getSharedPreferences("shared preferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("task list", null);
        Type type = new TypeToken<ArrayList<Node>>() {}.getType();
        nodes = gson.fromJson(json, type);

        if(nodes==null){
            nodes = new ArrayList<Node>();
        }
    }

}
