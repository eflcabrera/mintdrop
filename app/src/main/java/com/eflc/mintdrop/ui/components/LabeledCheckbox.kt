package com.eflc.mintdrop.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LabeledCheckbox(
    label: String,
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
        )
        Text(text = label, Modifier.padding(end = 12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun LabeledCheckboxPreview() {
    var isSharedExpenseInput by remember { mutableStateOf(false) }
    LabeledCheckbox(label = "Es gasto compartido",
        isChecked = isSharedExpenseInput,
        onCheckedChange = { },
        modifier = Modifier
            .clickable { isSharedExpenseInput = !isSharedExpenseInput })
}
