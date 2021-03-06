package net.sinasoheili.best_sellers.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.sinasoheili.best_sellers.model.Discount
import net.sinasoheili.best_sellers.repository.DiscountRepository
import net.sinasoheili.best_sellers.util.DataState

class SellerDashboardFragmentViewModel
    constructor( val discountRepository: DiscountRepository )
    : ViewModel()
{
    val registerDiscountData: MutableLiveData<DataState<Discount>> = MutableLiveData()
    val deleteDiscountData: MutableLiveData<DataState<Boolean>> = MutableLiveData()
    val getShopDiscountData: MutableLiveData<DataState<Discount>> = MutableLiveData()
    val checkUserHasDiscountData: MutableLiveData<DataState<Boolean>> = MutableLiveData()
    val deleteUserDiscountData: MutableLiveData<DataState<Boolean>> = MutableLiveData()

    fun registerDiscount(discount: Discount) {
        viewModelScope.launch {

            discountRepository.registerDiscount(discount).onEach {
                setRegisterDiscountDataState(it)
            }.launchIn(viewModelScope)

        }
    }

    fun deleteDiscount() {
        viewModelScope.launch {

            discountRepository.deleteDiscount().onEach {
                setDeleteDiscountDataState(it)
            }.launchIn(viewModelScope)

        }
    }

    fun getShopDiscount() {
        viewModelScope.launch {
            discountRepository.getShopDiscount().onEach {
                setGetShopDiscountDataState(it)
            }.launchIn(viewModelScope)
        }
    }

    fun checkUserHasDiscount(userId: Int) {
        viewModelScope.launch {
            discountRepository.checkUserHasDiscount(userId).onEach {
                setUserCheckDiscountDataState(it)
            }.launchIn(viewModelScope)
        }
    }

    fun deleteDiscountOfUser(userId: Int) {
        viewModelScope.launch {
            discountRepository.deleteUserDiscount(userId).onEach {
                setDeleteUserDiscountDataState(it)
            }.launchIn(viewModelScope)
        }
    }

    private fun setRegisterDiscountDataState(data: DataState<Discount>) {
        registerDiscountData.value = data
    }

    private fun setDeleteDiscountDataState(data: DataState<Boolean>) {
        deleteDiscountData.value = data
    }

    private fun setGetShopDiscountDataState(data: DataState<Discount>) {
        getShopDiscountData.value = data
    }

    private fun setUserCheckDiscountDataState(data: DataState<Boolean>) {
        checkUserHasDiscountData.value = data
    }

    private fun setDeleteUserDiscountDataState(data: DataState<Boolean>) {
        deleteUserDiscountData.value = data
    }
}