package net.sinasoheili.best_sellers.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.sinasoheili.best_sellers.R
import net.sinasoheili.best_sellers.model.Message
import net.sinasoheili.best_sellers.util.CacheToPreference
import net.sinasoheili.best_sellers.util.DataState
import net.sinasoheili.best_sellers.util.Keys
import net.sinasoheili.best_sellers.webService.*
import java.lang.Exception

class MessageRepository constructor(val context: Context,
                                    private val webService: WebService,
                                    private val messageMapper: MessageMapper)
{
    suspend fun getShopMessages(shopId: Int = -1) : Flow<DataState<MutableList<Message>>> = flow {
        emit(DataState.Loading())
        delay(1000)

        try {

            var id: Int

            if(shopId == -1) {
                id = getShopIdFromCache()
            } else {
                id = shopId
            }

            val  shopResponse: ShopGetMessageResponse = webService.shopGetMessage(id)
            if (shopResponse.find) {
                val messageListEntity: List<MessageEntity> = shopResponse.messages
                val messageResult: MutableList<Message> = arrayListOf()
                for (i in messageListEntity) {
                    messageResult.add(messageMapper.toBase(i))
                }
                emit(DataState.Success(messageResult))
            } else {
                emit(DataState.Error(context.getString(R.string.message_not_found)))
            }

        } catch (e: Exception) {
            emit(DataState.ConnectionError(e))
        }
    }

    suspend fun registerMessage(shopId: Int, message: String) : Flow<DataState<Boolean>> = flow {
        emit(DataState.Loading())
        delay(1000)

        try {
            val rme: RegisterMessageEntity = webService.registerMessage( getUserIdFromCache(), shopId , message)
            if(rme.messageRegister) {
                emit(DataState.Success(true))
            } else {
                emit(DataState.Error(context.getString(R.string.message_is_not_registered)))
            }
        } catch(e: Exception) {
            emit(DataState.ConnectionError(e))
        }
    }

    suspend fun getUserShopMessage(shopId: Int) : Flow<DataState<Message>> = flow {
        emit(DataState.Loading())
        delay(1000)

        try {
            val usme: UserShopMessageEntity = webService.getUserShopMessage(shopId,getUserIdFromCache())
            if(usme.findMessage) {
                val message: Message = messageMapper.toBase(usme.message)
                emit(DataState.Success(message))
            } else {
                emit(DataState.Error(context.getString(R.string.message_not_found)))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.ConnectionError(e))
        }
    }

    private fun getShopIdFromCache() : Int{
        return CacheToPreference.getShopId(context)
    }

    private fun getUserIdFromCache() : Int {
        return if(CacheToPreference.getWhoLogIn(context).equals(Keys.USER)) CacheToPreference.getPersonId(context) else -1;
    }
}