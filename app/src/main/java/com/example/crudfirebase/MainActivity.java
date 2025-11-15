package com.example.crudfirebase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextInputEditText etDescripcion, etCantidad, etCategoria;
    Button btnAgregar;
    TextView tvResult;
    DatabaseReference databaseGastos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etDescripcion = findViewById(R.id.etDescripcion);
        etCantidad = findViewById(R.id.etCantidad);
        etCategoria = findViewById(R.id.etCategoria);
        btnAgregar = findViewById(R.id.btnAgregar);
        tvResult = findViewById(R.id.tvResult);

        // Apunta al nodo "gastos" en tu base de datos
        databaseGastos = FirebaseDatabase.getInstance().getReference("gastos");

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarGasto();
            }
        });

        // Escuchar cambios en la base de datos y actualizar el TextView
        databaseGastos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvResult.setText("");
                StringBuilder gastosTexto = new StringBuilder("Gastos Registrados:\n");

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Gasto gasto = postSnapshot.getValue(Gasto.class);
                    if (gasto != null) {
                        gastosTexto.append("- ")
                                .append(gasto.descripcion).append(": $")
                                .append(gasto.cantidad).append(" (")
                                .append(gasto.categoria).append(") \n[ID: ")
                                .append(gasto.id.substring(0, 4))
                                .append("...]\n");
                    }
                }
                tvResult.setText(gastosTexto.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error al leer datos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void agregarGasto() {
        String descripcion = etDescripcion.getText().toString().trim();
        String cantidadStr = etCantidad.getText().toString().trim();
        String categoria = etCategoria.getText().toString().trim();

        if (descripcion.isEmpty() || cantidadStr.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "La cantidad debe ser un número", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generar un ID único para el nuevo gasto
        String id = databaseGastos.push().getKey();

        Gasto gasto = new Gasto(id, descripcion, cantidad, categoria);

        // Guardar el gasto en Firebase
        if (id != null) {
            databaseGastos.child(id).setValue(gasto).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Gasto agregado", Toast.LENGTH_SHORT).show();
                    // Limpiar campos
                    etDescripcion.setText("");
                    etCantidad.setText("");
                    etCategoria.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Error al agregar gasto", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}