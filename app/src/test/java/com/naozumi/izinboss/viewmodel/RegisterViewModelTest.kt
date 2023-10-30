package com.naozumi.izinboss.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.viewmodel.entry.RegisterViewModel
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.util.MainDispatcherRule
import com.naozumi.izinboss.util.getOrAwaitValue
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
class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var dataRepository: DataRepository
    private lateinit var registerViewModel: RegisterViewModel

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(dataRepository)
    }

    @Test
    fun `when registerUser is valid should Return Success`() = runBlocking {
        val expectedData = Unit
        val expectedResult = Result.Success(Unit)

        Mockito.`when`(dataRepository.registerWithEmail("Bima", "test@gmail.com", "12345678"))
            .thenReturn(flowOf(expectedResult))

        val actualResult = registerViewModel.registerWithEmail("Bima", "test@gmail.com", "12345678")
            .getOrAwaitValue()

        Mockito.verify(dataRepository).registerWithEmail("Bima", "test@gmail.com", "12345678")
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Result.Success)
        if (actualResult is Result.Success) {
            Assert.assertEquals(expectedData, actualResult.data)
        }
    }

    @Test
    fun `when registerUser is invalid should Return Error`() = runBlocking {
        val expectedErrorMessage = "Invalid input"

        // Create a Result.Error with the expected error message
        val expectedResult = Result.Error(expectedErrorMessage)

        // Use the 'flowOf' function to create a Flow with the expected result
        Mockito.`when`(dataRepository.registerWithEmail("InvalidName", "invalidEmail", "invalidPassword"))
            .thenReturn(flowOf(expectedResult))
        val actualResult = registerViewModel.registerWithEmail("InvalidName", "invalidEmail", "invalidPassword")
            .getOrAwaitValue()
        Mockito.verify(dataRepository).registerWithEmail("InvalidName", "invalidEmail", "invalidPassword")

        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Result.Error)
        if (actualResult is Result.Error) {
            Assert.assertEquals(expectedErrorMessage, actualResult.error)
        }
    }
}