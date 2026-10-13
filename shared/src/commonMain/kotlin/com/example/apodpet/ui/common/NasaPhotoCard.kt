package com.example.apodpet.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.apodpet.app_routes.ApodPhoto

@Composable
fun NasaPhotoCard(
    photo: ApodPhoto,
    isSaved: Boolean = false,
    onToggleSave: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onPhotoClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                NasaMediaView(
                    imageUrl = photo.imageUrl,
                    title = photo.title,
                    mediaType = photo.mediaType,
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                            )
                        )
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.65f)
                ) {
                    Text(
                        text = "📅 ${photo.date}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    if (onToggleSave != null) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp).clickable { onToggleSave() },
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isSaved) "❤️" else "🤍",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }

                    if (onDelete != null) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp).clickable { onDelete() },
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "🗑️",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = photo.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = if (isExpanded) 10 else 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (!photo.copyright.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "© ${photo.copyright}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = photo.explanation,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onPhotoClick != null) {
                    FilledTonalButton(
                        onClick = onPhotoClick,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🔍 Open details", style = MaterialTheme.typography.labelMedium)
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                TextButton(onClick = { isExpanded = !isExpanded }) {
                    Text(
                        text = if (isExpanded) "Collapse ▲" else "Expand ▼",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
