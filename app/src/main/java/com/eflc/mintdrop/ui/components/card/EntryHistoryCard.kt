package com.eflc.mintdrop.ui.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eflc.mintdrop.R
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun EntryHistoryCard(
    modifier: Modifier,
    entryHistory: EntryHistory
) {
    val format: NumberFormat = NumberFormat.getNumberInstance(Locale.GERMAN)
    format.maximumFractionDigits = 2

    val description = entryHistory.description.ifBlank { "???" }
    val date = entryHistory.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val amount = format.format(entryHistory.amount)

    return Card(
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier
            .padding(4.dp)
            .width(350.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .height(70.dp)
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxHeight(),
            ) {
                Text(
                    text = description,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.width(170.dp)
                )
                Text(text = date, fontSize = 12.sp)
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Text(
                    text = "$ $amount",
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.width(50.dp)
                ) {
                    if (entryHistory.isShared == true) {
                        Image(
                            painter = painterResource(id = R.drawable.hearts_svgrepo_com),
                            colorFilter = ColorFilter.tint(color = Color.Gray),
                            contentDescription = "android image",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EntryHistoryCardPreview() {
    EntryHistoryCard(modifier = Modifier.width(320.dp),
        entryHistory = EntryHistory(
            subcategoryId = 1L,
            date = LocalDateTime.now(),
            description = "Imp. a los ingresos personales",
            amount = -91260.15,
            lastModified = LocalDateTime.now(),
            isShared = false,
        )
    )
}
