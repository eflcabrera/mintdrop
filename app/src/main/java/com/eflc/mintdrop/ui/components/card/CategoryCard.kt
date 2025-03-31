package com.eflc.mintdrop.ui.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eflc.mintdrop.R
import com.eflc.mintdrop.models.ExpenseCategory
import com.eflc.mintdrop.models.ExpenseSubCategory
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth()
                .background(color = Color.White)
        ) {
            Box(modifier = Modifier.fillMaxHeight().width(10.dp))
            iconMap?.get(category.name)?.let { painterResource(id = it) }?.let {
                Image(
                    painter = it,
                    contentDescription = "android image",
                    modifier = Modifier
                        .size(30.dp)
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

@Preview(showBackground = true)
@Composable
fun CategoryCardPreview() {
    val iconMap = mapOf(
        "Deuda" to R.drawable.apartment,
        "Educación" to R.drawable.graduation_hat,
        "Ocio" to R.drawable.dice,
        "Gastos diarios" to R.drawable.cart,
        "Regalos" to R.drawable.gift,
        "Salud/médicos" to R.drawable.heart_pulse,
        "Vivienda" to R.drawable.home,
        "Seguros" to R.drawable.lock,
        "Mascotas" to R.drawable.paw,
        "Tecnología" to R.drawable.laptop_phone,
        "Transporte" to R.drawable.car,
        "Viajes" to R.drawable.map,
        "Servicios básicos" to R.drawable.inbox,
        "Ahorro o Inversión" to R.drawable.star,
        "Impuestos" to R.drawable.poop
    )
    CategoryCard(
        iconMap = iconMap,
        category = ExpenseCategory("1", "Gastos diarios",
            listOf(ExpenseSubCategory("1", "subcat", 1))),
        onClick = {},
        modifier = Modifier
    )
}
