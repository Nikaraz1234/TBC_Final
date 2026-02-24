package com.example.mycomposeapp.core.domain.usecase.user

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetAllUsersUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var useCase: GetAllUsersUseCase

    @Before
    fun setUp() {
        userRepository = mock()
        useCase = GetAllUsersUseCase(userRepository)
    }

    @Test
    fun `invoke returns the exact flow from the repository`() = runTest {
        val flow = flowOf<Resource<List<User>>>()
        whenever(userRepository.getAllUser()).thenReturn(flow)

        val result = useCase()

        verify(userRepository).getAllUser()
        assertEquals(flow, result)
    }

    @Test
    fun `invoke emits Loading state`() = runTest {
        whenever(userRepository.getAllUser()).thenReturn(flowOf(Resource.Loading))

        val emissions = useCase().toList()

        assertEquals(listOf(Resource.Loading), emissions)
    }

    @Test
    fun `invoke emits Success with a list of users`() = runTest {
        val users = listOf(
            User(userId = "1", username = "Alice"),
            User(userId = "2", username = "Bob")
        )
        whenever(userRepository.getAllUser()).thenReturn(flowOf(Resource.Success(users)))

        val emissions = useCase().toList()

        assertEquals(listOf(Resource.Success(users)), emissions)
    }

    @Test
    fun `invoke emits Success with an empty list`() = runTest {
        whenever(userRepository.getAllUser()).thenReturn(flowOf(Resource.Success(emptyList())))

        val emissions = useCase().toList()

        assertEquals(listOf(Resource.Success(emptyList<User>())), emissions)
    }

    @Test
    fun `invoke emits Error with message`() = runTest {
        val message = "Network error"
        whenever(userRepository.getAllUser()).thenReturn(flowOf(Resource.Error(message)))

        val emissions = useCase().toList()

        assertEquals(listOf(Resource.Error(message)), emissions)
    }
}
