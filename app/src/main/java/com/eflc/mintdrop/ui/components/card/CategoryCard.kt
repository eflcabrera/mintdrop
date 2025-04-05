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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
                .height(60.dp)
                .fillMaxWidth()
                .background(color = Color.White)
        ) {
            Box(modifier = Modifier.fillMaxHeight().width(10.dp))
            iconMap?.get(category.name)?.let { painterResource(id = it) }?.let {
                Image(
                    painter = it,
                    contentDescription = "android image",
                    colorFilter = ColorFilter.tint(color = Color(66, 135, 245)),
                    modifier = Modifier
                        .size(30.dp)
                )
            }
            Text(
                text = category.name,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontSize = fontSize,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryCardPreview() {
    val iconMap = mapOf(
        "Deuda" to R.drawable.buildings_svgrepo_com,
        "Educación" to R.drawable.square_academic_cap_svgrepo_com,
        "Ocio" to R.drawable.gamepad_svgrepo_com,
        "Gastos diarios" to R.drawable.cart_3_svgrepo_com,
        "Regalos" to R.drawable.gift_svgrepo_com,
        "Salud/médicos" to R.drawable.heart_svgrepo_com,
        "Vivienda" to R.drawable.home_1_svgrepo_com,
        "Seguros" to R.drawable.shield_svgrepo_com,
        "Mascotas" to R.drawable.cat_svgrepo_com,
        "Tecnología" to R.drawable.cpu_svgrepo_com,
        "Transporte" to R.drawable.bus_svgrepo_com,
        "Viajes" to R.drawable.suitcase_tag_svgrepo_com,
        "Servicios básicos" to R.drawable.lightbulb_bolt_svgrepo_com,
        "Ahorro o Inversión" to R.drawable.star_shine_svgrepo_com,
        "Impuestos" to R.drawable.star_1_svgrepo_com
    )
    CategoryCard(
        iconMap = iconMap,
        category = ExpenseCategory("1", "Gastos diarios",
            listOf(ExpenseSubCategory("1", "subcat", 1))),
        onClick = {},
        modifier = Modifier
    )
}
