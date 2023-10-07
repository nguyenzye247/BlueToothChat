package nguyen.hl.bluetoothchat.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nguyen.hl.bluetoothchat.data.chat.AndroidBlueToothController
import nguyen.hl.bluetoothchat.domain.chat.BlueToothController
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideBlueToothController(@ApplicationContext context: Context): BlueToothController {
        return AndroidBlueToothController(context)
    }
}
