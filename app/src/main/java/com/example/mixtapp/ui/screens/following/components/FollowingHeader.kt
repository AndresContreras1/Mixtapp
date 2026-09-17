package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.displayFontFamily

@Composable
fun FollowingHeader(
    followingCount: Int,
    followersCount: Int,
    friendQuery: String,
    onFriendQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
    ) {
        Spacer(modifier = Modifier.height(49.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.following_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 27.sp,
                    lineHeight = 32.sp,
                    fontFamily = displayFontFamily,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = stringResource(
                        R.string.following_followers_count,
                        followingCount,
                        followersCount,
                    ),
                    modifier = Modifier.padding(top = 9.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f),
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontFamily = displayFontFamily,
                    fontWeight = FontWeight.Bold,
                )
            }

            FriendSearchField(
                value = friendQuery,
                onValueChange = onFriendQueryChange,
            )
        }
    }
}
