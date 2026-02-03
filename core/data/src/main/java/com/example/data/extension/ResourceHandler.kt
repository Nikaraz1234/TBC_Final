package com.example.data.extension

import com.example.domain.Resource

inline fun <T, R> Resource<T>.asResource(transform: (T) -> R): Resource<R> {
    return when (this) {
        is Resource.Success -> Resource.Success(transform(data))
        is Resource.Error -> Resource.Error(message)
        Resource.Loading -> Resource.Loading
    }
}