package io.element.android.support.zero.data.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import io.element.android.libraries.di.AppScope
import io.element.android.libraries.di.SingleIn
import io.element.android.support.zero.data.delegate.DataCleaner
import io.element.android.support.zero.data.delegate.Preferences
import io.element.android.support.zero.data.repository.AuthRepository
import io.element.android.support.zero.data.repository.AuthRepositoryImpl
import io.element.android.support.zero.data.repository.ConversationRepository
import io.element.android.support.zero.data.repository.ConversationRepositoryImpl
import io.element.android.support.zero.network.service.ZeroAuthService
import io.element.android.support.zero.network.service.ZeroConversationService

@Module
@ContributesTo(AppScope::class)
object RepositoryModule {

    @Provides
    @SingleIn(AppScope::class)
    fun bindAuthRepository(
        preferences: Preferences,
        zeroAuthService: ZeroAuthService,
        dataCleaner: DataCleaner
    ): AuthRepository = AuthRepositoryImpl(preferences, zeroAuthService, dataCleaner)

    @Provides
    @SingleIn(AppScope::class)
    fun bindConversationRepository(
        zeroConversationService: ZeroConversationService
    ): ConversationRepository = ConversationRepositoryImpl(zeroConversationService)
}
