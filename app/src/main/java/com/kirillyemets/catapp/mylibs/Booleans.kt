package com.kirillyemets.catapp.mylibs

fun any(vararg booleans: Boolean) = booleans.any { it }
fun all(vararg booleans: Boolean) = booleans.all { it }
fun none(vararg booleans: Boolean) = booleans.none { it }