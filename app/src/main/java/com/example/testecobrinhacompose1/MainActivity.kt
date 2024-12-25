package com.example.testecobrinhacompose1

import android.R
import android.R.attr.maxWidth
import android.R.attr.name
import android.R.attr.value
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.testecobrinhacompose1.ui.theme.TesteCobrinhaCompose1Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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
    val estadoJogo = game.estadoJogo.collectAsState(initial = null)
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(30.dp))
        estadoJogo.value?.let{
            QuadroJogo(it)
        }

        BotoesSeta()
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

@Composable
fun BotoesSeta(modifier: Modifier = Modifier){
    val tamBotao = Modifier.size(64.dp)
    Column(modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { /*TODO*/ },
            modifier = tamBotao,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(imageVector = androidx.compose.material.icons.Icons.Filled.KeyboardArrowUp
                , contentDescription = "Cima")
        }
        Row{
            Button(onClick = { /*TODO*/ },
                modifier = tamBotao,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = androidx.compose.material.icons.Icons.Filled.ArrowBack
                    ,contentDescription = "Esquerda")
            }
            Spacer(modifier = tamBotao)
            Button(onClick = { /*TODO*/ },
                modifier = tamBotao,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = androidx.compose.material.icons.Icons.Filled.ArrowForward
                    , contentDescription = "Direita")
            }
        }
        Button(onClick = { /*TODO*/ },
            modifier = tamBotao,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(imageVector = androidx.compose.material.icons.Icons.Filled.KeyboardArrowDown
                , contentDescription = "Baixo")
        }
    }
}


data class EstadoJogo(
    val comida:Pair <Int,Int>,
    val cobra:List<Pair<Int,Int>>
)

class Game(private val scope:CoroutineScope=CoroutineScope(scope.coroutineContext)) {
    private val mutex: Mutex = Mutex()
    private val estadoMutavel: MutableStateFlow<EstadoJogo> =
        MutableStateFlow(EstadoJogo(comida = Pair(5, 5), cobra = listOf(Pair(7, 7))))
    val estadoJogo: Flow<EstadoJogo> = estadoMutavel
    var direcao = Pair(1, 0)
        set(value) {
            scope.launch {
                mutex.withLock {
                    field = value
                }
            }
        }
    init {
        scope.launch {
            var tamanhoCobra = 4
            while (true){
                delay(500)
                estadoMutavel.update {
                    val novaCabeca : Pair<Int,Int> =
                          it.cobra.first().let {
                               poz:Pair<Int, Int> -> mutex.withLock {
                                        Pair(
                                                (poz.first+direcao.first+ QTD_QUADRADOS)% QTD_QUADRADOS,
                                                (poz.second+direcao.second+QTD_QUADRADOS)%QTD_QUADRADOS
                                            )
                               }
                          }
                    it.copy(
                        comida = if(novaCabeca == it.comida){
                            Pair(
                                (0 until QTD_QUADRADOS).random(),
                                (0 until QTD_QUADRADOS).random()
                            )
                        }else
                            it.comida,
                                  cobra = listOf(novaCabeca)+it.cobra.take(tamanhoCobra-1)
                    )
                }


            }
        }
    }


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
