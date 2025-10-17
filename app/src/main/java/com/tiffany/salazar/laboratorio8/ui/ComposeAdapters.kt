package com.tiffany.salazar.laboratorio8.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.tiffany.salazar.laboratorio8.data.Photo

@Composable
fun PhotoGrid(
    items: LazyPagingItems<Photo>,
    onPhotoClick: (String) -> Unit,
    onToggleFavorite: (Photo) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items.itemCount, key = { index -> items[index]?.id ?: index }) { index ->
            items[index]?.let { photo ->
                PhotoCard(
                    photo = photo,
                    onClick = { onPhotoClick(photo.id) },
                    onToggleFavorite = { onToggleFavorite(photo) }
                )
            }
        }
    }
}

@Composable
fun PhotoCard(
    photo: Photo,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box {
            AsyncImage(
                model = photo.thumbUrl,
                contentDescription = photo.author,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if (photo.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (photo.isFavorite) Color.Red else Color.White
                )
            }
        }
    }
}