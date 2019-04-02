package com.dfernandezguerreiro.apphttpjson

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView

import com.dfernandezguerreiro.apphttpjson.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list_content.view.*
import kotlinx.android.synthetic.main.item_list.*
import kotlinx.serialization.json.JsonObject
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ItemListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    val LOGTAG = "peticionwp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        if (item_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }
        //progressBar.visibility = View.VISIBLE
        textView2.visibility=View.VISIBLE
        peticionwp()
        setupRecyclerView(item_list)
    }

    fun peticionwp(){
        DummyContent.lista.clear()
        //lanza la corutina
        doAsync{
            try{
                val respuesta= URL("http://18.188.166.203/wp5/?rest_route=/wp/v2/posts/").readText()
                //http://18.188.166.203/wp5/?rest_route=/wp/v2/posts/
                //https://jsonplaceholder.typicode.com/posts
                // sabemos que recibimos un array de objetos JSON
                Log.d(LOGTAG, respuesta)
                val miJSONArray = JSONArray(respuesta)
                // recorremos el Array
                for (jsonIndex in 0..(miJSONArray.length() - 1)) {
                    // creamos el objeto 'misDatos' a partir de la clase 'Datos'
                    // asignamos el valor de 'title' en el constructor de la data class 'Datos'

                    val titulo=miJSONArray.getJSONObject(jsonIndex).getJSONObject("title").getString("rendered")//rendered
                    Log.d(LOGTAG,titulo)
                    val cuerpo=miJSONArray.getJSONObject(jsonIndex).getJSONObject("content").getString("rendered") //content
                    Log.d(LOGTAG, cuerpo)
                    val datos = DummyContent.Datos(titulo,cuerpo)
                    DummyContent.lista.add(datos)
                }
            }catch (e:Exception){
                uiThread {
                    //progressBar.visibility = View.INVISIBLE
                    longToast("Algo va mal: $e")
                    Log.d(LOGTAG, e.toString())
                }
            }finally {
                textView2.visibility=View.INVISIBLE
                setupRecyclerView(item_list)
            }
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, DummyContent.lista, twoPane)
    }

    class SimpleItemRecyclerViewAdapter(private val parentActivity: ItemListActivity,
                                        private val values: List<DummyContent.Datos>,
                                        private val twoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as DummyContent.Datos
                if (twoPane) {
                    val fragment = ItemDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(ItemDetailFragment.ARG_ITEM_TITLE, item.titulo)
                            putString(ItemDetailFragment.ARG_ITEM_CUERPO, item.cuerpo)
                        }
                    }
                    parentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, ItemDetailActivity::class.java).apply {
                        putExtra(ItemDetailFragment.ARG_ITEM_TITLE, item.titulo)
                        putExtra(ItemDetailFragment.ARG_ITEM_CUERPO, item.cuerpo)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = item.titulo
            //holder.contentView.text = item.content

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.id_text
            val contentView: TextView = view.content
        }
    }
}
