package com.example.estacionamento;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.estacionamento.model.Veiculo;

import java.util.ArrayList;

public class AdapterVeiculo extends ArrayAdapter<Veiculo> {
    int groupid;
    ArrayList<Veiculo> lista;
    Context context;

    public AdapterVeiculo(Context context, int vg, int id, ArrayList<Veiculo> lista) {
        super(context, vg, id, lista);
        this.context = context;
        this.groupid = vg;
        this.lista = lista;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(groupid, parent, false);

        TextView textPlaca = itemView.findViewById(R.id.id_veiculo);
        textPlaca.setText(lista.get(position).getPlaca());

        return itemView;
    }
}
