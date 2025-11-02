package com.eflc.mintdrop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.eflc.mintdrop.utils.FormatUtils
import com.eflc.mintdrop.utils.FormatUtils.Companion.formatDateFromString
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EntryDatePicker(
    showDatePicker: Boolean,
    datePickerState: DatePickerState,
    selectedDate: String,
    onClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val formattedDate = formatDateFromString(selectedDate)
    val textFieldValue = resolveTextFieldValue(formattedDate)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = textFieldValue,
                onValueChange = { },
                label = { Text("Fecha") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = onClick) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            if (showDatePicker) {
                Popup(
                    onDismissRequest = onDismissRequest,
                    alignment = Alignment.TopStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = 64.dp)
                            .shadow(elevation = 4.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp)
                    ) {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = false,
                        )
                    }
                }
            }
        }

        // Botones de atajo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickDateButton(
                label = "Ayer",
                modifier = Modifier.weight(1f),
                onClick = {
                    val yesterday = LocalDate.now().minusDays(1)
                    val millis = yesterday.atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli()
                    onDateSelected(millis)
                }
            )
            QuickDateButton(
                label = "Hoy",
                modifier = Modifier.weight(1f),
                onClick = {
                    val today = LocalDate.now()
                    val millis = today.atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli()
                    onDateSelected(millis)
                }
            )
            QuickDateButton(
                label = "Mañana",
                modifier = Modifier.weight(1f),
                onClick = {
                    val tomorrow = LocalDate.now().plusDays(1)
                    val millis = tomorrow.atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli()
                    onDateSelected(millis)
                }
            )
        }
    }
}

@Composable
private fun QuickDateButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(160, 221, 230),
            contentColor = Color.Black
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun resolveTextFieldValue(date: LocalDate): String {
    if (date == LocalDate.now()) return "Hoy"
    if (date == LocalDate.now().minusDays(1)) return "Ayer"
    if (date == LocalDate.now().plusDays(1)) return "Mañana"
    return if (date.year != LocalDate.now().year) {
        DateTimeFormatter.ofPattern("EEEE dd MMMM, yyyy").format(date)
    } else {
        DateTimeFormatter.ofPattern("EEEE dd, MMMM").format(date)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun EntryDatePickerPreview() {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        FormatUtils.convertMillisToStringDate(it.plus(1000 * 60 * 60 * 5))
    } ?: FormatUtils.convertMillisToStringDate(Instant.now().toEpochMilli())
    EntryDatePicker(
        showDatePicker,
        datePickerState,
        selectedDate,
        { showDatePicker = !showDatePicker },
        { showDatePicker = false },
        { millis -> 
            // En el Preview, simplemente actualizamos el estado local
            // En producción, esto será manejado por el componente padre
        }
    )
}
