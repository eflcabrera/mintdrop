package com.eflc.mintdrop.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eflc.mintdrop.R
import com.eflc.mintdrop.room.dao.entity.PaymentMethod
import com.eflc.mintdrop.room.dao.entity.PaymentMethodType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodDropdown(
    paymentMethods: List<PaymentMethod?>,
    selectedValue: PaymentMethod?,
    onClick: (index: Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .padding(bottom = 20.dp)
    ) {
        Box(modifier = Modifier.height(IntrinsicSize.Min)) {
            TextField(
                label = {
                    Text("Método de pago (opc.)")
                },
                modifier = Modifier.fillMaxWidth(),
                value = selectedValue?.description ?: "",
                onValueChange = {},
                readOnly = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.chat_round_money_svgrepo_com),
                        contentDescription = "payment method icon",
                        modifier = Modifier.size(30.dp)
                    )
                }
            )
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.extraSmall)
                    .clickable(enabled = true) { expanded = true },
                color = Color.Transparent,
            ) {}
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(),
        ) {
            DropdownMenuItem(
                text = { Text(text = "Ninguno") },
                onClick = {
                    onClick(-1)
                    expanded = false
                }
            )
            paymentMethods.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(text = item?.description ?: "") },
                    onClick = {
                        onClick(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentMethodDropdownPreview() {
    var paymentMethodInput by remember { mutableStateOf<PaymentMethod?>(null) }
    val paymentMethods = listOf(
        PaymentMethod(1, "Visa Crédito", PaymentMethodType.CREDIT_CARD),
        PaymentMethod(2, "Master Crédito", PaymentMethodType.CREDIT_CARD),
        PaymentMethod(3, "MercadoPago", PaymentMethodType.DIGITAL_WALLET)
    )
    PaymentMethodDropdown(
        selectedValue = paymentMethodInput,
        paymentMethods = paymentMethods,
        onClick = {
            index -> paymentMethodInput = if (index >= 0) paymentMethods[index] else null
        }
    )
}
