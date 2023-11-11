import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.util.DataDummy
import com.naozumi.izinboss.util.getOrAwaitValue
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.util.MainDispatcherRule
import com.naozumi.izinboss.viewmodel.MainViewModel
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
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var dataRepository: DataRepository
    private lateinit var mainViewModel: MainViewModel
    private val dummyUser = DataDummy.generateDummyUsers()[0]
    private val dummyLeaveRequest = DataDummy.generateDummyLeaveRequests()

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(dataRepository)
    }

    @Test
    fun testGetAllLeaveRequests() = runBlocking {
        val expectedData = dummyLeaveRequest
        val expectedResult = Result.Success(expectedData)

        Mockito.`when`(dataRepository.getAllLeaveRequests(dummyUser))
            .thenReturn(flowOf(expectedResult))
        val actualResult = mainViewModel.getAllLeaveRequests()
            .getOrAwaitValue()
        Mockito.verify(dataRepository).getAllLeaveRequests(dummyUser)

        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Result.Success)
        Assert.assertEquals(dummyLeaveRequest.size, (actualResult as Result.Success).data.size)
    }

    @Test
    fun getCurrentUserId_shouldCallGetCurrentUserIdOnRepository() = runBlocking {
        mainViewModel.getCurrentUserId()

        Mockito.verify(dataRepository).getCurrentUserId()
    }

    @Test
    fun saveUserToPreferences_shouldSaveUserToRepository() = runBlocking {
        val user = dummyUser
        mainViewModel.saveUserToPreferences(user)

        Mockito.verify(dataRepository).saveUserToPreferences(user)
    }

    @Test
    fun deleteCurrentUserPref_shouldDeleteCurrentUserFromPreferences() = runBlocking {
        mainViewModel.deleteCurrentUserPref()

        Mockito.verify(dataRepository).deleteCurrentUserFromPreferences()
    }
}
