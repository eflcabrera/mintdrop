package com.eflc.mintdrop.ui.screens.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.models.ExpenseCategory
import com.eflc.mintdrop.models.ExpenseSubCategory
import com.eflc.mintdrop.repository.CategoryRepository
import com.eflc.mintdrop.repository.GoogleSheetsRepository
import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.repository.SubcategoryRowRepository
import com.eflc.mintdrop.room.dao.entity.Category
import com.eflc.mintdrop.room.dao.entity.Subcategory
import com.eflc.mintdrop.room.dao.entity.SubcategoryRow
import com.eflc.mintdrop.room.dao.entity.relationship.CategoryAndSubcategory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndSubcategoryRow
import com.eflc.mintdrop.utils.Constants
import com.google.android.gms.common.util.Strings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val googleSheetsRepository: GoogleSheetsRepository,
    private val categoryRepository: CategoryRepository,
    private val subcategoryRepository: SubcategoryRepository,
    private val subcategoryRowRepository: SubcategoryRowRepository,
) : ViewModel() {
    private val _incomeCategoryList = MutableStateFlow(emptyList<ExpenseCategory>())
    val incomeCategoryList = _incomeCategoryList.asStateFlow()

    fun getIncomeCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            var categories = mutableListOf<ExpenseCategory>()

            var data: List<CategoryAndSubcategory> = categoryRepository.findCategoriesByType(EntryType.INCOME)

            if (data.isEmpty()) {
                val response = googleSheetsRepository.getCategories(Constants.GOOGLE_SHEET_ID_2024, Constants.INCOME_SHEET_NAME)
                categories = response.categories.toMutableList()
                response.categories
                    .filter { it.name != "end" && !Strings.isEmptyOrWhitespace(it.id) }
                    .forEach { incomeCategory ->
                        val categoryId = categoryRepository.saveCategory(
                            Category(
                                externalId = incomeCategory.id,
                                name = incomeCategory.name,
                                type = EntryType.INCOME
                            )
                        )
                        incomeCategory.subCategories
                            .filter { it.name != "end" && !Strings.isEmptyOrWhitespace(it.id) }
                            .forEachIndexed { index, incomeSubCategory ->
                                if (index != 0) {
                                    val subcategoryId = subcategoryRepository.saveSubcategory(
                                        Subcategory(
                                            categoryId = categoryId,
                                            externalId = incomeSubCategory.id,
                                            name = incomeSubCategory.name
                                        )
                                    )
                                    subcategoryRowRepository.saveSubcategoryRow(
                                        SubcategoryRow(
                                            subcategoryId = subcategoryId,
                                            rowNumber = incomeSubCategory.rowNumber
                                        )
                                    )
                                }
                            }
                    }
            } else {
                data.forEach {
                    val subcategories: List<ExpenseSubCategory> = it.subcategories.map {
                        subcategory: SubcategoryAndSubcategoryRow -> ExpenseSubCategory(
                            subcategory.subcategory.externalId,
                            subcategory.subcategory.name,
                            subcategory.subcategoryRow.rowNumber
                        )
                    }
                    categories.add(ExpenseCategory(id = it.category.externalId, name = it.category.name, subCategories = subcategories))
                }
            }

            _incomeCategoryList.tryEmit(categories)
        }
    }
}