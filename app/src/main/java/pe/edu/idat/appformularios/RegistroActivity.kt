package pe.edu.idat.appformularios

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import pe.edu.idat.appformularios.comunes.AppMensaje
import pe.edu.idat.appformularios.comunes.TipoMensaje
import pe.edu.idat.appformularios.databinding.ActivityRegistroBinding

class RegistroActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityRegistroBinding
    private var estadocivil = ""
    private val listaPreferencias = ArrayList<String>()
    private val listaPersonas = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnlistar.setOnClickListener(this)
        binding.btnregistrar.setOnClickListener(this)
        ArrayAdapter.createFromResource(
            this, R.array.estado_civil,
            android.R.layout.simple_spinner_item
        ).also {
            adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spestadocivil.adapter = adapter
        }
        binding.spestadocivil.onItemSelectedListener = this
    }
    override fun onClick(vista: View) {
        if(vista is CheckBox){
            agregarQuitarPreferenciaSeleccionada(vista)
        } else {
            when (vista.id) {
                R.id.btnregistrar -> registrarPersona()
                R.id.btnlistar -> startActivity(Intent(applicationContext,
                    ListadoActivity::class.java).apply {
                        putExtra("listapersonas", listaPersonas)
                })
            }
        }
    }

    private fun agregarQuitarPreferenciaSeleccionada(vista: CheckBox) {
        val checkbox = vista as CheckBox
        if(checkbox.isChecked){
            when(checkbox.id){
                R.id.cbdeportes -> listaPreferencias.add(vista.text.toString())
                R.id.cbmusica -> listaPreferencias.add(vista.text.toString())
                R.id.cbotros -> listaPreferencias.add(vista.text.toString())
            }
        }else{
            when(checkbox.id){
                R.id.cbdeportes -> listaPreferencias.remove(vista.text.toString())
                R.id.cbmusica -> listaPreferencias.remove(vista.text.toString())
                R.id.cbotros -> listaPreferencias.remove(vista.text.toString())
            }
        }
    }

    fun registrarPersona(){
        if(validarFormulario()){
            val infoPersona = binding.etnombres.text.toString()+ " " +
                    binding.etapellidos.text.toString() + " " +
                    obtenerGeneroSeleccionado()+" " +
                    listaPreferencias.toArray()+" " +
                    estadocivil+ " " +
                    binding.swnotificacion.isChecked
            listaPersonas.add(infoPersona)
            AppMensaje.enviarMensaje(binding.root,
                "Persona registrada correctamente",
                tipoMensaje = TipoMensaje.SUCCESSFULL)
            setearControles()
        }
    }

    private fun setearControles() {
        listaPreferencias.clear()
        binding.etnombres.setText("")
        binding.etapellidos.setText("")
        binding.swnotificacion.isChecked = false
        binding.cbdeportes.isChecked = false
        binding.cbmusica.isChecked = false
        binding.cbotros.isChecked = false
        binding.rggenero.clearCheck()
        binding.spestadocivil.setSelection(0)
    }

    fun obtenerGeneroSeleccionado():String{
        var genero = ""
        when(binding.rggenero.checkedRadioButtonId){
            R.id.rbmasculino -> {
                genero = binding.rbmasculino.text.toString()
            }
            R.id.rbfemenino -> {
                genero = binding.rbfemenino.text.toString()
            }
        }
        return genero
    }

    fun validarFormulario():Boolean{
        var respuesta = false
        if(!validarNombreApellido()){
            AppMensaje.enviarMensaje(binding.root,
                "Ingrese nombre y apellido", TipoMensaje.ERROR)
        }else if(!validarGenero()){
            AppMensaje.enviarMensaje(binding.root,
                "Seleccione su genero", TipoMensaje.ERROR)
        }else if(!validarPreferencias()){
            AppMensaje.enviarMensaje(binding.root,
                "Seleccione al menos una preferencia", TipoMensaje.ERROR)
        }else if(!validarEstadoCivil()){
            AppMensaje.enviarMensaje(binding.root,
                "Seleccione su estado civil", TipoMensaje.ERROR)
        }else respuesta = true
        return respuesta
    }

    fun validarEstadoCivil():Boolean{
        return estadocivil != ""
    }

    fun validarPreferencias(): Boolean{
        var respuesta = false
        if (binding.cbdeportes.isChecked ||
            binding.cbmusica.isChecked ||
            binding.cbotros.isChecked){
            respuesta = true
        }
        return respuesta
    }

    fun validarGenero(): Boolean{
        var respuesta = true
        if (binding.rggenero.checkedRadioButtonId== -1){
            respuesta = false
        }
        return respuesta
    }

    fun validarNombreApellido():Boolean{
        var respuesta = true
        if(binding.etnombres.text.toString().trim().isEmpty()){
            binding.etnombres.isFocusableInTouchMode = true
            binding.etnombres.requestFocus()
            respuesta = false
        }else if(binding.etapellidos.text.toString().trim().isEmpty()){
            binding.etapellidos.isFocusableInTouchMode = true
            binding.etapellidos.requestFocus()
            respuesta = false
        }
        return respuesta
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        estadocivil = if(position > 0){
            parent!!.getItemAtPosition(position).toString()
        }else ""
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}