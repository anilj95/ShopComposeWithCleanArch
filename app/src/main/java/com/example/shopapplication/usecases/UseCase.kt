package com.example.shopapplication.usecases

interface UseCase<in InputT, out OutputT> {
    fun execute(input: InputT): OutputT
}

interface SuspendingUseCase<in InputT, out OutputT> {
    suspend fun execute(input: InputT): OutputT
}

interface NoInputUseCase<out OutputT> : UseCase<Nothing, OutputT> {
    override fun execute(input: Nothing) = execute()
    fun execute(): OutputT
}

interface NoInputSuspendingUseCase<out OutputT> : SuspendingUseCase<Nothing, OutputT> {
    override suspend fun execute(input: Nothing) = execute()
    suspend fun execute(): OutputT
}