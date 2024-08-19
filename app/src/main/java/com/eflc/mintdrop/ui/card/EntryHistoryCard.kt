package com.eflc.mintdrop.ui.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Currency
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
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .padding(8.dp)
            .width(320.dp)
            .shadow(
                elevation = 5.dp,
                shape = MaterialTheme.shapes.medium
            )
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
                Text(text = description, fontWeight = FontWeight.Bold)
                Text(text = date, fontSize = 12.sp)
            }
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Text(text = "$ $amount", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EntryHistoryCardPreview() {
    EntryHistoryCard(modifier = Modifier, entryHistory = EntryHistory(
        subcategoryId = 1L,
        date = LocalDateTime.now(),
        description = "Prueba de texto",
        amount = 1200.15,
        lastModified = LocalDateTime.now()
    )
    )
}