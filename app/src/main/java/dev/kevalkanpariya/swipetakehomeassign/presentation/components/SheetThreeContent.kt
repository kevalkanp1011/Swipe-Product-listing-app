package dev.kevalkanpariya.swipetakehomeassign.presentation.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.kevalkanpariya.swipetakehomeassign.R
import dev.kevalkanpariya.swipetakehomeassign.ui.theme.mierFontFamily
import dev.kevalkanpariya.swipetakehomeassign.utils.createTempImageFile
import dev.kevalkanpariya.swipetakehomeassign.utils.getUriForFile

@Composable
fun SheetThreeContent(
    isProductCreating: Boolean,
    photoUri: Uri,
    onImageUriChanged:(Uri) -> Unit,
    onDone: () -> Unit
) {

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val photoFile = remember { createTempImageFile(context) }
    val mUri = getUriForFile(context, photoFile)
    val takePictureLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            if (mUri != Uri.EMPTY) {
                Log.d("AheetThree", "mUri is: $mUri and photoUri is:$photoUri")
                context.contentResolver.openInputStream(mUri)?.use { inputStream -> bitmap = BitmapFactory.decodeStream(inputStream)}
            }
        }

    }

    val getContentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            onImageUriChanged(it)
            context.contentResolver.openInputStream(it)?.use { inputStream -> bitmap = BitmapFactory.decodeStream(inputStream)}
        }
    }

    Box(
        Modifier.fillMaxSize()
    ) {

        if (isProductCreating) {

            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(14.dp)
                )
                Text(text = "Creating a Product..")
            }

        } else {

            Column(
                modifier = Modifier
                    .padding(14.dp),
            ) {

                val stroke = Stroke(width = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
                Box(
                    modifier = Modifier
                        .aspectRatio(2f)
                        .drawBehind {
                            drawRoundRect(color = Color(0xff5f00d3).copy(0.4f), style = stroke, cornerRadius = CornerRadius(5.dp.toPx()))
                        }
                        .clickable {
                            getContentLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(R.drawable.social_15049663),
                            contentDescription = "image selection",
                            tint = Color.Black.copy(0.4f)
                        )
                        Text(
                            text = "Select file",
                            style = TextStyle(
                                fontFamily = mierFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.Black.copy(0.4f)
                            )
                        )
                    }

                    bitmap?.let {
                        Image(
                            it.asImageBitmap(), null,
                            modifier = Modifier.fillMaxSize().aspectRatio(2f),
                            contentScale = ContentScale.Crop
                        )
                    }


                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        modifier = Modifier.weight(0.2f),
                        text = "or",
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontFamily = mierFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.Black.copy(0.4f)
                        )
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                        onImageUriChanged(getUriForFile(context, photoFile))
                        takePictureLauncher.launch(getUriForFile(context, photoFile))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff5f00d3).copy(0.1f)),
                    border = BorderStroke(width = 1.dp, Color(0xff5f00d3)),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Icon(painter = painterResource(R.drawable.baseline_camera_alt_24), contentDescription = "camera", tint = Color(0xff5f00d3))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Open Camera",
                        style = TextStyle(
                            fontFamily = mierFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xff5f00d3)
                        )
                    )
                }

            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)


            ) {
                HorizontalDivider()
                Spacer(Modifier.height(5.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 10.dp),
                    onClick = {
                        onDone()
                    },
                    enabled = !isProductCreating,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff5f00d3)),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        text = "Done",
                        style = TextStyle(
                            fontFamily = mierFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }
            }
        }



    }


}