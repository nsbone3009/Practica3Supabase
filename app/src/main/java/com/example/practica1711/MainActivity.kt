package com.example.practica1711

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.practica1711.data.SupabaseManager
import com.example.practica1711.models.Alumno
import com.example.practica1711.utils.SupabaseErrorHandler
import com.facebook.shimmer.ShimmerFrameLayout
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var txtListaAlumnos: TextView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        txtListaAlumnos = findViewById(R.id.txtListaAlumnos)
        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout)
        consultarAlumnos()
    }

    private fun consultarAlumnos() {
        shimmerFrameLayout.visibility = View.VISIBLE
        shimmerFrameLayout.startShimmer()
        txtListaAlumnos.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val alumnos = SupabaseManager.client
                    .from("alumnos")
                    .select {
                        order("nombres", Order.ASCENDING)
                    }
                    .decodeList<Alumno>()

                txtListaAlumnos.text = if (alumnos.isEmpty()) {
                    "No se encontraron alumnos."
                } else {
                    alumnos.joinToString("\n\n") { alumno ->
                        "Nombre: ${alumno.nombres}\n" +
                                "Paralelo: ${alumno.paralelo}\n" +
                                "Correo: ${alumno.correo ?: "Sin registrar"}\n" +
                                "Teléfono: ${alumno.telefono ?: "Sin registrar"}"
                    }
                }
            } catch (e: Exception) {
                SupabaseErrorHandler.show(this@MainActivity, e)
            } finally {
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE
                txtListaAlumnos.visibility = View.VISIBLE
            }
        }
    }
}
