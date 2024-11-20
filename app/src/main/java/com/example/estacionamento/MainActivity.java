package com.example.estacionamento;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.estacionamento.model.Proprietario;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends AppCompatActivity {
    EditText edtnome, edtcpf;
    Button btncad, btnListar, btnNext;
    AsyncHttpClient cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtnome = (EditText) findViewById(R.id.edtnome);
        edtcpf = (EditText) findViewById(R.id.edtcpf);
        cliente = new AsyncHttpClient();
        //Botão de cadastrar
        btncad = (Button) findViewById(R.id.button1);
        btncad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtnome.getText().toString().isEmpty() || edtcpf.getText().toString()
                        .isEmpty()) {
                    Toast.makeText(MainActivity.this, "Existes campos  em brancos!", Toast.LENGTH_SHORT).show();
                } else {
                    Proprietario obj = new Proprietario();
                    obj.setNome(edtnome.getText().toString());
                    obj.setCpf(edtcpf.getText().toString());
                    cadastrarProprietario(obj);
                }
            }
        });
        //Botão de Listar os proprietários
        btnListar = (Button) findViewById(R.id.button2);
        btnListar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Lista_Proprietario.class);
                startActivity(i);
            }
        });


        btnNext = (Button) findViewById(R.id.button3);
        btnNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,  AdicionarVeiculos.class);
                startActivity(i);
            }
        });
    }

    public void cadastrarProprietario(Proprietario obj) {
        String url;
        url = "http://192.168.1.9:8081/proprietario";
        // Crie um objeto JSON para os parâmetros
        JSONObject parametros = new JSONObject();
        try {
            parametros.put("nome", obj.getNome());
            parametros.put("cpf", obj.getCpf());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = new StringEntity(parametros.toString(),
                ContentType.APPLICATION_JSON);
        cliente.post(MainActivity.this, url, entity,
                "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        if (statusCode == 200) {
                            Toast.makeText(MainActivity.this, "Proprietário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            edtnome.setText(null);
                            edtcpf.setText(null);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(MainActivity.this,
                                "Erro ao cadastrar proprietário: " +
                                        error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
