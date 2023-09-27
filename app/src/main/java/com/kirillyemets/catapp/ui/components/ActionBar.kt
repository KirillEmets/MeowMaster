package com.kirillyemets.catapp.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kirillyemets.catapp.R
import com.kirillyemets.catapp.ui.theme.CoralPink
import com.kirillyemets.catapp.ui.theme.SoftLilac
import com.kirillyemets.catapp.ui.theme.WarmTeal

@Composable
fun ActionBar(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        content()
    }
}

@Composable
fun LikedAnimalInfoActionBar(
    modifier: Modifier = Modifier,
    onDownloadButtonClick: () -> Unit,
    onShareButtonClick: () -> Unit,
    onRemoveButtonClick: () -> Unit
) {
    ActionBar(
        modifier = modifier,
        content = {
            DownloadButton(enabled = true, onClick = onDownloadButtonClick)
            ShareButton(enabled = true, onClick = onShareButtonClick)
            RemoveButton(enabled = true, onClick = onRemoveButtonClick)
        }
    )
}

@Composable
fun ExploreAnimalInfoActionBar(
    modifier: Modifier = Modifier,
    onDownloadButtonClick: () -> Unit,
    onShareButtonClick: () -> Unit,
    onLikeButtonClick: () -> Unit
) {
    ActionBar(
        modifier = modifier,
        content = {
            DownloadButton(enabled = true, onClick = onDownloadButtonClick)
            ShareButton(enabled = true, onClick = onShareButtonClick)
            LikeButton(enabled = true, iconColor = CoralPink, onClick = onLikeButtonClick)
        }
    )
}

@Composable
fun BatchImageViewerActionBar(
    isLiked: Boolean,
    modifier: Modifier = Modifier,
    onDownloadButtonClick: () -> Unit,
    onShareButtonClick: () -> Unit,
    onLikeButtonClick: () -> Unit
) {
    ActionBar(
        modifier = modifier,
        content = {
            DownloadButton(enabled = true, onClick = onDownloadButtonClick)
            ShareButton(enabled = true, onClick = onShareButtonClick)
            LikeButton(
                enabled = true,
                iconColor = when {
                    isLiked -> CoralPink
                    else -> MaterialTheme.colorScheme.onSurface
                },
                onClick = onLikeButtonClick
            )
        }
    )
}

@Composable
fun LikeButton(
    enabled: Boolean,
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    ActionButton(iconId = R.drawable.outline_favorite_24, enabled = enabled, iconColor = iconColor, onClick = onClick)
}

@Composable
fun DownloadButton(enabled: Boolean, onClick: () -> Unit) {
    ActionButton(
        iconId = R.drawable.ic_outline_file_download_24dp,
        enabled = enabled,
        iconColor = WarmTeal,
        onClick = onClick
    )
}

@Composable
fun ShareButton(enabled: Boolean, onClick: () -> Unit) {
    ActionButton(
        iconId = R.drawable.ic_outline_share_24dp,
        enabled = enabled,
        iconColor = SoftLilac,
        onClick = onClick
    )
}

@Composable
fun RemoveButton(enabled: Boolean, onClick: () -> Unit) {
    ActionButton(
        iconId = R.drawable.round_close_24,
        enabled = enabled,
        iconColor = CoralPink,
        onClick = onClick
    )
}

@Composable
fun ActionButton(
    @DrawableRes iconId: Int,
    enabled: Boolean,
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit,
) {
    OutlinedIconButton(
        modifier = Modifier.size(64.dp),
        enabled = enabled,
        onClick = onClick,
        content = {
            Icon(
                modifier = Modifier.size(36.dp),
                painter = painterResource(id = iconId),
                contentDescription = "Dislike"
            )
        },
        colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = iconColor)
    )
}


//Actions
// Explore: Like, Share, Download
// Liked: Share, Download, Remove
// Breed gallery: Like, Share, Download

// Share, Download -> Use cases
// Like, delete -> Viewmodel