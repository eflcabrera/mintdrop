package com.eflc.mintdrop.ui.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
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
import com.eflc.mintdrop.room.dao.entity.SharedExpenseEntryDetail
import com.eflc.mintdrop.utils.Constants.MY_USER_ID
import com.eflc.mintdrop.utils.FormatUtils.Companion.formatAsCurrency
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun EntryHistoryCard(
    modifier: Modifier,
    entryRecord: EntryHistory,
    paymentMethods: List<PaymentMethod>? = listOf(),
    sharedExpenseDetails: List<SharedExpenseEntryDetail>? = listOf(),
    onLongPress: (() -> Unit)? = null
) {
    // Valores memoizados para evitar recálculos en cada recomposición
    val description by remember(entryRecord.description) {
        derivedStateOf { entryRecord.description.ifBlank { "???" } }
    }
    
    val formattedDate by remember(entryRecord.date) {
        derivedStateOf { 
            entryRecord.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
    }
    
    val formattedAmount by remember(entryRecord.amount) {
        derivedStateOf { formatAsCurrency(entryRecord.amount) }
    }
    
    val paymentMethod by remember(entryRecord.paymentMethodId, paymentMethods) {
        derivedStateOf { 
            paymentMethods?.find { it.uid == entryRecord.paymentMethodId }
        }
    }
    
    val hasSharedExpenseDetails by remember(sharedExpenseDetails) {
        derivedStateOf { !sharedExpenseDetails.isNullOrEmpty() }
    }
    
    // Constantes memoizadas
    val colorSharedGreen = remember { Color(54, 180, 103) }
    val colorCreditCard = remember { Color(66, 135, 245) }
    val colorSynced = remember { Color(54, 180, 103) } // Verde para sincronizado
    val colorPending = remember { Color(255, 152, 0) } // Naranja para pendiente
    
    // Estado de sincronización memoizado
    val syncStatus by remember(entryRecord.syncedToSheets) {
        derivedStateOf {
            entryRecord.syncedToSheets
        }
    }

    return Card(
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier
            .padding(4.dp)
            .width(350.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongPress?.invoke()
                    }
                )
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .height(80.dp)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(text = formattedDate, fontSize = 12.sp)
                    // Ícono de sincronización
                    if (syncStatus) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Sincronizado",
                            tint = colorSynced,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 6.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Sincronizado",
                            tint = colorPending,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 6.dp)
                        )
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Text(
                    text = formattedAmount,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(125.dp)
                ) {
                    if (paymentMethod?.type == PaymentMethodType.CREDIT_CARD) {
                        Image(
                            painter = painterResource(id = R.drawable.card_svgrepo_com),
                            colorFilter = ColorFilter.tint(color = colorCreditCard),
                            contentDescription = "credit card payment icon",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (entryRecord.isShared == true) {
                        val colorTint by remember(entryRecord.isSettled) {
                            derivedStateOf {
                                if ((entryRecord.isSettled == null) || entryRecord.isSettled == true)
                                    Color.Gray
                                else
                                    colorSharedGreen
                            }
                        }

                        if (hasSharedExpenseDetails) {
                            val splitInfo by remember(sharedExpenseDetails, entryRecord.paidBy) {
                                derivedStateOf {
                                    val myDetail = sharedExpenseDetails?.find { it.userId == MY_USER_ID }
                                    val theirDetail = sharedExpenseDetails?.filter { it.userId != MY_USER_ID } ?: emptyList()
                                    
                                    if (myDetail != null) {
                                        if (MY_USER_ID != entryRecord.paidBy) {
                                            val split = myDetail.split.times(-1)
                                            val color = Color.Gray
                                            Triple(split, color, true)
                                        } else {
                                            val split = theirDetail.sumOf { it.split }
                                            val color = colorSharedGreen
                                            Triple(split, color, true)
                                        }
                                    } else {
                                        Triple(0.0, Color.Gray, false)
                                    }
                                }
                            }
                            
                            if (splitInfo.third) {
                                val (split, color, _) = splitInfo
                                Text(
                                    text = "(${if (split < 0.0) "" else "+ "}${formatAsCurrency(split)})",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = color,
                                    modifier = Modifier.padding(start = 5.dp)
                                )
                            }
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.hearts_svgrepo_com),
                                colorFilter = ColorFilter.tint(color = colorTint),
                                contentDescription = "shared expense icon",
                                modifier = Modifier.size(20.dp)
                            )
                        }
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
        entryRecord = EntryHistory(
            subcategoryId = 1L,
            date = LocalDateTime.now(),
            description = "Imp. a los ingresos personales",
            amount = -91260.15,
            lastModified = LocalDateTime.now(),
            isShared = true,
            paymentMethodId = 1,
            isSettled = false,
            paidBy = 1L,
            syncedToSheets = false
        ),
        paymentMethods = listOf(PaymentMethod(1, "Crédito", PaymentMethodType.CREDIT_CARD)),
        sharedExpenseDetails = listOf(
            SharedExpenseEntryDetail(
                entryRecordId = 1L,
                userId = 1L,
                sharedExpenseConfigurationId = 1L,
                split = 41260.15
            ),
            SharedExpenseEntryDetail(
                entryRecordId = 1L,
                userId = 2L,
                sharedExpenseConfigurationId = 1L,
                split = 50000.00
            )
        ),
        onLongPress = { /* Preview no necesita funcionalidad */ }
    )
}
