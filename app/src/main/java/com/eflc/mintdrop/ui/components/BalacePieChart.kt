package com.eflc.mintdrop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.eflc.mintdrop.models.SubCategoryBalanceData
import com.eflc.mintdrop.utils.RandomColors
import kotlin.math.roundToInt

@Composable
fun BalancePieChart(
    modifier: Modifier,
    data: List<SubCategoryBalanceData>,
    referencesEnabled: Boolean = true
) {
    val slices = mutableListOf<PieChartData.Slice>()
    val randomColors = RandomColors()

    data.forEach {
        if (it.balance > 0.0) {
            slices.add(
                PieChartData.Slice(
                    it.subcategoryName,
                    (it.balance.roundToInt() / 100).toFloat(),
                    it.color ?: Color(randomColors.getColor())
                )
            )
        }
    }

    val pieChartData = PieChartData(slices = slices, plotType = PlotType.Pie)

    val pieChartConfig = PieChartConfig(
        showSliceLabels = false,
        isAnimationEnable = false,
        labelVisible = true,
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (referencesEnabled) {
            LazyVerticalGrid(
                modifier = Modifier.padding(bottom = 16.dp),
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.Center
            ) {
                items(slices) { slice: PieChartData.Slice ->
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(20.dp)
                                .background(color = slice.color)
                                .padding(5.dp)
                        )
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            text = slice.label,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
        PieChart(
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
                .background(color = MaterialTheme.colorScheme.background),
            pieChartData,
            pieChartConfig
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BalancePieChartPreview() {
    BalancePieChart(data = listOf(
        SubCategoryBalanceData(1, "Cafés y helados", 50788.129, 9, 2024),
        SubCategoryBalanceData(2, "Lavandería",100.0, 9, 2024),
        SubCategoryBalanceData(3, "Compras", 110714.59, 9, 2024),
        SubCategoryBalanceData(4, "Restaurantes", 20000.0, 9, 2024),
        SubCategoryBalanceData(5, "Delivery", 356000.1, 9, 2024)
    ), modifier = Modifier)
}
