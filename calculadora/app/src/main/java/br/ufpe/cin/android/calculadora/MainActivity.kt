package br.ufpe.cin.android.calculadora

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder

@SuppressLint("StaticFieldLeak")
lateinit var expr : EditText
lateinit var info : TextView
class MainActivity : AppCompatActivity() {
    var info_result = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        expr = findViewById(R.id.text_calc)
        info = findViewById(R.id.text_info)
        val btn_9 : Button = findViewById(R.id.btn_9)
        val btn_8 : Button = findViewById(R.id.btn_8)
        val btn_7 : Button = findViewById(R.id.btn_7)
        val btn_6 : Button = findViewById(R.id.btn_6)
        val btn_5 : Button = findViewById(R.id.btn_5)
        val btn_4 : Button = findViewById(R.id.btn_4)
        val btn_3 : Button = findViewById(R.id.btn_3)
        val btn_2 : Button = findViewById(R.id.btn_2)
        val btn_1 : Button = findViewById(R.id.btn_1)
        val btn_0 : Button = findViewById(R.id.btn_0)
        val btn_dot : Button = findViewById(R.id.btn_Dot)
        val btn_Divide : Button = findViewById(R.id.btn_Divide)
        val btn_Multiply : Button = findViewById(R.id.btn_Multiply)
        val btn_Add : Button = findViewById(R.id.btn_Add)
        val btn_Subtract : Button = findViewById(R.id.btn_Subtract)
        val btn_Power : Button = findViewById(R.id.btn_Power)
        val btn_Equal : Button = findViewById(R.id.btn_Equal)
        val btn_RParen : Button = findViewById(R.id.btn_RParen)
        val btn_LParen : Button = findViewById(R.id.btn_LParen)
        val btn_Clear : Button = findViewById(R.id.btn_Clear)
        btn_9.setOnClickListener { expr.append("9") }
        btn_8.setOnClickListener { expr.append("8") }
        btn_7.setOnClickListener { expr.append("7") }
        btn_6.setOnClickListener { expr.append("6") }
        btn_5.setOnClickListener { expr.append("5") }
        btn_4.setOnClickListener { expr.append("4") }
        btn_3.setOnClickListener { expr.append("3") }
        btn_2.setOnClickListener { expr.append("2") }
        btn_1.setOnClickListener { expr.append("1") }
        btn_0.setOnClickListener { expr.append("0") }
        btn_Add.setOnClickListener { expr.append("+") }
        btn_Subtract.setOnClickListener { expr.append("-") }
        btn_Multiply.setOnClickListener { expr.append("*") }
        btn_Divide.setOnClickListener { expr.append("/") }
        btn_RParen.setOnClickListener { expr.append(")") }
        btn_LParen.setOnClickListener { expr.append("(") }
        btn_Equal.setOnClickListener {
            Log.d("MainActivity","Valor da expressão: " + expr.text.toString())
            info_result = eval(expr.text.toString()).toString()
            info.text = info_result
            expr.setText("")
        }
        btn_dot.setOnClickListener { expr.append(".") }
        btn_Power.setOnClickListener { expr.append("^") }
        btn_Clear.setOnClickListener {
            expr.run { setText("") }
            info.text = ""
            info_result = ""
        }
        if(savedInstanceState != null){
            info_result = savedInstanceState.getString("result").toString()
            info.text = info_result
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("result", info_result)
    }


    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) {
                    Toast.makeText(applicationContext, "Caractere inesperado: " + ch,Toast.LENGTH_LONG).show()
                    return 0.0
                }
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                x = 0.0
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                }
                else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                }
                else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else {
                        Toast.makeText(
                            applicationContext,
                            "Função desconhecida: " + func,
                            Toast.LENGTH_LONG
                        ).show()
                        return 0.0
                    }
                }
                else {
                    Toast.makeText(applicationContext,"Caractere inesperado: " + ch.toChar(),Toast.LENGTH_LONG).show()
                    return 0.0
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência

                return x
            }
        }.parse()
    }
}
