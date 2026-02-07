package com.example.data.common.datastore;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DataStoreManagerImpl_Factory implements Factory<DataStoreManagerImpl> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  private DataStoreManagerImpl_Factory(Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public DataStoreManagerImpl get() {
    return newInstance(dataStoreProvider.get());
  }

  public static DataStoreManagerImpl_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new DataStoreManagerImpl_Factory(dataStoreProvider);
  }

  public static DataStoreManagerImpl newInstance(DataStore<Preferences> dataStore) {
    return new DataStoreManagerImpl(dataStore);
  }
}
