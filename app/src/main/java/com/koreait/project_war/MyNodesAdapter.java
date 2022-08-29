package com.koreait.project_war;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MyNodesAdapter extends ArrayAdapter {

    Context context;
    int resource;
    List<NodeVO> my_node_list;

    TextView nodename;

    public MyNodesAdapter(@NonNull Context context, int resource, @NonNull List<NodeVO> my_node_list) {
        super(context, resource, my_node_list);
        this.context = context;
        this.resource = resource;
        this.my_node_list = my_node_list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater vlinf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = vlinf.inflate(resource, null);

        nodename = convertView.findViewById(R.id.nodename);

        nodename.setText(my_node_list.get(position).getNode_name().toString());

        return convertView;
    }
}
