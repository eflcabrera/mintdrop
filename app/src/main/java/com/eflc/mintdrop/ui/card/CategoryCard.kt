package com.eflc.mintdrop.ui.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eflc.mintdrop.models.GenericCategory

@Composable
fun CategoryCard(
    iconMap: Map<String, Int>?,
    category: GenericCategory,
    onClick: (category: GenericCategory) -> Unit,
    modifier: Modifier,
    fontSize: TextUnit = 12.sp
) {
    return Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .padding(8.dp)
            .width(320.dp)
            .shadow(
                elevation = 5.dp,
                shape = MaterialTheme.shapes.medium
            )
            .clickable { onClick(category) }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .height(140.dp)
                .fillMaxWidth()
                .background(color = Color.White)
        ) {
            iconMap?.get(category.name)?.let { painterResource(id = it) }?.let {
                Image(
                    painter = it,
                    contentDescription = "android image",
                    modifier = Modifier.size(65.dp)
                )
            }
            Text(
                text = category.name,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontSize = fontSize,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp)
            )
        }
    }
}