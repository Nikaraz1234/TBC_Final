package com.example.mycomposeapp.core.domain.network

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val hasInternet: Flow<Boolean>

}