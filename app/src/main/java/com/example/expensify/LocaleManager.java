package com.example.expensify;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleManager {
  public static final String LANGUAGE_ENGLISH = "en";
  public static final String LANGUAGE_VIETNAMESE = "vi";
  public static final String LANGUAGE_JAPANESE = "ja";

  public static final String LANGUAGE_FRANCE = "fr";

  public LocaleManager(Context context) {
    this.baseContext = context;
    this.savedConfiguration = new SavedConfiguration<>("locale", "locale");

    if (!savedConfiguration.getValue().isPresent()) {
      savedConfiguration.save(Locale.getDefault().getLanguage());
    }

    setLocale(savedConfiguration.getValue().get());
  }

  public void setLocale(String localeCode) {
    String crrContextLocale = baseContext.getResources().getConfiguration().getLocales().get(0).getLanguage();

    if (crrContextLocale.equals(localeCode)) {
      return;
    }

    Locale locale = new Locale(localeCode);
    Locale.setDefault(locale);
    Configuration config = baseContext.getResources().getConfiguration();
    config.setLocale(locale);
    baseContext
        .getResources()
        .updateConfiguration(config, baseContext.getResources().getDisplayMetrics());
    savedConfiguration.save(localeCode);
  }

  public String getCurrentLocaleCode() {
    return savedConfiguration.getValue().get();
  }

  private final Context baseContext;
  private final SavedConfiguration<String> savedConfiguration;
}
