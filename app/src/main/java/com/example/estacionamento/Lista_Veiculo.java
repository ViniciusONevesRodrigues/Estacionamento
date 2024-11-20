package com.example.estacionamento;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.estacionamento.model.Proprietario;
import com.example.estacionamento.model.Veiculo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Lista_Veiculo extends AppCompatActivity {
    Activity context;
    ListView lsveiculos;
    AsyncHttpClient cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_veiculo);

        context = Lista_Veiculo.this;
        lsveiculos = findViewById(R.id.lvVeiculos);
        cliente = new AsyncHttpClient();

        carregaVeiculos();
    }

    public void carregaVeiculos() {
        String url = "http://192.168.1.9:8081/veiculo";

        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    listarTodosVeiculos(new String(responseBody));
                } else {
                    Toast.makeText(Lista_Veiculo.this,
                            "Falha ao carregar veículos. Código de status: " + statusCode,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(Lista_Veiculo.this,
                        "Erro ao carregar veículos: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void listarTodosVeiculos(String resposta) {
        final ArrayList<Veiculo> lista = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(resposta);

            for (int i = 0; i < jsonArray.length(); i++) {
                Veiculo v = new Veiculo();
                v.setId_veiculo(jsonArray.getJSONObject(i).getInt("id_veiculo"));
                v.setPlaca(jsonArray.getJSONObject(i).getString("placa"));
                v.setAno(jsonArray.getJSONObject(i).getInt("ano"));
                v.setMensalidade(jsonArray.getJSONObject(i).getDouble("mensalidade"));
                v.setFk_proprietario(jsonArray.getJSONObject(i).getInt("fk_proprietario"));

                lista.add(v);
            }

            AdapterVeiculo adapter = new AdapterVeiculo(context, R.layout.adapter_veiculo, R.id.id_veiculo, lista);
            lsveiculos.setAdapter(adapter);
        } catch (JSONException erro) {
            Log.d("erro", "erro" + erro);
        }

        lsveiculos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Veiculo v = lista.get(i);
                StringBuilder b = new StringBuilder();
                b.append("id_veiculo: ").append(v.getId_veiculo()).append("\n");
                b.append("Placa: ").append(v.getPlaca()).append("\n");
                b.append("Ano: ").append(v.getAno()).append("\n");
                b.append("Mensalidade: ").append(v.getMensalidade()).append("\n");
                b.append("Proprietario: ").append(v.getFk_proprietario()).append("\n");

                AlertDialog.Builder a = new AlertDialog.Builder(Lista_Veiculo.this);
                a.setCancelable(true);
                a.setTitle("Detalhes do veiculo");
                a.setMessage(b.toString());
                a.setIcon(R.drawable.ic_launcher_background);

                a.setNegativeButton("Editar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent i2 = new Intent(Lista_Veiculo.this, AlterarVeiculo.class);
                        i2.putExtra("veiculo", v);
                        startActivity(i2);
                    }
                });
                a.show();
            }
        });
    }
}
