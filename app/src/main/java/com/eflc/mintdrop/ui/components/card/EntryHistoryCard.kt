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
import com.eflc.mintdrop.room.dao.entity.PaymentMethod
import com.eflc.mintdrop.room.dao.entity.PaymentMethodType
import com.eflc.mintdrop.utils.FormatUtils.Companion.formatAsCurrency
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun EntryHistoryCard(
    modifier: Modifier,
    entryHistory: EntryHistory,
    paymentMethods: List<PaymentMethod>?
) {
    val description = entryHistory.description.ifBlank { "???" }
    val date = entryHistory.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val amount = formatAsCurrency(entryHistory.amount)
    val paymentMethod = paymentMethods?.find { it.uid == entryHistory.paymentMethodId }

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
                    if (paymentMethod?.type == PaymentMethodType.CREDIT_CARD) {
                        Image(
                            painter = painterResource(id = R.drawable.card_svgrepo_com),
                            colorFilter = ColorFilter.tint(color = Color(66, 135, 245)),
                            contentDescription = "credit card payment icon",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (entryHistory.isShared == true) {
                        Image(
                            painter = painterResource(id = R.drawable.hearts_svgrepo_com),
                            colorFilter = ColorFilter.tint(color = Color(54, 180, 103)),
                            contentDescription = "shared expense icon",
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
            isShared = true,
            paymentMethodId = 1
        ),
        paymentMethods = listOf(PaymentMethod(1, "Crédito", PaymentMethodType.CREDIT_CARD))
    )
}
