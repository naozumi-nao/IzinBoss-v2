package com.naozumi.izinboss.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.viewmodel.entry.RegisterViewModel
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.util.MainDispatcherRule
import com.naozumi.izinboss.util.getOrAwaitValue
import com.naozumi.izinboss.viewmodel.entry.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var dataRepository: DataRepository
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(dataRepository)
    }

    @Test
    fun testLoginEmailReturnSuccess() = runBlocking {
        val expectedData = Unit
        val expectedResult = Result.Success(Unit)

        Mockito.`when`(dataRepository.loginWithEmail("test@gmail.com", "12345678"))
            .thenReturn(flowOf(expectedResult))

        val actualResult = loginViewModel.loginWithEmail("test@gmail.com", "12345678")
            .getOrAwaitValue()

        Mockito.verify(dataRepository).loginWithEmail("test@gmail.com", "12345678")
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Result.Success)
        if (actualResult is Result.Success) {
            Assert.assertEquals(expectedData, actualResult.data)
        }
    }

    @Test
    fun testLoginEmailReturnError() = runBlocking {
        val expectedErrorMessage = "Invalid input"
        val expectedResult = Result.Error(expectedErrorMessage)

        Mockito.`when`(dataRepository.loginWithEmail("invalidEmail", "123"))
            .thenReturn(flowOf(expectedResult))
        val actualResult = loginViewModel.loginWithEmail("invalidEmail", "123")
            .getOrAwaitValue()
        Mockito.verify(dataRepository).loginWithEmail("invalidEmail", "123")

        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Result.Error)
        if (actualResult is Result.Error) {
            Assert.assertEquals(expectedErrorMessage, actualResult.error)
        }
    }

    @Test
    fun testLoginEmailReturnLoading() = runBlocking {
        val expectedResult = Result.Loading

        Mockito.`when`(dataRepository.loginWithEmail("test@gmail.com", "12345678"))
            .thenReturn(flowOf(expectedResult))

        val actualResult = loginViewModel.loginWithEmail("test@gmail.com", "12345678")
            .getOrAwaitValue()

        Mockito.verify(dataRepository).loginWithEmail("test@gmail.com", "12345678")
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Result.Loading)
    }
}