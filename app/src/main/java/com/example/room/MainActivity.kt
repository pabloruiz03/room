package com.example.room

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.room.*
import kotlinx.coroutines.launch

@Entity(tableName = "job_table")
data class Job(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "customer_name") val customerName: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "start_date") val startDate: String,
    @ColumnInfo(name = "end_date") val endDate: String,
    @ColumnInfo(name = "job_type") val jobType: String // e.g., "Painting", "Wallpapering", "Both"
)

@Dao
interface JobDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: Job)

    @Query("SELECT * FROM job_table")
    suspend fun getAllJobs(): List<Job>
}

@Database(entities = [Job::class], version = 1, exportSchema = false)
abstract class JobDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao

    companion object {
        @Volatile
        private var INSTANCE: JobDatabase? = null

        fun getDatabase(context: android.content.Context): JobDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JobDatabase::class.java,
                    "job_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    private lateinit var jobDatabase: JobDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jobDatabase = JobDatabase.getDatabase(this)

        setContent {
            MyApp(jobDatabase)
        }
    }
}

@Composable
fun MyApp(jobDatabase: JobDatabase) {
    var customerName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var jobType by remember { mutableStateOf("") }
    var jobList by remember { mutableStateOf(emptyList<Job>()) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = { Text("Customer Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("Start Date (dd/mm/yyyy)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("End Date (dd/mm/yyyy)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = jobType,
            onValueChange = { jobType = it },
            label = { Text("Job Type (Painting, Wallpapering, Both)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    if (customerName.isNotBlank() && location.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && jobType.isNotBlank()) {
                        val job = Job(
                            customerName = customerName,
                            location = location,
                            startDate = startDate,
                            endDate = endDate,
                            jobType = jobType
                        )
                        jobDatabase.jobDao().insertJob(job)

                        // Fetch all jobs to display
                        jobList = jobDatabase.jobDao().getAllJobs()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Job")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Job List:", fontSize = 18.sp)

        jobList.forEach { job ->
            Text("Customer: ${job.customerName}, Location: ${job.location}, Job Type: ${job.jobType}")
        }
    }
}
