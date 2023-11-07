package com.naozumi.izinboss.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import com.naozumi.izinboss.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class RequestLeaveViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var dataRepository: DataRepository
    @Mock
    private lateinit var userPreferences: UserPreferences
    private lateinit var requestLeaveViewModel: RequestLeaveViewModel

    @Before
    fun setup() {
        requestLeaveViewModel = RequestLeaveViewModel(dataRepository, userPreferences)
    }
}