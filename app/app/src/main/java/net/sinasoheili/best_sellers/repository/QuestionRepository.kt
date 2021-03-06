package net.sinasoheili.best_sellers.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import net.sinasoheili.best_sellers.R
import net.sinasoheili.best_sellers.model.AnsweredQuestion
import net.sinasoheili.best_sellers.model.Question
import net.sinasoheili.best_sellers.util.CacheToPreference
import net.sinasoheili.best_sellers.util.DataState
import net.sinasoheili.best_sellers.util.Keys
import net.sinasoheili.best_sellers.webService.*

class QuestionRepository constructor(val context: Context,
                                     private val webService: WebService ,
                                     private val questionMapper: QuestionMapper,
                                     private val answeredQuestionMapper: AnsweredQuestionMapper)
{
    suspend fun getCategoryQuestion(categoryId: Int) = flow<DataState<MutableList<Question>>> {
        emit(DataState.Loading())
        delay(1000)

        try {
            val qe: UserQuestionEntity = webService.userGetQuestion(categoryId)
            if(qe.findQuestion) {
                val listQe: List<QuestionEntity> = qe.questions
                val result: MutableList<Question> = mutableListOf()
                for(i in listQe) {
                    result.add(questionMapper.toBase(i))
                }
                emit(DataState.Success(result))
            } else {
                emit(DataState.Error(context.getString(R.string.question_not_found)))
            }
        } catch (e: Exception) {
            emit(DataState.ConnectionError(e))
        }
    }

    suspend fun submitQuestion(shopId: Int, ans: Map<String , Int>) = flow<DataState<Boolean>> {
        emit(DataState.Loading())
        delay(1000)

        try {
            val sre: SubmitResultEntity = webService.submitQuestion(getUserIdFromCache() , shopId , ans)
            if(sre.registered) {
                emit(DataState.Success(true))
            } else {
                emit(DataState.Error(context.getString(R.string.question_not_register)))
            }

        } catch (e: Exception) {
            emit(DataState.ConnectionError(e))
        }
    }

    suspend fun checkUserAnsQuestion(shopId: Int) = flow<DataState<Boolean>> {
        emit(DataState.Loading())
        delay(1000)

        try {
            val uaqe: UserAnsQuestionEntity = webService.checkUserAnsQuestion(getUserIdFromCache() , shopId)
            emit(DataState.Success(uaqe.userAnsStatus))

        } catch(e: Exception) {
            emit(DataState.ConnectionError(e))
        }
    }

    suspend fun getUserAnsweredQuestion(shopId: Int) = flow<DataState<List<AnsweredQuestion>>> {
        emit(DataState.Loading())
        delay(1000)

        try {
            val aqer: AnsweredQuestionEntityResponse= webService.getUserAnsweredQuestion(getUserIdFromCache(), shopId)
            val result : List<AnsweredQuestionEntity> = aqer.result
            val response: MutableList<AnsweredQuestion> = mutableListOf()
            for(i in result) {
                response.add(answeredQuestionMapper.toBase(i))
            }
            emit(DataState.Success(response))
        } catch (e: Exception) {
            emit(DataState.ConnectionError(e))
        }
    }

    suspend fun removeSurvey(shopId: Int) = flow<DataState<Boolean>> {
        emit(DataState.Loading())
        delay(1000)

        try {
            val dsr: DeleteSurveyResponse = webService.removeSurvey(getUserIdFromCache(), shopId)
            if(dsr.result) {
                emit(DataState.Success(true))
            } else {
                emit(DataState.Error(context.getString(R.string.unknown_error_happen)))
            }

        } catch (e: Exception) {
            emit(DataState.ConnectionError(e))
        }
    }

    private fun getUserIdFromCache() : Int {
        return if(CacheToPreference.getWhoLogIn(context).equals(Keys.USER)) CacheToPreference.getPersonId(context) else -1;
    }
}