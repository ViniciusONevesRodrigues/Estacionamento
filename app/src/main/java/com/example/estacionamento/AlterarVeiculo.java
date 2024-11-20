package com.example.estacionamento;

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

import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;

public class AlterarVeiculo extends AppCompatActivity {

    private EditText edtPlaca, edtAno, edtMensalidade;
    private Spinner spinnerProprietarios;
    private Button btnAlterarVeiculo;
    private AsyncHttpClient cliente;
    private int fkProprietario;
    private Veiculo veiculo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_veiculo);

        // Inicializando componentes da UI
        edtPlaca = findViewById(R.id.edtPlaca);
        edtAno = findViewById(R.id.edtAno);
        edtMensalidade = findViewById(R.id.edtMensalidade);
        spinnerProprietarios = findViewById(R.id.spinnerProprietarios);
        btnAlterarVeiculo = findViewById(R.id.btnAdicionarVeiculo);

        cliente = new AsyncHttpClient();

        // Receber dados do veículo selecionado
        veiculo = (Veiculo) getIntent().getSerializableExtra("veiculo");

        // Preencher os campos com os dados do veículo
        preencherCampos(veiculo);

        // Carregar proprietários no Spinner
        carregarProprietarios();

        // Botão de alteração
        btnAlterarVeiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos()) {
                    alterarVeiculo();
                }
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AlterarVeiculo.this, android.R.layout.simple_spinner_dropdown_item);
                        adapter.add("Selecione o proprietário");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int id = obj.getInt("id_proprietario");
                            String nome = obj.getString("nome");
                            adapter.add(id + " - " + nome);
                        }

                        spinnerProprietarios.setAdapter(adapter);

                        // Pré-selecionar o proprietário atual
                        preSelecionarProprietario(veiculo.getFk_proprietario());

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
                Toast.makeText(AlterarVeiculo.this, "Erro ao carregar proprietários", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void preencherCampos(Veiculo veiculo) {
        edtPlaca.setText(veiculo.getPlaca());
        edtAno.setText(String.valueOf(veiculo.getAno()));
        edtMensalidade.setText(String.valueOf(veiculo.getMensalidade()));
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

    private void alterarVeiculo() {
        String url = "http://192.168.1.9:8081/veiculo/" + veiculo.getId_veiculo();

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
        cliente.put(AlterarVeiculo.this, url, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    Toast.makeText(AlterarVeiculo.this, "Veículo alterado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AlterarVeiculo.this, "Erro ao alterar veículo: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void preSelecionarProprietario(int fkProprietario) {
        for (int i = 1; i < spinnerProprietarios.getCount(); i++) {
            String item = (String) spinnerProprietarios.getItemAtPosition(i);
            if (item.startsWith(fkProprietario + " - ")) {
                spinnerProprietarios.setSelection(i);
                break;
            }
        }
    }
}
