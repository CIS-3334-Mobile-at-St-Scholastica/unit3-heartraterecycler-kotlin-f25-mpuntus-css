package cis3334.kotlin_heartrate_roomdb

// =============================================
// MainActivity.kt — Jetpack Compose UI
// =============================================
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType  // <-- for KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        val db = HeartrateDatabase.get(applicationContext)
        MainViewModelFactory(HeartrateRepository(db.dao()))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App(viewModel) }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(vm: MainViewModel) {
    val items by vm.items.collectAsState()
    val pulse by vm.pulseText.collectAsState()
    val age by vm.ageText.collectAsState()
    val enabled by vm.isInsertEnabled.collectAsState()


    Scaffold(topBar = { TopAppBar(title = { Text("Add a Heartrate") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PulseInputField(pulse = pulse, onValueChange = { vm.pulseText.value = it.filter { ch -> ch.isDigit() } })
            AgeInputField(age = age, onValueChange = { vm.ageText.value = it.filter { ch -> ch.isDigit() } })
            InsertButton(onClick = vm::insert, enabled = enabled)
            ClearAllButton(onClick = vm::clearAll)
            SavedEntriesHeader()
            HeartrateList(heartrates = items)
        }
    }
}

@Composable
fun PulseInputField(pulse: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = pulse,
        onValueChange = onValueChange,
        label = { Text("Pulse") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun AgeInputField(age: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = age,
        onValueChange = onValueChange,
        label = { Text("Age") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun InsertButton(onClick: () -> Unit, enabled: Boolean) {
    Button(onClick = onClick, enabled = enabled) {
        Text("Insert")
    }
}

@Composable
fun ClearAllButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Clear All")
    }
}

@Composable
fun SavedEntriesHeader() {
    Text(
        text = "Saved Entries",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start
    )
}

@Composable
fun HeartrateList(heartrates: List<Heartrate>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    )
    {
        items(heartrates) { heartrate ->
            HeartrateItem(heartrate = heartrate);
        }
//        for (item in items) {
//            Text(text = "Pulse ${item.pulse}, Age ${item.age}")
//        }
    }
}

@Composable
fun HeartrateItem(heartrate: Heartrate, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Heart icon
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Heartrate",
                tint = when (heartrate.rangeIndex) {
                    0 -> Color.Gray
                    1 -> Color.Green
                    2 -> Color.Blue
                    3 -> Color.Yellow
                    4 -> Color.Magenta
                    else -> Color.Red
                },
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = "Pulse: ${heartrate.pulse}, Age: ${heartrate.age}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Display Cardio Level (Range Name)
                Text(
                    text = "Level: ${heartrate.rangeName()}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Display Percentage of Max Heart Rate
                Text(
                    text = "Max HR %: ${(heartrate.percentOfMax * 100).roundToInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}