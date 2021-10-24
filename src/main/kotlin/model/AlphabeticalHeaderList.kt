package fr.tb_lab.model

class AlphabeticalHeaderList(size: Int) : List<String> by List(size, ::str)

private fun str(i: Int): String = if (i < 0) "" else str(i / 26 - 1) + (65 + i % 26).toChar()