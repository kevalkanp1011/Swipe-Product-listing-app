package dev.kevalkanpariya.swipetakehomeassign.utils

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    val isConnected: Flow<Boolean>
}