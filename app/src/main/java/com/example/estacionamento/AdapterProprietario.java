package com.example.estacionamento;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.estacionamento.model.Proprietario;
import java.util.ArrayList;
public class AdapterProprietario extends ArrayAdapter<Proprietario> {
    int groupid;
    ArrayList<Proprietario> lista;
    Context context;
    public AdapterProprietario(Context context, int vg, int id,
                               ArrayList<Proprietario> lista) {
        super(context, vg, id, lista);
        this.context = context;
        groupid = vg;
        this.lista = lista;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)

                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(groupid, parent, false);
        TextView textName = (TextView)
                itemView.findViewById(R.id.id_proprietario);
        textName.setText(lista.get(position).getNome());
        return itemView;
    }
}