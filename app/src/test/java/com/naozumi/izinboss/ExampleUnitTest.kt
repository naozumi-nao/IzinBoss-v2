package com.naozumi.izinboss

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.naozumi.izinboss.core.data.DataRepository
import com.naozumi.izinboss.core.data.UserPreferences
import com.naozumi.izinboss.core.model.local.LeaveRequest
import com.naozumi.izinboss.util.MainDispatcherRule
import com.naozumi.izinboss.viewmodel.MainViewModel
import com.naozumi.izinboss.core.helper.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    @Mock
    private lateinit var dataRepository: DataRepository
    @Mock
    private lateinit var pref: UserPreferences
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        viewModel = MainViewModel(dataRepository, pref)
    }
    @Test
    fun `when Getting leaveRequests Should Return Success Instead of Null`() {
        val observer = Observer<Result<List<LeaveRequest>>> {}
        val expectedLeaveRequests = MutableLiveData<Result<List<LeaveRequest>>>()
        //expectedLeaveRequests.value = Result.Success()
    }
}