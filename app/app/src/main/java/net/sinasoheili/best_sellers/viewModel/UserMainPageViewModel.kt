package net.sinasoheili.best_sellers.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.sinasoheili.best_sellers.model.Shop
import net.sinasoheili.best_sellers.model.User
import net.sinasoheili.best_sellers.repository.ShopRepository
import net.sinasoheili.best_sellers.repository.UserRepository
import net.sinasoheili.best_sellers.util.DataState

class UserMainPageViewModel constructor( private val userRepository: UserRepository ,
                                         private val shopRepository: ShopRepository): ViewModel() {

    val userInfoData: MutableLiveData<DataState<User>> = MutableLiveData<DataState<User>>()
    val userDeleteData: MutableLiveData<DataState<Boolean>> = MutableLiveData<DataState<Boolean>>()
    val shopData: MutableLiveData<DataState<Shop>> = MutableLiveData<DataState<Shop>>()

    fun getUserInfo() {
        viewModelScope.launch {
            userRepository.getUserInfo().onEach {
                userInfoData.value = it
            }.launchIn(viewModelScope)
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            userRepository.removeUserAccount().onEach {
                userDeleteData.value = it
            }.launchIn(viewModelScope)
        }
    }

    fun getShopInfo(shopId: Int) {
        viewModelScope.launch {
            shopRepository.getShopInfo(shopId).onEach {
                shopData.value = it
            }.launchIn(viewModelScope)
        }
    }
}