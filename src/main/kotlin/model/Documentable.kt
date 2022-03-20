package model

import org.bson.Document

abstract class Documentable {
    fun toDoc(): Document = Document(mapOf(*dataPairs()))
    
    abstract fun dataPairs(): Array<Pair<String, Any>>
}