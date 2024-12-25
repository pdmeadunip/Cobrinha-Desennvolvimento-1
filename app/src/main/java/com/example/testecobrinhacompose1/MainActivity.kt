package com.example.testecobrinhacompose1

import android.R
import android.R.attr.maxWidth
import android.R.attr.name
import android.R.attr.x
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.testecobrinhacompose1.ui.theme.TesteCobrinhaCompose1Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val game = Game(lifecycleScope)
        enableEdgeToEdge()
        setContent {
            TesteCobrinhaCompose1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        game = game,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(game: Game=Game(), modifier: Modifier = Modifier) {
    JogoCobrinha(game)
}



@Composable
fun JogoCobrinha(game: Game, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(30.dp))
        QuadroJogo(estadoJogo = game.estadoJogo)
        Text(text = "----------------------------------------------------------")
    }
}


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun QuadroJogo(estadoJogo: EstadoJogo,modifier: Modifier = Modifier){
    BoxWithConstraints(modifier = modifier.padding(16.dp)) {
        //quadro do jogo
        val tamanhoUnidade = maxWidth/Game.QTD_QUADRADOS
        Box(modifier = modifier
            .size(maxWidth)
            .border(2.dp, Color.Green)
        )
        //comida
        Box(modifier = modifier
            .offset(x=tamanhoUnidade * estadoJogo.comida.first,y=tamanhoUnidade*estadoJogo.comida.second)
            .size(tamanhoUnidade)
            .background(Color.Red,shape = CircleShape)
             ){}
        estadoJogo.cobra.forEach {
            corpo ->Box(modifier = modifier
                .offset(x=tamanhoUnidade * corpo.first,y=tamanhoUnidade*corpo.second)
                .size(tamanhoUnidade)
                .background(Color.Green,Shapes().small)
            ){}
        }
    }
}

data class EstadoJogo(
    val comida:Pair <Int,Int>,
    val cobra:List<Pair<Int,Int>>
)

class Game(private val scope:CoroutineScope=CoroutineScope(scope.coroutineContext)){
    val estadoJogo = EstadoJogo(Pair(2,5), listOf(Pair(7,7),Pair(7,6),Pair(7,5)))


    companion object{
        const val  QTD_QUADRADOS = 16

    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TesteCobrinhaCompose1Theme {
        Greeting()
    }
}
