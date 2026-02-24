package com.example.mycomposeapp.core.domain.usecase.user

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAllUsersUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var useCase: GetAllUsersUseCase

    @Before
    fun setUp() {
        userRepository = mockk()
        useCase = GetAllUsersUseCase(userRepository)
    }

    @Test
    fun `invoke returns the exact flow from the repository`() = runTest {
        val flow = flowOf<Resource<List<User>>>()
        every { userRepository.getAllUser() } returns flow

        val result = useCase()

        verify { userRepository.getAllUser() }
        assertEquals(flow, result)
    }

    @Test
    fun `invoke emits Loading state`() = runTest {
        every { userRepository.getAllUser() } returns flowOf(Resource.Loading)

        val emissions = useCase().toList()

        assertEquals(listOf(Resource.Loading), emissions)
    }

    @Test
    fun `invoke emits Success with a list of users`() = runTest {
        val users = listOf(
            User(userId = "1", username = "Alice"),
            User(userId = "2", username = "Bob")
        )
        every { userRepository.getAllUser() } returns flowOf(Resource.Success(users))

        val emissions = useCase().toList()

        assertEquals(listOf(Resource.Success(users)), emissions)
    }

    @Test
    fun `invoke emits Success with an empty list`() = runTest {
        every { userRepository.getAllUser() } returns flowOf(Resource.Success(emptyList()))

        val emissions = useCase().toList()

        assertEquals(listOf(Resource.Success(emptyList<User>())), emissions)
    }

    @Test
    fun `invoke emits Error with message`() = runTest {
        val message = "Network error"
        every { userRepository.getAllUser() } returns flowOf(Resource.Error(message))

        val emissions = useCase().toList()

        assertEquals(listOf(Resource.Error(message)), emissions)
    }
}
