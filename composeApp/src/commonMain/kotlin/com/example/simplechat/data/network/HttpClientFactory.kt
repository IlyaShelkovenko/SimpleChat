package com.example.simplechat.data.network

import io.ktor.client.HttpClient

expect fun createHttpClient(): HttpClient
