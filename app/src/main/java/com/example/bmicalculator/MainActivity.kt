package com.example.bmicalculator

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.bmicalculator.ui.theme.BMICalculatorTheme
import com.example.bmicalculator.ui.theme.GrayLight
import com.example.bmicalculator.ui.theme.Pink
import java.math.RoundingMode
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BMICalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    BMICalculator { bmiValue ->
                        val intent = Intent(this, ResultActivity::class.java)
                        intent.putExtra("BMI_VALUE", bmiValue)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}

fun calculateBMI(height: String, weight: String, onBMICalculated: (String) -> Unit) {
    val heightDecimal = height.toDouble()
    val weightDecimal = weight.toDouble()

    val heightInMeters = heightDecimal / 100

    val bmi = weightDecimal / (heightInMeters * heightInMeters)

    val roundBMI = bmi.toBigDecimal().setScale(2,RoundingMode.HALF_UP)

    onBMICalculated(roundBMI.toString())
}
@Composable
fun AgeSlider() {
    var sliderPosition by remember { mutableIntStateOf(0) }
    Column(
    ) {
        Text(text = sliderPosition.toString(),
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            fontSize = 40.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Slider(
            value = sliderPosition.toFloat(),
            onValueChange = { sliderPosition = it.toInt() },
            colors = SliderDefaults.colors(
                thumbColor = Pink,
                activeTrackColor = Pink,
                inactiveTrackColor = Color.White,
            ),
            steps = 100,
            valueRange = 0f..100f,
            modifier = Modifier.padding(30.dp)
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BMIToolBar(){
    TopAppBar(
        title = { Text(text = "BMI Calculator",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold) },

    )
}

@Composable
fun CardComponent(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val textColor = Color.White

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Pink else GrayLight
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Image(painter = if (title == "Male")
            painterResource(id = R.drawable.male_white)
            else painterResource(id = R.drawable.female_white), contentDescription = title,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally))

        Text(
            text = title,
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = textColor
        )
    }
}



@Composable
fun BMICalculator(onBMICalculated: (String) -> Unit) {
    val context = LocalContext.current
    var maleSelected by remember { mutableStateOf(false) }
    var femaleSelected by remember { mutableStateOf(false) }
    var height by remember {
        mutableStateOf("")
    }
    var weight by remember {
        mutableStateOf("")
    }
    Scaffold(
        topBar = {
            BMIToolBar()
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.2f),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(modifier = Modifier.weight(1f)) {

                    CardComponent(
                        title = "Male",
                        selected = maleSelected
                    ) {
                        maleSelected = true
                        femaleSelected = false
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    CardComponent(
                        title = "Female",
                        selected = femaleSelected
                    ) {
                        femaleSelected = true
                        maleSelected = false
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
            ) {
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = GrayLight
                    )



                ) {
                    Text(
                        text = "Age",
                        fontSize = 30.sp,
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    AgeSlider()

                }

            }
            Row(
                modifier = Modifier
                    .weight(0.4f)

            ) {
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =  GrayLight)


                ) {
                    Text(
                        text = "Height (in cm)", fontSize = 20.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                    TextField(
                        value = height,
                        onValueChange = { newValue ->
                            height = if (newValue.isEmpty()) {
                                newValue
                            } else {
                                val newValueNumeric = newValue.toDoubleOrNull()
                                if (newValueNumeric != null && newValueNumeric in 0.0..260.0) {
                                    newValue
                                } else {
                                    height
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.padding(10.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = GrayLight,
                            focusedContainerColor = Color.White
                        )
                    )


                }

                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =  GrayLight)



                ) {
                    Text(
                        text = "Weight (in Kg)",fontSize = 20.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                    TextField(
                        value = weight,
                        onValueChange = { newValue ->
                            weight = if (newValue.isEmpty()) {
                                newValue
                            } else {
                                val newValueNumeric = newValue.toDoubleOrNull()
                                if (newValueNumeric != null && newValueNumeric in 0.0..650.0) {
                                    newValue
                                } else {
                                    weight
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.padding(10.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = GrayLight,
                            focusedContainerColor = Color.White
                        )
                    )


                }



            }


            Column(
                modifier = Modifier
                    .weight(0.2f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {



                Button(modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                    shape = RoundedCornerShape(0.dp),
                    onClick = {
                        if (height.isNotEmpty() && weight.isNotEmpty()) {
                            calculateBMI(height, weight) { bmiValue ->
                                onBMICalculated(bmiValue)

                            }
                        } else {
                            // Show toast message if height or weight is empty
                            Toast.makeText(context, "Please input both height and weight", Toast.LENGTH_SHORT).show()
                        }
                    }
                    ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Pink
                    )) {
                    Text(text = "Calculate BMI",
                        fontSize = 25.sp
                    )

                }
            }

        }


    }



}

fun getResult(bmi: Float): String {
    return when {
        bmi >= 25 -> "Overweight"
        bmi > 18.5 -> "Normal"
        else -> "Underweight"
    }
}

fun getMsg(bmi: Float): String {
    return when {
        bmi >= 25 -> "You have a higher than normal body weight. Try to exercise more."
        bmi > 18.5 -> "You have a normal body weight. Good job."
        else -> "You have a lower than normal body weight. You can eat a bit more."
    }
}



class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bmiValue = intent.getStringExtra("BMI_VALUE")
        val bmi = bmiValue?.toFloatOrNull() ?: 0f

        setContent {
            BMICalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShowBMIResult(this,bmiValue ?: "", bmi)
                }
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}



@Composable
fun CustomCircularProgressIndicator(
    modifier: Modifier = Modifier,
    initialValue: Float,
    primaryColor:Color,
    secondaryColor: Color,
    minValue: Int = 0,
    maxValue: Int = 100,
    circleRadius:Float,
    onPositionChange:(Int)->Unit
){
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    val positionValue by remember {
        mutableStateOf(initialValue)
    }
    
    Box(modifier = modifier){
        Canvas(modifier = Modifier
            .fillMaxSize()) {
            val width = size.width
            val height = size.height
            val circleThickness = width/25f
            circleCenter = Offset(x = width/2f, y = height/2f)

            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        primaryColor.copy(0.45f),
                        secondaryColor.copy(0.15f)
                    )
                ),
                radius = circleRadius,
                center = circleCenter
            )

            drawCircle(
                style = Stroke(
                    width = circleThickness
                ),
                color = secondaryColor,
                radius = circleRadius,
                center = circleCenter
            )
            drawArc(
                color = primaryColor,
                startAngle = 90f,
                sweepAngle = (360f/maxValue) *positionValue.toFloat(),
                style = Stroke(
                    width = circleThickness,
                    cap = StrokeCap.Round
                ),
                useCenter = false,
                size = Size(
                    width = circleRadius * 2f,
                    height= circleRadius * 2f
                ),
                topLeft = Offset(
                    (width - circleRadius * 2f)/2f,
                    (height - circleRadius * 2f)/2f
                )
            )

            val outerRadius = circleRadius + circleThickness/2f
            val gap = 25f
            for (i in 1..(maxValue-minValue)) {
                val color = if (i < positionValue- minValue) primaryColor else
                    primaryColor.copy(alpha = 0.3f)
                val angleInDegrees = i * 368f/(maxValue-minValue).toFloat()
                val angleInRad = angleInDegrees * PI/180f + PI/2f

                val yGapAdjustment = cos(angleInDegrees * PI/180f)*gap
                val xGapAdjustment = -sin(angleInDegrees * PI/180f)*gap
                val start = Offset(
                    x = (outerRadius * cos(angleInRad) + circleCenter.x + xGapAdjustment).toFloat(),
                    y = (outerRadius * sin(angleInRad) + circleCenter.y + yGapAdjustment).toFloat()
                )

                val end = Offset(
                    x = (outerRadius * cos(angleInRad) + circleCenter.x + xGapAdjustment).toFloat(),
                    y = (outerRadius * sin(angleInRad) + circleThickness + circleCenter.y + yGapAdjustment).toFloat()
                )

                rotate(
                    angleInDegrees,
                    pivot = start
                ){
                    drawLine(
                    color = color,
                    start = start,
                    end = end,
                    strokeWidth = 1.dp.toPx()
                    )
                }

            }
            drawContext.canvas.nativeCanvas.apply {
                drawIntoCanvas {
                    drawText(
                        "$positionValue ",
                        circleCenter.x,
                        circleCenter.y + 50.dp.toPx()/3f,
                        Paint().apply {
                            textSize = 38.sp.toPx()
                            textAlign = Paint.Align.CENTER
                            color = Color.White.toArgb()
                            isFakeBoldText = true

                        }

                    )
                }
            }


        }
    }
}

@Composable
fun ShowBMIResult(activity: Activity,bmiValue: String, bmi: Float) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(30.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(){
                    CustomCircularProgressIndicator(
                        modifier = Modifier
                            .size(300.dp),
                        initialValue = bmi,
                        primaryColor = Pink,
                        secondaryColor = Color.Gray,
                        circleRadius = 150f
                    ) {}

                }

                Text(
                    text = "Your BMI",
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Pink,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = bmiValue,
                    textAlign = TextAlign.Center,
                    fontSize = 42.sp,
                    color = Pink,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                val result = getResult(bmi)
                Text(
                    text = result,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Pink,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                val msg = getMsg(bmi)
                Text(
                    text = msg,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = GrayLight,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(0.dp),
            onClick = {
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Pink
            )
        ) {
            Text(
                text = "Re-calculate BMI",
                fontSize = 25.sp
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BMICalculatorTheme {

        BMICalculator{}
    }
}



