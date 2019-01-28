package com.wecast.mobile.ui.screen.settings.buffer;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class BufferActivityModule {

    @Provides
    BufferActivityViewModel provideBufferActivityViewModel() {
        return new BufferActivityViewModel();
    }
}
