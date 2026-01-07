package com.example.todolist

import android.R.attr.value
import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RestrictTo
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            navHost()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun homescreen(nav: NavHostController, modifier: Modifier = Modifier){
    var notetitle by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val notes = remember { mutableStateListOf<Pair<String,String>>() }
    var edit by remember { mutableStateOf(false)}
    var editnote by remember { mutableStateOf(-1) }
    var edititle by remember { mutableStateOf("") }
    var edittext by remember { mutableStateOf("") }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("我的筆記") },
                actions = {
                    IconButton(onClick = { nav.navigate("gamescreen") } )
                    { Icon(imageVector = Icons.Default.VideogameAsset, contentDescription = "") }
                    IconButton(onClick = { nav.navigate("aboutscreen") } )
                    { Icon(imageVector = Icons.Default.Info, contentDescription = "") }
                }
            )
        },
        floatingActionButton = { FloatingActionButton( onClick = {
            if (notetitle.isNotBlank() && note.isNotBlank())
        {
            notes.add(Pair(notetitle, note))
            notetitle = ""
            note = ""
        }else{
            scope.launch {
                snackbar.showSnackbar("筆記資訊不完整")
            }
        } }, modifier = Modifier.offset(y = (-185).dp))
        { Icon(Icons.Default.Add, contentDescription = "")} }
    ) { innerPadding ->
        if (edit){
            AlertDialog(onDismissRequest = { edit = false},
                title = {Text("編輯筆記")},
                text = {Column{OutlinedTextField(value = edititle,
                    onValueChange = {edititle = it},
                    label = {Text("標題")},
                    modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = edittext,
                    onValueChange = {edittext = it},
                    label = {Text("敘述")},
                    modifier = Modifier.fillMaxWidth()) }
                }, confirmButton = { TextButton(onClick =
                    {
                        if (editnote >= 0)
                        {
                            notes.removeAt(editnote)
                            notes.add(editnote, Pair(edititle,edittext))
                        }
                        edit = false
                    }){ Text("編輯")}
                }, dismissButton = {
                    TextButton(onClick = {edit = false})
                    {
                        Text("取消")
                    }
                }
            )
        }
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .fillMaxSize())
        {
            if (notes.isEmpty()){
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                    contentAlignment = Alignment.Center)
                {Text("你還沒有建立任何清單")}
            }else{
                LazyColumn(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ){
                    itemsIndexed(notes){ index,noteitem->
                        Card(modifier = Modifier
                            .fillMaxWidth()
                            .clickable
                            {
                                editnote = index
                                edititle = noteitem.first
                                edittext = noteitem.second
                                edit = true
                            },
                            shape = RoundedCornerShape(16.dp))
                        {
                            Row(modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween)
                            {
                                Column(Modifier.weight(1f))
                                {
                                    Text(text = noteitem.first,
                                         fontSize = 24.sp,
                                         fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(4.dp))
                                    Text(text = noteitem.second,
                                         fontSize = 16.sp)
                                }
                                IconButton(onClick = {notes.remove(noteitem)}) {
                                    Icon(Icons.Default.Delete,contentDescription ="")
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(contentColor = Color.White))
            {
                Column(modifier = Modifier.padding(16.dp))
                {
                    OutlinedTextField(value = notetitle,
                        onValueChange = {notetitle = it},
                        label = { Text("筆記標題")},
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = note,
                        onValueChange = {note = it},
                        label = { Text("筆記敘述")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun aboutscreen(nav: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("關於") },
                navigationIcon = {IconButton(onClick = {nav.popBackStack()})
                { Icon(Icons.Default.ArrowBack,null)}}
            )
        }
    ) {paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Image(painter = painterResource(R.drawable.logo),
                contentDescription = "",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 6.dp))
            Text(
                text = "Skills Reminder",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "提醒自己 也提醒夢想",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                text = "V1.0",
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Gray, RoundedCornerShape(16.dp))
                    .padding(8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun gamescreen(nav: NavHostController) {
    var ans by remember { mutableStateOf(Random.nextInt(0,9)) }
    val open = remember { mutableStateListOf(false,false,false,false,false,false,false,false,false) }
    var gameover by remember { mutableStateOf(false) }
    var mes by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("遊戲") },
                navigationIcon = {IconButton(onClick = {nav.popBackStack()})
                { Icon(Icons.Default.ArrowBack,null)}}
            )
        }
    ) {innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
               horizontalAlignment = Alignment.CenterHorizontally,
               verticalArrangement = Arrangement.Center)
        {
            Text(
                text = "鞋子在哪裡",
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "貓貓訓練結束，但找不到鞋子，請你幫他尋找",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
            if (mes.isNotEmpty()){
                Text(
                    text = mes,
                    fontSize = 24.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
                for (row in 0..2){
                    Row (horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                        for (col in 0..2){
                            val index = row * 3 + col
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Button(onClick =
                                    { if(gameover)return@Button
                                        open[index] = true
                                        if (index == ans)
                                        {
                                            gameover = true
                                            mes = "找到了!"
                                        }
                                    },modifier = Modifier.size(100.dp).padding(4.dp),
                                    shape = RectangleShape,
                                    enabled = !open[index]
                                ){
                                    if (open[index] && index == ans)
                                    {
                                        Image(
                                            painter = painterResource(R.drawable.shose),
                                            contentDescription = "",
                                            modifier = Modifier.size(80.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                if (gameover){
                    Spacer(modifier = Modifier.height(20.dp))
                    Button({ans = Random.nextInt(0,9)
                        for (i in 0 until 9) open[i] = false
                        gameover = false
                        mes = ""
                    }) {
                        Text("重新開始")
                    }
                }
            }
        }
    }

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun navHost() {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        navController = navController,
        startDestination = "homescreen"
    ) {composable("homescreen")
      { homescreen(navController) }
        composable (
            route = "aboutscreen",
            enterTransition = {slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(400)
            ) },
            exitTransition = { slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(400)
                )
            },
            popEnterTransition = { slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(400)
                )
            },
            popExitTransition = { slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(400)
                )
            }
        ){
            aboutscreen(navController)
        }
        composable(
            route = "gamescreen",
            enterTransition = { slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(400)
                )
            },
            exitTransition = { slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(400)
                )
            },
            popEnterTransition = { slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(400)
                )
            },
            popExitTransition = { slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(400)
                )
            }
        ) {
            gamescreen(navController)
        }
    }
}


