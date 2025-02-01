package com.eflc.mintdrop.ui.components.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eflc.mintdrop.R

@Composable
fun ConfirmationEntryDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    isVisible: MutableState<Boolean>,
    confirmLabel: String = "OK",
    cancelLabel: String = "Cancelar"
) {
    if (isVisible.value) {
        AlertDialog(
            icon = {
                Icon(
                    painterResource(id = R.drawable.danger_triangle_svgrepo_com),
                    contentDescription = "Undo warning icon",
                    modifier = Modifier.size(30.dp)
                )
            },
            title = {
                Text(text = dialogTitle, fontSize = 18.sp)
            },
            text = {
                Text(text = dialogText)
            },
            onDismissRequest = {
                onDismissRequest()
                isVisible.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmation()
                        isVisible.value = false
                    }
                ) {
                    Text(confirmLabel)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        isVisible.value = false
                    }
                ) {
                    Text(cancelLabel)
                }
            }
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun UndoEntryDialogPreview() {
    ConfirmationEntryDialog(
        onDismissRequest = { /*TODO*/ },
        onConfirmation = { /*TODO*/ },
        dialogTitle = "Deshacer última entrada",
        dialogText = "¿Revertir gasto 'Pau SW' en 'Otros' por ARS 10.200,00?",
        isVisible = mutableStateOf(true)
    )
}
