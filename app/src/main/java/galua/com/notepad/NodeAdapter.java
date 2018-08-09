package galua.com.notepad;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class NodeAdapter extends BaseAdapter {

    private Context ctx;
    private LayoutInflater lInflater;
    private ArrayList<Node> nodes;

    private TextView emptyText;

    public NodeAdapter(Context context, ArrayList<Node> _nodes, TextView _emptyText) {
        ctx = context;
        nodes = _nodes;
        emptyText = _emptyText;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return nodes.size();
    }

    @Override
    public Object getItem(int position) {
        return nodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_item, parent, false);
        }

        Node p = getNode(position);

        ((TextView) view.findViewById(R.id.titleNode)).setText(p.getName());
        ((TextView) view.findViewById(R.id.dateNode)).setText(p.getDate());

        ImageView deleteButton = (ImageView) view.findViewById(R.id.deleteButton);
        deleteButton.setTag(position);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View position) {
                final int pos = (int)position.getTag();
                new AlertDialog.Builder(ctx)
                    .setTitle("Удаление записки")
                    .setMessage("Вы уверены, что хотите удалить записку?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            nodes.remove(pos);
                            NodeAdapter.this.notifyDataSetChanged();
                            if(nodes.isEmpty()){
                                emptyText.setVisibility(View.VISIBLE);
                            }
                            else{
                                emptyText.setVisibility(View.GONE);
                            }
                            saveData();
                            Toast toast = Toast.makeText(ctx, "Записка удалена", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    })
                    .setNegativeButton("Нет", null)
                    .show();

            }
        });
        return view;
    }

    public Node getNode(int position) {
        return ((Node) getItem(position));
    }

    public void saveData(){
        SharedPreferences prefs = ctx.getSharedPreferences("shared preferences",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(nodes);
        editor.putString("task list", json);
        editor.apply();
    }
}
