package com.example.estacionamento;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.estacionamento.model.Veiculo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;

public class AdicionarVeiculos extends AppCompatActivity {

    private EditText edtPlaca, edtAno, edtMensalidade;
    private Spinner spinnerProprietarios;
    private Button btnAdicionarVeiculo, btnListar, btnBack;
    private AsyncHttpClient cliente;
    private int fkProprietario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_veiculo);

        // Inicializando os componentes
        edtPlaca = findViewById(R.id.edtPlaca);
        edtAno = findViewById(R.id.edtAno);
        edtMensalidade = findViewById(R.id.edtMensalidade);
        spinnerProprietarios = findViewById(R.id.spinnerProprietarios);
        btnAdicionarVeiculo = findViewById(R.id.btnAdicionarVeiculo);
        btnListar = findViewById(R.id.button2);
        btnBack = findViewById(R.id.button4);

        cliente = new AsyncHttpClient();

        // Carregar proprietários no Spinner
        carregarProprietarios();

        btnAdicionarVeiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    cadastrarVeiculo();
                }
            }
        });

        btnBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdicionarVeiculos.this,  MainActivity.class);
                startActivity(i);
            }
        });

        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdicionarVeiculos.this, Lista_Veiculo.class);
                startActivity(i);
            }
        });
    }

    private void carregarProprietarios() {
        String url = "http://192.168.1.9:8081/proprietario";
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AdicionarVeiculos.this, android.R.layout.simple_spinner_dropdown_item);
                        adapter.add("Selecione o proprietário");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int id = obj.getInt("id_proprietario");
                            String nome = obj.getString("nome");
                            adapter.add(id + " - " + nome);
                        }

                        spinnerProprietarios.setAdapter(adapter);

                        // Setar o Listener para capturar o ID do proprietário
                        spinnerProprietarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) { // Ignorar "Selecione o proprietário"
                                    String selectedItem = (String) parent.getItemAtPosition(position);
                                    String[] parts = selectedItem.split(" - ");
                                    fkProprietario = Integer.parseInt(parts[0]);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                fkProprietario = -1;
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AdicionarVeiculos.this, "Erro ao carregar proprietários", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validarCampos() {
        if (edtPlaca.getText().toString().isEmpty() ||
                edtAno.getText().toString().isEmpty() ||
                edtMensalidade.getText().toString().isEmpty() ||
                fkProprietario == -1) {

            Toast.makeText(this, "Por favor, preencha todos os campos corretamente", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void cadastrarVeiculo() {
        String url = "http://192.168.1.9:8081/veiculo";

        JSONObject parametros = new JSONObject();
        try {
            parametros.put("placa", edtPlaca.getText().toString());
            parametros.put("ano", Integer.parseInt(edtAno.getText().toString()));
            parametros.put("mensalidade", Double.parseDouble(edtMensalidade.getText().toString()));
            parametros.put("fk_proprietario", fkProprietario);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = new StringEntity(parametros.toString(), ContentType.APPLICATION_JSON);
        cliente.post(AdicionarVeiculos.this, url, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    Toast.makeText(AdicionarVeiculos.this, "Veículo adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                    edtPlaca.setText(null);
                    edtAno.setText(null);
                    edtMensalidade.setText(null);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AdicionarVeiculos.this, "Erro ao adicionar veículo: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
