package com.dfernandezguerreiro.apphttpjson.dummy

import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */

object DummyContent {

    val lista: MutableList<Datos> = ArrayList()

    init {
    }

    data class Datos(val titulo: String, val cuerpo: String) {
        // personalizamos to String
        override fun toString(): String {
            return "Titulo del post: $titulo"
        }
    }
}
